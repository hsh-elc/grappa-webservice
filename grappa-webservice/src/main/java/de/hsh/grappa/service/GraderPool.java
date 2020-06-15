package de.hsh.grappa.service;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.QueuedSubmission;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.exceptions.NoResultGraderExecption;
import de.hsh.grappa.plugins.backendplugin.BackendPlugin;
import de.hsh.grappa.proforma.ProformaResponse;
import de.hsh.grappa.proforma.ProformaV201ResponseGenerator;
import de.hsh.grappa.utils.ClassLoaderHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages worker threads for a specific graderId.
 */
public class GraderPool {
    private static Logger log = LoggerFactory.getLogger(GraderPool.class);
    private final AtomicLong totalGradingProcessesExecuted =
        new AtomicLong(0);
    private final AtomicLong totalGradingProcessesSucceeded =
        new AtomicLong(0);
    private final AtomicLong totalGradingProcessesFailed =
        new AtomicLong(0);
    private final AtomicLong totalGradingProcessesCancelled =
        new AtomicLong(0);
    private final AtomicLong totalGradingProcessesTimedOut =
        new AtomicLong(0);

    private BackendPlugin backendPlugin;
    private GraderConfig graderConfig;

    //    private ConcurrentHashMap<String, CompletableFuture<ProformaResponse>> hp =
//        new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Future<ProformaResponse>> gpMap =
        new ConcurrentHashMap<>();

    private Semaphore semaphore;
    //private PropertyChangeSupport workerFreeEvent = new PropertyChangeSupport(this);
    private GraderPoolManager graderWorkersMgr;

    public GraderPool(GraderConfig graderConfig, GraderPoolManager graderManager) throws Exception {
        this.graderConfig = graderConfig;
        log.info("Loading and initializing grader backend plugin {}...", graderConfig.getId());
        this.backendPlugin = loadBackendPlugin(graderConfig);
        log.debug("Using grader '{}' with {} concurrent instances.",
            graderConfig.getId(), graderConfig.getConcurrent_grading_processes());
        this.semaphore = new Semaphore(graderConfig.getConcurrent_grading_processes());
        this.graderWorkersMgr = graderManager;
    }

    /**
     * Checks if a submission is availbale for grading for this grader (pool),
     * and if so, the submission is retrieved and graded.
     * <p>
     * Runs asynchronously.
     *
     * @return Frue, if a grade process has started. False, if all workers are busy
     */
    public boolean tryGrade() {
        // We need to acquire the semaphore this soon already, since we
        // pop a submission from the queue in the next step and commit
        // ourselves to grading it. Otherwise, we'd have to put the submission
        // back into the queue.
        if (semaphore.tryAcquire()) {
            log.debug("Grader '{}': semaphore aquired, {} left", graderConfig.getId(), semaphore.availablePermits());
            boolean releaseSemaphore = true; // release in current thread if we can't start a grading process
            try {
                QueuedSubmission queuedSubm = GrappaServlet.redis.popSubmission(graderConfig.getId());
                if (null != queuedSubm) {
                    releaseSemaphore = false; // starting another thread that will release the semaphore eventually
                    log.info("[GraderId: '{}', GradeProcessId: '{}']: Starting grading process...",
                        graderConfig.getId(), queuedSubm.getGradeProcId());
                    CompletableFuture.supplyAsync(() -> {
                        return runGradingProcess(queuedSubm);
                    }).thenAccept(resp -> {
                        processProformaResponseResult(resp, queuedSubm.getGradeProcId());
                    }).thenRun(() -> {
                        // Grading slot has become free, try grading if there's anything queued.
                        tryGrade();
                    });
                } else
                    log.debug("[GraderID: '{}']: This grader's submission queue is empty.", graderConfig.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (releaseSemaphore) {
                    semaphore.release();
                    log.debug("Grader '{}': Nothing to do here. Semaphore released, {} left", graderConfig.getId(),
                        semaphore.availablePermits());
                }
            }
            return true; // will grade
        }
        return false; // will not grade (all workers are busy)
    }

    public ProformaResponse runGradingProcess(QueuedSubmission subm) {
        FutureTask<ProformaResponse> futureTask = null;
        try {
            log.debug("GRADE START: {}", subm.getGradeProcId());
            futureTask = new FutureTask<ProformaResponse>(() -> {
                return backendPlugin.grade(subm.getSubmission());
            });
            gpMap.put(subm.getGradeProcId(), futureTask);
            new Thread(futureTask).start();
            ProformaResponse resp = futureTask.get(graderConfig.getTimeout_seconds(), TimeUnit.SECONDS);
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process exited.",
                graderConfig.getId(), subm.getGradeProcId());
            if (null != resp) {
                totalGradingProcessesSucceeded.incrementAndGet();
                log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process finished successfully.",
                    graderConfig.getId(), subm.getGradeProcId());
                return resp;
            }
            throw new NoResultGraderExecption("Grader did not supply a proforma response.");
        } catch (ExecutionException | NoResultGraderExecption e) { // BackendPlugin threw an exception
            totalGradingProcessesFailed.incrementAndGet();
            log.error("[GraderId: '{}', GradeProcessId: '{}']: Grading process failed with error: {}",
                graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            // TODO: maybe create a is-internal-error response with the grader log as feedback? What about
            //  timeouts though, it could be the student's code that timed out.
        } catch (TimeoutException e) {
            totalGradingProcessesTimedOut.incrementAndGet();
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Grader timed out after {} seconds. Trying to cancel " +
                    "grading process...",
                graderConfig.getId(), subm.getGradeProcId(), graderConfig.getTimeout_seconds());
            futureTask.cancel(true);
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process has been cancelled after timing out.",
                graderConfig.getId(), subm.getGradeProcId(), graderConfig.getTimeout_seconds());
            // Don't increment totalGradingProcessesCancelled, since the cancellation was due to a timeout.
            // TODO: return resp with is-internal-true and errorMessage=timeout.
            // How do we know this timeout was due to the grader and not the student submission???
            return null;
        } catch (CancellationException e) {
            totalGradingProcessesCancelled.incrementAndGet();
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process cancelled.",
                graderConfig.getId(), subm.getGradeProcId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Parent thread of grade process interrupted. Trying to " +
                "cancel grading process...", graderConfig.getId(), subm.getGradeProcId());
            futureTask.cancel(true);
            // Treat this as cancellation, a direct result of the interrupt
            totalGradingProcessesCancelled.incrementAndGet();
            log.debug("[GraderId: '{}', GradeProcessId: '{}']: Grading process has been cancelled after  parent " +
                    "thread interruption.",
                graderConfig.getId(), subm.getGradeProcId(), graderConfig.getTimeout_seconds());
            // TODO: return errorResponse with is-inernal-error?
        } catch (Exception e) { // anything else unpredictable
            totalGradingProcessesFailed.incrementAndGet();
            log.error("[GraderId: '{}', GradeProcessId: '{}']: Grading process failed with error: {}",
                graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            // TODO: return errorResp with is-internal-error=true and errorMsg=e.getMessage()
            return null;
        } finally {
            totalGradingProcessesExecuted.incrementAndGet(); // finished one way or the other
            log.debug("GRADE ENDE: {}", subm.getGradeProcId());
            semaphore.release();
            gpMap.remove(subm.getGradeProcId());
            log.debug("Grader '{}': semaphore released, {} left", graderConfig.getId(),
                semaphore.availablePermits());
        }
        return null;
    }

    private void processProformaResponseResult(ProformaResponse resp, String gradeProcId) {
        if (null != resp) {
            log.debug("[Grader '{}']: Caching response: {}", graderConfig.getId(), resp);
            GrappaServlet.redis.setResponse(gradeProcId, resp);
        } else {
            log.debug("[Grader '{}']: Grading process did not supply a response result. " +
                "Nothing to cache.", graderConfig.getId());
        }
    }

    public boolean cancelGradeProcess(String gradeProcId) {
        var process = gpMap.get(gradeProcId);
        if (null != process) {
            log.debug("GraderPool.cancel(): Trying to cancel grading process with gradeProcId '{}'...",
                gradeProcId);
            process.cancel(true);
            log.debug("GraderPool.cancel(): Grading process with gradeProcId '{}' cancelled: {}",
                gradeProcId, process.isDone());
            return true;
        }
        log.debug("GraderPool.cancel(): No grading process active with gradeProcId '{}'", gradeProcId);
        return false;
    }

    public GraderStatistics getGraderStatistics() {
        return new GraderStatistics(
            totalGradingProcessesExecuted.get(),
            totalGradingProcessesSucceeded.get(),
            totalGradingProcessesFailed.get(),
            totalGradingProcessesTimedOut.get(),
            totalGradingProcessesCancelled.get()
        );
    }

    public boolean isGradeProcIdBeingGradedRightNow(String gradeProcId) {
        return gpMap.containsKey(gradeProcId);
    }

    private ProformaResponse createInternalErrorResponse(String message) {
        return ProformaV201ResponseGenerator.createInternalErrorResponse(message);
    }

    private BackendPlugin loadBackendPlugin(GraderConfig grader) throws Exception {
        BackendPlugin bp = new ClassLoaderHelper<BackendPlugin>().LoadClass(grader.getClass_path(),
            grader.getClass_name(),
            BackendPlugin.class);
        log.info("Grader '{}' loaded. Initializing grader...", grader.getId());
        try (InputStream is = new FileInputStream(new File(grader.getConfig_path()))) {
            Properties props = new Properties();
            props.load(is);
            bp.init(props);
        }
        return bp;
    }

    public long getTotalGradingProcessesExecuted() {
        return totalGradingProcessesExecuted.get();
    }

    public long getTotalGradingProcessesSucceeded() {
        return totalGradingProcessesSucceeded.get();
    }

    public long getTotalGradingProcessesFailed() {
        return totalGradingProcessesFailed.get();
    }

    public long getTotalGradingProcessesCancelled() {
        return totalGradingProcessesCancelled.get();
    }

    public long getTotalGradingProcessesTimedOut() {
        return totalGradingProcessesTimedOut.get();
    }
}