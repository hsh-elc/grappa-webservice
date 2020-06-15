package de.hsh.grappa.service;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.exceptions.GrappaException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (null != graderId) {
            GraderPool gp = pools.get(graderId);
            if (null != gp)
                return gp.cancelGradeProcess(gradeProcId);
            // Treat it as a server error if the associated grader pool is missing
            throw new GrappaException(String.format("Missing GraderPool for graderId '%s' and associated gradeProcId '%s'",
                graderId, gradeProcId));
        }
        return false;
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
