package de.hsh.grappa.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MinMaxPriorityQueue;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.backendplugin.BackendPlugin;
import de.hsh.grappa.backendplugin.dockerproxy.DockerProxyBackendPlugin;
import de.hsh.grappa.boundary.BoundaryImpl;
import de.hsh.grappa.cache.QueuedSubmission;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.DockerProxyConfig;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.config.GraderID;
import de.hsh.grappa.config.GraderDockerJvmBpConfig;
import de.hsh.grappa.config.GraderHostJvmBpConfig;
import de.hsh.grappa.config.LmsConfig;
import de.hsh.grappa.exceptions.AuthenticationException;
import de.hsh.grappa.util.ClassPathClassLoader;
import de.hsh.grappa.util.ClassPathClassLoader.Classpath;
import de.hsh.grappa.util.DebugUtils;
import proforma.util.ProformaResponseHelper.Audience;
import proforma.util.ProformaVersion;
import proforma.util.SubmissionLive;
import proforma.util.boundary.Boundary;
import proforma.util.div.XmlUtils;
import proforma.util.exception.NotFoundException;
import proforma.util.resource.ResponseResource;

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

    private final GraderConfig graderConfig;
    private ClassPathClassLoader<BackendPlugin> backendPluginLoader;
    private Boundary boundary;

    private static final String OP_MODE_LOCAL_VM="host_jvm_bp";
    private static final String OP_MODE_DOCKER_VM="docker_jvm_bp";
	private static final String GRADER_BP_JAR_FILENAME = "graderBP.jar";

    private ConcurrentHashMap<String /*gradeProcId*/, Future<ResponseResource>> gpMap =
        new ConcurrentHashMap<>();

    private HashMap<String /*taskUuid*/, MinMaxPriorityQueue<Duration> /*seconds*/>
        gradingDurationMap = new HashMap<>();

    private ExecutorService jaxbExecutor;
    private Semaphore semaphore;
    private GraderPoolManager graderWorkersMgr;

    public GraderPool(GraderConfig graderConfig, GraderPoolManager graderManager) throws Exception {
        this.graderConfig = graderConfig;
        this.backendPluginLoader = new ClassPathClassLoader<>(BackendPlugin.class, graderConfig.getId().toString());
        this.boundary = new BoundaryImpl();
        
        if(0 >= graderConfig.getConcurrent_grading_processes())
            throw new IllegalArgumentException(String.format("concurrent_grading_processes must not be less than 1 " +
                "for graderId '%s'.", graderConfig.getConcurrent_grading_processes()));

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
    
    private BackendPlugin loadAndInitBackendPlugin(String submId) throws Exception{
    	BackendPlugin bp=null;
    	Properties props=graderConfig.getGrader_plugin_defaults();
        if (props == null) props = new Properties(); // empty map instead of null

        String logLevel=graderConfig.getLogging_level();
        GraderID graderId=graderConfig.getId();
        //Encodings 
        String fileEncoding=graderConfig.getFile_encoding();
        String userLanguage=graderConfig.getUser_language();
        String userCountry=graderConfig.getUser_country();

        //Determines whether dockerProxy is used
    	String operatingMode=graderConfig.getOperating_mode();
    	if(operatingMode.equals(OP_MODE_DOCKER_VM)) {
    		//DOCKER BP
    		
        	
        	//TODO: this is always *.jar since we build this plugin, right?
        	//String dockerBpFileExtension="*.jar";
    		//not used anymore since Dockerproxy is included into grappa webservice
        	
    		//TODO remove
            //log.info("Loading grader plugin '{}' with classpathes '{}'...", graderId, dockerBpClassPath);
            //backendPluginLoader.configure(Classpath.of(dockerBpClassPath, dockerBpFileExtension));
    		//DockerProxyBackendPlugin dockerBp=(DockerProxyBackendPlugin)backendPluginLoader.instantiateClass(dockerBpClassName);
    		
            log.info("Loading grader plugin '{}' with '{}'...", graderId, DockerProxyBackendPlugin.class.getSimpleName());
    		DockerProxyBackendPlugin dockerBp=new DockerProxyBackendPlugin();
    		
        	//init and call 3 additional DockerBP methods
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Initializing DockerProxyBackendPlugin...",graderId,submId);

    		dockerBp.init(props,boundary,logLevel, fileEncoding, userLanguage, userCountry);
    		
    		dockerBp.setContext(graderId.toString(),submId);
    		
    		//Docker 
    		//Host prefs
        	DockerProxyConfig dockerConfig=GrappaServlet.CONFIG.getDocker_proxy();
        	if(dockerConfig==null)throw new IllegalArgumentException(String.format("Missing definition of 'docker_proxy' for operating_mode: %s.",OP_MODE_DOCKER_VM));
    		String dockerHost=dockerConfig.getHost();
    		if(dockerHost==null || dockerHost.equals(""))throw new IllegalArgumentException(String.format("Missing definition of 'docker_proxy.host' for operating_mode: %s.",OP_MODE_DOCKER_VM));
    		
    		//Docker prefs
    		GraderDockerJvmBpConfig dockerBPConfig=graderConfig.getDocker_jvm_bp();
    		if(dockerBPConfig==null)throw new IllegalArgumentException(String.format("Missing '%s' for operating_mode.",OP_MODE_DOCKER_VM));
    		//Image info
    		String imageName=dockerBPConfig.getImage_name();
    		if(imageName==null || imageName.equals(""))throw new IllegalArgumentException(String.format("Missing definition of 'image_name' for operating_mode '%s'.",OP_MODE_DOCKER_VM));
    		String username=dockerBPConfig.getUsername();
    		String passwordPat=dockerBPConfig.getPassword_pat();
    		//Pathes (default values in case of null will be set in DockerProxyBackendPlugin)
    		String copySubmissionToDirPath=dockerBPConfig.getCopy_submission_to_dir_path();
    		String loadResponseFromDirPath=dockerBPConfig.getLoad_response_from_dir_path();
    		String copyGraderPluginDefaultsPropertiesToFile=dockerBPConfig.getCopy_grader_plugin_defaults_properties_to_file();
    		//set prefs
    		dockerBp.setDockerPrefs(dockerHost,imageName, username, passwordPat, copySubmissionToDirPath, loadResponseFromDirPath, copyGraderPluginDefaultsPropertiesToFile);
            
    		//TODO: remove
    		//backend-starter prefs
    		//dockerBp.setBackendStarterPrefs(bpClassName,bpRelativeClassPaths,bpFileExtensions);
    		
           
            bp = dockerBp;

    	}else if(operatingMode.equals(OP_MODE_LOCAL_VM)) {
    		//"NORMAL" BP
    		
    		GraderHostJvmBpConfig bpConfig=graderConfig.getHost_jvm_bp();
    		if(bpConfig==null)throw new IllegalArgumentException(String.format("Missing '%s' for operating_mode.",OP_MODE_LOCAL_VM));
    		
    		String bpDirectory=bpConfig.getDir();
    		String bpAdditionalAbsoluteClasspathes=bpConfig.getAdditional_absolute_classpathes();
            String bpClassName=bpConfig.getBackend_plugin_classname();
    		String bpFileExtensions=bpConfig.getFileextensions();

    		if(bpDirectory==null || bpDirectory.equals(""))throw new IllegalArgumentException(String.format("Missing '%s.dir' definition.", OP_MODE_LOCAL_VM));
    		if(bpClassName==null || bpClassName.equals(""))throw new IllegalArgumentException(String.format("Missing '%s.backend_plugin_classname' definition.", OP_MODE_LOCAL_VM));
    		//choose ".jar" by default
    		if(bpFileExtensions==null)bpFileExtensions=".jar";
    		
    		//build classpath 
    		//add absolute pathes
    		
    		
        	//String gradersHomeDir=GrappaServlet.CONFIG.getGraders_home();
        	//if(gradersHomeDir==null)throw new IllegalArgumentException(String.format("Missing 'graders_home' definition."));
        	//String graderSubDir=graderConfig.getSubdir();
        	//if(graderSubDir==null)throw new IllegalArgumentException(String.format("Missing 'subdir' definition."));
        	
    		//String absoluteGraderDir=gradersHomeDir+"/"+graderSubDir;
        	
    		//collect everything in bpDirectory
        	String absoluteClassPathes=bpDirectory;        	
    		if(bpAdditionalAbsoluteClasspathes!=null && !bpAdditionalAbsoluteClasspathes.equals("")){
    			//add absolute pathes to classpath
    			absoluteClassPathes+=";"+bpAdditionalAbsoluteClasspathes;
//    			String[] relativeCPsArray=bpRelativeClassPaths.split(";");
//    			for(String relCP:relativeCPsArray){
//    				absoluteClassPathes+=absoluteGraderDir+"/"+relCP+";";				
//    			}
    			//remove trailing ";"
//    			absoluteClassPathes=absoluteClassPathes.substring(0, absoluteClassPathes.length() - 1);
    		}
    		
//    		GraderHostJvmBpConfig bpConfig=graderConfig.getHost_jvm_bp();
//    		if(bpConfig!=null){
//    			String additionalAbsolutePathes=bpConfig.getHostonly_classpathes();
//    			if(additionalAbsolutePathes!=null && !additionalAbsolutePathes.equals("")){
//    				absoluteClassPathes+=(absoluteClassPathes.length()<1?"":";")+additionalAbsolutePathes;
//    			}
//    			String pluginJarName=bpConfig.getPlugin_jar_name();
//    			if(pluginJarName!=null && !pluginJarName.equals("")){
//    				absoluteClassPathes+=(absoluteClassPathes.length()<1?"":";")+absoluteGraderDir+"/"+pluginJarName;
//    			}else{
//    				absoluteClassPathes+=(absoluteClassPathes.length()<1?"":";")+absoluteGraderDir+"/"+GRADER_BP_JAR_FILENAME;
//    			}
//    		}else{
//				absoluteClassPathes+=(absoluteClassPathes.length()<1?"":";")+absoluteGraderDir+"/"+GRADER_BP_JAR_FILENAME;
//    		}
        	
            log.info("Loading grader plugin '{}' with classpathes '{}'...", graderId, absoluteClassPathes);
            backendPluginLoader.configure(Classpath.of(absoluteClassPathes, bpFileExtensions));
            bp = backendPluginLoader.instantiateClass(bpClassName);
            
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Initializing BackendPlugin...",graderId,submId);
            bp.init(props, boundary, logLevel, fileEncoding, userLanguage, userCountry);

    	}else {
    		//neither host_jvm_bp nor docker_jvm_bp 
            throw new IllegalArgumentException(String.format("operating_mode must be either '%s' or '%s'. Given was '%s'.",OP_MODE_DOCKER_VM,OP_MODE_LOCAL_VM,operatingMode));        		
    	}
    	return bp;
    }

    
    public ResponseResource runGradingProcess(QueuedSubmission subm) {
        @SuppressWarnings("serial")
        class NoResultGraderExecption extends Exception {
            public NoResultGraderExecption(String s) { super(s); }
        }
        try {
            LocalDateTime beginTime = LocalDateTime.now();
            // Create a fresh backend plugin instance for every grading request
            if (backendPluginLoader == null) {
                log.error("Class loader of BackendPlugin '{}' not found", graderConfig.getId());
                throw new Exception("Class loader of backend plugin '" + graderConfig.getId() + "' not found");
            }
            
            BackendPlugin bp=loadAndInitBackendPlugin(subm.getGradeProcId());
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
                return createInternalErrorResponse(errorMessage, subm, Audience.TEACHER_ONLY, e);
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
                return createInternalErrorResponse(errorMessage, subm, Audience.BOTH, e);
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
                return createInternalErrorResponse("Grading process was interrupted.", subm, Audience.TEACHER_ONLY, e);
            } catch (Throwable e) { // catch any other error
                totalGradingProcessesFailed.incrementAndGet();
                String errorMessage = String.format("[GraderId: '%s', GradeProcessId: '%s']: Grading process " +
                    "failed with error: %s", graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
                log.error(errorMessage);
                log.error(ExceptionUtils.getStackTrace(e));
                return createInternalErrorResponse(errorMessage, subm, Audience.TEACHER_ONLY, e);
            }
        } catch (Throwable e) {
            String errorMessage = String.format("[GraderId: '%s', GradeProcessId: '%s']: Grading process encountered " +
                    "error: %s", graderConfig.getId(), subm.getGradeProcId(), e.getMessage());
            log.error(errorMessage);
            log.error(ExceptionUtils.getStackTrace(e));
            try {
				return createInternalErrorResponse(errorMessage, subm, Audience.TEACHER_ONLY, e);
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
    
    private ProformaVersion detectProformaVersion(QueuedSubmission subm) {
        try {
            return new SubmissionLive(subm.getSubmission()).getProformaVersion();
        } catch (UnsupportedOperationException ex) {
            return ProformaVersion.getDefault();
        } catch (Throwable t) {
            throw new AssertionError("Unexpected error ", t);
        }
    }

    private ResponseResource createInternalErrorResponse(String errorMessage, QueuedSubmission subm, Audience audience, final Throwable throwable)  throws Exception {
        if (audience == Audience.TEACHER_ONLY && this.graderConfig.getShow_stacktrace()) {
            errorMessage += "<br><strong>Stacktrace</strong><br><small>" + DebugUtils.getStackTrace(throwable) + "</small>";
        }
        LmsConfig lmsConfig = getLmsConfig(subm.getLmsId());
        boolean ietm = lmsConfig.getEietamtf();
        ProformaVersion pv = detectProformaVersion(subm);
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