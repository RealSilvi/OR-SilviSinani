package it.unibo.api;

import java.util.List;

/**
 * This interface defines the methods to implement for interacting easily with IBM ILOG CPLEX.
 */
public interface DualProblem {

    String MINIMIZE_SENSE_LABEL = "Minimize";


    /**
     * Adds a new branch cut to the model.
     *
     * @param branchCut the branch that will be added. Note: it must have a bran new id.
     */
    void addBranchCut(BranchCut branchCut);

    /**
     * Delete a branch cut added by {@code addBranchCut}
     *
     * @param branchCut branch to delete.
     */
    void deleteBranchCut(BranchCut branchCut);

    /**
     * Solves the current model updating the current values of the variables.
     */
    boolean solve();

    /**
     * Check if current values of the variables result integer.
     *
     * @return true if all the current values of the variables result integer.
     */
    boolean areCurrentVariablesInteger();

    /**
     * The current value of the objective function.
     *
     * @return the current value of the objective function.
     * Note for impossible infinity is returned as Double.MAX or Double.Min
     */
    double getCurrentSolution();

    /**
     * @return current variables
     */
    List<DecisionVariable> getCurrentValues();

    /**
     * @return current cuts
     */
    List<BranchCut> getCurrentCuts();

    /**
     * @return true if the decision variable passed has a positive coefficient in the objective function.
     */
    boolean doesDecisionVariableGrowTheObjective(DecisionVariable decisionVariable);

    /**
     * @return true if the objective function sense is minimum.
     */
    boolean isMinimumProblem();

    /**
     * Ends and correctly closes the model.
     */
    void endDualProblem();
}
