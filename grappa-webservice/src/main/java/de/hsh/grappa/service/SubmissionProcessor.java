package de.hsh.grappa.service;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.proforma.*;
import de.hsh.grappa.utils.ObjectId;
import de.hsh.grappa.utils.XmlUtils;
import de.hsh.grappa.utils.Zip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionProcessor {
    private static final Logger log = LoggerFactory.getLogger(SubmissionProcessor.class);
    private SubmissionInternals subm;
    private String graderId;

    public SubmissionProcessor(/*GrappaConfig config,*/ ProformaSubmission subm, String graderId) throws Exception {
        //this.config = config;
        this.subm = createVersionedProformaSubmission(subm);
        this.graderId = graderId;
    }

    private SubmissionInternals createVersionedProformaSubmission(ProformaSubmission proformaSubmission) throws Exception {
        // get the submission xml file bytes, unless it's a zipped submission...
        byte[] submXmlFileBytes = proformaSubmission.getContent();
        if (proformaSubmission.getMimeType().equals(MimeType.ZIP)) {
            String submXmlFileContent = Zip.getTextFileContentFromZip(proformaSubmission.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, Charsets.UTF_8);
            submXmlFileBytes = submXmlFileContent.getBytes(Charsets.UTF_8);
        }

        AbstractSubmissionType abstractSubmType = XmlUtils.unmarshalToObject(submXmlFileBytes,
            AbstractSubmissionType.class);

        if (abstractSubmType instanceof de.hsh.grappa.proformaxml.v201.SubmissionType) {
            return new SubmissionInternalsV201(proformaSubmission);
        } /*else if (abstractSubmType instanceof de.hsh.grappa.proformaxml.v2xx.SubmissionType) {
            // add new versions here
        }*/

        throw new GrappaException("Unknown Proforma version of submission.");
    }

    /**
     * @throws BadRequestException when a ill-formatted submission is received
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

        var task = subm.getTask();
        String taskuuid = task.getUuid();
        if (Strings.isNullOrEmpty(taskuuid)) {
            // TODO: taskuuid may not be set in the submission, it might be in the task ojbect though
            throw new BadRequestException("taskuuid is not set in the submission file.");
        }
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
        RedisController.getInstance().pushSubmission(graderId, gradeProcId, subm.getTask().getUuid(),
            subm.getProformaSubmission(), prioritize);
        synchronized (GraderPoolManager.getInstance()) {
            GraderPoolManager.getInstance().notify();
        }
        return gradeProcId;
    }

    private void cacheTask() throws Exception {
        TaskInternals task = subm.getTask();
        if (!RedisController.getInstance().isTaskCached(task.getUuid())) {
            RedisController.getInstance().cacheTask(task.getUuid(), task.getProformaTask());
        } else {
            // otherwise, refresh existing cached task timeout
            RedisController.getInstance().refreshTaskTimeout(task.getUuid());
        }
    }
}