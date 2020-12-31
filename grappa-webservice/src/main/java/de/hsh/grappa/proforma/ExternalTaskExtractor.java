package de.hsh.grappa.proforma;

import com.google.common.base.Strings;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;
import proforma.xml.SubmissionType;

/**
 * This class downloads a task resource from a task repository.
 * That resource can be either a ZIP or an XML.
 *
 * This class is called [..]Extractor for the sake of its base class,
 * but calling it ExternalTaskDownloader would make more
 * sense in terms of what it does.
 */
public class ExternalTaskExtractor extends TaskExtractor {
    private SubmissionType concreteSubmPojo;
    private SubmissionWrapper submissionWrapper;
    private TaskWrapper taskWrapper;

    public ExternalTaskExtractor(SubmissionType concreteSubmPojo,
                                 SubmissionWrapper submissionWrapper) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached xml task file comes only in a zipped submission
        this.submissionWrapper = submissionWrapper;
    }

    @Override
    public TaskWrapper getTask() throws Exception {
        String taskUuid = concreteSubmPojo.getExternalTask().getUuid();
        String taskRepoUrl = concreteSubmPojo.getExternalTask().getUri();
        if (Strings.isNullOrEmpty(taskRepoUrl)) {
            if (Strings.isNullOrEmpty(taskUuid))
                throw new BadRequestException("Neither the task repository url nor the task uuid have been " +
                    "specified.");

            // If the task repo url is empty and the taskuuid is set, try getting the task from cache
            try {
                TaskResource task = RedisController.getInstance().getCachedTask(taskUuid);
                return new TaskWrapperImpl(task);
            } catch (NotFoundException e) {
                throw new NotFoundException(String.format("The task uuid '%s' specified in the external task element " +
                    "(with the task repo url being empty) is not cached by the middleware.", taskUuid), e);
            }
        } else {
            try {
                TaskResource ts = new ResourceDownloader().downloadTaskResource(taskRepoUrl);
                return new TaskWrapperImpl(ts);
            } catch (Exception e) {
                throw new GrappaException(String.format("Downloading external task resource failed: %s",
                    taskRepoUrl), e);
            }
        }
    }
}
