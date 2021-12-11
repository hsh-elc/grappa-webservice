package de.hsh.grappa.service;

import com.google.common.collect.MinMaxPriorityQueue;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.boundary.BoundaryImpl;
import de.hsh.grappa.cache.QueuedSubmission;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.common.BackendPlugin;
import de.hsh.grappa.common.Boundary;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.impl.ProformaResponseHelper.Audience;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.config.LmsConfig;
import de.hsh.grappa.exceptions.AuthenticationException;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.util.ClassPathClassLoader;
import de.hsh.grappa.util.ClassPathClassLoader.Classpath;
import de.hsh.grappa.util.XmlUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages worker threads for a specific graderId.
 * A grader pool consists of a configured number
 * of grader instances of a specific grader type.
 */
public class GraderPool {
    private static final Logger log = LoggerFactory.getLogger(GraderPool.class);
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

    private GraderConfig graderConfig;
    private ClassPathClassLoader<BackendPlugin> backendPluginLoader;
    private Boundary boundary;

    private static final String GRAPPA_CONTEXT_GRADER_ID = "Grappa.Context.GraderId";
    private static final String GRAPPA_CONTEXT_GRADE_PROCESS_ID = "Grappa.Context.GraderProcessId";
    private static final String GRAPPA_CONTEXT_LOG_LEVEL = "Grappa.Context.LogLevel";
    private Properties graderConfigInitProps;

    private ConcurrentHashMap<String /*gradeProcId*/, Future<ResponseResource>> gpMap =
        new ConcurrentHashMap<>();

    private HashMap<String /*taskUuid*/, MinMaxPriorityQueue<Duration> /*seconds*/>
        gradingDurationMap = new HashMap<>();

    private ExecutorService jaxbExecutor;
    private Semaphore semaphore;
    private GraderPoolManager graderWorkersMgr;

    public GraderPool(GraderConfig graderConfig, GraderPoolManager graderManager) throws Exception {
        this.graderConfig = graderConfig;
        this.backendPluginLoader = new ClassPathClassLoader<>(BackendPlugin.class, graderConfig.getId());
        this.boundary = new BoundaryImpl();
        
        if(0 >= graderConfig.getConcurrent_grading_processes())
            throw new IllegalArgumentException(String.format("concurrent_grading_processes must not be less than 1 " +
                "for graderId '%s'.", graderConfig.getConcurrent_grading_processes()));

        loadBackendPlugin(graderConfig);
        log.info("Using grader '{}' with {} concurrent instances.",
            graderConfig.getId(), graderConfig.getConcurrent_grading_processes());
        this.semaphore = new Semaphore(graderConfig.getConcurrent_grading_processes());
        this.graderWorkersMgr = graderManager;

        jaxbExecutor = Executors.newFixedThreadPool(graderConfig.getConcurrent_grading_processes(),
            new XmlUtils.JaxbThreadFactory());
    }

    public void shutdown() {
        jaxbExecutor.shutdownNow();
    }

    /**
     * Checks if a submission is availbale for grading for this grader (pool),
     * and if so, the submission is retrieved and graded.
     * <p>
     * Runs asynchronously.
     *
     * @return True, if a grade process has started. False, if all workers are busy
     */
    public boolean tryGrade() {
        // We need to acquire the semaphore this soon already, since we
        // pop a submission from the queue in the next step and commit
        // ourselves to grading it. Otherwise, we'd have to put the submission
        // back into the queue if no available grader instances are available,
        // and that would make the whole concurrency code a whole lot more complicated.
        if (semaphore.tryAcquire()) {
            log.debug("[Grader: '{}']: semaphore aquired, {} left", graderConfig.getId(), semaphore.availablePermits());
            boolean releaseSemaphore = true; // release in current thread if we can't start a grading process
            try {
                QueuedSubmission queuedSubm = RedisController.getInstance().popSubmission(graderConfig.getId());
                if (null != queuedSubm) {
                    releaseSemaphore = false; // starting another thread that will release the semaphore eventually
                    log.info("[GraderId: '{}', GradeProcessId: '{}']: Starting grading process...",
                        graderConfig.getId(), queuedSubm.getGradeProcId());
                    CompletableFuture.supplyAsync(() -> {
                        return runGradingProcess(queuedSubm);
                    }, jaxbExecutor).thenAccept(resp -> {
                        cacheProformaResponseResult(resp, queuedSubm.getGradeProcId());
                    }).thenRun(() -> {
                        // We are done. This grader instance has become free, so check if there's anything
                        // queued without prompting.
                        tryGrade(); // reuse future's async thread
                    });
                } else
                    log.debug("[GraderID: '{}']: This grader's submission queue is empty.", graderConfig.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (releaseSemaphore) {
                    semaphore.release();
                    log.debug("[GraderID: '{}']: Nothing to do here. Semaphore released, {} left", graderConfig.getId(),
                        semaphore.availablePermits());
                }
            }
            return true; // will grade
        }
        return false; // will not grade (all workers are busy)
    }

    /**
     * Sets additional grading context properties for the plugin.
     *
     * Sets the graderId and gradeProcessId, so plugins, such as the docker proxy plugin,
     * can use these ids in the plugin's logging context.
     * @param graderId
     * @param gradeProcId
     */
    private Properties getGraderConfigWithContextIds(String graderId, String gradeProcId) {
        Properties propsWithLoggingContext = new Properties();
        synchronized (graderConfigInitProps) {
            graderConfigInitProps.forEach((key, value) -> {
                propsWithLoggingContext.setProperty((String)key, (String)value);
            });
        }
        propsWithLoggingContext.setProperty(GRAPPA_CONTEXT_GRADER_ID, graderId);
        propsWithLoggingContext.setProperty(GRAPPA_CONTEXT_GRADE_PROCESS_ID, gradeProcId);
        propsWithLoggingContext.setProperty(GRAPPA_CONTEXT_LOG_LEVEL, this.graderConfig.getLogging_level());
        return propsWithLoggingContext;
    }

    public ResponseResource runGradingProcess(QueuedSubmission subm) {
        @SuppressWarnings("serial")
        class NoResultGraderExecption extends Exception {
            public NoResultGraderExecption(String s) { super(s); }
        }
        try {
            LocalDateTime beginTime = LocalDateTime.now();
            Properties props = getGraderConfigWithContextIds(graderConfig.getId(), subm.getGradeProcId());
            // Create a fresh backend plugin instance for every grading request
            if (backendPluginLoader == null) {
                log.error("Class loader of BackendPlugin '{}' not found", graderConfig.getId());
                throw new Exception("Class loader of backend plugin '" + graderConfig.getId() + "' not found");
            }
            BackendPlugin bp = backendPluginLoader.instantiateClass(graderConfig.getClass_name());
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Initializing BackendPlugin...",
                graderConfig.getId(), subm.getGradeProcId());
            bp.init(props, boundary);
            FutureTask<ResponseResource> futureTask = null;
            int timeoutSeconds = graderConfig.getTimeout_seconds();
            try {
                futureTask = new FutureTask<ResponseResource>(() -> {
                    return bp.grade(subm.getSubmission());
                });
                gpMap.put(subm.getGradeProcId(), futureTask);
                new Thread(futureTask).start();
                ResponseResource resp = futureTask.get(timeoutSeconds, TimeUnit.SECONDS);
                log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process exited.",
                    graderConfig.getId(), subm.getGradeProcId());
                if (null != resp) {
                    totalGradingProcessesSucceeded.incrementAndGet();
                    log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process finished successfully.",
                        graderConfig.getId(), subm.getGradeProcId());
                    // set the average grading duration only for grading processes that actually produced
                    // a valid proforma response. Anything else (such as errors) will skew the average duration.
                    setAverageGradingDuration(Duration.between(beginTime, LocalDateTime.now()),
                        subm.getGradeProcId());
                    return resp;
                }
                throw new NoResultGraderExecption("Grader did not supply a proforma response.");
            } catch (ExecutionException | NoResultGraderExecption e) { // BackendPlugin threw an exception
                totalGradingProcessesFailed.incrementAndGet();
                String errorMessage = String.format("[GraderId: '%s', GradeProcessId: '%s']: Grading process failed " +
                        "with error: %s", graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
                log.error(errorMessage);
                log.error(ExceptionUtils.getStackTrace(e));
                return createInternalErrorResponse(errorMessage, subm, Audience.TEACHER_ONLY);
            } catch (TimeoutException e) {
                totalGradingProcessesTimedOut.incrementAndGet();
                String errorMessage = String.format("[GraderId: '%s', GradeProcessId: '%s']: Grading process timed " +
                    "out after %d seconds.", graderConfig.getId(), subm.getGradeProcId(), timeoutSeconds);
                log.warn(errorMessage);
                log.info("[GraderId: '{}', GradeProcessId: '{}']: Trying to stop timed out grading process...",
                    graderConfig.getId(), subm.getGradeProcId());
                futureTask.cancel(true);
                // Don't increment totalGradingProcessesCancelled, since the cancellation was due to a timeout,
                // not due to a client's delete request
                log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process has been cancelled after timing out.",
                    graderConfig.getId(), subm.getGradeProcId());
                // How do we know this timeout was due to the grader and not a forever loop in the student submission?
                return createInternalErrorResponse(errorMessage, subm, Audience.BOTH);
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
                log.debug("[GraderId: '{}', GradeProcessId: '{}']: Grading process has been cancelled after parent " +
                        "thread interruption.", graderConfig.getId(), subm.getGradeProcId());
                return createInternalErrorResponse("Grading process was interrupted.", subm, Audience.TEACHER_ONLY);
            } catch (Throwable e) { // catch any other error
                totalGradingProcessesFailed.incrementAndGet();
                String errorMessage = String.format("[GraderId: '%s', GradeProcessId: '%s']: Grading process " +
                    "failed with error: %s", graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
                log.error(errorMessage);
                log.error(ExceptionUtils.getStackTrace(e));
                return createInternalErrorResponse(errorMessage, subm, Audience.TEACHER_ONLY);
            }
        } catch (Throwable e) {
            String errorMessage = String.format("[GraderId: '%s', GradeProcessId: '%s']: Grading process encountered " +
                    "error: %s", graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
            log.error(errorMessage);
            log.error(ExceptionUtils.getStackTrace(e));
            try {
				return createInternalErrorResponse(errorMessage, subm, Audience.TEACHER_ONLY);
			} catch (Exception e1) {
				throw new Error(e1);
			}
        } finally {
            totalGradingProcessesExecuted.incrementAndGet(); // finished one way or the other
            semaphore.release();
            gpMap.remove(subm.getGradeProcId());
            log.debug("[GraderId: '{}', GradeProcessId: '{}']: semaphore released, {} left", graderConfig.getId(),
                subm.getGradeProcId(), semaphore.availablePermits());
        }
        return null;
    }

    private LmsConfig getLmsConfig(String lmsId) {
        var lms = GrappaServlet.CONFIG.getLms().stream()
                .filter(l -> l.getId().equals(lmsId)).findFirst();
        if (!lms.isPresent())
            throw new AuthenticationException("Unknown lmsId '"+lmsId+"'.");
        return lms.get();
    }

    private ResponseResource createInternalErrorResponse(String errorMessage, QueuedSubmission subm, Audience audience)  throws Exception {
        LmsConfig lmsConfig = getLmsConfig(subm.getLmsId());
        boolean ietm = lmsConfig.getEietamtf();
        ProformaVersion pv = ProformaVersion.getDefault();
        return pv.getResponseHelper().createInternalErrorResponse(errorMessage, subm.getSubmission(), boundary, audience, ietm);

        // when eliminating the flag isExpected_internal_error_type_always_merged_test_feedback,
        // then the following call we do:
        //return ProformaResponseGenerator.createInternalErrorResponse(errorMessage, subm.getSubmission(), audience);
    }

    private void setAverageGradingDuration(Duration d, String gradeProcId) {
        try {
            String taskUuid = RedisController.getInstance().getAssociatedTaskUuid(gradeProcId);
            if(null == taskUuid)
                throw new NotFoundException(String
                    .format("There is no associated taskUuid for gradeProcId '{}'.",
                    gradeProcId));

            long avgDuration;
            synchronized (gradingDurationMap) {
                var queue = gradingDurationMap.get(taskUuid);
                if (null == queue) {
                    queue = MinMaxPriorityQueue.maximumSize
                        (GrappaServlet.CONFIG.getService()
                            .getPrev_grading_seconds_max_list_size()).create();
                    gradingDurationMap.put(taskUuid, queue);
                }
                queue.add(d);
                avgDuration = queue.stream().reduce((a, b) -> a.plus(b)).get().toSeconds() / queue.size();
            }

            log.debug("[GraderId: '{}', GradeProcessId: '{}', Task-UUID: '{}']: Average grading duration: {} seconds",
                graderConfig.getId(), gradeProcId, taskUuid, avgDuration);
            RedisController.getInstance().setTaskAverageGradingDurationSeconds(taskUuid, avgDuration);
        } catch (Exception e) {
            log.error("Failed to set grading duration.");
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void cacheProformaResponseResult(ResponseResource resp, String gradeProcId) {
        if (null != resp) {
            log.debug("[GraderId: '{}', GradeProcessId: '{}']: Caching response: {}", graderConfig.getId(),
                gradeProcId, resp);
            RedisController.getInstance().setResponse(gradeProcId, resp);
        } else {
            log.debug("[GraderId: '{}', GradeProcessId: '{}']: Grading process did not supply a response result. " +
                "Nothing to cache.", graderConfig.getId(), gradeProcId);
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

    public int getPoolSize() {
        return graderConfig.getConcurrent_grading_processes();
    }

    public int getBusyCount() {
        return getPoolSize() - semaphore.availablePermits();
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

    private void loadBackendPlugin(GraderConfig grader) throws Exception {
        log.info("Loading grader plugin '{}' with classpathes '{}'...", grader.getId(), grader.getClass_path());
        backendPluginLoader.configure(Classpath.of(grader.getClass_path(), grader.getFile_extension()));
        log.info("Loading grader config file '{}'...", grader.getConfig_path());
        graderConfigInitProps = new Properties();
        try (InputStream is = new FileInputStream(new File(grader.getConfig_path()))) {
            graderConfigInitProps.load(is);
        }
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

//    private static ForkJoinPool getJaxbExecutor() {
//        XmlUtils.JaxbForkJoinWorkerThreadFactory threadFactory = new XmlUtils.JaxbForkJoinWorkerThreadFactory();
//        int parallelism = Math.min(0x7fff /* copied from ForkJoinPool.java */, Runtime.getRuntime().availableProcessors());
//        return new ForkJoinPool(parallelism, threadFactory, null, false);
//    }
}