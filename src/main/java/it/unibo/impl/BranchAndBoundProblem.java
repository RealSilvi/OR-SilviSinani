package it.unibo.impl;

import java.util.*;

public class BranchAndBoundProblem {

    private final DualProblemImpl dualProblemResolver;
    private boolean minimumProblem;
    private int bestSolution;
    private DecisionTreeImpl decisionTree;
    private Optional<DecisionTreeImpl> solution;

    private int branchCutsCount = 0;

    public BranchAndBoundProblem(String pathToFile) {
        this.dualProblemResolver = new DualProblemImpl(pathToFile);
        this.minimumProblem = this.dualProblemResolver.isMinimumProblem();
        this.bestSolution = this.minimumProblem ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        this.solution = Optional.empty();
        this.resolve();
        this.printSolution();
        this.dualProblemResolver.endDualProblem();
    }

    private void printSolution() {
        System.out.println("\n\n||| SOLUTION |||");
        this.solution.ifPresent(System.out::println);
    }

    private void resolve() {
        if (!this.dualProblemResolver.solve()) {
            this.decisionTree.setBranchProblemSolution(this.minimumProblem ? Double.MAX_VALUE : Double.MIN_VALUE);
            this.decisionTree.setCurrentValues(this.dualProblemResolver.getCurrentValues());
            return;
        }

        if (this.decisionTree == null) {
            this.decisionTree = new DecisionTreeImpl(
                    DecisionTreeImpl.ROOT_ID,
                    this.dualProblemResolver.getCurrentValues(),
                    this.dualProblemResolver.getCurrentCuts(),
                    this.dualProblemResolver.getCurrentSolution()
            );
        } else {
            this.decisionTree.setBranchProblemSolution(this.dualProblemResolver.getCurrentSolution());
            this.decisionTree.setCurrentValues(this.dualProblemResolver.getCurrentValues());
        }

        if (isCurrentSolutionBrakingTheBranch()) {
            if (isCurrentSolutionIsInteger() && !isCurrentSolutionBoundWorse()) {
                this.bestSolution = (int) this.dualProblemResolver.getCurrentSolution();
                this.solution = Optional.of(this.decisionTree);
            }
            return;
        }

        Optional<DecisionVariableImpl> decisionVariable = findWhichVariableWillBeConstrained();
        if (decisionVariable.isEmpty()) {
            return;
        }

        ArrayList<BranchCutImpl> branches = new ArrayList<>();
        this.branchCutsCount++;
        branches.add(new BranchCutImpl(branchCutsCount, decisionVariable.get(), true, (int) Math.floor(decisionVariable.get().getCurrentValue())));
        this.branchCutsCount++;
        branches.add(new BranchCutImpl(branchCutsCount, decisionVariable.get(), false, (int) Math.ceil(decisionVariable.get().getCurrentValue())));

        branches = new ArrayList<>(sortWhichBranchToSolveFirst(branches));

        for (BranchCutImpl branch : branches) {
            this.dualProblemResolver.addBranchCut(branch);

            this.decisionTree.addChild(branch.getId(), (branchCutsCount == branch.getId()) ? DecisionTreeImpl.RIGHT_DIRECTION : DecisionTreeImpl.LEFT_DIRECTION, this.dualProblemResolver.getCurrentCuts());
            this.decisionTree.getChild(branch.getId()).ifPresent(child -> this.decisionTree = child);

            this.resolve();

            this.decisionTree.getParent().ifPresent(parent -> this.decisionTree = parent);
            this.dualProblemResolver.deleteBranchCut(branch);
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
