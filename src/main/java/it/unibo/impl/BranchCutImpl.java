package it.unibo.impl;

public class BranchCutImpl {

    private final int id;
    private final DecisionVariableImpl decisionVariable;
    private final boolean upper;
    private final int bound;

    public BranchCutImpl(int id, DecisionVariableImpl decisionVariable, boolean upper, int bound) {
        this.id = id;
        this.decisionVariable = decisionVariable;
        this.upper = upper;
        this.bound = bound;
    }

    public int getId() {
        return this.id;
    }

    public DecisionVariableImpl getDecisionVariable() {
        return this.decisionVariable;
    }

    public boolean isUpper() {
        return this.upper;
    }

    public int getBound() {
        return this.bound;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Branch cut ");
        out.append(this.getId());
        out.append(" : {\n\t");
        out.append(this.getDecisionVariable());
        out.append("\n\tIs Upper Bound : ");
        out.append(this.isUpper());
        out.append("\n\tBound : ");
        out.append(this.getBound());
        out.append(" \n}");
        return out.toString();
    }

}
