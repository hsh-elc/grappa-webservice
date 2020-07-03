package de.hsh.grappa.proforma;

import com.google.common.base.Strings;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.proformaxml.v201.SubmissionType;
import org.apache.commons.lang3.NotImplementedException;

public class ExternalTaskRetrieverV201 extends TaskRetriever {
    private SubmissionType concreteSubmPojo;
    private SubmissionInternals submissionInternals;
    private TaskInternals taskInternals;

    public ExternalTaskRetrieverV201(SubmissionType concreteSubmPojo,
                                     SubmissionInternals submissionInternals) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached xml task file comes only in a zipped submission
        this.submissionInternals = submissionInternals;
    }

    @Override
    public TaskInternals getTask() throws Exception {
        String taskUuid = concreteSubmPojo.getExternalTask().getUuid();
        String taskRepoUrl = concreteSubmPojo.getExternalTask().getValue();
        if (Strings.isNullOrEmpty(taskRepoUrl)) {
            if (Strings.isNullOrEmpty(taskUuid))
                throw new BadRequestException("Neither the task repository url nor the task uuid have been " +
                    "specified.");

            // If the task repo url is empty and the taskuuid is set, try getting the task from cache
            try {
                ProformaTask task = GrappaServlet.redis.getCachedTask(taskUuid);
                return new TaskInternalsV201(task);
            } catch (NotFoundException e) {
                throw new NotFoundException(String.format("The task uuid '%s' specified in the external task element " +
                    "(with the task repo url being empty) is not cached by the middleware.", taskUuid), e);
            }
        } else {
            throw new NotImplementedException("Downloading external task from a repository is not supported yet.");
        }
    }
}
