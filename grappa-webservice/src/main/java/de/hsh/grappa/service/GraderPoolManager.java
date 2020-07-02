package de.hsh.grappa.service;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.config.GrappaConfig;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GraderPoolManager implements /*PropertyChangeListener {*/ Runnable {
    private static Logger log = LoggerFactory.getLogger(GraderPoolManager.class);
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
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else log.debug("Ignoring grader '{}', since it is configured to be disabled.",
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

    public void stopStartingNewGradingProcesses() {
        stopStartingNewGradingProcesses.set(true);
    }

    public void resumeStartingNewGradingProcesses() {
        stopStartingNewGradingProcesses.set(false);
    }

    public boolean cancelGradingProcess(String gradeProcId) throws Exception {
        String graderId = GrappaServlet.redis.getAssociatedGraderId(gradeProcId);
        GraderPool gp = pools.get(graderId);
        if (null != gp)
            return gp.cancelGradeProcess(gradeProcId);
        // Treat it as a server error if the associated grader pool is missing
        throw new GrappaException(String.format("Missing GraderPool for graderId '%s' and associated gradeProcId '%s'",
            graderId, gradeProcId));
    }

//    @Override public void propertyChange(PropertyChangeEvent evt) {
//    }

    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            for (Map.Entry<String, GraderPool> e : pools.entrySet()) {
                // Iterate through all grade workers, check if any of them
                // have queued submissions
                while (0 < GrappaServlet.redis.getSubmissionQueueCount(e.getKey())
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
        throw new NotFoundException(String.format("GraderId '{}' does not exist.", graderId));
    }

    public int getBusyCount(String graderId) throws NotFoundException {
        var pool = pools.get(graderId);
        if(null != pool)
            return pool.getBusyCount();
        throw new NotFoundException(String.format("GraderId '{}' does not exist.", graderId));
    }

//    public long getEstimatedSecondsUntilQueueIsGraded(String graderId) throws NotFoundException {
//        var pool = pools.get(graderId);
//        if (null != pool) {
//            int busy = pool.getBusyCount();
//            int poolSize = pool.getPoolSize();
//            long avgGradingSeconds = GrappaServlet.redis.getSubmissionAverageGradingDurationSeconds(gradeProcId,
//                GrappaServlet.CONFIG.getService().getDefault_estimated_grading_seconds());
//            long queueCount = GrappaServlet.redis.getSubmissionQueueCount(graderId);
//            long estimatedSeconds = (queueCount / poolSize) * avgGradingSeconds;
//            if(busy > 0)
//                estimatedSeconds += estimatedSeconds;
//            return estimatedSeconds;
//        }
//        throw new NotFoundException(String.format("GraderId '{}' does not exist.", graderId));
//    }

    public long getEstimatedSecondsUntilGradeProcIdIsFinished(String gradeProcId) throws NotFoundException {
        String graderId = GrappaServlet.redis.getAssociatedGraderId(gradeProcId);
        var pool = pools.get(graderId);
        if (null != pool) {
            int poolSize = pool.getPoolSize();
            int freeCount = poolSize - pool.getBusyCount();
            long avgGradingSeconds = GrappaServlet.redis.getSubmissionAverageGradingDurationSeconds(gradeProcId,
                GrappaServlet.CONFIG.getService().getDefault_estimated_grading_seconds());
            int submPos = GrappaServlet.redis.getQueuedSubmissionIndex(gradeProcId);

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
        throw new NotFoundException(String.format("GraderId '{}' does not exist.", graderId));
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
