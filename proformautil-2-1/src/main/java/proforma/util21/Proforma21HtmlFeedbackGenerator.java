package proforma.util21;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import proforma.util21.format.CombineNode;
import proforma.util21.format.GradingNode;
import proforma.util21.format.ResponseFormatter;
import proforma.util21.format.TestNode;

import proforma.xml21.EmbeddedTxtFileType;
import proforma.xml21.FeedbackType;
import proforma.xml21.FilerefType;
import proforma.xml21.FilerefsType;
import proforma.xml21.ResponseFileType;
import proforma.xml21.ResponseFilesType;
import proforma.xml21.SeparateTestFeedbackType;
import proforma.xml21.TaskType;

/**
 * Class that can be used to convert Separate Test Feedback to a
 * HTML document that displays the Separate Test Feedback Response
 * as a HTML page
 */
public class Proforma21HtmlFeedbackGenerator {

    private static final int INDENTATION_SIZE = 4; // Spaces to be used for nested HTML

    private static final String CSS_BODY = "max-width: 1300px; margin: 20px auto; border: 1px solid #ccc; box-shadow: 2px 2px 5px rgba(0,0,0,0.1); padding: 10px;";
    private static final String CSS_BLACK = "color: black;";
    private static final String CSS_RED = "color: red;";
    private static final String CSS_H1_H2 = "color: navy; margin-bottom: 0;";
    private static final String CSS_EXPAND_COLLAPSE_BUTTONS = "display: flex; gap: 10px; margin: 10px 0; padding: 10px; justify-content: flex-end;";
    private static final String CSS_EXPAND_COLLAPSE_BUTTON = "padding: 5px 10px; background-color:navy; color: white; border: none; border-radius: 4px; cursor: pointer;";
    private static final String CSS_COLLAPSIBLE = "cursor: pointer; padding: 10px; width: 100%; border: none; text-align: left; outline: none; font-size: 15px; background-color: #f0f0f0; margin-top: 5px;";
    private static final String CSS_INNER_COLLAPSIBLE = "cursor: pointer; padding: 5px 10px; width: auto; border: 1px solid #ccc; text-align: left; outline: none; font-size: 12px; background-color: white; margin-top: 3px; display: inline-block;";
    private static final String CSS_CONTENT = "overflow: hidden; background-color: inherit; max-height: none;";
    private static final String CSS_INNER_CONTENT = "padding: 5px 10px; border-top: 1px dashed #ccc; overflow: hidden; background-color: inherit;";
    private static final String CSS_FEEDBACK_LIST = "margin-bottom: 5px;";
    private static final String CSS_FEEDBACK_CONTENT = "margin-bottom: 5px;";
    private static final String CSS_FEEDBACK_ELEMENT = "margin-top: 10px; margin-bottom: 10px;";
    private static final String CSS_GRADING_NODE = "padding: 5px; margin-bottom: 0; border-bottom: 1px solid #ddd;";
    private static final String CSS_TITLE_CONTAINER = "display: flex; justify-content: space-between; align-items: center; gap: 10px; flex-wrap: wrap; padding-bottom: 5px;";
    private static final String CSS_TITLE_CONTAINER_P_H3 = "color: black; margin: 0; flex-grow: 1;";
    private static final String CSS_TITLE_RESULT = "margin-left: auto; padding-left: 10px; white-space: normal; font-style: italic; font-size: 1.0em;";
    private static final String CSS_NULLIFY_REASON = "color: red; font-style: italic; font-weight: bold; margin-top: 10px;";
    private static final String CSS_TEST_REF = "padding: 5px; margin-bottom: 0; border-bottom: 1px solid #ddd;";
    private static final String CSS_FILE_DOWNLOAD_LINK = "padding: 20px 0 0 20px; display: block; color: blue;";

    private static final String TEXT_EXPAND_ALL = "Expand All";
    private static final String TEXT_COLLAPSE_ALL = "Collapse All";
    private static final String TEXT_DETAILS = "Details";
    private static final String TEXT_DETAILS_FEEDBACK = "Details & Feedback";
    private static final String TEXT_EMBEDDED_TXT_FILE_NOT_FOUND = "Error: File Ref found in Feedback but response file does not contain a embedded txt file";

    private static final String PAGE_TITLE = "Evaluation Report";
    private static final String FEEDBACK_TITLE_SUMMARIZED = "Summarized Feedback";
    private static final String FEEDBACK_TITLE_DETAILED = "Detailed Feedback";
    private static final String FEEDBACK_TITLE_STUDENT = "Student Feedback";
    private static final String FEEDBACK_TITLE_TEACHER = "Teacher Feedback";
    private static final String ROOT_NODE_TITLE = "Overall result";
    private static final String NO_FEEDBACK_MSG = "No feedback provided.";
    private static final String NULLIFIED_REASON_PREFIX = "Nullified. Reason for nullification:";
    private static final String NULLIFIED_SUFFIX = "[nullified]";
    private static final String INTERNAL_FEEDBACK_PREFIX = "Internal Feedback";
    private static final String SCORE_CALC_PREFIX = "Score calculation:";
    private static final String SCORE_CALC_POSTFIX = "of the following sub-aspects";
    private static final String TEST_RESULT_CORRECT = "correct";
    private static final String TEST_RESULT_WRONG = "wrong";
    private static final String TEST_ACTUAL_SCORE = "actual score.";
    private static final String TEST_RAW_SCORE = "raw test score.";

    private static String CLASS_COLLAPSIBLE = "collapsible";
    private static String CLASS_INNER_COLLAPSIBLE = "inner-collapsible";
    private static String ID_EXPAND_ALL = "expand-all";
    private static String ID_COLLAPSE_ALL = "collapse-all";

    private final SeparateTestFeedbackType separateFeedback;
    private final TaskType task;
    private final ResponseFilesType responseFiles;
    private final CombineNode rootNode;
    private final Random random;
    private final double scaleFactor;
    private final boolean hasInternalError;
    private StringBuilder sb;
    private boolean includeTeacherFeedback;
    private boolean includeJavaScript;
    private int indentationLevel;
    
    /**
     * @param maxScoreLMS determine a maximum score that the used LMS expects for the task to scale each node accordingly. No scaling applied if null.
     */
    public Proforma21HtmlFeedbackGenerator(SeparateTestFeedbackType separateFeedback, TaskType task, ResponseFilesType responseFiles, Double maxScoreLMS) {
        this.separateFeedback = separateFeedback;
        this.task = task;
        this.responseFiles = responseFiles;
        this.random = new Random();
        
        ResponseFormatter formatter = new ResponseFormatter(this.separateFeedback, this.task, maxScoreLMS);
        this.rootNode = formatter.generateGradingStructure();
        this.scaleFactor = formatter.calculateScaleFactor();
        this.hasInternalError = formatter.getHasInternalError();
    }

    /**
     * Builds the entire HTML from Separate Test Feedback
     * 
     * @param includeTeacherFeedback should teacher feedback be included in HTML?
     * @param generateWholeHtmlDocument should the document also contain &lt;!DOCTYPE html&gt;, &lt;html&gt;, &lt;head&gt; and &lt;body&gt; tags?
     */
    public String buildFeedbackHtml(boolean includeTeacherFeedback, boolean generateWholeHtmlDocument, boolean includeJavaScript) {
        randomizeHTMLIdentifiers();
        this.sb = new StringBuilder();
        this.includeTeacherFeedback = includeTeacherFeedback;
        this.includeJavaScript = includeJavaScript;
        this.indentationLevel = 0;

        if (generateWholeHtmlDocument) {
            initializeHtmlDocument();
            addHeadSection();
            addOpeningBodyTag(); // leaves indentationLevel incremented after call
        }

        if (this.includeJavaScript) {
            addExpandCollapseAllButtons();
        }
        addSummarizedFeedbackSection();
        addDetailedFeedbackSection();
        
        if (this.includeJavaScript) {
            addCommonJavaScript();
        }

        if (generateWholeHtmlDocument) {
            addClosingBodyTag(); // leaves indentationLevel decremented after call
            finalizeHtmlDocument();
        }

        return this.sb.toString();
    }

    /**
     * Returns the overall score
     */
    public BigDecimal getScore() {
        return new BigDecimal(this.rootNode.getActualScore());
    }
    
    /**
     * Returns the scale factor
     */
    public double getScaleFactor() {
        return this.scaleFactor;
    }
    
    /**
     * Returns true if the Feedback indicates that an internal error has occured
     */
    public boolean getHasInternalError() {
        return this.hasInternalError;
    }

    private void randomizeHTMLIdentifiers() {
        CLASS_COLLAPSIBLE = CLASS_COLLAPSIBLE + generateRandomHexString();
        CLASS_INNER_COLLAPSIBLE = CLASS_INNER_COLLAPSIBLE + generateRandomHexString();
        ID_EXPAND_ALL = ID_EXPAND_ALL + generateRandomHexString();
        ID_COLLAPSE_ALL = ID_COLLAPSE_ALL + generateRandomHexString();
    }

    private String generateRandomHexString() {
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }

    /**
     * Appends indented text to sb and appends a new-line-character
     * Indentation level is determined by class attribute
     */
    private void appendLine(String text) {
        if (this.indentationLevel < 0) {
            throw new IllegalArgumentException("indentationLevel can't be smaller than 0");
        }
        for (int i = 0; i < this.indentationLevel; i++) {
            for (int j = 0; j < INDENTATION_SIZE; j++) {
                this.sb.append(" ");
            }
        }
        this.sb.append(text).append("\n");
    }

    /**
     * Creates a style attribute for use in a html tag
     */
    private String createStyle(String stylesText) {
        return "style=\"" + stylesText + "\"";
    }

    /**
     * Generates the CSS for detailed feedback indentation levels
     */
    private String generateIndentCSS(int indentLevel) {
        int margin = 20 * indentLevel;
        String background = switch (indentLevel) {
            case 0 -> "#ffffff";
            case 1 -> "#efefef";
            case 2 -> "#dfdfdf";
            case 3 -> "#cfcfcf";
            case 4 -> "#bfbfbf";
            default -> "#afafaf";
        };
        if (margin > 100) {
            margin = 100;
        }
        return "margin-left: " + margin + "px; background-color: " + background + ";";
    }

    /**
     * Escapes common HTML characters to the respective HTML character codes
     */
    private String escapeHtml(String text) {
        if (null == text) {
            return "";
        }
        return  text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
    
    /**
     * Calculates the catual score of a Grading Node based on if it is nullified or not
     */
    private double calculateActualScore(GradingNode node) {
        return node.isNullified() ? 0 : node.getActualScore();
    }
    
    /**
     * Calculates the scaled max score of a Grading Node based on provided scaling factor
     */
    private double calculateScaledMaxScore(GradingNode node) {
        return Math.round(node.getMaxScore() * this.scaleFactor);
    }
    
    /**
     * Calculates the scaled actual score of a Grading Node based on the provided scaling factor
     */
    private double calculateScaledActualScore(GradingNode node) {
        return Math.round(calculateActualScore(node) * this.scaleFactor);
    }

    /**
     * Adds HTML Document initializer
     */
    private void initializeHtmlDocument() {
        appendLine("<!DOCTYPE html>");
        appendLine("<html lang=\"en\">");
    }

    /**
     * Adds head-Section to HTML Document
     */
    private void addHeadSection() {
        appendLine("<head>");
        this.indentationLevel++;
        appendLine("<meta charset=\"UTF-8\">");
        appendLine("<title>" + PAGE_TITLE + "</title>");
        this.indentationLevel--;
        appendLine("</head>");
    }

    /**
     * Adds the opening body tag to the html document.
     * Also leaves the indentationlevel incremented after call for proper indentation
     * inside the body section
     */
    private void addOpeningBodyTag() {
        appendLine("<body " + createStyle(CSS_BODY) + ">");
        this.indentationLevel++;
    }

    /**
     * Adds the "Expand All" and "Collapse All" buttons
     */
    private void addExpandCollapseAllButtons() {
        appendLine("<div " + createStyle(CSS_EXPAND_COLLAPSE_BUTTONS) + ">");
        this.indentationLevel++;
        appendLine("<button type=\"button\" " + createStyle(CSS_EXPAND_COLLAPSE_BUTTON) + " id=\"" + ID_EXPAND_ALL + "\">" + TEXT_EXPAND_ALL + "</button>");
        appendLine("<button type=\"button\" " + createStyle(CSS_EXPAND_COLLAPSE_BUTTON) + " id=\"" + ID_COLLAPSE_ALL + "\">" + TEXT_COLLAPSE_ALL + "</button>");
        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds collapsible "Summarized Feedback" section
     */
    private void addSummarizedFeedbackSection() {
        addCollapsibleTopButton(FEEDBACK_TITLE_SUMMARIZED);
        appendLine("<div " + createStyle(CSS_CONTENT + " background-color: white;") + ">");
        this.indentationLevel++;
        addStudentFeedbackList();
        if (this.includeTeacherFeedback) {
            addTeacherFeedbackList();
        }
        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds button for expanding/collapsing when clicking section titles
     */
    private void addCollapsibleTopButton(String title) {
        appendLine("<button type=\"button\" " + createStyle(CSS_COLLAPSIBLE) + " class=\"" + CLASS_COLLAPSIBLE + "\">");
        this.indentationLevel++;
        appendLine("<h1 " + createStyle(CSS_H1_H2) + ">" + title + "</h1>");
        this.indentationLevel--;
        appendLine("</button>");
    }

    /**
     * Adds Student feedback list to summarized feedback
     */
    private void addStudentFeedbackList() {
        if (this.includeTeacherFeedback) {
            // Only add "Student Feedback" title if the document contains both student and teacher feedback
            appendLine("<h2 " + createStyle(CSS_H1_H2 + " padding: 5px 10px;") + ">" + FEEDBACK_TITLE_STUDENT + "</h2>");
        }
        appendLine("<div " + createStyle(CSS_FEEDBACK_LIST + " padding: 5px 10px; " + CSS_BLACK) + ">");
        this.indentationLevel++;

        List<String> feedbackStrings = new ArrayList<>();
        this.separateFeedback.getSubmissionFeedbackList().getStudentFeedback().forEach(feedbackType -> {
            feedbackStrings.add(feedbackType.getContent().getValue());
        });
        addFeedbackList(feedbackStrings);
        
        List<FilerefType> fileRefs = extractFileRefsFromFeedbackList(this.separateFeedback.getSubmissionFeedbackList().getStudentFeedback());
        addFileRefDownloadLinks(fileRefs);

        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds teacher feedback list to summarized feedback
     */
    private void addTeacherFeedbackList() {
        appendLine("<h2 " + createStyle(CSS_H1_H2 + " padding: 5px 10px;") + ">" + FEEDBACK_TITLE_TEACHER + "</h2>");
        appendLine("<div " + createStyle(CSS_FEEDBACK_LIST + " padding: 5px 10px; " + CSS_BLACK) + ">");
        this.indentationLevel++;

        List<String> feebackStrings = new ArrayList<>();
        this.separateFeedback.getSubmissionFeedbackList().getTeacherFeedback().forEach(feedbackType -> {
            feebackStrings.add(feedbackType.getContent().getValue());
        });
        addFeedbackList(feebackStrings);

        List<FilerefType> fileRefs = extractFileRefsFromFeedbackList(this.separateFeedback.getSubmissionFeedbackList().getTeacherFeedback());
        addFileRefDownloadLinks(fileRefs);

        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds feedback line-by-line to HTML document
     * Can be reused
     */
    private void addFeedbackList(List<String> feedbackList) {
        if (null == feedbackList || feedbackList.isEmpty()) {
            appendLine("<p " + createStyle(CSS_BLACK + " " + CSS_FEEDBACK_ELEMENT) + "><i>" + NO_FEEDBACK_MSG + "</i></p>");
            return;
        }
        for (String feedback : feedbackList) {
            appendLine("<p " + createStyle(CSS_FEEDBACK_ELEMENT) + ">");
            feedback.lines().forEach(line -> {
                appendLine(line);
            });
            appendLine("</p>");
        }
    }

    /**
     * Adds Download links for each file ref to the HTML document
     * Files get embedded as base64 right into the document
     */
    private void addFileRefDownloadLinks(List<FilerefType> fileRefs) {
        for (FilerefType fileRef : fileRefs) {
            String refId = fileRef.getRefid();
            EmbeddedTxtFileType embeddedTxtFile = getEmbeddedTxtFileByFileRefId(refId);
            if (null == embeddedTxtFile) {
                appendLine("<p " + createStyle(CSS_RED) + ">" + TEXT_EMBEDDED_TXT_FILE_NOT_FOUND + "</p>");
                continue;
            }
            
            String embeddedTxtContent = embeddedTxtFile.getValue();
            String base64Content = Base64.getEncoder().encodeToString(embeddedTxtContent.getBytes());

            String linkHtml =
                "<a " + createStyle(CSS_FILE_DOWNLOAD_LINK) + " href=\"data:text/plain;base64," +
                base64Content + "\" download=\"" + embeddedTxtFile.getFilename() + "\">" + embeddedTxtFile.getFilename() + "</a>";
            appendLine(linkHtml);
        }
        if (!fileRefs.isEmpty()) {
            appendLine("<br>");
        }
    }

    /**
     * Returns the embedded txt file type for a given file ref id
     * Returns null if response file does not exist or if it does not
     * contain an embedded txt file
     */
    private EmbeddedTxtFileType getEmbeddedTxtFileByFileRefId(String refId) {
        for (ResponseFileType responseFile : this.responseFiles.getFile()) {
            if (responseFile.getId().equals(refId) && responseFile.getEmbeddedTxtFile() != null) {
                return responseFile.getEmbeddedTxtFile();
            }
        }
        return null;
    }

    /**
     * Extracts FileRefs from a given Feedback List and packs them into one List
     */
    private List<FilerefType> extractFileRefsFromFeedbackList(List<FeedbackType> feedbackList) {
        List<FilerefType> fileRefs = new ArrayList<>();
        feedbackList.forEach(feedback -> {
            FilerefsType feedbackFileRefs = feedback.getFilerefs();
            if (null != feedbackFileRefs) {
                fileRefs.addAll(feedbackFileRefs.getFileref());
            }
        });
        return fileRefs;
    }

    /**
     * Adds collapsible "Detailed Feedback" section
     */
    private void addDetailedFeedbackSection() {
        addCollapsibleTopButton(FEEDBACK_TITLE_DETAILED);
        appendLine("<div " + createStyle(CSS_CONTENT) + ">");
        this.indentationLevel++;
        
        addNodesRecursive(this.rootNode);

        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds a CombineNode to the HTML Document
     * This method will be called recursively if the given node
     * also contains CombineNode(s) as children
     */
    private void addNodesRecursive(CombineNode node) {
        addCombineNodeStart(node); // leaves indentationLevel incremented after call
        addCombineNodeInfo(node);
        
        for (GradingNode child: node.getChildren()) {
            if (child instanceof TestNode testChild) {
                addTestNode(testChild);
            } else if (child instanceof CombineNode combineChild) {
                addNodesRecursive(combineChild);
            }
        }

        this.indentationLevel--;
        appendLine("</div>"); // combine node start div
    }

    /**
     * Adds starting components for a combine node
     * Also leaves indentationLevel incremented after call
     */
    private void addCombineNodeStart(CombineNode node) {
        double actualScore = calculateScaledActualScore(node);
        double maxScore = calculateScaledMaxScore(node);
        
        appendLine("<div " + createStyle(CSS_GRADING_NODE + " " + generateIndentCSS(node.getIndentLevel())) + ">");
        this.indentationLevel++;
        appendLine("<div " + createStyle(CSS_TITLE_CONTAINER) + ">");
        this.indentationLevel++;
        String title = node.getRefId().equals("root") ? ROOT_NODE_TITLE : escapeHtml(node.getTitle());
        String titleText = String.format("%s [max. %.2f]", title, maxScore);
        appendLine("<p " + createStyle(CSS_TITLE_CONTAINER_P_H3) +  ">" + titleText + "</p>");
        appendLine("<span " + createStyle(CSS_TITLE_RESULT + " " + CSS_BLACK) + ">" + String.format("[%s %.2f]", TEST_ACTUAL_SCORE, actualScore) + "</span>");
        if (this.includeJavaScript) {
            appendLine("<button type=\"button\" " + createStyle(CSS_INNER_COLLAPSIBLE) + " class=\"" + CLASS_COLLAPSIBLE + " " + CLASS_INNER_COLLAPSIBLE + "\">" + TEXT_DETAILS + "</button>");
        }
        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds info about combine node
     */
    private void addCombineNodeInfo(CombineNode node) {
        appendLine("<div " + createStyle(CSS_INNER_CONTENT) + ">");
        this.indentationLevel++;
        if (node.isNullified()) {
            addNullificationInfo(node.getNullifyReason());
        }
        addDescription(node.getDescription(), node.getInternalDescription());
        addScoreCalculationInfo(node.getFunction());
        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Adds info about the nullification reason to combine node
     */
    private void addNullificationInfo(String reason) {
        if (null != reason && !reason.isEmpty()) {
            appendLine("<div " + createStyle(CSS_NULLIFY_REASON) + ">");
            this.indentationLevel++;
            appendLine(NULLIFIED_REASON_PREFIX + " " + escapeHtml(reason));
            this.indentationLevel--;
            appendLine("</div>");
        }
    }

    /**
     * Adds Description to node
     */
    private void addDescription(String description, String internalDescription) {
        if (null != description && !description.isEmpty()) {
            appendLine("<p " + createStyle(CSS_BLACK) + ">" + escapeHtml(description) + "</p>");
        }
        if (this.includeTeacherFeedback && null != internalDescription && !internalDescription.isEmpty()) {
            appendLine("<p " + createStyle(CSS_BLACK) + "><i>" + INTERNAL_FEEDBACK_PREFIX + " " + escapeHtml(internalDescription) + "</i></p>");
        }
    }

    /**
     * Adds calculation info to combine node
     */
    private void addScoreCalculationInfo(String function) {
        if (null != function && !function.isEmpty()) {
            String calculationText = SCORE_CALC_PREFIX + " " + escapeHtml(function) + " " + SCORE_CALC_POSTFIX;
            appendLine("<p " + createStyle(CSS_BLACK) + "><em " + createStyle(CSS_BLACK) + ">" + calculationText + "</em></p>");
        }
    }

    /**
     * Adds Test Node with all information
     */
    private void addTestNode(TestNode node) {
        double maxScore = calculateScaledMaxScore(node);
        double actualScore = calculateScaledActualScore(node);
        addTestNodeStart(node.getTitle(), actualScore, node.getRawScore(), maxScore, node.getIndentLevel(), node.isNullified()); // leaves indentationLevel incremented after call

        appendLine("<div " + createStyle(CSS_INNER_CONTENT) + ">");
        this.indentationLevel++;
        if (node.isNullified() && node.getRawScore() != 0) {
            addNullificationInfo(node.getNullifyReason());
        }
        addDescription(node.getDescription(), node.getInternalDescription());

        List<String> studentFeedback = node.getStudentFeedback();
        if (this.includeTeacherFeedback) {
            // Only add "Student Feedback" title if the document contains both student and teacher feedback
            appendLine("<h4 " + createStyle(CSS_BLACK) + ">" + FEEDBACK_TITLE_STUDENT + "</h4>");
        }
        appendLine("<div " + createStyle(CSS_FEEDBACK_CONTENT + " " + CSS_BLACK) + ">");
        this.indentationLevel++;
        addFeedbackList(studentFeedback);
        this.indentationLevel--;
        appendLine("</div>");

        if (this.includeTeacherFeedback) {
            List<String> teacherFeedback = node.getTeacherFeedback();
            appendLine("<h4 " + createStyle(CSS_BLACK) + ">" + FEEDBACK_TITLE_TEACHER + "</h4>");
            appendLine("<div " + createStyle(CSS_FEEDBACK_CONTENT + " " + CSS_BLACK) + ">");
            this.indentationLevel++;
            addFeedbackList(teacherFeedback);
            this.indentationLevel--;
            appendLine("</div>");
        }

        this.indentationLevel--;
        appendLine("</div>"); // inner content div
        this.indentationLevel--;
        appendLine("</div>"); // test node start div
    }

    /**
     * Adds all starting components for a test node
     * Also leaves indentation level incremented after call
     */
    private void addTestNodeStart(String title, double actualScore, double rawScore, double maxScore, int indentLevel, boolean isNullified) {
        appendLine("<div " + createStyle(CSS_TEST_REF + " " + generateIndentCSS(indentLevel)) + ">");
        this.indentationLevel++;
        appendLine("<div " + createStyle(CSS_TITLE_CONTAINER) + ">");
        this.indentationLevel++;
        String titleText = String.format("%s [max. %.2f]", title, maxScore);
        appendLine("<p " + createStyle(CSS_TITLE_CONTAINER_P_H3) + ">" + escapeHtml(titleText) + "</p>");

        StringBuilder resultText = new StringBuilder();
        resultText.append(String.format("[%s %.2f]", TEST_RAW_SCORE, rawScore))
            .append(" ")
            .append(String.format("[%s %.2f]", TEST_ACTUAL_SCORE, actualScore))
            .append(" - ").append(rawScore != 0.0 ? TEST_RESULT_CORRECT : TEST_RESULT_WRONG);
        if (rawScore != 0.0 && isNullified) {
            resultText.append(NULLIFIED_SUFFIX);
        }

        appendLine("<span " + createStyle(CSS_TITLE_RESULT + " " + CSS_BLACK) + ">" + resultText.toString() + "</span>");
        if (this.includeJavaScript) {
            appendLine("<button type=\"button\" " + createStyle(CSS_INNER_COLLAPSIBLE) + " class=\"" + CLASS_COLLAPSIBLE + " " + CLASS_INNER_COLLAPSIBLE + "\">" + TEXT_DETAILS_FEEDBACK + "</button>");
        }
        this.indentationLevel--;
        appendLine("</div>");
    }

    /**
     * Add JavaScript for interactivity
     */
    private void addCommonJavaScript() {
        appendLine("<script>");
        this.indentationLevel++;

        String js = """

        document.addEventListener('DOMContentLoaded', () => {
            const collapsibles = document.getElementsByClassName('%s');
            for (const button of collapsibles) {
                let content;
                if (button.classList.contains('%s')) {
                    content = button.parentElement.nextElementSibling;
                } else {
                    content = button.nextElementSibling;
                }

                if (content) {
                    content.style.maxHeight = '0px';
                }

                button.addEventListener('click', () => {
                    let currentContent;
                    if (button.classList.contains('%s')) {
                        currentContent = button.parentElement.nextElementSibling;
                    } else {
                        currentContent = button.nextElementSibling;
                    }

                    if (!currentContent) return;

                    if (currentContent.style.maxHeight && currentContent.style.maxHeight !== '0px') {
                        currentContent.style.maxHeight = '0px';
                    } else {
                        currentContent.style.maxHeight = 'none';
                    }
                });
            }

            const expandAllBtn = document.getElementById('%s');
            const collapseAllBtn = document.getElementById('%s');

            if (expandAllBtn) {
                expandAllBtn.addEventListener('click', () => {
                    const collapsibles = document.getElementsByClassName('%s');
                    for (const button of collapsibles) {
                        let content;
                        if (button.classList.contains('%s')) {
                            content = button.parentElement.nextElementSibling;
                        } else {
                            content = button.nextElementSibling;
                        }
                        if (content && content.style.maxHeight === '0px') {
                            button.click();
                        }
                    }
                });
            }

            if (collapseAllBtn) {
                collapseAllBtn.addEventListener('click', () => {
                    const collapsibles = document.getElementsByClassName('%s');
                    for (const button of collapsibles) {
                        let content;
                        if (button.classList.contains('%s')) {
                            content = button.parentElement.nextElementSibling;
                        } else {
                            content = button.nextElementSibling;
                        }
                        if (content && content.style.maxHeight !== '0px') {
                            button.click();
                        }
                    }
                });
            }
        });

        """.formatted(CLASS_COLLAPSIBLE,
            CLASS_INNER_COLLAPSIBLE,
            CLASS_INNER_COLLAPSIBLE,
            ID_EXPAND_ALL,
            ID_COLLAPSE_ALL,
            CLASS_COLLAPSIBLE,
            CLASS_INNER_COLLAPSIBLE,
            CLASS_COLLAPSIBLE,
            CLASS_INNER_COLLAPSIBLE
        );

        js.lines().forEach(line -> {
            appendLine(line);
        });

        this.indentationLevel--;
        appendLine("</script>");
    }

    /**
     * Adds the closing body tag to the html document.
     * Also leaves the indentationlevel decremented after call for proper indentation
     * after the body section
     */
    private void addClosingBodyTag() {
        this.indentationLevel--;
        appendLine("</body>");
    }

    /**
     * Adds final closing html tag
     */
    private void finalizeHtmlDocument() {
        appendLine("</html>");
    }
}