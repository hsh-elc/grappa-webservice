package proforma.util21.format;

public abstract class GradingNode {
    private final String refId;
    private final NodeType type;
    private final String title;
    private final String description;
    private final String internalDescription;

    private final double weight;
    private final double rawScore;
    private final double maxScore;
    private double actualScore;

    private final int indentLevel;

    private boolean nullified;
    private String nullifyReason;

    protected GradingNode(
        String refId, NodeType type, String title, String description, String internalDescription,
        double weight, double rawScore, double maxScore, double actualScore, int indentLevel
    ) {
        this.refId = refId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.internalDescription = internalDescription;
        this.weight = weight;
        this.rawScore = rawScore;
        this.maxScore = maxScore;
        this.actualScore = actualScore;
        this.indentLevel = indentLevel;
        this.nullified = false;
        this.nullifyReason = "";
    }

    // Getters
    public String getRefId() { return this.refId; }
    public NodeType getType() { return this.type; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getInternalDescription() { return this.internalDescription; }
    public double getWeight() { return this.weight; }
    public double getRawScore() { return this.rawScore; }
    public double getMaxScore() { return this.maxScore; }
    public double getActualScore() { return this.actualScore; }
    public int getIndentLevel() { return this.indentLevel; }
    public boolean isNullified() { return this.nullified; }
    public String getNullifyReason() { return this.nullifyReason; }

    // Setters for mutable properties
    public void setActualScore(double actualScore) { this.actualScore = actualScore; }
    public void setNullified(boolean nullified) { this.nullified = nullified; }
    public void setNullifyReason(String nullifyReason) { this.nullifyReason = nullifyReason; }
}