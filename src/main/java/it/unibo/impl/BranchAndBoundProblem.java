package it.unibo.impl;

import java.util.*;

public class BranchAndBoundProblem {

    private final DualProblemImpl dualProblemResolver;
    private boolean minimumProblem;
    private int bestSolution;

    private int branchCutsCount = 0;

    public BranchAndBoundProblem(String pathToFile) {
        this.dualProblemResolver = new DualProblemImpl(pathToFile);
        this.minimumProblem = this.dualProblemResolver.isMinimumProblem();
        this.bestSolution = this.minimumProblem ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        this.resolve();
        this.printSolution();
        this.dualProblemResolver.endDualProblem();
    }

    private void printSolution() {
        System.out.println("\n\nSOLUTION");
        System.out.println("obj = " + bestSolution);

    }

    private void resolve() {
        if (!this.dualProblemResolver.solve()) {
            return;
        }

        System.out.println("\nBest integer bound");
        System.out.println("obj = " + this.bestSolution);

        if (isCurrentSolutionBrakingTheBranch()) {
            if (isCurrentSolutionIsInteger() && !isCurrentSolutionBoundWorse()) {
                this.bestSolution = (int) this.dualProblemResolver.getCurrentSolution();
            }
            return;
        }

        Optional<DecisionVariableImpl> decisionVariable = findWhichVariableWillBeConstrained();
        if (decisionVariable.isEmpty()) {
            return;
        }

        ArrayList<BranchCutImpl> branches = new ArrayList<>();
        branches.add(new BranchCutImpl(branchCutsCount, decisionVariable.get(), true, (int) Math.floor(decisionVariable.get().getCurrentValue())));
        this.branchCutsCount += 1;
        branches.add(new BranchCutImpl(branchCutsCount, decisionVariable.get(), false, (int) Math.ceil(decisionVariable.get().getCurrentValue())));
        this.branchCutsCount += 1;

        branches = new ArrayList<>(sortWhichBranchToSolveFirst(branches));

        for (BranchCutImpl branch : branches) {
            this.dualProblemResolver.addBranchCut(branch);
            this.resolve();
            this.dualProblemResolver.deleteBranchCut(branch.getId());
        }
    }

    private boolean isCurrentSolutionBrakingTheBranch() {
        return isCurrentSolutionIsImpossible() ||
                isCurrentSolutionIsInteger() ||
                isCurrentSolutionBoundWorse();
    }

    private boolean isCurrentSolutionIsImpossible() {
        return (this.dualProblemResolver.getCurrentSolution() == Double.MAX_VALUE) ||
                (this.dualProblemResolver.getCurrentSolution() == Double.MIN_VALUE);
    }

    private boolean isCurrentSolutionIsInteger() {
        return (Math.floor(this.dualProblemResolver.getCurrentSolution()) == Math.ceil(this.dualProblemResolver.getCurrentSolution())) &&
                this.dualProblemResolver.areCurrentVariablesInteger();
    }

    private boolean isCurrentSolutionBoundWorse() {
        return (this.dualProblemResolver.getCurrentSolution() > this.bestSolution && this.minimumProblem) ||
                (this.dualProblemResolver.getCurrentSolution() < this.bestSolution && !this.minimumProblem);
    }

    private Optional<DecisionVariableImpl> findWhichVariableWillBeConstrained() {
        return this.dualProblemResolver.getCurrentValues().stream()
                .filter(decisionVariable -> !decisionVariable.isInteger())
                .min(Comparator.comparingDouble(decisionVariable ->
                        Math.abs(
                                decisionVariable.getCurrentValue()
                                        - Math.floor(decisionVariable.getCurrentValue())
                                        - 0.5
                        )
                ));
    }

    private List<BranchCutImpl> sortWhichBranchToSolveFirst(List<BranchCutImpl> branchCuts) {
        boolean doesGrowTheObjective = this.dualProblemResolver.doesDecisionVariableGrowTheObjective(branchCuts.get(0).getDecisionVariable());

        ArrayList<BranchCutImpl> sortedBranchByCut = new ArrayList<>(
                branchCuts
                        .stream()
                        .sorted(Comparator.comparingInt(BranchCutImpl::getBound))
                        .toList()
        );

        if ((this.minimumProblem && !doesGrowTheObjective) || (!this.minimumProblem && doesGrowTheObjective)) {
            Collections.reverse(sortedBranchByCut);
        }

        return sortedBranchByCut;
    }
}
