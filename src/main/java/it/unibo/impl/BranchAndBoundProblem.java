package it.unibo.impl;

import ilog.concert.IloNumVar;

import java.util.Arrays;

public class BranchAndBoundProblem {

    private final DualProblemImpl dualProblem;
    private boolean minimumProblem;

    public BranchAndBoundProblem(boolean minimumProblem) {
        this.minimumProblem = minimumProblem;
        this.dualProblem = new DualProblemImpl(minimumProblem);
    }

    public BranchAndBoundProblem(String pathToFile) {
        this.minimumProblem = true;
        this.dualProblem = new DualProblemImpl(pathToFile, minimumProblem);
        this.resolve();

    }

    private void resolve() {
        do {
            if (this.dualProblem.solve()) {
                this.dualProblem.solutionToString().ifPresent(System.out::println);
            } else {
                System.err.println("Cannot resolve the model");
            }
            if (this.dualProblem.isSolutionInteger().isPresent() && !this.dualProblem.isSolutionInteger().get()) {
                IloNumVar decisionVariable = this.findWhichVariableWillBeConstrained();
            } else {
                break;
            }

        } while (true);
    }

    private IloNumVar findWhichVariableWillBeConstrained() {
        if (this.dualProblem.getVariables().isPresent()
                && this.dualProblem.getVariableValues().isPresent()
                && this.dualProblem.getVariables().get().length != 0
                && this.dualProblem.getVariableValues().get().length != 0
        ) {
            IloNumVar[] vars = this.dualProblem.getVariables().get();
            double[] values = this.dualProblem.getVariableValues().get();
//           trova la logica di quale scegliere per il branching cosi trovi la var e riprendi

            return null;
        } else {
            System.err.println("Cannot find the variable");
            System.exit(1);
            return null;
        }
    }
}
