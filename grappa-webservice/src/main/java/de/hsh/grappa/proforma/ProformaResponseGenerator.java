package de.hsh.grappa.proforma;

import de.hsh.grappa.utils.XmlUtils;
import org.apache.commons.codec.Charsets;
import proforma.xml.*;

import java.math.BigDecimal;

/**
 * This class creates a proforma response with the is-internal-error
 * flag set to true.
 * In case anything goes wrong server-side while processing (i.e. after
 * passing the submission on to the grader), such as an error thrown by
 * the grader, the client needs a proper proforma response so it can
 * invalidate the submission as the error is attributed to the grading
 * system.
 *
 * Using a HTTP status code in case of an server-side error is not
 * sufficient since there is only a generic 500 error code without
 * any indication to the true nature of the problem. If we were to
 * return an HTTPstatus code instead, the client would be inclined to
 * try re-submitting the submission again and again until a 200 OK is
 * received, which would obviously only result
 * in the same error occurring until the server-side problem is fixed.
 * So, using a proforma response with is-internal-error=true would ensure
 * that the submission would be automatically invalidated by the client
 * (as specified by the proforma format), and so the client would not retry
 * submitting the submission until the actual problem has been resolved by
 * the grading system.
 */
public class ProformaResponseGenerator {
    private ProformaResponseGenerator() {
    }

    public static ResponseResource createInternalErrorResponse(String errorMessage) {
        final String finalMsg = "Grappa encountered a fatal error: " + errorMessage;

        OverallResultType result = new OverallResultType();
        result.setIsInternalError(true);
        result.setScore(BigDecimal.ZERO);
        result.setValidity(BigDecimal.ZERO);

        MergedTestFeedbackType merged = new MergedTestFeedbackType();
        merged.setOverallResult(result);
        merged.setTeacherFeedback(finalMsg);

        GraderEngineType graderEngine = new GraderEngineType();
        graderEngine.setName("N/A");
        graderEngine.setVersion("N/A");

        ResponseMetaDataType responseMetaData = new ResponseMetaDataType();
        responseMetaData.setGraderEngine(graderEngine);

        ResponseType resp = new ResponseType();
        resp.setMergedTestFeedback(merged);
        resp.setLang("en");
        resp.setFiles(new ResponseFilesType());
        resp.setResponseMetaData(responseMetaData);

        String responseXml = XmlUtils.marshalToXml(resp, ResponseType.class);
        System.out.println(responseXml);
        return new ResponseResource(responseXml.getBytes(Charsets.UTF_8), MimeType.XML);
    }

    /**
     * Creates a proforma response that indicates an internal error.
     * This one is currently not quite Proforma compliant.
     * @param errorMessage
     * @return
     * @throws Exception
     */
//    public static ResponseResource createInternalErrorResponse(String errorMessage) {
//        FeedbackListType feedbackList = new FeedbackListType();
//        FeedbackType fb = new FeedbackType();
//        fb.setLevel(FeedbackLevelType.ERROR);
//        fb.setTitle("Error");
//        FeedbackType.Content content = new FeedbackType.Content();
//        content.setValue(errorMessage);
//        content.setFormat("plaintext");
//        fb.setContent(content);
//        feedbackList.getTeacherFeedback().add(fb);
//        feedbackList.getStudentFeedback().add(fb);
//
//        ResultType result = new ResultType();
//        result.setIsInternalError(true);
//        result.setScore(BigDecimal.ZERO);
//        result.setValidity(BigDecimal.ZERO);
//
//        TestResultType testResult = new TestResultType();
//        testResult.setResult(result);
//        testResult.setFeedbackList(feedbackList);
//
//        TestResponseType testResp = new TestResponseType();
//        testResp.setId("N/A");
//        testResp.setTestResult(testResult);
//
//        TestsResponseType testsResponse = new TestsResponseType();
//        testsResponse.getTestResponse().add(testResp);
//
//        FeedbackListType emptyFblist = new FeedbackListType();
//
//        SeparateTestFeedbackType separateTestFeedback = new SeparateTestFeedbackType();
//        separateTestFeedback.setTestsResponse(testsResponse);
//        separateTestFeedback.setSubmissionFeedbackList(new FeedbackListType());
//
//        GraderEngineType graderEngine = new GraderEngineType();
//        graderEngine.setName("N/A");
//        graderEngine.setVersion("N/A");
//
//        ResponseMetaDataType responseMetaData = new ResponseMetaDataType();
//        responseMetaData.setGraderEngine(graderEngine);
//
//        ResponseType resp = new ResponseType();
//        resp.setSeparateTestFeedback(separateTestFeedback);
//        resp.setLang("en");
//        resp.setFiles(new ResponseFilesType());
//        resp.setResponseMetaData(responseMetaData);
//
//        String responseXml = XmlUtils.marshalToXml(resp, ResponseType.class);
//        return new ResponseResource(responseXml.getBytes(Charsets.UTF_8), MimeType.XML);
//    }
}
