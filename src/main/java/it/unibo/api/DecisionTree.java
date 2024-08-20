package it.unibo.api;

import java.util.List;
import java.util.Optional;

/**
 * Defines a binary tree for storing the problems, and it's solutions.
 */
public interface DecisionTree {

    int ROOT_ID = 0;
    String LEFT_DIRECTION = "Left";
    String RIGHT_DIRECTION = "Right";

    /**
     * Add a child to this node.
     *
     * @param id        the id of the child. Note: it has to be unique.
     * @param direction indicates in which direction to append the child. If {@code DecisionTree.LEFT} or {@code DecisionTree.RIGHT}
     * @param cuts      the cuts that defined the child from the original problem.
     * @return the child.
     */
    Optional<DecisionTree> addChild(int id, String direction, List<BranchCut> cuts);

    /**
     * Solution field's setter.
     *
     * @param solution indicates the new solution to set this node.
     */
    void setBranchProblemSolution(double solution);

    /**
     * Variables values field's setter.
     *
     * @param values indicates the values to set the deciasion variables.
     */
    void setCurrentValues(List<DecisionVariable> values);

    /**
     * Parent field's setter.
     *
     * @param parent the node to set as parent to this one.
     */
    void setParent(DecisionTree parent);

    /**
     * @return the parent of this node.
     */
    Optional<DecisionTree> getParent();

    /**
     * @return the root node.
     */
    DecisionTree getRootProblem();

    /**
     * @return this id.
     */
    int getId();

    /**
     * Search an adjacent child by id.
     *
     * @param id the child's id.
     * @return the child.
     */
    Optional<DecisionTree> getChild(int id);

    /**
     * Search a node by id in the whole tree.
     *
     * @param id the id of the node.
     * @return Optional of the node if it is found.
     */
    Optional<DecisionTree> findById(int id);

    /**
     * @return an Optional of the left child.
     */
    Optional<DecisionTree> getLeftChild();

    /**
     * @return an Optional of the right child.
     */
    Optional<DecisionTree> getRightChild();

    /**
     * @return the node log.
     */
    String toString();
}
