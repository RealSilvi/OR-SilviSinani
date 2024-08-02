package it.unibo.impl;

import ilog.concert.*;
import ilog.opl.IloCplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DualProblemImpl {
    private static final String MINIMIZE_SENSE_LABEL = "Minimize";

    private boolean isMinimumProblem;
    private IloCplex cplex;
    private IloObjective objectiveFunction;
    private final ArrayList<DecisionVariableImpl> currentValues = new ArrayList<>();
    private final Map<Integer, IloRange> currentCuts = new HashMap<>();

    public DualProblemImpl(String absolutePathToFile) {
        try {
            if (!new File(absolutePathToFile).exists()) {
                throw new FileNotFoundException();
            }
            this.cplex = new IloCplex();
            this.setDualAlgorithm();
            this.setQuiet();

            this.cplex.importModel(absolutePathToFile);
            this.objectiveFunction = this.cplex.getObjective();
            this.isMinimumProblem = MINIMIZE_SENSE_LABEL.equals(this.objectiveFunction.getSense().toString());

            IloNumVar[] vars = this.cplex.getMatrix().getNumVars();
            for (int i = 0; i < vars.length; i++) {
                this.currentValues.add(
                        new DecisionVariableImpl(
                                vars[i].getName(),
                                i
                        ));
            }


            this.printProblem("Original problem", this.cplex.getMatrix(), this.objectiveFunction);

//            this.printCurrentVariables();
//            this.solve();
//            this.printCurrentVariables();
//
//            this.addBranchCut(new BranchCutImpl(1, this.currentValues.get(0), false, 3));
//            this.solve();
//            this.printCurrentVariables();
//
//            this.deleteBranchCut(1);
//            this.solve();
//            this.printCurrentVariables();

        } catch (IloException e) {
            System.err.println("Failed to instance the cplex model " + e);
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("File not found" + e);
            System.exit(1);
        }
    }

    /**
     * Adds a new branch cut to the model.
     *
     * @param branchCut the branch that will be added. Note: it must have a bran new id.
     */
    public final void addBranchCut(BranchCutImpl branchCut) {
        try {
            IloLinearNumExpr cut = this.cplex.linearNumExpr();
            cut.addTerm(1, this.cplex.getMatrix().getNumVar(branchCut.getDecisionVariable().getIndex()));

            IloRange newConstraint = branchCut.isUpper() ?
                    this.cplex.le(cut, branchCut.getBound()) :
                    this.cplex.ge(cut, branchCut.getBound());

            System.out.println("\n\nAdding new constraint: " + newConstraint.toString());

            this.currentCuts.put(branchCut.getId(), newConstraint);
            this.cplex.add(newConstraint);
        } catch (IloException e) {
            System.err.println("File to add constraint" + e);
            System.exit(1);
        }
    }

    /**
     * Delete a branch cut added by {@code addBranchCut}
     *
     * @param branchCutId branch id.
     */
    public final void deleteBranchCut(int branchCutId) {
        try {
            System.out.println("\n\nDeleting constraint: " + this.currentCuts.get(branchCutId).toString());
            this.cplex.remove(this.currentCuts.get(branchCutId));
            this.currentCuts.remove(branchCutId);
        } catch (IloException e) {
            System.err.println("File to add constraint" + e);
            System.exit(1);
        }
    }

    /**
     * Solves the current model updating the current values of the variables.
     */
    public final boolean solve() {
        try {
            if (this.cplex.solve()) {
                this.updateCurrentValues();
                this.printSolution(this.cplex);
                return true;
            }
            return false;
        } catch (IloException e) {
            System.err.println("Filed to solve" + e);
            System.exit(1);
        }
        return false;
    }


    /**
     * Prints by {@code System.out} the given problem.
     *
     * @param problemName       the name of the given problem.
     * @param problemTableau    the matrix describing the problem.
     * @param objectiveFunction the objective function of the problem
     */
    public final void printProblem(String problemName, IloLPMatrix problemTableau, IloObjective objectiveFunction) {
        StringBuilder status = new StringBuilder();
        try {
            status.append("\n\n");
            status.append(problemName.toUpperCase());
            status.append("\n\nVariables");
            IloNumVar[] decisionVariables = problemTableau.getNumVars();
            for (IloNumVar decisionVariable : decisionVariables) {
                status.append("\n");
                status.append(decisionVariable.getLB());
                status.append(" < ");
                status.append(decisionVariable.getName());
                status.append(" < ");
                status.append(decisionVariable.getUB());
            }

            status.append("\n\nConstraints");
            IloRange[] constraints = problemTableau.getRanges();
            for (IloRange constraint : constraints) {
                status.append("\n");
                status.append(constraint.getName());
                status.append(" : ");
                status.append(constraint.getLB());
                status.append(" <= ");
                status.append(constraint.getExpr().toString());
                status.append(" <= ");
                status.append(constraint.getUB());
            }

            status.append("\n\nObjective");
            status.append("\n");
            status.append(this.isMinimumProblem ? "min " : "max ");
            status.append(objectiveFunction.getName());
            status.append(" : ");
            status.append(objectiveFunction.getExpr().toString());

            System.out.println(status);
        } catch (IloException e) {
            System.err.println("\n\nFailed to print the problem.");
            System.exit(1);
        }

    }

    /**
     * Prints by {@code System.out} the given problem.
     *
     * @param cplex
     */
    public final void printSolution(IloCplex cplex) {
        StringBuilder status = new StringBuilder();
        try {
            status.append("\n\nSOLUTION");
            status.append("\n\nInfo ");
            status.append("\nSolution status = ");
            status.append(cplex.getStatus());
            status.append("\nSolution value  = ");
            status.append(cplex.getObjValue());

            status.append("\nSolution result integer  = ");
            status.append(this.areCurrentVariablesInteger());

            status.append("\n\nVariables");
            for (IloNumVar decisionVariable : this.cplex.getMatrix().getNumVars()) {
                status.append("\n");
                status.append(decisionVariable.getName());
                status.append(" => {[ Value: ");
                status.append(cplex.getValue(decisionVariable));
                status.append(" ], [ Status: ");
                status.append(cplex.getBasisStatus(decisionVariable));
                status.append(" ], [ Reduced cost: ");
                status.append(cplex.getReducedCost(decisionVariable));
                status.append(" ]}");
            }

            status.append("\n\nConstraints");
            for (IloRange constraint : this.cplex.getMatrix().getRanges()) {
                status.append("\n");
                status.append(constraint.getName());
                status.append(" => {[ Slack: ");
                status.append(cplex.getSlack(constraint));
                status.append(" ], [ Pi: ");
                status.append(cplex.getDual(constraint));
                status.append(" ]}");
            }

            for (Map.Entry<Integer, IloRange> constraint : this.currentCuts.entrySet()) {
                status.append("\nbranchCut");
                status.append(constraint.getKey().toString());
                status.append(" => {[ Slack: ");
                status.append(cplex.getSlack(constraint.getValue()));
                status.append(" ], [ Pi: ");
                status.append(cplex.getDual(constraint.getValue()));
                status.append(" ]}");
            }

            System.out.println(status);
        } catch (IloException e) {
            System.err.println("Failed to print the solution");
            System.exit(1);
        }
    }

    /**
     * Prints by {@code System.out} the current values of the model.
     */
    public final void printCurrentVariables() {
        StringBuilder status = new StringBuilder();
        status.append("\n\nCURRENT VALUES OF VARIABLES");
        for (DecisionVariableImpl decisionVariable : this.currentValues) {
            status.append("\n");
            status.append(decisionVariable.toString());
        }
        System.out.println(status);
    }

//    public final void endDualProblem() {
//        this.cplex.end();
//    }
//
//
//    public Optional<Double> getOptimalValue() {
//        try {
//            return Optional.of(this.cplex.getObjValue());
//        } catch (IloException e) {
//            System.err.println("Failed to retrieve the solution");
//            return Optional.empty();
//        }
//    }
//
//    public Optional<List<DecisionVariableImpl>> getProblemVariables() {
//        try {
//            this.updateProblemVariables();
//            return Optional.of(this.problemVariables);
//        } catch (IloException e) {
//            System.err.println("Failed to get the variables");
//            return Optional.empty();
//        }
//    }
//
//    private Optional<double[]> getVariableValues() {
//        if (this.getVariables().isEmpty() || this.getVariables().get().length == 0) {
//            return Optional.empty();
//        }
//        try {
//            return Optional.of(this.cplex.getValues(this.getVariables().get()));
//        } catch (IloException e) {
//            System.err.println("Failed to retrieve the variables");
//            return Optional.empty();
//        }
//    }
//
//    private Optional<IloNumVar[]> getVariables() {
//        try {
//            return Optional.of(this.tableau.getNumVars());
//        } catch (IloException e) {
//            System.err.println("Failed to retrieve the variables");
//            return Optional.empty();
//        }
//    }
//
//    private Optional<Boolean> isOptimal() throws IloException {
//        try {
//            return Optional.of(this.cplex.getStatus() == IloCplex.Status.Optimal);
//        } catch (IloException e) {
//            System.err.println("Failed to check if the solution is optimal");
//            return Optional.empty();
//        }
//    }
//

    /**
     * Check if current values of the variables result integer.
     *
     * @return true if all the current values of the variables result integer.
     */
    public final Boolean areCurrentVariablesInteger() {
        for (DecisionVariableImpl decisionVariable : this.currentValues) {
            if (!decisionVariable.isInteger()) {
                return false;
            }
        }
        return true;
    }

    /**
     * The current value of the objective function.
     *
     * @return the current value of the objective function.
     * Note for impossible infinity is returned as Double.MAX or Double.Min
     */
    public final double getCurrentSolution() {
        try {
            return this.cplex.getObjValue();
        } catch (IloException e) {
            return this.isMinimumProblem ? Double.MAX_VALUE : Double.MIN_VALUE;
        }
    }

    /**
     * @return current variables
     */
    public final List<DecisionVariableImpl> getCurrentValues() {
        return this.currentValues;
    }

    /**
     * @return true if the decision variable passed has positive coefficient in the objective function.
     */
    public final boolean doesDecisionVariableGrowTheObjective(DecisionVariableImpl decisionVariable) {
        try {
            if (this.objectiveFunction.getExpr() instanceof IloLinearNumExpr linearObjectiveFunction) {
                IloLinearNumExprIterator it = linearObjectiveFunction.linearIterator();
                while (it.hasNext()) {
                    if (it.nextNumVar().getName().equals(decisionVariable.getName())) {
                        return it.getValue() > 0;
                    }
                }
            }

            System.err.println("Failed to retrive decision variable's coefficient from the objective");
            System.exit(1);
            return false;
        } catch (IloException e) {
            System.err.println("Failed to retrive decision variable's coefficient from the objective");
            System.exit(1);
            return false;
        }
    }

    /**
     * @return true if the objective function sense is minimum.
     */
    public boolean isMinimumProblem() {
        return this.isMinimumProblem;
    }

    /**
     * Updates the current values of the variables from last solution.
     *
     * @throws IloException
     */
    private void updateCurrentValues() throws IloException {
        IloNumVar[] vars = this.cplex.getMatrix().getNumVars();
        for (int i = 0; i < vars.length; i++) {
            DecisionVariableImpl problemVariable = this.currentValues.get(i);
            problemVariable.setCurrentValue(cplex.getValue(vars[i]));
        }
    }


    /**
     * Activate only the dual algorithm.
     *
     * @throws IloException
     */
    private void setDualAlgorithm() throws IloException {
        this.cplex.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Dual);
    }

    /**
     * Turn off presolve and logging.
     *
     * @throws IloException
     */
    private void setQuiet() throws IloException {
        this.cplex.setParam(IloCplex.Param.Preprocessing.Presolve, false);
        this.cplex.setOut(null);
    }
}
