package com.uva.aa.policies;

import java.util.HashMap;
import java.util.Map;

import com.uva.aa.enums.Action;

/**
 * A bundle for the properties of a state related to a policy.
 */
public class StatePolicyProperties {

    /** The value for the state */
    private double mValue = 0.0;

    /** The probabilities for actions within the state */
    private Map<Action, Double> mActionProbabilities = new HashMap<Action, Double>();

    /**
     * Retrieves the value for the state.
     * 
     * @return The state's value
     */
    public double getValue() {
        return mValue;
    }

    /**
     * Sets the value for the state.
     * 
     * @param value
     *            The state's value
     */
    public void setValue(final double value) {
        mValue = value;
    }

    /**
     * Retrieves the mapping of actions to probabilities
     * 
     * @return The actions' probability mapping
     */
    public Map<Action, Double> getActionProbabilities() {
        return mActionProbabilities;
    }

    /**
     * Retrieves the probability for an action within the state.
     * 
     * @param action
     *            The action to get the probability for
     * 
     * @return The action's probability
     */
    public double getActionProbability(final Action action) {
        final Double val = mActionProbabilities.get(action);
        return (val != null ? val : 0.0);
    }

    /**
     * Sets the probability for an action within the state.
     * 
     * @param action
     *            The action to set the probability for
     * @param probability
     *            The action's probability
     */
    public void setActionProbability(final Action action, final double probability) {
        mActionProbabilities.put(action, probability);
    }

}
