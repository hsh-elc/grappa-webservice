package de.hsh.grappa.proforma;

import de.hsh.grappa.utils.XmlUtils;
import proforma.xml.*;
import proforma.xml.FeedbackType.Content;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * This class creates a proforma response with the is-internal-error
 * flag set to true.
 * In case anything goes wrong server-side while processing (i.e. after
 * passing the submission on to the grader), such as an error thrown by
 * the grader, the client needs a proper proforma response so it can
 * invalidate the submission as the error is attributed to the grading
 * system.
 *
 * Using a HTTP status code in case of a server-side error is not
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

    public enum Audience {
        TEACHER_ONLY,
        BOTH
    }

    private static final String DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE = "An error occurred during the grading process. Please ask your teacher for details.";

    private ProformaResponseGenerator() {
    }

    private static GraderEngineType createUnknownGraderEngineObject() {
        GraderEngineType graderEngine = new GraderEngineType();
        graderEngine.setName("N/A");
        graderEngine.setVersion("N/A");
        return graderEngine;
    }

    private static ResponseMetaDataType createResponseMetaData() {
        ResponseMetaDataType responseMetaData = new ResponseMetaDataType();
        responseMetaData.setGraderEngine(createUnknownGraderEngineObject());
        return responseMetaData;
    }

    /**
     * @param merged Either merged or separate should be null
     * @param separate Either separate or merged should be null
     */
    private static ResponseType createResponse(MergedTestFeedbackType merged, SeparateTestFeedbackType separate) {
        ResponseType resp = new ResponseType();
        resp.setMergedTestFeedback(merged);
        resp.setSeparateTestFeedback(separate);
        resp.setLang("en");
        resp.setFiles(new ResponseFilesType());
        resp.setResponseMetaData(createResponseMetaData());
        return resp;
    }

    private static FeedbackType createFeedback(String errorMessage) {
        FeedbackType feedback = new FeedbackType();
        feedback.setTitle("Internal error");
        feedback.setLevel(FeedbackLevelType.ERROR);
        Content content = new Content();
        content.setFormat("html");
        content.setValue("<p>" + errorMessage + "</p>");
        feedback.setContent(content);
        return feedback;
    }

    private static FeedbackListType createFeedbackList(String errorMessage, Audience audience) {
        FeedbackListType feedbackList= new FeedbackListType();
        feedbackList.getTeacherFeedback().add(createFeedback(errorMessage));
        if (audience.equals(Audience.BOTH)) {
            feedbackList.getStudentFeedback().add(createFeedback(errorMessage));
        } else {
            feedbackList.getStudentFeedback().add(createFeedback(DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE));
        }
        return feedbackList;
    }

    private static SeparateTestFeedbackType tryCreateSeparateTestFeedback(String errorMessage, SubmissionResource subm, Audience audience) {
        SeparateTestFeedbackType separate = null;
        try {
            SubmissionWrapper sw = new SubmissionWrapperImpl(subm);
            AbstractSubmissionType as = sw.getAbstractSubmPojo();
            if (as instanceof SubmissionType) {
                SubmissionType submPojo = ((SubmissionType)as);
                String structure = submPojo.getResultSpec().getStructure();
                if ("separate-test-feedback".equals(structure)) {
                    TaskWrapper tw = sw.getTask();
                    AbstractTaskType at = tw.getAbstractTaskPojo();
                    if (at instanceof TaskType) {
                        TaskType taskPojo = (TaskType)at;
                        TestsResponseType testsResponse = new TestsResponseType();
                        for (TestType test : taskPojo.getTests().getTest() ) {
                            TestResponseType testResponse= new TestResponseType();
                            testResponse.setId(test.getId());
                            TestResultType testResult = new TestResultType();
                            testResult.setFeedbackList(createFeedbackList(errorMessage, audience));
                            ResultType result = new ResultType();
                            result.setIsInternalError(true);
                            result.setScore(BigDecimal.ZERO);
                            testResult.setResult(result);
                            testResponse.setTestResult(testResult);
                            testsResponse.getTestResponse().add(testResponse);
                        }
                        separate = new SeparateTestFeedbackType();
                        separate.setSubmissionFeedbackList(createFeedbackList(errorMessage, audience));
                        separate.setTestsResponse(testsResponse);
                        return separate;
                    }
                }
            }
            // Falling back to merged feedback
            // nothing to do
        } catch (Exception e) {
            // cannot identify desired response type.
            // Falling back to merged feedback
        }
        return null; // set null in case of a half-prepared separate test feedback object.
    }

    private static MergedTestFeedbackType createMergedTestFeedback(String errorMessage, Audience audience) {
        OverallResultType result = new OverallResultType();
        result.setIsInternalError(true);
        result.setScore(BigDecimal.ZERO);
        result.setValidity(BigDecimal.ZERO);

        MergedTestFeedbackType merged = new MergedTestFeedbackType();
        merged.setOverallResult(result);
        merged.setTeacherFeedback(errorMessage);
        if (audience.equals(Audience.BOTH)) {
            merged.setStudentFeedback(errorMessage);
        } else {
            merged.setStudentFeedback(DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE);
        }
        return merged;
    }

    public static ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, Audience audience) {
        return createInternalErrorResponse(errorMessage, subm, audience, false);
    }

    @Deprecated
    public static ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback) {
        final String finalMsg = "Grappa encountered a fatal error: " + errorMessage;

        SeparateTestFeedbackType separate = null;
        if (!isExpectedInternalErrorTypeAlwaysMergedTestFeedback) {
            separate = tryCreateSeparateTestFeedback(finalMsg, subm, audience);
        }
        MergedTestFeedbackType merged = null;
        if (separate == null) merged = createMergedTestFeedback(finalMsg, audience);

        ResponseType resp = createResponse(merged, separate);

        String responseXml = XmlUtils.marshalToXml(resp, ResponseType.class);
        System.out.println(responseXml);
        return new ResponseResource(responseXml.getBytes(StandardCharsets.UTF_8), MimeType.XML);
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
