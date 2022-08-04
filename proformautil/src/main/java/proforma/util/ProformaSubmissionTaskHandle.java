package proforma.util;

import proforma.util.boundary.TaskBoundary;
import proforma.util.div.Strings;
import proforma.util.div.Zip.ZipContent;
import proforma.util.div.Zip.ZipContentElement;
import proforma.util.exception.NotFoundException;
import proforma.util.resource.MimeType;
import proforma.util.resource.TaskResource;
import proforma.xml.AbstractTaskType;

import java.util.Map;

/**
 * <p>An instance of a subclass can interact with a task associated with or
 * part of a ProFormA submission.</p>
 *
 * <p>This abstract class is independent of a specific ProFormA version.
 * Subclasses are version specific.</p>
 */
public abstract class ProformaSubmissionTaskHandle {

    private SubmissionLive submission;
    private TaskBoundary taskBoundary;
    private TaskLive task;

    protected ProformaSubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb) {
        this.submission = submission;
        this.taskBoundary = tb;
    }

    public TaskLive getTask() throws Exception {
        if (task == null) {
            task = getTaskImpl();
        }
        return task;
    }

    protected SubmissionLive getSubmission() {
        return submission;
    }

    protected TaskBoundary getTaskBoundary() {
        return taskBoundary;
    }

    public abstract ProformaIncludedTaskFileHandle includedTaskFileHandle() throws Exception;

    public abstract ProformaExternalTaskHandle externalTaskHandle() throws Exception;

    public abstract ProformaChildElementTaskHandle childElementTaskHandle() throws Exception;


    /**
     * Retrieves the task as part of the submission.
     * This will trigger a {@link #getSubmission()} call.
     * Currently supported are:
     * <ul>
     *   <li>Supported: attached xml and zip task files</li>
     *   <li>Supported: external tasks retrievable from a local cache or from the internet</li>
     *   <li>Supported: embedded task zip file and embedded task xml file</li>
     *   <li>Unsupported: native task element</li>
     * </ul>
     *
     * @return The task as a resource
     * @throws Exception
     */
    private TaskLive getTaskImpl() throws Exception {
        if (childElementTaskHandle().get() != null) {
            throw new UnsupportedOperationException("TODO: implement native xml task element");
            // TODO: kopiere Zip-Elemente des task-Unterordners der submissionLive rüber.
            // Außer task.xml, aber die gibt es ja sowieso nicht.
            // Offen: Kopien der ZipContentElemente oder Referenzen auf die Originale?
        } else {
            TaskResource taskResource = null;
            if (externalTaskHandle().get() != null) {
                taskResource = createTaskFromExternal();
            } else if (includedTaskFileHandle().get() != null) {
                if (null != includedTaskFileHandle().attachedXmlFileHandle().get()) {
                    taskResource = createTaskFromAttachedXmlFile();
                } else if (null != includedTaskFileHandle().attachedZipFileHandle().get()) {
                    taskResource = createTaskFromAttachedZipFile();
                } else if (null != includedTaskFileHandle().embeddedXmlFileHandle().get()) {
                    taskResource = createTaskFromEmbeddedXmlFile();
                } else if (null != includedTaskFileHandle().embeddedZipFileHandle().get()) {
                    taskResource = createTaskFromEmbeddedZipFile();
                }
            }
            if (taskResource != null) {
                return new TaskLive(taskResource, getSubmission().getContextClasses());
            }
        }
        throw new IllegalArgumentException("Unknown task element in submission");
    }


    /**
     * Copies all zip elements (if any) of {@link #getTask()} to {@link #getSubmission()}. Skips
     * the file task.xml. If there are no such zip elements in the task, nothing is copied and the method
     * silently returns false.
     *
     * @return true, if anything was copied.
     * @throws Exception
     */
    protected boolean copyZipElementsFromTaskToSubmission() throws Exception {
        boolean result = false;
        if (MimeType.ZIP.equals(getTask().getMimeType())) {
            for (Map.Entry<String, ZipContentElement> entry : getTask().getZipContent().entrySet()) {
                String path = entry.getKey();
                boolean doSkip = ProformaTaskZipPathes.TASK_XML_FILE_NAME.equals(path);
                if (doSkip) continue;

                ZipContentElement elem = entry.getValue();
                ZipContent submissionZip = getSubmission().getZipContent();
                if (submissionZip == null) {
                    throw new UnsupportedOperationException("Cannot copy task zip elements to non-zip submission");
                }
                submissionZip.put(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + path, elem);
                result = true;
            }
        }
        return result;
    }


    protected boolean removeAttachedTaskFromSubmissionZip(String relativePath) throws Exception {
        if (MimeType.ZIP.equals(getSubmission().getMimeType())) {
            String key = ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + relativePath;
            if (getSubmission().getZipContent().containsKey(key)) {
                getSubmission().getZipContent().remove(key);
                return true;
            } else {
                StringBuilder sb = new StringBuilder();
                for (String k : getSubmission().getZipContent().keySet()) {
                    sb.append("- ").append(k).append("\n");
                }
                throw new IllegalArgumentException("Cannot find attached task in submission ('" + key + "'). Zip content is:\n" + sb.toString());
            }
        }
        return false;
    }


    /**
     * <p>If the task is a zip (external or included), then it's zip entries are copied
     * into the task subfolder of the submission. If the submission is not a zip file,
     * currently this method fails. TODO: add switches that allow to embed all task attachments
     * automatically.</p>
     *
     * <p>If the task formerly is included as an attached file, the respective zip content element
     * is removed from the submission.</p>
     *
     * @throws UnsupportedOperationException if the task is a zip with attachments, that cannot be copied
     *                                       to a non-zip submission.
     */

    public boolean convertToChildTaskElement() throws Exception {
        AbstractTaskType t = getTask().getTask();
        if (externalTaskHandle().get() != null) {
            externalTaskHandle().remove();
            childElementTaskHandle().set(t);
            // The external task could be a zip, so we have to include the attachments
            // in the submission:
            copyZipElementsFromTaskToSubmission();
            return true;
        } else if (childElementTaskHandle().get() != null) {
            // nothing to be done
            return false;
        } else if (includedTaskFileHandle().get() != null) {
            if (null != includedTaskFileHandle().attachedXmlFileHandle().get()) {
                removeAttachedTaskFromSubmissionZip(includedTaskFileHandle().attachedXmlFileHandle().getPath());
            } else if (null != includedTaskFileHandle().attachedZipFileHandle().get()) {
                copyZipElementsFromTaskToSubmission();
                removeAttachedTaskFromSubmissionZip(includedTaskFileHandle().attachedZipFileHandle().getPath());
            } else if (null != includedTaskFileHandle().embeddedXmlFileHandle().get()) {
                // nothing to be done
            } else if (null != includedTaskFileHandle().embeddedZipFileHandle().get()) {
                copyZipElementsFromTaskToSubmission();
            } else {
                throw new IllegalArgumentException("Unknown task element in submission");
            }

            includedTaskFileHandle().remove();
            childElementTaskHandle().set(t);
            return true;
        }
        throw new IllegalArgumentException("Unknown task element in submission");
    }


    private TaskResource createTaskFromAttachedXmlFile() throws Exception {
        ZipContent zipContent = getSubmission().getZipContent();
        String filePath = includedTaskFileHandle().attachedXmlFileHandle().getPath();
        return createTaskFromAttachedFile(filePath, MimeType.XML, zipContent);
    }

    private TaskResource createTaskFromAttachedZipFile() throws Exception {
        ZipContent zipContent = getSubmission().getZipContent();
        String filePath = includedTaskFileHandle().attachedZipFileHandle().getPath();
        return createTaskFromAttachedFile(filePath, MimeType.ZIP, zipContent);
    }


    private TaskResource createTaskFromAttachedFile(String filePath, MimeType mimeType, ZipContent zipContent) throws Exception {
        String taskFilePath = ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + filePath;
        ZipContentElement task = zipContent.get(taskFilePath);
        if (task == null) {
            throw new IllegalArgumentException("There is no file '" + taskFilePath + "' inside the ProFormA submission");
        }
        return new TaskResource(task.getBytes(), mimeType);
    }


    private TaskResource createTaskFromEmbeddedXmlFile() throws Exception {
        byte[] bytes = includedTaskFileHandle().embeddedXmlFileHandle().getContent();
        return new TaskResource(bytes, MimeType.XML);
    }

    private TaskResource createTaskFromEmbeddedZipFile() throws Exception {
        byte[] bytes = includedTaskFileHandle().embeddedZipFileHandle().getContent();
        return new TaskResource(bytes, MimeType.ZIP);
    }


    private TaskResource createTaskFromExternal() throws Exception {
        String taskUuid = externalTaskHandle().getUuid();
        String taskRepoUrl = externalTaskHandle().getUri();
        if (Strings.isNullOrEmpty(taskRepoUrl)) {
            if (Strings.isNullOrEmpty(taskUuid))
                throw new Exception("Neither the task repository url nor the task uuid have been " +
                    "specified.");

            // If the task repo url is empty and the taskuuid is set, try getting the task from cache
            try {
                return getTaskBoundary().getCachedTask(taskUuid);
            } catch (NotFoundException e) {
                throw new NotFoundException(String.format("The task uuid '%s' specified in the external task element " +
                    "(with the task repo url being empty) is not cached by the middleware.", taskUuid), e);
            }
        } else {
            try {
                return getTaskBoundary().downloadTask(taskRepoUrl);
            } catch (Exception e) {
                throw new Exception(String.format("Downloading external task resource failed: %s",
                    taskRepoUrl), e);
            }
        }

    }


    public boolean convertExternalToEmbeddedTask() throws Exception {
        if (externalTaskHandle().get() == null) {
            return false;
        }

        TaskResource taskResource = getTask().getResource();

        ProformaIncludedTaskFileHandle itfh = includedTaskFileHandle().createAndSet();
        if (MimeType.XML.equals(taskResource.getMimeType())) {
            itfh.embeddedXmlFileHandle().createAndSet().setContent(taskResource.getContent())
                .setFilename(ProformaTaskZipPathes.TASK_XML_FILE_NAME);
        } else if (MimeType.ZIP.equals(taskResource.getMimeType())) {
            itfh.embeddedZipFileHandle().createAndSet().setContent(taskResource.getContent())
                .setFilename("task.zip");
        } else {
            throw new UnsupportedOperationException("Unexpected mimetype '" + taskResource.getMimeType() + "' when embedding external task into submission before starting docker backend");
        }
        externalTaskHandle().remove();
        return true;
    }


}
