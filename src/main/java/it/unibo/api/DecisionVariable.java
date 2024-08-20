package it.unibo.api;

/**
 * Defines a wrapper for the decision variables of the problem.
 */
public interface DecisionVariable {

    /**
     * Integer field's setter.
     *
     * @param integer true if is integer.
     */
    void setInteger(boolean integer);

    /**
     * Current value field's setter.
     *
     * @param currentValue the value to set the variable.
     */
    void setCurrentValue(double currentValue);

    /**
     * @return the name of the variable.
     */
    String getName();

    /**
     * @return the index of the variable.
     */
    int getIndex();

    /**
     * @return true if the variable is integer.
     */
    boolean isInteger();

    /**
     * @return the current value of the variable.
     */
    double getCurrentValue();

    /**
     * @return the variable status log.
     */
    String toString();
}
