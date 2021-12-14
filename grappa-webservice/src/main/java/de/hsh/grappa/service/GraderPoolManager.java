package de.hsh.grappa.service;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.exceptions.GrappaException;
import proforma.util.exception.NotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A facade/manager pattern of sorts for GraderPool instances.
 * Any access and calls to GraderPools are exclusively done
 * using this manager class.
 */
public class GraderPoolManager implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GraderPoolManager.class);
    private static GraderPoolManager gwm = null;
    private final AtomicBoolean stopStartingNewGradingProcesses =
        new AtomicBoolean(false);

    //private ConcurrentHashMap<String, GraderPool> pools;
    private HashMap<String, GraderPool> pools;

    public static GraderPoolManager getInstance() {
        if (null == gwm)
            gwm = new GraderPoolManager();
        return gwm;
    }

    public void init(List<GraderConfig> graders) {
        this.pools = new HashMap<>(graders.size()); //new ConcurrentHashMap<>(graders.size());
        for (GraderConfig g : graders) {
            if (g.getEnabled()) {
                try {
                    this.pools.put(g.getId(), new GraderPool(g, this));
                } catch (Exception e) {
                    log.error("Could not load grader '{}'.", g.getId());
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else log.debug("Ignoring disabled grader '{}'.",
                g.getId());
        }
    }

    public boolean isGradeProcIdBeingGradedRightNow(String gradeProcId) {
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            if (e.getValue().isGradeProcIdBeingGradedRightNow(gradeProcId))
                return true;
        }
        return false;
    }

    public void shutdown() {
        stopStartingNewGradingProcesses.set(true);

        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            try {
                e.getValue().shutdown();
            } catch(Throwable ex) {
                log.error(ex.getMessage());
                log.error(ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    public void resumeStartingNewGradingProcesses() {
        stopStartingNewGradingProcesses.set(false);
    }

    public boolean cancelGradingProcess(String gradeProcId) throws Exception {
        String graderId = RedisController.getInstance().getAssociatedGraderId(gradeProcId);
        GraderPool gp = pools.get(graderId);
        if (null != gp)
            return gp.cancelGradeProcess(gradeProcId);
        // Treat it as a server error if the associated grader pool is missing
        throw new GrappaException(String.format("Missing GraderPool for graderId '%s' and associated gradeProcId '%s'",
            graderId, gradeProcId));
    }

    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
                // Iterate through all graders (grader pools), check if any of them
                // have queued submissions
                while (0 < RedisController.getInstance().getSubmissionQueueCount(e.getKey())
                    && !Thread.currentThread().isInterrupted()) {
                    if (!stopStartingNewGradingProcesses.get()) {
                        if (!e.getValue().tryGrade()) {
                            log.debug("[GraderId: '{}']: Grader pool is currently exhausted.",
                                e.getKey());
                            break; // this particular grader pool's exhausted
                        }
                    }
                }
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * @return A list of grader ids. All of these graders are guaranteed to be active (enabled).
     */
    public Collection<String> getGraderIds() {
        return Collections.unmodifiableCollection(pools.keySet());
    }

    public int getPoolSize(String graderId) throws NotFoundException {
        var pool = pools.get(graderId);
        if(null != pool)
            return pool.getPoolSize();
        throw new NotFoundException(String.format("GraderId '%s' does not exist.", graderId));
    }

    /**
     * Get the number of currently busy (i.e. in use) grader instances
     * for a particular graderId.
     * @param graderId
     * @return
     * @throws NotFoundException
     */
    public int getBusyCount(String graderId) throws NotFoundException {
        var pool = pools.get(graderId);
        if(null != pool)
            return pool.getBusyCount();
        throw new NotFoundException(String.format("GraderId '%s' does not exist.", graderId));
    }

//    public long getEstimatedSecondsUntilQueueIsGraded(String graderId) throws NotFoundException {
//        var pool = pools.get(graderId);
//        if (null != pool) {
//            int busy = pool.getBusyCount();
//            int poolSize = pool.getPoolSize();
//            long avgGradingSeconds = RedisController.getInstance().getSubmissionAverageGradingDurationSeconds(gradeProcId,
//                GrappaServlet.CONFIG.getService().getDefault_estimated_grading_seconds());
//            long queueCount = RedisController.getInstance().getSubmissionQueueCount(graderId);
//            long estimatedSeconds = (queueCount / poolSize) * avgGradingSeconds;
//            if(busy > 0)
//                estimatedSeconds += estimatedSeconds;
//            return estimatedSeconds;
//        }
//        throw new NotFoundException(String.format("GraderId '%s' does not exist.", graderId));
//    }

    /**
     * Get the estimated remaining seconds remaining until a particular
     * grading process (which equals a particular submission) is finished.
     *
     * This takes into account the number of the total pool size of a grader
     * that a submission has been submitted to, as well as the number of
     * currently busy grader instances in that pool, along with the previously
     * measured time it took to grade the task that the submission has been
     * submitted for.
     * @param gradeProcId
     * @return
     * @throws NotFoundException
     */
    public long getEstimatedSecondsUntilGradeProcIdIsFinished(String gradeProcId) throws NotFoundException {
        String graderId = RedisController.getInstance().getAssociatedGraderId(gradeProcId);
        var pool = pools.get(graderId);
        if (null != pool) {
            int poolSize = pool.getPoolSize();
            int freeCount = poolSize - pool.getBusyCount();
            long avgGradingSeconds = RedisController.getInstance().getSubmissionAverageGradingDurationSeconds(gradeProcId,
                GrappaServlet.CONFIG.getService().getDefault_estimated_grading_seconds());
            int submPos = RedisController.getInstance().getQueuedSubmissionIndex(gradeProcId);

            // Calculating a submission's estimated grading seconds remaining
            // relies heavily on the submission's position/index in a queue:
            // if the subm. index is -1, it is being processed right now (in case it's not been graded already)
            // if the subm. index is 0 or above, it's probably next up for grading once a grader instance
            // becomes free. We also need to account for asynchronous grading, i.e. the grader pool size
            if(-1 == submPos)
                return avgGradingSeconds;
            // A group of N graders can pick up N submissions from the submission queue.
            // the groupIndex is the multiplicator for the average time it takes to garde
            // a task.
            long groupIndex = (submPos + poolSize) / poolSize;
            // account for some or all graders being busy at this point
            boolean noGraderAvailableToGradeMe = submPos + 1 > freeCount;
            long addAvgSec = noGraderAvailableToGradeMe ? avgGradingSeconds : 0;
            // if there's no free grader available to grade this queued submission,
            // add 1 avgGradingSeconds on top to account for busy graders
            long addedAvg = (noGraderAvailableToGradeMe ? avgGradingSeconds : 0);
            long estimatedSeconds = groupIndex * avgGradingSeconds + addedAvg;
            log.debug("[GradeProcId: {}]: submIndex: {}, groupIndex: {}, noGraderFreeToGradeMe: {}, estimatedSec: {}",
                gradeProcId, submPos, groupIndex, noGraderAvailableToGradeMe, estimatedSeconds);
            return estimatedSeconds;
        }
        throw new NotFoundException(String.format("GraderId '%s' does not exist.", graderId));
    }

    public Map<String, GraderStatistics> getGraderStatistics() {
        HashMap<String, GraderStatistics> m = new HashMap<>();
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            m.put(e.getKey(), e.getValue().getGraderStatistics());
        }
        return m;
    }

    public long getTotalGradingProcessesExecuted() {
        long total = 0;
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            total += e.getValue().getTotalGradingProcessesExecuted();
        }
        return total;
    }

    public long getTotalGradingProcessesSucceeded() {
        long total = 0;
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            total += e.getValue().getTotalGradingProcessesSucceeded();
        }
        return total;
    }

    public long getTotalGradingProcessesFailed() {
        long total = 0;
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            total += e.getValue().getTotalGradingProcessesFailed();
        }
        return total;
    }

    public long getTotalGradingProcessesCancelled() {
        long total = 0;
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            total += e.getValue().getTotalGradingProcessesCancelled();
        }
        return total;
    }

    public long getTotalGradingProcessesTimedOut() {
        long total = 0;
        for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
            total += e.getValue().getTotalGradingProcessesTimedOut();
        }
        return total;
    }
}
