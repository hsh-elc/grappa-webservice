package de.hsh.grappa.util.proforma;

import proforma.xml.*;
import proforma.xml.FeedbackType.Content;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.util.XmlUtils;

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
 * 
 * This converter can handle ProFormA 2.1 submission and response only.
 */
public class Proforma21ResponseGenerator {

    public enum Audience {
        TEACHER_ONLY,
        BOTH
    }

    private static final String DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE = "An error occurred during the grading process. Please ask your teacher for details.";

    private Proforma21ResponseGenerator() {
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

    /**
     * This currently only works for ProFormA 2.1 submissions.
     * Otherwise null is returned.
     */
    private static SeparateTestFeedbackType tryCreateSeparateTestFeedback(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) {
        SeparateTestFeedbackType separate = null;
        try {
            SubmissionLive sw = new SubmissionLive(subm);
            SubmissionType submPojo = sw.getSubmissionAs(SubmissionType.class);
            String structure = submPojo.getResultSpec().getStructure();
            if ("separate-test-feedback".equals(structure)) {
                TaskResource tr = sw.getTask(tb);
                TaskLive tw = new TaskLive(tr);
                TaskType taskPojo = tw.getTaskAs(TaskType.class);
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

    /**
     * Currently this supports only ProFormA 2.1 submissions
     */
    public static ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) {
        return createInternalErrorResponse(errorMessage, subm, tb, audience, false);
    }

    /**
     * Currently this supports only ProFormA 2.1 submissions
     */
    @Deprecated
    public static ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback) {
        final String finalMsg = "Grappa encountered a fatal error: " + errorMessage;

        SeparateTestFeedbackType separate = null;
        if (!isExpectedInternalErrorTypeAlwaysMergedTestFeedback) {
            separate = tryCreateSeparateTestFeedback(finalMsg, subm, tb, audience);
        }
        MergedTestFeedbackType merged = null;
        if (separate == null) merged = createMergedTestFeedback(finalMsg, audience);

        ResponseType resp = createResponse(merged, separate);

        String responseXml = XmlUtils.marshalToXml(resp, ResponseType.class);
        System.out.println(responseXml);
        return new ResponseResource(responseXml.getBytes(StandardCharsets.UTF_8), MimeType.XML);
    }


}
