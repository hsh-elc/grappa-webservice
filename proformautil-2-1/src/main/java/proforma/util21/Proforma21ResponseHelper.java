package proforma.util21;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import proforma.util.ProformaResponseHelper;
import proforma.util.ProformaVersion;
import proforma.util.ResponseLive;
import proforma.util.SubmissionLive;
import proforma.util.TaskLive;
import proforma.util.boundary.TaskBoundary;
import proforma.util.div.XmlUtils.MarshalOption;
import proforma.util.div.Zip.ZipContent;
import proforma.util.resource.MimeType;
import proforma.util.resource.ResponseResource;
import proforma.util.resource.SubmissionResource;
import proforma.xml.AbstractProformaType;
import proforma.xml.AbstractResponseType;
import proforma.xml21.*;
import proforma.xml21.FeedbackType.Content;

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
public class Proforma21ResponseHelper extends ProformaResponseHelper {
	
	public Proforma21ResponseHelper(ProformaVersion pv) {
		super(pv);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractProformaType> Class<T>  getPojoType() {
		return (Class<T>) ResponseType.class;
	}


    private static final String DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE = "An error occurred during the grading process. Please ask your teacher for details.";

    private GraderEngineType createUnknownGraderEngineObject() {
        GraderEngineType graderEngine = new GraderEngineType();
        graderEngine.setName("N/A");
        graderEngine.setVersion("N/A");
        return graderEngine;
    }

    private ResponseMetaDataType createUnknownGraderEngineResponseMetaData() {
        ResponseMetaDataType responseMetaData = new ResponseMetaDataType();
        responseMetaData.setGraderEngine(createUnknownGraderEngineObject());
        return responseMetaData;
    }

    /**
     * @param merged Either merged or separate should be null
     * @param separate Either separate or merged should be null
     */
    private ResponseType createUnknownGraderEngineResponse(MergedTestFeedbackType merged, SeparateTestFeedbackType separate) {
        ResponseType resp = new ResponseType();
        resp.setMergedTestFeedback(merged);
        resp.setSeparateTestFeedback(separate);
        resp.setLang("en");
        resp.setFiles(new ResponseFilesType());
        resp.setResponseMetaData(createUnknownGraderEngineResponseMetaData());
        return resp;
    }

    private FeedbackType createInternalErrorFeedback(String errorMessage) {
        FeedbackType feedback = new FeedbackType();
        feedback.setTitle("Internal error");
        feedback.setLevel(FeedbackLevelType.ERROR);
        Content content = new Content();
        content.setFormat("html");
        content.setValue("<p>" + errorMessage + "</p>");
        feedback.setContent(content);
        return feedback;
    }

    private FeedbackListType createInternalErrorFeedbackList(String errorMessage, Audience audience) {
        FeedbackListType feedbackList= new FeedbackListType();
        feedbackList.getTeacherFeedback().add(createInternalErrorFeedback(errorMessage));
        if (audience.equals(Audience.BOTH)) {
            feedbackList.getStudentFeedback().add(createInternalErrorFeedback(errorMessage));
        } else {
            feedbackList.getStudentFeedback().add(createInternalErrorFeedback(DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE));
        }
        return feedbackList;
    }

    /**
     * This currently only works for ProFormA 2.1 submissions.
     * Otherwise null is returned.
     */
    private SeparateTestFeedbackType tryCreateInternalErrorSeparateTestFeedback(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) {
        SeparateTestFeedbackType separate = null;
        try {
            SubmissionLive sw = new SubmissionLive(subm);
            SubmissionType submPojo = sw.getSubmission();
            String structure = submPojo.getResultSpec().getStructure();
            if ("separate-test-feedback".equals(structure)) {
            	TaskLive tw = sw.getTask(tb);
                TaskType taskPojo = tw.getTask();
                TestsResponseType testsResponse = new TestsResponseType();
                for (TestType test : taskPojo.getTests().getTest() ) {
                    TestResponseType testResponse= new TestResponseType();
                    testResponse.setId(test.getId());
                    TestResultType testResult = new TestResultType();
                    testResult.setFeedbackList(createInternalErrorFeedbackList(errorMessage, audience));
                    ResultType result = new ResultType();
                    result.setIsInternalError(true);
                    result.setScore(BigDecimal.ZERO);
                    testResult.setResult(result);
                    testResponse.setTestResult(testResult);
                    testsResponse.getTestResponse().add(testResponse);
                }
                separate = new SeparateTestFeedbackType();
                separate.setSubmissionFeedbackList(createInternalErrorFeedbackList(errorMessage, audience));
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

    private MergedTestFeedbackType createInternalErrorMergedTestFeedback(String errorMessage, Audience audience) {
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

    @Override
    public ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) throws Exception {
        return createInternalErrorResponse(errorMessage, subm, tb, audience, false);
    }

    @Deprecated @Override
    public ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback) throws Exception {
        final String finalMsg = "Grappa encountered a fatal error: " + errorMessage;

        SeparateTestFeedbackType separate = null;
        if (!isExpectedInternalErrorTypeAlwaysMergedTestFeedback) {
            separate = tryCreateInternalErrorSeparateTestFeedback(finalMsg, subm, tb, audience);
        }
        MergedTestFeedbackType merged = null;
        if (separate == null) merged = createInternalErrorMergedTestFeedback(finalMsg, audience);

        ResponseType resp = createUnknownGraderEngineResponse(merged, separate);
        
        return new ResponseLive(resp, null, MimeType.XML, MarshalOption.of(MarshalOption.CDATA)).getResource();
    }

    @Override
    public boolean hasInternalError(ResponseResource responseResource) throws Exception {
        ResponseLive responseLive = new ResponseLive(responseResource);
        ResponseType resp = responseLive.getResponse();

        if (null != resp.getMergedTestFeedback()) {
            return resp.getMergedTestFeedback().getOverallResult().isIsInternalError();
        }

        if (null != resp.getSeparateTestFeedback()) {
            TestsResponseType testsResponse = resp.getSeparateTestFeedback().getTestsResponse();
            if (null != testsResponse) {
                for (TestResponseType test : testsResponse.getTestResponse()) {
                    if (null != test.getTestResult()) {
                        if (test.getTestResult().getResult().isIsInternalError())
                            return true;
                    } else {
                        SubtestsResponseType subtestsResponse = test.getSubtestsResponse();
                        for (SubtestResponseType subtest : subtestsResponse.getSubtestResponse()) {
                            if (subtest.getTestResult().getResult().isIsInternalError())
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public ResponseType createMergedTestFeedbackResponse(String feedbackString, BigDecimal score, String submissionId, String graderEngineName) {
        MergedTestFeedbackType mtf = new MergedTestFeedbackType();
        mtf.setStudentFeedback(feedbackString);
        mtf.setTeacherFeedback(feedbackString);
        OverallResultType or = new OverallResultType();
        or.setScore(score);
        mtf.setOverallResult(or);

        ResponseType response = new ResponseType();
        response.setFiles(new ResponseFilesType()); // empty, but required
        response.setMergedTestFeedback(mtf);
        response.setSubmissionId(submissionId);
        ResponseMetaDataType meta = new ResponseMetaDataType();
        GraderEngineType ge = new GraderEngineType();
        ge.setName(graderEngineName);
        meta.setGraderEngine(ge);
        response.setResponseMetaData(meta);
    	
        return response;
    }

    

	private Proforma21ResponseFileHandle getResponseFileHandle(ResponseFileType file, ZipContent zipContent) {
		if (file == null) return null;
		return new Proforma21ResponseFileHandle(file, zipContent);
	}
		
	@Override
	public List<Proforma21ResponseFileHandle> getResponseFileHandles(AbstractResponseType response, ZipContent zipContent) {
        ResponseType r = (ResponseType) response;
		ResponseFilesType rf = r.getFiles();
		if (rf == null) return null;
		ArrayList<Proforma21ResponseFileHandle> list= new ArrayList<>();
		for (ResponseFileType file : rf.getFile()) {
			list.add(getResponseFileHandle(file, zipContent));
		}
		return list;
	}

}
