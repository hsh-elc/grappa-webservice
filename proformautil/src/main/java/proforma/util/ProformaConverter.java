package proforma.util;

//obsolete file
//
//import java.nio.charset.StandardCharsets;
//
//import de.hsh.grappa.common.MimeType;
//import de.hsh.grappa.common.ProformaResource;
//import de.hsh.grappa.common.ResponseResource;
//import de.hsh.grappa.common.SubmissionResource;
//import de.hsh.grappa.common.TaskResource;
//import de.hsh.grappa.util.XmlUtils;
//import proforma.ProformaResponseZipPathes;
//import proforma.ProformaSubmissionZipPathes;
//import proforma.ProformaTaskZipPathes;
//import proforma.xml.AbstractProformaType;
//import proforma.xml.AbstractResponseType;
//import proforma.xml.AbstractSubmissionType;
//import proforma.xml.AbstractTaskType;
///**
// * Converts a Proforma resource from a ZIP or a
// * bare-bone XML to POJOs using XML binding.
// */
//public class ProformaConverter {
//    private ProformaConverter() {}
//
//    public static AbstractSubmissionType convertToPojo(SubmissionResource submissionResource) throws Exception {
//    	return convertToPojo(submissionResource, AbstractSubmissionType.class);
//    }
//
//    public static AbstractResponseType convertToPojo(ResponseResource responseResource) throws Exception {
//    	return convertToPojo(responseResource, AbstractResponseType.class);
//    }
//
//    public static AbstractTaskType convertToPojo(TaskResource taskResource) throws Exception {
//    	return convertToPojo(taskResource, AbstractTaskType.class);
//    }
//
//    public static <P extends AbstractSubmissionType> P convertToPojo(SubmissionResource submissionResource, Class<P> submissionSubclass) throws Exception {
//    	return convertToPojo(submissionResource, submissionSubclass, ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME);
//    }
//
//    public static <P extends AbstractResponseType> P convertToPojo(ResponseResource responseResource, Class<P> responseSubclass) throws Exception {
//    	return convertToPojo(responseResource, responseSubclass, ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME);
//    }
//
//    public static <P extends AbstractTaskType> P convertToPojo(TaskResource taskResource, Class<P> taskSubclass) throws Exception {
//    	return convertToPojo(taskResource, taskSubclass, ProformaTaskZipPathes.TASK_XML_FILE_NAME);
//    }
//
//
//    private static <P extends AbstractProformaType, R extends ProformaResource> P convertToPojo(R resource, Class<P> clazz, String xmlFileName) throws Exception {
//        byte[] xmlFileBytes = resource.getContent();
//        if (resource.getMimeType().equals(MimeType.ZIP)) {
//            String xmlFileContent = de.hsh.grappa.util.Zip.getTextFileContentFromZip(resource.getContent(),
//            		xmlFileName, StandardCharsets.UTF_8);
//            xmlFileBytes = xmlFileContent.getBytes(StandardCharsets.UTF_8);
//        }
//        return XmlUtils.unmarshalToObject(xmlFileBytes, clazz);
//    }
//}