package it.unibo.impl;

import ilog.concert.IloNumVar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

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
        while (true) {
            if (this.dualProblem.solve()) {
                this.dualProblem.solutionToString().ifPresent(System.out::println);
            } else {
                System.err.println("Cannot resolve the model");
            }
            if (this.dualProblem.isSolutionInteger().isPresent() && !this.dualProblem.isSolutionInteger().get()) {
                DecisionVariableImpl decisionVariable = this.findWhichVariableWillBeConstrained();
                System.out.println(decisionVariable);
                this.dualProblem.addSingleConstraint(
                        0,
                        Math.ceil(decisionVariable.getCurrentValue()),
                        decisionVariable.getIndex()
                );
            } else {
                break;
            }
        }
    }

    private DecisionVariableImpl findWhichVariableWillBeConstrained() {
        if (this.dualProblem.getProblemVariables().isPresent()
                && !this.dualProblem.getProblemVariables().get().isEmpty()
        ) {
            ArrayList<DecisionVariableImpl> decisionVariables =
                    new ArrayList<>(this.dualProblem.getProblemVariables().get());

            Optional<DecisionVariableImpl> result = decisionVariables.stream()
                    .filter(decisionVariable -> !decisionVariable.isInteger())
                    .min(Comparator.comparingDouble(decisionVariable0 ->
                            Math.abs(
                                    decisionVariable0.getCurrentValue()
                                            - Math.floor(decisionVariable0.getCurrentValue())
                                            - 0.5
                            )
                    ));

            if (result.isEmpty()) {
                System.err.println("Cannot find the branching variable");
                System.exit(1);
                return null;
            }
            return result.get();
        } else {
            System.err.println("Cannot find the branching variable");
            System.exit(1);
            return null;
        }
    }
}
