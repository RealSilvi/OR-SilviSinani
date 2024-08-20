package it.unibo.impl;

import it.unibo.api.DecisionVariable;

/**
 * This class is a wrapper for the problem variables from IloCplex.
 */
public class DecisionVariableImpl implements DecisionVariable {
    private final String name;
    private final int index;
    private double currentValue;
    private boolean integer;

    /**
     * Define an instance by name and index.
     *
     * @param name  the name of the variable.
     * @param index the index of the variable.
     */
    public DecisionVariableImpl(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Define an instance by name, index, and value.
     *
     * @param name         the name of the variable.
     * @param index        the index of the variable.
     * @param currentValue the value of the variable.
     */
    public DecisionVariableImpl(String name, int index, double currentValue) {
        this(name, index);
        this.setCurrentValue(currentValue);
    }

    /**
     * {@inheritDoc}
     */
    public void setInteger(boolean integer) {
        this.integer = integer;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        this.setInteger(Math.ceil(currentValue) == Math.floor(currentValue));
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public int getIndex() {
        return index;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInteger() {
        return integer;
    }

    /**
     * {@inheritDoc}
     */
    public double getCurrentValue() {
        return currentValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Decision Variable " +
                this.getName() +
                " : [ " +
                "index = " +
                this.getIndex() +
                ", value = " +
                this.getCurrentValue() +
                ", isInteger = " +
                this.isInteger() +
                " ]";
    }
}
