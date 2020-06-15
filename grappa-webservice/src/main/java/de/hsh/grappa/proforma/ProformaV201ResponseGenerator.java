package de.hsh.grappa.proforma;

import de.hsh.grappa.proformaxml.v201.*;

import java.math.BigDecimal;

public class ProformaV201ResponseGenerator {

    private ProformaV201ResponseGenerator() {
    }


    /**
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static ProformaResponse createInternalErrorResponse(String errorMessage) {

        ResultType result = new ResultType();
        result.setIsInternalError(true);
        result.setScore(BigDecimal.ZERO);
        result.setValidity(BigDecimal.ZERO);

        FeedbackListType feedbackList = new FeedbackListType();
        FeedbackType fb = new FeedbackType();
        fb.setLevel(FeedbackLevelType.ERROR);
        fb.setTitle("Error");
        FeedbackType.Content content = new FeedbackType.Content();
        content.setValue(errorMessage);
        content.setFormat("plaintext");
        fb.setContent(content);
        feedbackList.getTeacherFeedback().add(fb);
        feedbackList.getStudentFeedback().add(fb);
//        errorMessages.forEach(msg -> {
//            FeedbackType fb = new FeedbackType();
//            fb.setLevel(FeedbackLevelType.ERROR);
//            fb.setTitle(msg.getKey());
//            FeedbackType.Content content = new FeedbackType.Content();
//            content.setValue(msg.getValue());
//            content.setFormat("plaintext");
//            fb.setContent(content);
//            feedbackList.getTeacherFeedback().add(fb);
//            feedbackList.getStudentFeedback().add(fb);
//        });

        TestResultType testResult = new TestResultType();
        testResult.setResult(result);
        testResult.setFeedbackList(feedbackList);

        TestResponseType testResponse = new TestResponseType();
        testResponse.setTestResult(testResult);

        TestsResponseType testsResponse = new TestsResponseType();
        testsResponse.getTestResponse().add(testResponse);

        SeparateTestFeedbackType separateTestFeedback = new SeparateTestFeedbackType();
        separateTestFeedback.setTestsResponse(testsResponse);

//        ResponseFilesType responseFiles = new ResponseFilesType();
//        responseFiles.

        GraderEngineType graderEngine = new GraderEngineType();
        graderEngine.setName("unavailable");
        graderEngine.setVersion("unavailable");

        ResponseMetaDataType responseMetaData = new ResponseMetaDataType();
        responseMetaData.setGraderEngine(graderEngine);

        ResponseType resp = new ResponseType();
        resp.setSeparateTestFeedback(separateTestFeedback);
        resp.setLang("en_EN");
        resp.setFiles(new ResponseFilesType());
        resp.setResponseMetaData(responseMetaData);

        throw new RuntimeException("TODO: Resolve Jaxb-api impl could not be found.");

//        String responseXml = XmlUtils.marshalToXml(resp, ResponseType.class);
//        return new ProformaResponse(responseXml.getBytes(Charsets.UTF_8), MimeType.XML);
    }
}
