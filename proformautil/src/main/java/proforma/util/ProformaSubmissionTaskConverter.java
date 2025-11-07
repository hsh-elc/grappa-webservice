package proforma.util;

import proforma.util.boundary.TaskBoundary;
import proforma.util.div.XmlUtils;
import proforma.util.div.Zip;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;

/**
 * Utility class for task conversion inside a given submission.
 */
public class ProformaSubmissionTaskConverter {

    /**
     * Convert the format of the task inside the submissionLive to the target format.
     *
     * @param submissionResource The submission containing the task to be converted.
     * @param targetFormat The format to convert to. No conversion will take place if this value is null or the given task is already in the specific format.
     * @param taskBoundary
     * @return The submission with the converted task.
     * @throws Exception
     */
    public static SubmissionResource convertTaskFormat(SubmissionResource submissionResource, MimeType targetFormat, TaskBoundary taskBoundary) throws Exception {
        if (targetFormat != null) {
            SubmissionLive submissionLive = new SubmissionLive(submissionResource);
            ProformaSubmissionTaskHandle proformaSubmissionTaskHandle = submissionLive.getSubmissionTaskHandle(taskBoundary);

            if (proformaSubmissionTaskHandle.externalTaskHandle().get() == null) { //Dont convert if task is external
                TaskLive taskLive = proformaSubmissionTaskHandle.getTask();
                MimeType taskMimeType = taskLive.getMimeType();

                if (!targetFormat.equals(taskMimeType)) {
                    if (taskMimeType.equals(MimeType.XML) && targetFormat.equals(MimeType.ZIP)) {
                        submissionResource = convertXMLToZIP(submissionLive, taskBoundary).getResource();
                    } else if (taskMimeType.equals(MimeType.ZIP) && targetFormat.equals(MimeType.XML)) {
                        submissionResource = convertZIPToXML(submissionLive, taskBoundary).getResource();
                    } else {
                        throw new IllegalArgumentException("Unsupported task format conversion from " + taskMimeType + " to " + targetFormat);
                    }
                }
            }
        }
        return submissionResource;
    }

    /**
     * Converts the task inside the submissionLive from XML to ZIP format.
     * The resulting task.zip will contain the original task.xml
     *
     * @param submissionLive The submission containing the task to be converted.
     * @param taskBoundary
     * @return The submissionLive with the converted task
     * @throws IllegalArgumentException In case the submission.xml does not match the structure of the submission.
     * @throws Exception
     */
    private static SubmissionLive convertXMLToZIP(SubmissionLive submissionLive, TaskBoundary taskBoundary) throws Exception {
        ProformaSubmissionTaskHandle proformaSubmissionTaskHandle = submissionLive.getSubmissionTaskHandle(taskBoundary);
        String taskZipFileName = ProformaSubmissionZipPathes.TASK_XML_FILE_NAME.substring(0, ProformaSubmissionZipPathes.TASK_XML_FILE_NAME.length() - 3) + "zip";

        ProformaIncludedTaskFileHandle includedTaskFileHandle = getIncludedTaskFileHandle(proformaSubmissionTaskHandle);
        if (includedTaskFileHandle.attachedXmlFileHandle().get() != null) {
            //Edit submission.xml to match the new structure of the submission
            String taskXmlFileName = includedTaskFileHandle.attachedXmlFileHandle().getPath();
            includedTaskFileHandle.attachedXmlFileHandle().remove();
            includedTaskFileHandle.attachedZipFileHandle().createAndSet().setPath(taskZipFileName);

            // Get task.xml and put it into task.zip
            Zip.ZipContent unzippedSubmission = submissionLive.getZipContent();
            Zip.ZipContentElement taskXml = unzippedSubmission.get(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + taskXmlFileName);
            byte[] taskZip = Zip.wrapSingleFileIntoZip(taskXml.getBytes(), ProformaTaskZipPathes.TASK_XML_FILE_NAME);

            //Remove task.xml and put task.zip in
            unzippedSubmission.remove(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + taskXmlFileName);
            unzippedSubmission.put(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + taskZipFileName, new Zip.ZipContentElement(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + taskZipFileName, taskZip, System.currentTimeMillis()));
        } else if (includedTaskFileHandle.embeddedXmlFileHandle().get() != null) {
            //Get task.xml and put it into task.zip
            String taskXmlFileName = includedTaskFileHandle.embeddedXmlFileHandle().getFilename();
            byte[] taskXmlContent = includedTaskFileHandle.embeddedXmlFileHandle().getContent();
            byte[] taskZip = Zip.wrapSingleFileIntoZip(taskXmlContent, taskXmlFileName);

            //Remove task.xml and put task.zip in
            includedTaskFileHandle.embeddedXmlFileHandle().remove();
            includedTaskFileHandle.embeddedZipFileHandle().createAndSet().setFilename(taskZipFileName).setContent(taskZip);
        } else {
            throw new IllegalArgumentException("Invalid submission: Attached xml file handle for included task is missing.");
        }
        submissionLive.markPojoChanged(XmlUtils.MarshalOption.of(XmlUtils.MarshalOption.CDATA));

        return submissionLive;
    }

    /**
     * Converts the task inside the submissionLive from ZIP to XML format.
     * All attached files are converted to embedded files.
     *
     * @param submissionLive The submission containing the task to be converted.
     * @param taskBoundary
     * @return The submissionLive with the converted task
     * @throws IllegalArgumentException In case the submission.xml does not match the structure of the submission.
     * @throws Exception
     */
    private static SubmissionLive convertZIPToXML(SubmissionLive submissionLive, TaskBoundary taskBoundary) throws Exception {
        ProformaSubmissionTaskHandle proformaSubmissionTaskHandle = submissionLive.getSubmissionTaskHandle(taskBoundary);
        TaskLive taskLive = proformaSubmissionTaskHandle.getTask();

        //Convert all attached task files to embedded files
        for (ProformaTaskFileHandle ptfh : taskLive.getTaskFileHandles()) {
            ptfh.convertTaskFileToEmbedded(false, false);
        }
        taskLive.markPojoChanged(XmlUtils.MarshalOption.of(XmlUtils.MarshalOption.CDATA));

        //Convert task.zip to task.xml (just get the task.xml from the task containing the converted attached files)
        Zip.ZipContent unzippedTask = taskLive.getZipContent();
        Zip.ZipContentElement convertedTaskXml = unzippedTask.get(ProformaTaskZipPathes.TASK_XML_FILE_NAME);

        ProformaIncludedTaskFileHandle includedTaskFileHandle = getIncludedTaskFileHandle(proformaSubmissionTaskHandle);
        if (includedTaskFileHandle.attachedZipFileHandle().get() != null) {
            //Edit submission.xml to match the new structure of the submission
            includedTaskFileHandle.attachedZipFileHandle().remove();
            includedTaskFileHandle.attachedXmlFileHandle().createAndSet().setPath(ProformaSubmissionZipPathes.TASK_XML_FILE_NAME);

            //Replace old task.zip with the converted task.xml
            Zip.ZipContent unzippedSubmission = submissionLive.getZipContent();
            for (String key : unzippedSubmission.keySet()) {
                if (key.startsWith(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/")) {
                    unzippedSubmission.remove(key);
                    break;
                }
            }
            unzippedSubmission.put(ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + ProformaSubmissionZipPathes.TASK_XML_FILE_NAME, convertedTaskXml);
        } else if (includedTaskFileHandle.embeddedZipFileHandle().get() != null) {
            //Edit submission.xml to match the new structure of the submission
            includedTaskFileHandle.embeddedZipFileHandle().remove();
            includedTaskFileHandle.embeddedXmlFileHandle().createAndSet().setFilename(ProformaSubmissionZipPathes.TASK_XML_FILE_NAME).setContent(convertedTaskXml.getBytes());
        } else {
            throw new IllegalArgumentException("Invalid submission: Attached zip file handle for included task is missing.");
        }
        submissionLive.markPojoChanged(XmlUtils.MarshalOption.of(XmlUtils.MarshalOption.CDATA));

        return submissionLive;
    }

    /**
     * Get included task file handle from given submission task handle
     * 
     * @param proformaSubmissionTaskHandle
     * @return The included task file handle
     * @throws IllegalArgumentException In case the included task file handle is missing
     * @throws Exception
     */
    private static ProformaIncludedTaskFileHandle getIncludedTaskFileHandle(ProformaSubmissionTaskHandle proformaSubmissionTaskHandle) throws Exception {
        if (proformaSubmissionTaskHandle.includedTaskFileHandle().get() != null) {
            return proformaSubmissionTaskHandle.includedTaskFileHandle();
        } else {
            throw new IllegalArgumentException("Invalid submission: No task file handle found.");
        }
    }
}