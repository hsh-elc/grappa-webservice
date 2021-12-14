package de.hsh.grappa.service;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.boundary.BoundaryImpl;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.LmsConfig;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.util.ObjectId;
import proforma.util.ProformaVersion;
import proforma.util.SubmissionLive;
import proforma.util.TaskLive;
import proforma.util.boundary.Boundary;
import proforma.util.exception.NotFoundException;
import proforma.util.resource.SubmissionResource;
import proforma.util.resource.TaskResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes on a submitted student solution
 * (submission), makes sure that the submission
 * is valid, caches the underlying task of that submission and passes
 * the submission onto the requested GradingPool as specified
 * by the graderId.
 */
public class SubmissionProcessor {
    private static final Logger log = LoggerFactory.getLogger(SubmissionProcessor.class);
    private SubmissionLive subm;
    private TaskLive task;
    private String graderId;
    private LmsConfig lmsConfig;
    private Boundary boundary;

    public SubmissionProcessor(/*GrappaConfig config,*/ SubmissionResource subm, String graderId, LmsConfig lmsConfig) throws Exception {
        //this.config = config;
    	this.subm = new SubmissionLive(subm, ProformaVersion.getDefault());
        //this.subm = createProformaSubmission(subm);
        this.graderId = graderId;
        this.lmsConfig = lmsConfig;
        this.boundary = new BoundaryImpl();
    }

    private String getTaskUuid() throws Exception {
    	return getTask().getTaskUuid();
    }
    
    private TaskLive getTask() throws Exception {
    	if (task == null) {
    		task = subm.getTask(boundary);
    	}
    	return task;
    }
    
    private TaskResource getTaskResource() throws Exception {
    	return getTask().getResource();
    }
    
    
    /**
     * Makes sure the submission is not missing any required data as specified by
     * the Proforma format.
     * @throws BadRequestException when an ill-formatted submission is received
     * @throws GrappaException     when an internal service error occurs
     */
    private void validateSubmission() throws Exception {
        // Make sure the requested graderId exists and
        // is enabled in the config file
        var grader = GrappaServlet.CONFIG.getGraders().stream().filter(g -> g.getId().equals(graderId)).findFirst();
        if (!grader.isPresent())
            throw new NotFoundException(String.format("The requested grader '%s' to be used for " +
                "grading does not exist.", graderId));
        else if (!grader.get().getEnabled())
            throw new GrappaException(String.format("Grader '%s' is disabled in the service's configuration file.",
                graderId));

        getTaskUuid(); // trigger exception if invalid
    }

    /**
     * @return the gradeProcId
     * @throws BadRequestException
     * @throws NotFoundException
     * @throws GrappaException
     */
    public String process(boolean prioritize) throws Exception {// throws BadRequestException, NotFoundException,
        // GrappaException {
        validateSubmission();
        cacheTask();
        // Queue submission for grading
        String gradeProcId = ObjectId.createObjectId();
        RedisController.getInstance().pushSubmission(graderId, lmsConfig.getId(), gradeProcId, getTaskUuid(),
            subm.getResource(), prioritize);
        synchronized (GraderPoolManager.getInstance()) {
            GraderPoolManager.getInstance().notify();
        }
        return gradeProcId;
    }

    /**
     * Cache any incoming task of a submission.
     * If a task has already been cached at some point, its TTL
     * is simply re-newed.
     * @throws Exception
     */
    private void cacheTask() throws Exception {
    	String taskuuid = getTaskUuid();
        if (!RedisController.getInstance().isTaskCached(taskuuid)) {
            RedisController.getInstance().cacheTask(taskuuid, getTaskResource());
        } else {
            // otherwise, refresh existing cached task timeout
            RedisController.getInstance().refreshTaskTimeout(taskuuid);
        }
    }
}