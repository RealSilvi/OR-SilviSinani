package it.unibo.api;

/**
 * Defines a wrapper for the cuts.
 */
public interface BranchCut {

    /**
     * @return cut's id.
     */
    int getId();

    /**
     * @return cut's decision variable.
     */
    DecisionVariable getDecisionVariable();

    /**
     * @return true if it is an upper bound.
     */
    boolean isUpper();

    /**
     * @return the value of the bound.
     */
    int getBound();

    /**
     * @return the cut's log.
     */
    @Override
    String toString();

}
