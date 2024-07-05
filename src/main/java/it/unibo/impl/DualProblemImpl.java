package it.unibo.impl;

import ilog.concert.*;
import ilog.opl.IloCplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DualProblemImpl {

    private IloCplex cplex;
    private boolean minimumProblem;
    private IloLPMatrix tableau;
    private final ArrayList<DecisionVariableImpl> problemVariables = new ArrayList<>();

    public DualProblemImpl(boolean minimumProblem) {
        try {
            this.minimumProblem = minimumProblem;
            this.cplex = new IloCplex();
            this.setDualAlgorithm();
            this.setQuiet();
        } catch (IloException e) {
            System.err.println("Failed to instance the cplex model " + e);
            System.exit(1);
        }
    }

    public DualProblemImpl(String absolutePathToFile, boolean minimumProblem) {
        this(minimumProblem);
        try {
            if (!new File(absolutePathToFile).exists()) {
                throw new FileNotFoundException();
            }
            this.cplex.importModel(absolutePathToFile);
            this.tableau = this.cplex.getMatrix();

            IloNumVar[] vars = this.tableau.getNumVars();
            for (int i = 0; i < vars.length; i++) {
                this.problemVariables.add(
                        new DecisionVariableImpl(
                                vars[i].getName(),
                                i
                        ));
            }

        } catch (IloException e) {
            System.err.println("Failed to instance the cplex model " + e);
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("File not found" + e);
            System.exit(1);
        }
    }

    public final void addCut(double bound, DecisionVariableImpl decisionVariable, boolean upper) {
        try {
            IloLinearNumExpr cut = this.cplex.linearNumExpr();
            cut.addTerm(1, this.tableau.getNumVar(decisionVariable.getIndex()));
            IloConstraint newConstraint;

            if (upper) {
                newConstraint = this.cplex.addLe(cut, bound);
            } else {
                newConstraint = this.cplex.addGe(cut, bound);
            }

            System.out.println("New Constraint: " + newConstraint);

            this.tableau = this.cplex.getMatrix();

        } catch (IloException e) {
            System.err.println("File to add constraint" + e);
            System.exit(1);
        }
    }

    public final void endDualProblem() {
        this.cplex.end();
    }

    public final boolean solve() {
        try {
            if (this.cplex.solve()) {
                this.updateProblemVariables();
                return true;
            }
            return false;

        } catch (IloException e) {
            System.err.println("Filed to solve" + e);
            System.exit(1);
        }
        return false;
    }

    public final Optional<String> solutionToString() {
        StringBuilder status = new StringBuilder();
        try {
            status.append("\n\n");
            status.append("SOLUTION");
            status.append('\n');
            status.append("Solution status = ");
            status.append(cplex.getStatus());
            status.append('\n');
            status.append("Solution value  = ");
            status.append(cplex.getObjValue());

            status.append('\n');
            status.append("Solution result integer  = ");
            status.append(this.isSolutionInteger());
            status.append('\n');

            IloNumVar[] decisionVariables = this.tableau.getNumVars();
            IloRange[] constraints = this.tableau.getRanges();

            status.append("\n\n");
            status.append("VARIABLES");
            status.append("\n");
            for (IloNumVar decisionVariable : decisionVariables) {
                status.append(decisionVariable.getName());
                status.append(" => {[ Value: ");
                status.append(cplex.getValue(decisionVariable));
                status.append(" ], [ Status: ");
                status.append(cplex.getBasisStatus(decisionVariable));
                status.append(" ], [ Reduced cost: ");
                status.append(cplex.getReducedCost(decisionVariable));
                status.append(" ]}");
                status.append("\n");
            }

            status.append("\n\n");
            status.append("CONSTRAINTS");
            status.append("\n");
            for (IloRange constraint : constraints) {
                status.append(constraint.getName());
                status.append(" => {[ Slack: ");
                status.append(cplex.getSlack(constraint));
                status.append(" ], [ Pi: ");
                status.append(cplex.getDual(constraint));
                status.append(" ]}");
                status.append("\n");
            }

            return Optional.of(status.toString());
        } catch (IloException e) {
            System.err.println("Failed to convert the solution on string");
            return Optional.empty();
        }
    }

    public Optional<Double> getOptimalValue() {
        try {
            return Optional.of(this.cplex.getObjValue());
        } catch (IloException e) {
            System.err.println("Failed to retrieve the solution");
            return Optional.empty();
        }
    }

    public Optional<List<DecisionVariableImpl>> getProblemVariables() {
        try {
            this.updateProblemVariables();
            return Optional.of(this.problemVariables);
        } catch (IloException e) {
            System.err.println("Failed to get the variables");
            return Optional.empty();
        }
    }

    private Optional<double[]> getVariableValues() {
        if (this.getVariables().isEmpty() || this.getVariables().get().length == 0) {
            return Optional.empty();
        }
        try {
            return Optional.of(this.cplex.getValues(this.getVariables().get()));
        } catch (IloException e) {
            System.err.println("Failed to retrieve the variables");
            return Optional.empty();
        }
    }

    private Optional<IloNumVar[]> getVariables() {
        try {
            return Optional.of(this.tableau.getNumVars());
        } catch (IloException e) {
            System.err.println("Failed to retrieve the variables");
            return Optional.empty();
        }
    }

    private Optional<Boolean> isOptimal() throws IloException {
        try {
            return Optional.of(this.cplex.getStatus() == IloCplex.Status.Optimal);
        } catch (IloException e) {
            System.err.println("Failed to check if the solution is optimal");
            return Optional.empty();
        }
    }

    public final Boolean isSolutionInteger() {

        for (DecisionVariableImpl decisionVariable : this.problemVariables) {
            if (!decisionVariable.isInteger()) {
                return false;
            }
        }
        return true;
    }

    private void updateProblemVariables() throws IloException {
        IloNumVar[] vars = this.tableau.getNumVars();
        for (int i = 0; i < vars.length; i++) {
            DecisionVariableImpl problemVariable = this.problemVariables.get(i);
            problemVariable.setCurrentValue(cplex.getValue(vars[i]));
        }
    }

    private void setDualAlgorithm() throws IloException {
        this.cplex.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Dual);
    }

    private void setQuiet() throws IloException {
        // turn off presolve to prevent it from completely solving the model
        // before entering the actual LP optimizer
        this.cplex.setParam(IloCplex.Param.Preprocessing.Presolve, false);
        // turn off logging
        this.cplex.setOut(null);
    }
}
