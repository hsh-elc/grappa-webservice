package proforma.util21.format;

import proforma.xml21.*;

import java.util.*;
import java.util.function.BiFunction;
import java.math.BigDecimal;

public class GradingHintsHelper {

    private final GradingHintsType gradingHints;
    private final TestsType tests;
    private final Map<String, GradesNodeType> gradingHintsCombines = new HashMap<>();

    public GradingHintsHelper(GradingHintsType gradingHints, TestsType tests) {
        this.gradingHints = gradingHints;
        this.tests = tests;

        // Populate combine map
        if (gradingHints != null && gradingHints.getCombine() != null) {
            for (GradesNodeType combine : gradingHints.getCombine()) {
                gradingHintsCombines.put(combine.getId(), combine);
            }
        }
    }

    // Calculates max score for the entire grading-hints element
    public double calculateMaxScore() {
        if (gradingHints != null && gradingHints.getRoot() != null) {
            return calculateMaxScoreInternal(gradingHints.getRoot());
        }
        return 1.0; // Default max score if gradingHints is empty
    }

    // Calculates max score starting from the specified node
    public double calculateMaxScore(GradesNodeType node) {
        return calculateMaxScoreInternal(node);
    }

    public void adjustWeights(double factor) {
        if (gradingHints != null && gradingHints.getRoot() != null) {
            adjustWeightsInternal(gradingHints.getRoot(), factor);

            if (gradingHints.getCombine() != null) {
                for (GradesNodeType combineNode : gradingHints.getCombine()) {
                    adjustWeightsInternal(combineNode, factor);
                }
            }
        }
    }

    public boolean isEmpty() {
        if (gradingHints == null || gradingHints.getRoot() == null) {
            return true;
        }
        GradesNodeType root = gradingHints.getRoot();
        return (root.getTestRefOrCombineRef() == null || root.getTestRefOrCombineRef().isEmpty());
    }

    // Private helper methods
    private double calculateMaxScoreInternal(GradesNodeType elem) {
        String function = elem.getFunction();
        double value = 0.0;
        BiFunction<Double, Double, Double> mergeFunc;

        // Determine the merge function based on the "function" attribute
        if ("min".equals(function)) {
            value = Double.MAX_VALUE;
            mergeFunc = Math::min;
        } else if ("max".equals(function)) {
            value = 0.0;
            mergeFunc = Math::max;
        } else if ("sum".equals(function)) {
            mergeFunc = Double::sum;
        } else {
            mergeFunc = Double::sum; // Default merge function
        }

        // Process test-ref and combine-ref
        List<GradesBaseRefChildType> refs = elem.getTestRefOrCombineRef();
        if (refs != null) {
            for (GradesBaseRefChildType ref : refs) {
                if (ref instanceof GradesTestRefChildType) {
                    double weight = ref.getWeight() != null ? ref.getWeight() : 1.0;
                    value = mergeFunc.apply(value, weight);
                } else if (ref instanceof GradesCombineRefChildType combineRef) {
                    GradesNodeType refNode = gradingHintsCombines.get(combineRef.getRef());
                    double maxScore = calculateMaxScoreInternal(refNode);
                    double weight = ref.getWeight() != null ? ref.getWeight() : 1.0;
                    value = mergeFunc.apply(value, maxScore * weight);
                }
            }
        }

        // Handle root node with no children (default max score behavior)
        if ((refs == null || refs.isEmpty()) && "root".equals(elem.getId())) {
            int countTests = tests.getTest() != null ? tests.getTest().size() : 0;
            if (countTests == 0) {
                value = 1.0; // Default score when no tests exist
            } else {
                for (int i = 0; i < countTests; ++i) {
                    value = mergeFunc.apply(value, 1.0);
                }
            }
        }

        return value;
    }

    private void adjustWeightsInternal(GradesNodeType elem, double factor) {
        // Adjust weights in test-ref and combine-ref
        List<GradesBaseRefChildType> refs = elem.getTestRefOrCombineRef();
        if (refs != null) {
            for (GradesBaseRefChildType ref : refs) {
                if (ref instanceof GradesTestRefChildType) {
                    if (ref.getWeight() != null) {
                        ref.setWeight(ref.getWeight() * factor);
                    }
                }

                // Handle nullify-conditions and nullify-condition adjustments
                if (ref.getNullifyConditions() != null) {
                    adjustNullifyConditions(ref.getNullifyConditions(), factor);
                } else if (ref.getNullifyCondition() != null) {
                    adjustNullifyCondition(ref.getNullifyCondition(), factor);
                }
            }
        }
    }

    private void adjustNullifyConditions(GradesNullifyConditionsType nullifyConditions, double factor) {
        for (GradesNullifyBaseType conditionOrConditions : nullifyConditions.getNullifyConditionsOrNullifyCondition()) {
            if (conditionOrConditions instanceof GradesNullifyConditionsType) {
                adjustNullifyConditions((GradesNullifyConditionsType) conditionOrConditions, factor);
            } else if (conditionOrConditions instanceof GradesNullifyConditionType) {
                adjustNullifyCondition((GradesNullifyConditionType) conditionOrConditions, factor);
            }
        }
    }

    private void adjustNullifyCondition(GradesNullifyConditionType nullifyCondition, double factor) {
        // Adjust weight of comparison operands within nullify-condition
        List<GradesNullifyComparisonOperandType> operands = nullifyCondition.getNullifyCombineRefOrNullifyTestRefOrNullifyLiteral();
        for (GradesNullifyComparisonOperandType operand : operands) {
            if (operand instanceof GradesNullifyLiteralType literal) {
                if (literal.getValue() != null) {
                    literal.setValue(literal.getValue().multiply(BigDecimal.valueOf(factor)));
                }
            }
        }
    }

    public GradesNodeType getCombineNode(String refId) {
        return gradingHintsCombines.get(refId);
    }
}




