package it.unibo.impl;

import it.unibo.api.BranchCut;
import it.unibo.api.DecisionVariable;

/**
 * Implements a wrapper for cuts.
 */
public class BranchCutImpl implements BranchCut {

    private final int id;
    private final DecisionVariable decisionVariable;
    private final boolean upper;
    private final int bound;

    /**
     * Define a cut.
     *
     * @param id               the id of the cut. Note:must be unique.
     * @param decisionVariable the decision variable which is cut.
     * @param upper            true if the cut set an upper bound.
     * @param bound            the value of the bound.
     */
    public BranchCutImpl(int id, DecisionVariable decisionVariable, boolean upper, int bound) {
        this.id = id;
        this.decisionVariable = decisionVariable;
        this.upper = upper;
        this.bound = bound;
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    public DecisionVariable getDecisionVariable() {
        return this.decisionVariable;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUpper() {
        return this.upper;
    }

    /**
     * {@inheritDoc}
     */
    public int getBound() {
        return this.bound;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Branch cut " +
                this.getId() +
                " : {\n\t" +
                this.getDecisionVariable() +
                "\n\tIs Upper Bound : " +
                this.isUpper() +
                "\n\tBound : " +
                this.getBound() +
                " \n}";
    }

}
