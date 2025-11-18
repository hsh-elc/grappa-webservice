package proforma.util21;

import proforma.util.*;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class creates a proforma response with the is-internal-error
 * flag set to true.
 * In case anything goes wrong server-side while processing (i.e. after
 * passing the submission on to the grader), such as an error thrown by
 * the grader, the client needs a proper proforma response so it can
 * invalidate the submission as the error is attributed to the grading
 * system.
 * <p>
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
 * <p>
 * This converter can handle ProFormA 2.1 submission and response only.
 */
public class Proforma21ResponseHelper extends ProformaResponseHelper {

    public Proforma21ResponseHelper(ProformaVersion pv) {
        super(pv);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractProformaType> Class<T> getPojoType() {
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
     * @param merged   Either merged or separate should be null
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
        FeedbackListType feedbackList = new FeedbackListType();
        feedbackList.getTeacherFeedback().add(createInternalErrorFeedback(errorMessage));
        if (audience.equals(Audience.BOTH)) {
            feedbackList.getStudentFeedback().add(createInternalErrorFeedback(errorMessage));
        } else {
            feedbackList.getStudentFeedback().add(createInternalErrorFeedback(DEFAULT_STUDENT_INTERNAL_ERROR_MESSAGE));
        }
        return feedbackList;
    }

    private FeedbackType createSubmissionRestrictionViolationFeedback(String htmlError) {
        FeedbackType feedback = new FeedbackType();
        feedback.setTitle("Submission Restriction Violation");
        feedback.setLevel(FeedbackLevelType.ERROR);
        Content content = new Content();
        content.setFormat("html");
        content.setValue(htmlError);
        feedback.setContent(content);
        return feedback;
    }

    private FeedbackListType createSubmissionRestrictionViolationFeedbackList(ProformaSubmissionRestrictionViolations violations) {
        FeedbackListType feedbackList = new FeedbackListType();
        String html = buildSubmissionRestrictionViolationHtml(violations);
        feedbackList.getTeacherFeedback().add(createSubmissionRestrictionViolationFeedback(html));
        feedbackList.getStudentFeedback().add(createSubmissionRestrictionViolationFeedback(html));
        return feedbackList;
    }

    /**
     * Extracts all test refs into map if the test ref contains a sub-ref attribute
     */
    private void extractSubtestRefsForRefList(List<GradesBaseRefChildType> refs, Map<String, Set<String>> map) {
        for (GradesBaseRefChildType refChildType : refs) {
            if (refChildType instanceof GradesTestRefChildType testRefChildType && null != testRefChildType.getRef() && null != testRefChildType.getSubRef()) {
                map.computeIfAbsent(testRefChildType.getRef(), k -> new HashSet<>()).add(testRefChildType.getSubRef());
            }
        }
    }

    /**
     * Extracts all subtests grouped to their respective tests
     * Return Key: test id
     * Return Val: subtest id
     */
    private Map<String, Set<String>> extractSubtestRefsByTest(TaskType taskPojo) {
        Map<String, Set<String>> map = new HashMap<>();
        // Root node
        extractSubtestRefsForRefList(taskPojo.getGradingHints().getRoot().getTestRefOrCombineRef(), map);
        // Combine ndoes
        taskPojo.getGradingHints().getCombine().forEach(node -> {
            extractSubtestRefsForRefList(node.getTestRefOrCombineRef(), map);
        });
        return map;
    }

    /**
     * Creates a TestResultType with it's score set to ZERO
     */
    private TestResultType createZeroedTestResult(FeedbackListType feedbackList, boolean isInternalError) {
        TestResultType testResult = new TestResultType();
        testResult.setFeedbackList(feedbackList);
        ResultType result = new ResultType();
        result.setIsInternalError(isInternalError);
        result.setScore(BigDecimal.ZERO);
        testResult.setResult(result);
        return testResult;
    }

    /**
     * This currently only works for ProFormA 2.1 submissions.
     * Otherwise null is returned.
     */
    private SeparateTestFeedbackType tryCreateSeparateTestFeedbackFromFeedbackList(FeedbackListType feedbackList, SubmissionResource subm, TaskBoundary tb, boolean isInternalError) {
        try {
            SubmissionLive sw = new SubmissionLive(subm);
            SubmissionType submPojo = sw.getSubmission();
            String structure = submPojo.getResultSpec().getStructure();

            if (!"separate-test-feedback".equals(structure)) {
                // Falling back to merged feedback. Nothing to do.
                return null;
            }
            TaskLive tw = sw.getTask(tb);
            TaskType taskPojo = tw.getTask();

            Map<String, Set<String>> testRefsByTest = extractSubtestRefsByTest(taskPojo);

            TestsResponseType testsResponse = new TestsResponseType();
            for (TestType test : taskPojo.getTests().getTest()) {
                TestResponseType testResponse = new TestResponseType();
                testResponse.setId(test.getId());

                Set<String> subRefs = testRefsByTest.get(test.getId());
                if (null != subRefs && !subRefs.isEmpty()) {
                    SubtestsResponseType subtestsResponse = new SubtestsResponseType();
                    subRefs.forEach(subRef -> {
                        SubtestResponseType subtestResponse = new SubtestResponseType();
                        subtestResponse.setId(subRef);
                        subtestResponse.setTestResult(createZeroedTestResult(feedbackList, isInternalError));
                        subtestsResponse.getSubtestResponse().add(subtestResponse);
                    });
                    testResponse.setSubtestsResponse(subtestsResponse);
                } else {
                    testResponse.setTestResult(createZeroedTestResult(feedbackList, isInternalError));
                }
                testsResponse.getTestResponse().add(testResponse);
            }
            SeparateTestFeedbackType separate = new SeparateTestFeedbackType();
            separate.setSubmissionFeedbackList(feedbackList);
            separate.setTestsResponse(testsResponse);
            return separate;
        } catch (Exception e) {
            // cannot identify desired response type.
            // Falling back to merged feedback
            return null; // set null in case of a half-prepared separate test feedback object.
        }
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

    private MergedTestFeedbackType createSubmissionRestrictionViolationMergedTestFeedback(ProformaSubmissionRestrictionViolations violations) {
        OverallResultType result = new OverallResultType();
        result.setIsInternalError(false);
        result.setScore(BigDecimal.ZERO);
        result.setValidity(BigDecimal.ZERO);

        MergedTestFeedbackType merged = new MergedTestFeedbackType();
        merged.setOverallResult(result);

        String html = "<p><strong>Submission Restriction Violation</strong></p>" + buildSubmissionRestrictionViolationHtml(violations);
        merged.setStudentFeedback(html);
        merged.setTeacherFeedback(html);
        return merged;
    }

    /**
     * Builds a html string from a List of Submission Restriction Violations for display in LMS
     */
    private String buildSubmissionRestrictionViolationHtml(ProformaSubmissionRestrictionViolations violations) {
        List<ProformaSubmissionRestrictionViolation> groupedViolations = regroupSubmissionRestrictionViolations(violations.getViolations());

        StringBuilder html = new StringBuilder();
        for (ProformaSubmissionRestrictionViolation violation : groupedViolations) {
            String feedbackHtml = violation.buildFeedbackHtml();
            if (null != feedbackHtml) {
                html.append(feedbackHtml);
            }
        }
        String descriptionHtml = violations.buildRestrictionsDescriptionHtml();
        if (null != descriptionHtml) {
            html.append(descriptionHtml);
        }
        return html.toString();
    }

    /**
     * Groups Submission Restriction Violations together by variant
     * Creates a copy of original violations List and treats parameter "violations" read-only
     */
    private List<ProformaSubmissionRestrictionViolation> regroupSubmissionRestrictionViolations(List<ProformaSubmissionRestrictionViolation> violations) {
        List<ProformaSubmissionRestrictionViolation> copy = new ArrayList<>();
        violations.forEach((violation) -> copy.add(violation));

        List<ProformaSubmissionRestrictionViolation> newList = new ArrayList<>();
        String currentVairant = null;
        while (!copy.isEmpty()) {
            ProformaSubmissionRestrictionViolation violation = getSubmissionRestrictionViolationByVariant(currentVairant, copy);
            if (null == violation) {
                violation = copy.get(0);
                currentVairant = violation.getVariant();
            }
            newList.add(violation);
            copy.remove(violation);
        }
        return newList;
    }

    /**
     * Searches a Submission Restriction Violation inside a respective List by variant
     * Returns null if variant does not exist in List
     */
    private ProformaSubmissionRestrictionViolation getSubmissionRestrictionViolationByVariant(String vairant, List<ProformaSubmissionRestrictionViolation> violations) {
        for (ProformaSubmissionRestrictionViolation violation : violations) {
            if (violation.getVariant().equals(vairant)) {
                return violation;
            }
        }
        return null;
    }

    @Override
    public ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) throws Exception {
        return createInternalErrorResponse(errorMessage, subm, tb, audience, false);
    }

    @Deprecated
    @Override
    public ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback) throws Exception {
        final String finalMsg = "Grappa encountered a fatal error: " + errorMessage;

        SeparateTestFeedbackType separate = null;
        if (!isExpectedInternalErrorTypeAlwaysMergedTestFeedback) {
            FeedbackListType internalErrorFeedbackList = createInternalErrorFeedbackList(errorMessage, audience);
            separate = tryCreateSeparateTestFeedbackFromFeedbackList(internalErrorFeedbackList, subm, tb, true);
        }
        MergedTestFeedbackType merged = null;
        if (separate == null) merged = createInternalErrorMergedTestFeedback(finalMsg, audience);

        ResponseType resp = createUnknownGraderEngineResponse(merged, separate);

        return new ResponseLive(resp, null, MimeType.XML, MarshalOption.of(MarshalOption.CDATA)).getResource();
    }

    @Override
    public ResponseResource createSubmissionRestrictionViolationResponse(ProformaSubmissionRestrictionViolations violations, SubmissionResource subm, TaskBoundary tb) throws Exception {
        FeedbackListType submissionRestrictionViolationFeedbackList = createSubmissionRestrictionViolationFeedbackList(violations);
        SeparateTestFeedbackType separate = tryCreateSeparateTestFeedbackFromFeedbackList(submissionRestrictionViolationFeedbackList, subm, tb, false);

        MergedTestFeedbackType merged = null;
        if (separate == null) 
            merged = createSubmissionRestrictionViolationMergedTestFeedback(violations);

        ResponseType resp = createUnknownGraderEngineResponse(merged, separate);
        return new ResponseLive(resp, null, MimeType.XML, MarshalOption.of(MarshalOption.CDATA)).getResource();
    }

    @Override
    public ResponseResource generateMergedFeedbackIfRequested(ResponseResource responseResource, SubmissionResource subm, TaskBoundary tb) throws Exception {
        ResponseLive responseLive = new ResponseLive(responseResource);
        ResponseType response = responseLive.getResponse();

        if (null != response.getMergedTestFeedback()) {
            return responseResource; // If response already contains merged feedback, just return it
        }

        SeparateTestFeedbackType separateFeedback = response.getSeparateTestFeedback();
        if (null == separateFeedback) {
            throw new IllegalStateException("Response does not contain any merged or separate feedback");
        }

        SubmissionLive submLive = new SubmissionLive(subm);
        SubmissionType submType = submLive.getSubmission();

        if (!submType.getResultSpec().getStructure().equals("merged-test-feedback")) {
            return responseResource; // Merged Test Feedback is not requestet, just return the Response containing the Separate Test Feedback
        }

        TaskLive taskLive = submLive.getTask(tb);
        TaskType task = taskLive.getTask();

        ResponseFilesType responseFiles = response.getFiles();

        Proforma21HtmlFeedbackGenerator generator = new Proforma21HtmlFeedbackGenerator(separateFeedback, task, responseFiles, null);
        String studentHtml = generator.buildFeedbackHtml(false, false, true);
        String teacherHtml = generator.buildFeedbackHtml(true, false, true);

        OverallResultType overallResult = new OverallResultType();
        overallResult.setScore(generator.getScore());
        overallResult.setIsInternalError(false);

        MergedTestFeedbackType mergedFeedback = new MergedTestFeedbackType();
        mergedFeedback.setOverallResult(overallResult);
        mergedFeedback.setStudentFeedback(studentHtml);
        mergedFeedback.setTeacherFeedback(teacherHtml);

        ResponseType resp = createUnknownGraderEngineResponse(mergedFeedback, null);
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
        ArrayList<Proforma21ResponseFileHandle> list = new ArrayList<>();
        for (ResponseFileType file : rf.getFile()) {
            list.add(getResponseFileHandle(file, zipContent));
        }
        return list;
    }

    @Override
    public void addAttachedTxtFile(AbstractResponseType response, String filepath) {
        ResponseType r = (ResponseType) response;
        ResponseFileType file = createResponseFile(r, filepath);

        AttachedTxtFileType attachedTxt = new AttachedTxtFileType();
        attachedTxt.setValue(filepath);
        file.setAttachedTxtFile(attachedTxt);

        r.getFiles().getFile().add(file);
    }

    @Override
    public void addAttachedBinFile(AbstractResponseType response, String filepath) {
        ResponseType r = (ResponseType) response;
        ResponseFileType file = createResponseFile(r, filepath);

        file.setAttachedBinFile(filepath);

        r.getFiles().getFile().add(file);
    }

    @Override
    public void addEmbeddedTxtFile(AbstractResponseType response, String filename, String content) {
        ResponseType r = (ResponseType) response;
        ResponseFileType file = createResponseFile(r, filename);

        EmbeddedTxtFileType embeddedTxt = new EmbeddedTxtFileType();
        embeddedTxt.setFilename(filename);
        embeddedTxt.setValue(content);
        file.setEmbeddedTxtFile(embeddedTxt);

        r.getFiles().getFile().add(file);
    }

    @Override
    public void addEmbeddedBinFile(AbstractResponseType response, String filename, byte[] content) {
        ResponseType r = (ResponseType) response;
        ResponseFileType file = createResponseFile(r, filename);

        EmbeddedBinFileType embeddedBin = new EmbeddedBinFileType();
        embeddedBin.setFilename(filename);
        embeddedBin.setValue(content);
        file.setEmbeddedBinFile(embeddedBin);

        r.getFiles().getFile().add(file);
    }

    private ResponseFileType createResponseFile(ResponseType r, String filepath) {
        if (r.getFiles() == null) {
            r.setFiles(new ResponseFilesType());
        }
        ResponseFileType file = new ResponseFileType();
        file.setId("Response-File-" + r.getFiles().getFile().size());
        file.setTitle(filepath);

        return file;
    }
}
