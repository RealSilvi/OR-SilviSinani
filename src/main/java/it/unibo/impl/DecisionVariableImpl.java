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
        StringBuilder out = new StringBuilder();
        out.append("Decision Variable ");
        out.append(this.getName());
        out.append(" : [ ");
        out.append("index = ");
        out.append(this.getIndex());
        out.append(", value = ");
        out.append(this.getCurrentValue());
        out.append(", isInteger = ");
        out.append(this.isInteger());
        out.append(" ]");
        return out.toString();
    }

    public void setInteger(boolean integer) {
        this.integer = integer;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        this.setInteger(Math.ceil(currentValue) == Math.floor(currentValue));
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
