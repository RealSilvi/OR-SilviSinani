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
        while (this.dualProblem.solve() && Boolean.FALSE.equals(this.dualProblem.isSolutionInteger())) {
            this.dualProblem.solutionToString().ifPresent(System.out::println);


            DecisionVariableImpl decisionVariable = this.findWhichVariableWillBeConstrained();
            System.out.println(decisionVariable);

            this.dualProblem.addCut(
                    Math.ceil(decisionVariable.getCurrentValue()),
                    decisionVariable,
                    false
            );

            this.dualProblem.addCut(
                    Math.floor(decisionVariable.getCurrentValue()),
                    decisionVariable,
                    true
            );

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
