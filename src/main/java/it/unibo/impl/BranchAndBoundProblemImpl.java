package it.unibo.impl;

import it.unibo.api.*;

import java.util.*;

/**
 * Implements an instance of BranchAndBoundProblem.
 */
public class BranchAndBoundProblemImpl implements BranchAndBoundProblem {
    private static final double HALF_INTEGER = 0.5;

    private final DualProblem dualProblemResolver;
    private boolean minimumProblem;
    private int bestSolution;
    private DecisionTree decisionTree;
    private Optional<DecisionTree> solution;

    private int branchCutsCount = 0;

    /**
     * Resolve a PLI by Branch and Bound.
     *
     * @param pathToFile the path to the mps file which contains the PLI problem.
     */
    public BranchAndBoundProblemImpl(String pathToFile) {
        this.dualProblemResolver = new DualProblemImpl(pathToFile);
        this.minimumProblem = this.dualProblemResolver.isMinimumProblem();
        this.bestSolution = this.minimumProblem ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        this.solution = Optional.empty();
        this.resolve();
        this.printSolution();
        this.dualProblemResolver.endDualProblem();
    }

    /**
     * Prints by {@code System.out the solution id present}
     */
    private void printSolution() {
        System.out.println("\n\n||| SOLUTION |||");
        if (this.solution.isPresent()) {
            System.out.println(this.solution.get());
        } else {
            System.out.println("INTEGER SOLUTION NOT FOUND");
        }
    }

    /**
     * Solves the problem by branch and bound algorithm, searching for an integer solution.<br>
     * It works recursively as follows:<br>
     * 1) Solves the current problem.<br>
     * 2) It stops if <br>
     * - solution is integer and is better than current best bound.<br>
     * - all solutions are impossible.<br>
     * 3) The recursive pass consists on creating new problems from the current by adding cuts
     * that delete the current not integer solution.
     * And then calls itself to the new problems.<br><br>
     * Note: <br>
     * The next decision variable 'x' to cut is chose by which<br>(k = x % 1) is closer to 0.5.<br>
     * The next problem to solve is chose by best bound first.
     * The algorithm stores the problems in a binary tree of type {@code DecisionTree}<br>
     */
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

        Optional<DecisionVariable> decisionVariable = findWhichVariableWillBeConstrained();
        if (decisionVariable.isEmpty()) {
            return;
        }

        ArrayList<BranchCut> branches = new ArrayList<>();
        this.branchCutsCount++;
        branches.add(new BranchCutImpl(branchCutsCount, decisionVariable.get(), true, (int) Math.floor(decisionVariable.get().getCurrentValue())));
        this.branchCutsCount++;
        branches.add(new BranchCutImpl(branchCutsCount, decisionVariable.get(), false, (int) Math.ceil(decisionVariable.get().getCurrentValue())));

        branches = new ArrayList<>(sortWhichBranchToSolveFirst(branches));

        for (BranchCut branch : branches) {
            this.dualProblemResolver.addBranchCut(branch);

            this.decisionTree.addChild(branch.getId(), (branchCutsCount == branch.getId()) ? DecisionTreeImpl.RIGHT_DIRECTION : DecisionTreeImpl.LEFT_DIRECTION, this.dualProblemResolver.getCurrentCuts());
            this.decisionTree.getChild(branch.getId()).ifPresent(child -> this.decisionTree = child);

            this.resolve();

            this.decisionTree.getParent().ifPresent(parent -> this.decisionTree = parent);
            this.dualProblemResolver.deleteBranchCut(branch);
        }
    }

    /**
     * @return true if the current solution doesn't generate new problems.
     */
    private boolean isCurrentSolutionBrakingTheBranch() {
        return isCurrentSolutionIsImpossible() ||
                isCurrentSolutionIsInteger() ||
                isCurrentSolutionBoundWorse();
    }

    /**
     * @return true if the current solution is impossible.
     */
    private boolean isCurrentSolutionIsImpossible() {
        return (this.dualProblemResolver.getCurrentSolution() == Double.MAX_VALUE) ||
                (this.dualProblemResolver.getCurrentSolution() == Double.MIN_VALUE);
    }

    /**
     * @return true if the current solution is integer.
     */
    private boolean isCurrentSolutionIsInteger() {
        return (Math.floor(this.dualProblemResolver.getCurrentSolution()) == Math.ceil(this.dualProblemResolver.getCurrentSolution())) &&
                this.dualProblemResolver.areCurrentVariablesInteger();
    }

    /**
     * @return true if the current solution is worse that the best bound.
     */
    private boolean isCurrentSolutionBoundWorse() {
        return (this.dualProblemResolver.getCurrentSolution() > this.bestSolution && this.minimumProblem) ||
                (this.dualProblemResolver.getCurrentSolution() < this.bestSolution && !this.minimumProblem);
    }

    /**
     * @return the variable that cuts will constrain.
     */
    private Optional<DecisionVariable> findWhichVariableWillBeConstrained() {
        return this.dualProblemResolver.getCurrentValues().stream()
                .filter(decisionVariable -> !decisionVariable.isInteger())
                .min(Comparator.comparingDouble(decisionVariable ->
                        Math.abs(
                                decisionVariable.getCurrentValue()
                                        - Math.floor(decisionVariable.getCurrentValue())
                                        - BranchAndBoundProblemImpl.HALF_INTEGER
                        )
                ));
    }

    /**
     * Order by best bound the problems.
     *
     * @param branchCuts the cuts that define the new problems.
     * @return the cuts ordered.
     */
    private List<BranchCut> sortWhichBranchToSolveFirst(List<BranchCut> branchCuts) {
        boolean doesGrowTheObjective = this.dualProblemResolver.doesDecisionVariableGrowTheObjective(branchCuts.get(0).getDecisionVariable());

        ArrayList<BranchCut> sortedBranchByCut = new ArrayList<>(
                branchCuts
                        .stream()
                        .sorted(Comparator.comparingInt(BranchCut::getBound))
                        .toList()
        );

        if ((this.minimumProblem && !doesGrowTheObjective) || (!this.minimumProblem && doesGrowTheObjective)) {
            Collections.reverse(sortedBranchByCut);
        }

        return sortedBranchByCut;
    }
}
