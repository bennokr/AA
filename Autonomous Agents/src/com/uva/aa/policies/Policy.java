package com.uva.aa.policies;

import java.util.HashMap;
import java.util.Map;

import com.uva.aa.enums.Action;

/**
 * Holds the values and actions with probabilities for all states within a policy.
 */
public class Policy {

    /** The map holding the values actions with probabilities for their state */
    private Map<State, StatePolicyProperties> mStateMap = new HashMap<State, StatePolicyProperties>();

    /**
     * Returns the mapped values and actions with probabilities for their state.
     * 
     * @return The map of states with their properties
     */
    public Map<State, StatePolicyProperties> getStateMap() {
        return mStateMap;
    }

    /**
     * Retrieves the properties for a certain state. Creates a new mapping to default properties if the state wasn't
     * mapped yet.
     * 
     * @param state
     *            The state for which to get the properties
     * 
     * @return The state's properties
     */
    public StatePolicyProperties getProperties(final State state) {
        StatePolicyProperties properties = mStateMap.get(state);

        // Prepare the state if it wasn't mapped yet
        if (properties == null) {
            properties = new StatePolicyProperties();
            mStateMap.put(state, properties);
        }

        return properties;
    }

    /**
     * Retrieves the value of the state. Essentially returns a cached result of V^pi(s), but does not actually perform
     * the value function.
     * 
     * @param state
     *            The state to get the value for
     * 
     * @return The value of the state or a default value if not set
     */
    public double getStateValue(final State state) {
        return getProperties(state).getValue();
    }

    /**
     * Sets the value of the state.
     * 
     * @param state
     *            The state to set the value for
     * @param value
     *            The value of the state
     */
    public void setStateValue(final State state, final double value) {
        getProperties(state).setValue(value);
    }

    /**
     * Retrieves the probability of an action in the given state
     * 
     * @param state
     *            The state to check the action for
     * @param action
     *            The action to find the probability for
     * 
     * @return The value of the state or a default value if not set
     */
    public double getActionProbability(final State state, final Action action) {
        return getProperties(state).getActionProbability(action);
    }

    /**
     * Sets the probability of an action for the given state.
     * 
     * @param state
     *            The state from which the action is performed
     * @param action
     *            The action for which to set the probability
     * @param probability
     *            The probability of the action
     */
    public void setActionProbability(final State state, final Action action, final double probability) {
        getProperties(state).setActionProbability(action, probability);
    }

    /**
     * Returns a random action based on the probability within the given state.
     * 
     * @param state
     *            The state to choose an action for
     * 
     * @return A semi-random action
     */
    public Action getActionBasedOnProbability(final State state) {
        final double decision = Math.random();
        double decisionCount = 0;

        final Map<Action, Double> actionProbabilities = getProperties(state).getActionProbabilities();
        for (final Map.Entry<Action, Double> actionProbability : actionProbabilities.entrySet()) {
            decisionCount += actionProbability.getValue();
            if (decisionCount >= decision) {
                return actionProbability.getKey();
            }
        }

        return null;
    }

    /**
     * Prints out the full mapped contents.
     */
    public void print() {
        for (final Map.Entry<State, StatePolicyProperties> mapping : mStateMap.entrySet()) {
            final StatePolicyProperties properties = mapping.getValue();

            // Print the state
            mapping.getKey().print();
            System.out.println("    State value = " + properties.getValue());

            // Print the actions with their probability
            for (Map.Entry<Action, Double> actionProbability : properties.getActionProbabilities().entrySet()) {
                System.out.println("    Action probability " + actionProbability.getKey() + " = "
                        + actionProbability.getValue());
            }
        }
        System.out.println();
    }
}
