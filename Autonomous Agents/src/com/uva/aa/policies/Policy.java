package com.uva.aa.policies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.uva.aa.State;
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
     * Checks if the state is mapped in this policy.
     * 
     * @param state
     *            The state to check
     * 
     * @return True if it's mapped, false otherwise
     */
    public boolean containsState(final State state) {
        return mStateMap.containsKey(state);
    }

    /**
     * Retrieves the value of the state. Essentially returns a cached result of V^pi(s), but does not actually perform
     * the value function.
     * 
     * @param state
     *            The state to get the value for
     * 
     * @return The value of the state or a 0 if not set
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
     * @return The probability of the action or a default value if not set
     */
    public double getActionProbability(final State state, final Action action) {
        return getProperties(state).getActionProbability(action);
    }

    /**
     * Retrieves the value of an action in the given state
     * 
     * @param state
     *            The state to check the action for
     * @param action
     *            The action to find the value for
     * 
     * @return The value of the action or a default value if not set
     */
    public double getActionValue(final State state, final Action action) {
        return getProperties(state).getActionValue(action);
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
     * Sets the value of an action for the given state.
     * 
     * @param state
     *            The state from which the action is performed
     * @param action
     *            The action for which to set the probability
     * @param value
     *            The value of the action
     */
    public void setActionValue(final State state, final Action action, final double value) {
        getProperties(state).setActionValue(action, value);
    }

    /**
     * Returns a random action based on the probability within the given state.
     * 
     * @param state
     *            The state to choose an action for
     * 
     * @return A semi-random action or null if no actions are available
     */
    public Action getActionBasedOnProbability(final State state) {
        final double decision = Math.random();
        double decisionCount = 0;

        for (final Map.Entry<Action, Double> actionProb : getProperties(state).getActionProbabilities().entrySet()) {
            decisionCount += actionProb.getValue();
            if (decisionCount >= decision) {
                return actionProb.getKey();
            }
        }

        System.err.println("Error: cannot choose action!");
        System.err.println(getProperties(state).getActionProbabilities());
        return null;
    }

    /**
     * Returns the set of actions with maximum probability (argmax)
     * 
     * @param state
     * @return
     */
    public List<Action> getActionsBasedOnProbability(final State state) {
        double bestProbability = 0;
        final List<Action> bestActions = new LinkedList<Action>();

        for (final Map.Entry<Action, Double> actionProb : getProperties(state).getActionProbabilities().entrySet()) {
            final double probability = actionProb.getValue();
            if (probability > bestProbability) {
                bestActions.clear();
                bestProbability = probability;
            }
            if (probability >= bestProbability) {
                bestActions.add(actionProb.getKey());
            }
        }

        return bestActions;
    }

    /**
     * Returns a random action based on the value within the given state using epsilon-greedy.
     * 
     * @param state
     *            The state to choose an action for
     * @param epsilon
     *            The epsilon to use for epsilon-greedy selection
     * 
     * @return A semi-random action or null if no actions are available
     */
    public Action getActionBasedOnValueEpsilonGreedy(final State state, final double epsilon) {
        final double decision = Math.random();
        final List<Action> allActions = new LinkedList<Action>();
        final List<Action> bestActions = new LinkedList<Action>();
        double bestValue = Integer.MIN_VALUE;

        // Determine the best action(s)
        for (final Map.Entry<Action, Double> actionValue : getProperties(state).getActionValues().entrySet()) {
            final Action action = actionValue.getKey();
            final double value = actionValue.getValue();

            allActions.add(action);

            if (value > bestValue) {
                bestActions.clear();
                bestValue = value;
            }
            if (value >= bestValue) {
                bestActions.add(action);
            }
        }

        if (decision > epsilon || allActions.size() == bestActions.size()) {
            // Pick a best action
            return bestActions.get((int) Math.floor(Math.random() * bestActions.size()));
        } else {
            // Pick a non-best action for exploration
            for (final Action bestAction : bestActions) {
                allActions.remove(bestAction);
            }
            return allActions.get((int) Math.floor(Math.random() * allActions.size()));
        }
    }

    /**
     * Returns a random action based on the value within the given state using Softmax.
     * 
     * @param state
     *            The state to choose an action for
     * @param temperature
     *            The temperature for Softmax selection, make sure that this isn't too low
     * 
     * @return A semi-random action or null if no actions are available
     */
    public Action getActionBasedOnValueSoftmax(final State state, final double epsilon, final double temperature) {
        final double decision = Math.random();
        final List<Action> allActions = new LinkedList<Action>();
        final List<Action> bestActions = new LinkedList<Action>();
        double bestValue = Integer.MIN_VALUE;

        // Determine the best action(s)
        for (final Map.Entry<Action, Double> actionValue : getProperties(state).getActionValues().entrySet()) {
            final Action action = actionValue.getKey();
            final double value = actionValue.getValue();

            allActions.add(action);

            if (value > bestValue) {
                bestActions.clear();
                bestValue = value;
            }
            if (value >= bestValue) {
                bestActions.add(action);
            }
        }

        if (decision > epsilon || allActions.size() == bestActions.size()) {
            // Pick a best action
            return bestActions.get((int) Math.floor(Math.random() * bestActions.size()));

        } else {
            final Map<Action, Double> actionValues = getProperties(state).getActionValues();

            // Determine the sum of Softmax values for the actions
            double softmaxSum = 0;
            for (final double value : actionValues.values()) {
                softmaxSum += Math.exp(value / temperature);
            }

            // Make sure that we've got a valid sum
            if (softmaxSum == Double.POSITIVE_INFINITY) {
                throw new RuntimeException("The temperature for softmax is likely too low. "
                        + "The predator seems to be freezing! (Dohohh! Funny jokes...)");
            }

            // Pick a random action based on the Softmax probabilities
            double decisionCount = 0;
            for (final Map.Entry<Action, Double> actionValue : actionValues.entrySet()) {
                final Action action = actionValue.getKey();
                final double value = actionValue.getValue();

                decisionCount += Math.exp(value / temperature) / softmaxSum;

                if (decisionCount >= decision) {
                    return action;
                }
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
            // Print the actions with their value
            for (Map.Entry<Action, Double> actionValue : properties.getActionValues().entrySet()) {
                System.out.println("    Action value " + actionValue.getKey() + " = " + actionValue.getValue());
            }
        }
        System.out.println();
    }
}
