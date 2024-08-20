package it.unibo.impl;

import it.unibo.api.BranchCut;
import it.unibo.api.DecisionTree;
import it.unibo.api.DecisionVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements a DecisionTree for storing problems in a dataset.
 */
public class DecisionTreeImpl implements DecisionTree {
    private final int id;
    private final BranchProblemImpl branchProblem;
    private Optional<DecisionTree> parent;
    private Optional<DecisionTree> leftChild;
    private Optional<DecisionTree> rightChild;

    /**
     * Define a node by id and cuts. Note: the id must be unique.
     *
     * @param id         the id of the node.
     * @param branchCuts the cuts of the node.
     */
    public DecisionTreeImpl(int id, List<BranchCut> branchCuts) {
        this.id = id;
        this.parent = Optional.empty();
        this.leftChild = Optional.empty();
        this.rightChild = Optional.empty();
        this.branchProblem = new BranchProblemImpl(branchCuts);
        this.setBranchProblemSolution(Double.MAX_VALUE);
    }

    /**
     * Define a node by id, variables, and cuts. Note: the id must be unique.
     *
     * @param id                the id of the node.
     * @param decisionVariables the variables of the node.
     * @param branchCuts        the cuts of the node.
     */
    public DecisionTreeImpl(int id, List<DecisionVariable> decisionVariables, List<BranchCut> branchCuts) {
        this(id, branchCuts);
        this.setCurrentValues(decisionVariables);
    }

    /**
     * Define a node by id, variables, cuts, and solution value. Note: the id must be unique.
     *
     * @param id                the id of the node.
     * @param decisionVariables the variables of the node.
     * @param branchCuts        the cuts of the node.
     * @param solutionValue     the solution value of the node.
     */
    public DecisionTreeImpl(int id, List<DecisionVariable> decisionVariables, List<BranchCut> branchCuts, double solutionValue) {
        this(id, decisionVariables, branchCuts);
        this.setBranchProblemSolution(solutionValue);
    }

    /**
     * {@inheritDoc}
     */
    public final Optional<DecisionTree> addChild(int id, String direction, List<BranchCut> cuts) {
        switch (direction) {
            case RIGHT_DIRECTION -> {
                this.rightChild = Optional.of(new DecisionTreeImpl(id, cuts));
                this.rightChild.ifPresent(decisionTree -> decisionTree.setParent(this));
                return this.rightChild;
            }
            case LEFT_DIRECTION -> {
                this.leftChild = Optional.of(new DecisionTreeImpl(id, cuts));
                this.leftChild.ifPresent(decisionTree -> decisionTree.setParent(this));
                return this.leftChild;
            }
            default -> {
                return Optional.empty();
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public final void setBranchProblemSolution(double solution) {
        this.branchProblem.setSolutionValue(solution);
    }

    /**
     * {@inheritDoc}
     */
    public final void setCurrentValues(List<DecisionVariable> values) {
        this.branchProblem.setDecisionVariables(values);
    }

    /**
     * {@inheritDoc}
     */
    public void setParent(DecisionTree parent) {
        this.parent = Optional.of(parent);
    }

    /**
     * {@inheritDoc}
     */
    public Optional<DecisionTree> getParent() {
        return this.parent;
    }

    /**
     * {@inheritDoc}
     */
    public DecisionTree getRootProblem() {
        DecisionTree root = this;
        if (root.getId() == ROOT_ID) {
            return root;
        }
        while (root.getParent().isPresent()) {
            root = root.getParent().get();
        }
        return root;
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    public Optional<DecisionTree> getChild(int id) {
        if (this.rightChild.isPresent() && this.rightChild.get().getId() == id) {
            return this.rightChild;
        }
        if (this.leftChild.isPresent() && this.leftChild.get().getId() == id) {
            return this.leftChild;
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    public Optional<DecisionTree> findById(int id) {
        DecisionTree root = this;
        if (root.getId() == id) {
            return Optional.of(root);
        }
        while (root.getParent().isPresent()) {
            root = root.getParent().get();
        }
        return search(root, id);
    }

    /**
     * {@inheritDoc}
     */
    public Optional<DecisionTree> getLeftChild() {
        return this.leftChild;
    }

    /**
     * {@inheritDoc}
     */
    public Optional<DecisionTree> getRightChild() {
        return this.rightChild;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "*** DECISION TREE ***\n" +
                "\nId = " +
                id +
                "\nParentId = " +
                (this.parent.isPresent() ? this.parent.get().getId() : "NIL") +
                "\nLeftChildId = " +
                (this.leftChild.isPresent() ? this.leftChild.get().getId() : "NIL") +
                "\nRightChildId = " +
                (this.rightChild.isPresent() ? this.rightChild.get().getId() : "NIL") +
                "\n\n" +
                this.branchProblem +
                "\n\n*** EOF ***";
    }

    /**
     * Search a node from the given root.
     *
     * @param root indicates the starting point of the searching.
     * @param id   the id to search.
     * @return an Optional of the node if it's found.
     */
    private Optional<DecisionTree> search(DecisionTree root, int id) {
        Optional<DecisionTree> result = Optional.empty();

        if (root.getId() == id) {
            return Optional.of(root);
        }

        if (root.getLeftChild().isPresent()) {
            result = search(root.getLeftChild().get(), id);
        }

        if (root.getRightChild().isPresent() && result.isEmpty()) {
            result = search(root.getRightChild().get(), id);
        }
        return result;
    }


    /**
     * Defines a wrapper for the problem data saved in the single node of the tree.
     */
    private static class BranchProblemImpl {
        private double solutionValue;
        private final List<DecisionVariable> decisionVariables;
        private final List<BranchCut> branchCuts;

        /**
         * Define a BranchProblem by cuts.
         *
         * @param branchCuts the cuts that define the problem.
         */
        public BranchProblemImpl(List<BranchCut> branchCuts) {
            this.decisionVariables = new ArrayList<>();
            this.branchCuts = branchCuts;
        }

        /**
         * Set the decision variables of the current problem.
         *
         * @param decisionVariables the variables that define the problem.
         */
        public void setDecisionVariables(List<DecisionVariable> decisionVariables) {
            this.decisionVariables.addAll(decisionVariables);
        }

        /**
         * Set the solution value of the problem.
         *
         * @param solutionValue the value to set the solution of the problem.
         */
        public void setSolutionValue(double solutionValue) {
            this.solutionValue = solutionValue;
        }

        /**
         * @return the solution value of this problem.
         */
        public double getSolutionValue() {
            return this.solutionValue;
        }

        /**
         * @return the decision variables of this problem.
         */
        public List<DecisionVariable> getDecisionVariables() {
            return this.decisionVariables;
        }

        /**
         * @return the cuts of this problem.
         */
        public List<BranchCut> getBranchCuts() {
            return this.branchCuts;
        }

        /**
         * @return the problem log.
         */
        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append("BRANCH PROBLEM");
            out.append("\n\nSolution");
            out.append("\nValue = ");
            out.append(this.getSolutionValue());

            out.append("\n\nDecision Variables");
            for (DecisionVariable decisionVariable : this.getDecisionVariables()) {
                out.append("\n");
                out.append(decisionVariable);
            }
            out.append("\n\nCuts");
            for (BranchCut cut : this.getBranchCuts()) {
                out.append("\n");
                out.append(cut);
            }
            return out.toString();
        }
    }
}
