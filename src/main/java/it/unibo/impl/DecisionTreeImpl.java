package it.unibo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DecisionTreeImpl {
    public static final int ROOT_ID = 0;
    public static final String LEFT_DIRECTION = "Left";
    public static final String RIGHT_DIRECTION = "Right";

    private final int id;
    private final BranchProblem branchProblem;
    private Optional<DecisionTreeImpl> parent;
    private Optional<DecisionTreeImpl> leftChild;
    private Optional<DecisionTreeImpl> rightChild;

    public DecisionTreeImpl(int id, List<BranchCutImpl> branchCuts) {
        this.id = id;
        this.parent = Optional.empty();
        this.leftChild = Optional.empty();
        this.rightChild = Optional.empty();
        this.branchProblem = new BranchProblem(branchCuts);
        this.setBranchProblemSolution(Double.MAX_VALUE);
    }

    public DecisionTreeImpl(int id, List<DecisionVariableImpl> decisionVariables, List<BranchCutImpl> branchCuts) {
        this(id, branchCuts);
        this.setCurrentValues(decisionVariables);
    }

    public DecisionTreeImpl(int id, List<DecisionVariableImpl> decisionVariables, List<BranchCutImpl> branchCuts, double solutionValue) {
        this(id, decisionVariables, branchCuts);
        this.setBranchProblemSolution(solutionValue);
    }

    public final Optional<DecisionTreeImpl> addChild(int id, String direction, List<BranchCutImpl> cuts) {
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

    public final void setBranchProblemSolution(double solution) {
        this.branchProblem.setSolutionValue(solution);
    }

    public final void setCurrentValues(List<DecisionVariableImpl> values) {
        this.branchProblem.setDecisionVariables(values);
    }

    public void setParent(DecisionTreeImpl parent) {
        this.parent = Optional.of(parent);
    }

    public Optional<DecisionTreeImpl> getParent() {
        return this.parent;
    }

    public DecisionTreeImpl getRootProblem() {
        DecisionTreeImpl root = this;
        if (root.getId() == ROOT_ID) {
            return root;
        }
        while (root.parent.isPresent()) {
            root = root.parent.get();
        }
        return root;
    }

    public int getId() {
        return this.id;
    }

    public Optional<DecisionTreeImpl> getChild(int id) {
        if (this.rightChild.isPresent() && this.rightChild.get().getId() == id) {
            return this.rightChild;
        }
        if (this.leftChild.isPresent() && this.leftChild.get().getId() == id) {
            return this.leftChild;
        }
        return Optional.empty();
    }

    public Optional<DecisionTreeImpl> findById(int id) {
        DecisionTreeImpl root = this;
        if (root.getId() == id) {
            return Optional.of(root);
        }
        while (root.parent.isPresent()) {

            root = root.parent.get();
        }

        return search(root, id);
    }

    private Optional<DecisionTreeImpl> search(DecisionTreeImpl root, int key) {
        Optional<DecisionTreeImpl> result = Optional.empty();

        if (root.getId() == key) {
            return Optional.of(root);
        }

        if (root.leftChild.isPresent()) {
            result = search(root.leftChild.get(), key);
        }

        if (root.rightChild.isPresent() && result.isEmpty()) {
            result = search(root.rightChild.get(), key);
        }
        return result;
    }

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

    private static class BranchProblem {
        private double solutionValue;
        private final List<DecisionVariableImpl> decisionVariables;
        private final List<BranchCutImpl> branchCuts;

        public BranchProblem(List<BranchCutImpl> branchCuts) {
            this.decisionVariables = new ArrayList<>();
            this.branchCuts = branchCuts;
        }

        public void setDecisionVariables(List<DecisionVariableImpl> decisionVariables) {
            this.decisionVariables.addAll(decisionVariables);
        }

        public void setSolutionValue(double solutionValue) {
            this.solutionValue = solutionValue;
        }

        public double getSolutionValue() {
            return this.solutionValue;
        }

        public List<DecisionVariableImpl> getDecisionVariables() {
            return this.decisionVariables;
        }

        public List<BranchCutImpl> getBranchCuts() {
            return this.branchCuts;
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append("BRANCH PROBLEM");
            out.append("\n\nSolution");
            out.append("\nValue = ");
            out.append(this.getSolutionValue());

            out.append("\n\nDecision Variables");
            for (DecisionVariableImpl decisionVariable : this.getDecisionVariables()) {
                out.append("\n");
                out.append(decisionVariable);
            }
            out.append("\n\nCuts");
            for (BranchCutImpl cut : this.getBranchCuts()) {
                out.append("\n");
                out.append(cut);
            }
            return out.toString();
        }
    }
}
