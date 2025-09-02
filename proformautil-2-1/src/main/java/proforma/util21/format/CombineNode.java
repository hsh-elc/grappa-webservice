package proforma.util21.format;

import java.util.List;
import java.util.ArrayList;

public class CombineNode extends GradingNode {
    private final String function;
    private final List<GradingNode> children;
    private boolean nullificationChecked;

    public CombineNode(
        String refId, String title, String description, String internalDescription,
        double weight, double rawScore, double maxScore, double actualScore, int indentLevel,
        String function, List<GradingNode> children
    ) {
        super(refId, NodeType.COMBINE, title, description, internalDescription, weight, rawScore, maxScore, actualScore, indentLevel);
        this.function = function;
        this.children = new ArrayList<>(children);
        this.nullificationChecked = false;
    }

    public String getFunction() { return function; }
    public List<GradingNode> getChildren() { return new ArrayList<>(children); }
    public boolean isNullificationChecked() { return nullificationChecked; }

    public void setNullificationChecked(boolean nullificationChecked) { this.nullificationChecked = nullificationChecked; }

    public void addChild(GradingNode child) {
        this.children.add(child);
    }
}

