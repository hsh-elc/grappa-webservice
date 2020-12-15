package de.hsh.grappa.service;

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
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;

import java.nio.charset.StandardCharsets;

public class SubmissionProcessor {
    private static final Logger log = LoggerFactory.getLogger(SubmissionProcessor.class);
    private SubmissionWrapper subm;
    private String graderId;

    public SubmissionProcessor(/*GrappaConfig config,*/ SubmissionResource subm, String graderId) throws Exception {
        //this.config = config;
        this.subm = createVersionedProformaSubmission(subm);
        this.graderId = graderId;
    }

    // TODO: rename? versioned it is not anymore
    private SubmissionWrapper createVersionedProformaSubmission(SubmissionResource submissionBlob) throws Exception {
        // get the submission xml file bytes, unless it's a zipped submission...
        byte[] submXmlFileBytes = submissionBlob.getContent();
        if (submissionBlob.getMimeType().equals(MimeType.ZIP)) {
            String submXmlFileContent = Zip.getTextFileContentFromZip(submissionBlob.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, StandardCharsets.UTF_8);
            submXmlFileBytes = submXmlFileContent.getBytes(StandardCharsets.UTF_8);
        }

        AbstractSubmissionType abstractSubmType = XmlUtils.unmarshalToObject(submXmlFileBytes,
            AbstractSubmissionType.class);

        // This is is older code that supports different versions of Proforma.
        // The Proforma XML POJOs have been since moved to a separate proformaxml module that
        // does not intend to keep multiple versions in one JAR. Rather, one proformaxml JAR
        // represents one proforma version (currently version 2.1).
        // The code still works, of course, so it will stay for now as is.
        if (abstractSubmType instanceof proforma.xml.SubmissionType) {
            return new SubmissionWrapperImpl(submissionBlob);
        } /*else if (abstractSubmType instanceof proforma.xml.v2xx.SubmissionType) {
            // add new versions here
        }*/

        throw new GrappaException("Unknown Proforma version of submission.");
    }

    /**
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
            subm.getProformaSubmissionBlob(), prioritize);
        synchronized (GraderPoolManager.getInstance()) {
            GraderPoolManager.getInstance().notify();
        }
        return gradeProcId;
    }

    private void cacheTask() throws Exception {
        TaskWrapper task = subm.getTask();
        if (!RedisController.getInstance().isTaskCached(task.getUuid())) {
            RedisController.getInstance().cacheTask(task.getUuid(), task.getProformaTaskBlob());
        } else {
            // otherwise, refresh existing cached task timeout
            RedisController.getInstance().refreshTaskTimeout(task.getUuid());
        }
    }
}