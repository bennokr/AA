package com.uva.aa.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.agents.Environment;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.enums.Action;

/**
 * A policy evaluator with the goal of improving a policy. Provides several methods for doing so.
 */
public class PolicyEvaluator {

    /** The threshold that determines at what point we stop our evaluation */
    private static final double ERROR_THRESHOLD_THETA = 0.00000001;

    /** The discount factor of the bellman equation */
    private static final double DISCOUNT_FACTOR_GAMMA = 0.8;

    /** The policy evaluation to evaluate */
    private final Policy mPolicy;

    /** The environment in which the policy will be used */
    private final Environment mEnvironment;

    /** The agent for which the policy evaluation is done */
    private final PredatorAgent mPredator;

    /** The prey which the agent chases */
    private final PreyAgent mPrey;

    /** The number of iterations of the latest evaluation */
    private int mIterations;

    /**
     * Prepares the policy evaluator.
     * 
     * @param agent
     *            The agent which holds a policy/policies that should be evaluated
     * @param possibleStatesExclTerminal
     *            All the possible states the agent can be in, excluding the terminal states
     * @param possibleStatesInclTerminal
     *            All the possible states the agent can be in, including the terminal states
     */
    public PolicyEvaluator(final Policy policy, final Environment environment) {
        mPolicy = policy;
        mEnvironment = environment;
        mPredator = environment.getPredators().get(0);
        mPrey = environment.getPreys().get(0);
    }

    /**
     * Iterates the estimation of the value function and the improving of the current policy, until the optimal policy
     * is reached. See [Sutton & Barto, 4.3: Policy Iteration]. We continuously flip back and forth between estimating
     * the value function and improving the policy until it's optimal.
     * 
     * Only supports one predator and prey.
     */
    public void iteratePolicy() {
        boolean policyStable = false;
        while (!policyStable) {
            estimateValueFunction();
            policyStable = improvePolicy();
        }

        iterateValues();
    }

    /**
     * Computes the real value function to a given policy with the iterative policy evaluation.
     * 
     * Uses the Iterative Policy Evaluation after Sutton, Barto, Chapter 4.1. An agent in an MDP-environment which is
     * fully known to him can evaluate his current policy and thus estimate the true value function for that policy.
     * 
     * An estimation of the value function V which maps a value to each state that the agent can be in will be updated
     * incrementally using the Bellman equation:
     * V(s)<-sum_{a}[policy(s,a)*sum_{s'}P_{s,s'}^{a}*(R_{s,s'}^{a}+gamma*V(s'))] where the first sum is over all
     * possible actions in state s, policy(s,a) gives the probability for taking action a in state s due to that policy,
     * P_{s,s'}^{a} is the transition function (see environment.getTransitionProbability), R_{s,s'}^{a} is the immediate
     * reward function (see environment.getImmediateReward), gamma is the discount factor of the Bellman equation and V
     * is our (estimation of the) value function.
     * 
     * @param policy
     *            The policy for which the value function should be estimated
     */
    public void estimateValueFunction() {
        // Prepare all possible states (including terminal)
        for (final State state : mEnvironment.getPossibleStates(true)) {
            mPolicy.setStateValue(state, 0);
        }
        
        // Modify the policy's state values
        updateStateValues(false);
    }

    /**
     * Updates the state values of a policy based on the actions, probabilities and rewards.
     * 
     * @param useMaxInsteadOfSum
     *            True if the state value should be the maximum of action values instead of the sum
     */
    private void updateStateValues(final boolean useMaxInsteadOfSum) {

        // Reset the number of iterations
        mIterations = 0;

        // We use this variable to determine the changes we have made during a loop
        double maxValErrDelta;

        // Update the value function until it converges
        do {
            // Reset the delta for this update
            maxValErrDelta = 0;

            // Sweep through the state space of non-terminal states
            for (final State state : mEnvironment.getPossibleStates(false)) {
                // Save current estimate of the value of the current state (for later comparison)
                final double previousStateValue = mPolicy.getStateValue(state);

                // Replace the old values in place (like suggested in Sutton, Barto, Chapter 4.1)
                final double updatedStateValue = getUpdatedStateValue(state, useMaxInsteadOfSum);
                mPolicy.setStateValue(state, updatedStateValue);

                // Update the maximum error we have
                maxValErrDelta = Math.max(maxValErrDelta, Math.abs(previousStateValue - updatedStateValue));
            }

            // Keep track of how many iterations we've done
            ++mIterations;

        } while (maxValErrDelta > ERROR_THRESHOLD_THETA);
    }

    /**
     * Retrieves the number of iterations performed in the last evaluation.
     * 
     * @return The number of iterations
     */
    public int getNumberOfIterations() {
        return mIterations;
    }

    /**
     * Returns the next estimation of the state-value based on the Bellmann equation.
     * 
     * @param state
     *            The state for which we want to estimate the value
     * @param getMax
     *            True if the state value should be the maximum of action values instead of the sum
     * 
     * @return The (next) estimation of the value of the given state
     */
    private double getUpdatedStateValue(final State state, final boolean getMaxInsteadOfSum) {
        final Location predatorCurrLocation = state.getAgentLocation(mPredator);
        final Location preyCurrLocation = state.getAgentLocation(mPrey);

        // In the outer summation: iterate over all possible actions the predator can take
        double outerSum = 0;
        double maxStateValue = 0;
        for (final Action predatorAction : Action.values()) {
            final List<State> possibleNextStates = new ArrayList<State>();
            final Location nextPredatorLocation = predatorAction.getLocation(predatorCurrLocation);

            if (nextPredatorLocation == preyCurrLocation) {
                // If the predator catches the prey with its action, there is only one possible next state
                possibleNextStates.add(State.buildState(mPredator, nextPredatorLocation, mPrey, null));

            } else {
                // If the predator doesn't catch the prey, there are five possible actions we have to iterate over (if
                // the prey cannot perform an action, the possibility of this will be 0, so we don't care about this)
                for (final Action preyAction : Action.values()) {
                    possibleNextStates.add(State.buildState(mPredator, nextPredatorLocation, mPrey,
                            preyAction.getLocation(preyCurrLocation)));
                }
            }

            // In the inner sum: iterate over all the possible next states
            double innerSum = 0;
            for (final State nextState : possibleNextStates) {
                innerSum += getActionValue(state, nextState, predatorAction);
            }

            // Get pi(s,a)
            final double actionProbability = mPolicy.getActionProbability(state, predatorAction);
            final double actionValue = actionProbability * innerSum;

            // Outer sum of the Bellman equation
            if (getMaxInsteadOfSum) {
                maxStateValue = Math.max(maxStateValue, actionValue);
            } else {
                outerSum += actionValue;
            }
        }

        return outerSum;
    }

    /**
     * Adjusts the policy in every state to the best action according to the current state value function.
     * 
     * Only supports one predator and prey.
     * 
     * @return True if the policy has not improved, false if it remained the same
     */
    public boolean improvePolicy() {
        boolean policyStable = true;

        // Update actions the values for each state
        for (final Entry<State, StatePolicyProperties> stateMapping : mPolicy.getStateMap().entrySet()) {
            final State state = stateMapping.getKey();
            final StatePolicyProperties properties = stateMapping.getValue();

            final Location predatorLocation = state.getAgentLocation(mPredator);
            final Location preyLocation = state.getAgentLocation(mPrey);

            // Determine the best actions for the state
            final List<Action> bestActions = new ArrayList<Action>();
            double bestActionValue = 0;
            for (final Entry<Action, Double> actionProbability : properties.getActionProbabilities().entrySet()) {
                final Action action = actionProbability.getKey();

                // Find the state resulting of performing the action
                final Location nextPredatorLocation = action.getLocation(predatorLocation);
                final State nextState = State.buildState(mPredator, nextPredatorLocation, mPrey, preyLocation);

                // Note the next state's value
                final double actionValue = getActionValue(state, nextState, action);

                if (actionValue > bestActionValue) {
                    // Clear the list of best actions if we found something better
                    bestActions.clear();
                    bestActionValue = actionValue;
                }
                if (actionValue >= bestActionValue) {
                    // Append the list of best actions if we found an action at least just as good
                    bestActions.add(action);
                }
            }

            // Save the action probabilities for the current state to compare them after changing the policy
            HashMap<Action, Double> tempActionProbabilities = new HashMap<Action, Double>(
                    properties.getActionProbabilities());

            // Update the action probabilities based on the best values
            properties.clearActionProbabilities();
            final double bestActionProbability = 1.0 / bestActions.size();
            for (final Action bestAction : bestActions) {
                properties.setActionProbability(bestAction, bestActionProbability);
            }

            // Check whether we've changed the action probabilities for the current state
            if (!properties.getActionProbabilities().equals(tempActionProbabilities)) {
                policyStable = false;
            }
        }

        return policyStable;
    }

    /**
     * Value iteration looks like Policy Evaluation, but we maximize wrt action-values to create an optimal policy.
     */
    public void iterateValues() {
        updateStateValues(true);
        improvePolicy();
    }

    /**
     * Retrieves the value of the action when performing it to get from one state to the other.
     * 
     * @param initialState
     *            The initial state
     * @param resultingState
     *            The resulting state
     * @param action
     *            This agent's action
     * 
     * @return The value of the action based on P[R+gamma*V(s')]
     */
    private double getActionValue(final State initialState, final State resultingState, final Action action) {
        final double transitionProbability = mPredator.getTransitionProbability(initialState, resultingState, action);
        final double immediateReward = mPredator.getImmediateReward(initialState, resultingState, action);
        final double nextStateValue = mPolicy.getStateValue(resultingState);

        return transitionProbability * (immediateReward + DISCOUNT_FACTOR_GAMMA * nextStateValue);
    }

}
