package it.unibo.impl;

public class DecisionVariableImpl {
    private final String name;
    private final int index;
    private double currentValue;
    private boolean integer;

    public DecisionVariableImpl(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public DecisionVariableImpl(String name, int index, double currentValue) {
        this(name, index);
        this.setCurrentValue(currentValue);
    }

    @Override
    public String toString() {
        return "DecisionVariableImpl{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", currentValue=" + currentValue +
                ", integer=" + integer +
                '}';
    }

    public void setInteger(boolean integer) {
        this.integer = integer;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        if (Math.ceil(currentValue) == currentValue) {
            this.setInteger(true);
        }
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isInteger() {
        return integer;
    }

    public double getCurrentValue() {
        return currentValue;
    }
}
