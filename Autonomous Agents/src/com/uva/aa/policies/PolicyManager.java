package com.uva.aa.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.uva.aa.Config;
import com.uva.aa.Environment;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.enums.Action;

/**
 * A policy manager with the goal of improving a policy. Provides several methods for doing so:
 * 
 * POLICY EVALUATION (Sutton, Barto, 4.1) provided by the method evaluatePolicy(). In that method, we initialize V(s)=0
 * for all s in S^+ (all states, including terminal states). The actual loop can be called by the function
 * updatedStateValues(). The reason we did this is that we can then easily re-use that part for later exercises. In the
 * policy evaluation, we at one point updated the value of a state incrementally using the Bellman equation
 * V(s)<-sum_{a}[policy(s,a)*sum_{s'}P_{s,s'}^{a}*(R_{s,s'}^{a}+gamma*V(s'))]. This calculation is implemented in the
 * method getUpdatedStateValue().
 * 
 * POLICY IMPROVEMENT (Sutton, Barto, 4.2) provided by the method improvePolicy().
 * 
 * POLICY ITERATION (Sutton, Barto, 4.3) provided by the method iteratePolicy(). Loops over poliva evaluation and policy
 * improvement, until the improved policy is stable, that is it doesn't change anymore during an improvement step. The
 * corresponding value function then fulfills the Bellman equation.
 * 
 * VALUE ITERATION (Sutton, Barto, 4.4) provided by iterateValue(). Makes uses of the updateStateValues() which was
 * implemented for the policy evaluation.
 */
public class PolicyManager {

    /** The policy evaluation to evaluate */
    private final Policy mPolicy;

    /** The environment in which the policy will be used */
    private final Environment mEnvironment;

    /** The agent for which the policy evaluation is done */
    private final PredatorAgent mPredator;

    /** The prey which the agent chases */
    private final PreyAgent mPrey;

    /** The number of iterations of the latest state value update */
    private int mUpdateStateValueIterations;

    /** The total number of iterations of the latest state value update for a policy iteration */
    private int mPolicyUpdateStateValueIterations;

    /** The number of iterations of the policy iteration (evaluation + improvement = 1 iteration) */
    private int mPolicyIterationIterations;

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
    public PolicyManager(final Policy policy, final Environment environment) {
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
        mPolicyUpdateStateValueIterations = 0;
        mPolicyIterationIterations = 0;

        boolean policyStable = false;
        while (!policyStable) {
            evaluatePolicy();
            policyStable = improvePolicy();
            mPolicyIterationIterations++;
        }
    }

    /**
     * Value iteration looks like Policy Evaluation, but we maximize wrt action-values to create an optimal policy.
     */
    public void iterateValues() {
        // Prepare all possible states (including terminal)
        for (final State state : mEnvironment.getPossibleStates(true)) {
            mPolicy.setStateValue(state, 0);
        }

        updateStateValues(true);
        improvePolicy();
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
    public void evaluatePolicy() {
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
        mUpdateStateValueIterations = 0;

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
                double updatedStateValue;
                if (useMaxInsteadOfSum) {
                    updatedStateValue = getUpdatedStateValueMax(state);
                } else {
                    updatedStateValue = getUpdatedStateValueSum(state);
                }
                mPolicy.setStateValue(state, updatedStateValue);

                // Update the maximum error we have
                maxValErrDelta = Math.max(maxValErrDelta, Math.abs(previousStateValue - updatedStateValue));
            }

            // Keep track of how many iterations we've done
            ++mUpdateStateValueIterations;
            ++mPolicyUpdateStateValueIterations;

        } while (maxValErrDelta > Config.ERROR_THRESHOLD_THETA);
    }

    /**
     * Returns the next estimation of the state-value based on the Bellmann equation using a weighted sum.
     * 
     * @param state
     *            The state for which we want to estimate the value
     * 
     * @return The (next) estimation of the value of the given state
     */
    private double getUpdatedStateValueSum(final State state) {
        // In the outer summation: iterate over all possible actions the predator can take
        double stateValue = 0;
        for (final Action predatorAction : Action.values()) {
            // When we want the maximum instead of the sum, don't care about pi(s,a)
            double actionValue = mPolicy.getActionProbability(state, predatorAction)
                    * getInnerSum(state, predatorAction);

            // Outer sum of the Bellman equation
            stateValue += actionValue;
        }

        return stateValue;
    }

    /**
     * Returns the next estimation of the state-value based on the Bellmann equation using the maximum values.
     * 
     * @param state
     *            The state for which we want to estimate the value
     * 
     * @return The (next) estimation of the value of the given state
     */
    private double getUpdatedStateValueMax(final State state) {
        // In the outer summation: iterate over all possible actions the predator can take
        double stateValue = 0;
        for (final Action predatorAction : Action.values()) {
            // Determine the value of this action
            stateValue = Math.max(stateValue, getInnerSum(state, predatorAction));
        }

        return stateValue;
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

            // Determine the best actions for the state
            final List<Action> bestActions = new ArrayList<Action>();
            double bestActionValue = 0;
            for (final Entry<Action, Double> actionProbability : properties.getActionProbabilities().entrySet()) {
                final Action predatorAction = actionProbability.getKey();

                // Note the action's value based on the next states' quality through the inner sum
                final double actionValue = getInnerSum(state, predatorAction);

                if (actionValue > bestActionValue) {
                    // Clear the list of best actions if we found something better
                    bestActions.clear();
                    bestActionValue = actionValue;
                }
                if (actionValue >= bestActionValue) {
                    // Append the list of best actions if we found an action at least just as good
                    bestActions.add(predatorAction);
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
     * Calculates the inner sum of DP, in the form of sum_s'{P[R+gamma*V(s')]}. The result is not weighted according to
     * probability.
     * 
     * @param initialState
     *            The state before the action is performed
     * @param predatorAction
     *            The action the predator will perform in the given state
     * 
     * @return The value of the inner sum
     */
    private double getInnerSum(final State initialState, final Action predatorAction) {
        final List<State> possibleNextStates = new ArrayList<State>();
        final Location predatorLocation = initialState.getAgentLocation(mPredator);
        final Location preyLocation = initialState.getAgentLocation(mPrey);
        final Location nextPredatorLocation = predatorAction.getLocation(predatorLocation);

        if (nextPredatorLocation.equals(preyLocation)) {
            // If the predator catches the prey with its action, there is only one possible next state
            possibleNextStates.add(State.buildState(mPredator, nextPredatorLocation, mPrey, null));

        } else {
            // If the predator doesn't catch the prey, there are five possible actions we have to iterate over
            for (final Action preyAction : Action.values()) {
                final Location nextPreyLocation = preyAction.getLocation(preyLocation);
                if (!nextPreyLocation.equals(nextPredatorLocation)) {
                    possibleNextStates.add(State.buildState(mPredator, nextPredatorLocation, mPrey, nextPreyLocation));
                }
            }
        }

        // In the inner sum: iterate over all the possible next states
        double innerSum = 0;
        for (final State nextState : possibleNextStates) {
            final double transitionProbability = mPredator.getTransitionProbability(initialState, nextState,
                    predatorAction);
            final double immediateReward = mPredator.getImmediateReward(initialState, nextState, predatorAction);
            final double nextStateValue = mPolicy.getStateValue(nextState);

            innerSum += transitionProbability * (immediateReward + Config.DISCOUNT_FACTOR_GAMMA * nextStateValue);
        }

        return innerSum;
    }

    /**
     * Retrieves the total number of iterations of the latest state value update.
     * 
     * @return The number of iterations
     */
    public int getUpdateStateValueIterations() {
        return mUpdateStateValueIterations;
    }

    /**
     * Retrieves the total number of iterations of the latest state value update for a policy iteration.
     * 
     * @return The number of iterations
     */
    public int getPolicyUpdateStateValueIterations() {
        return mPolicyUpdateStateValueIterations;
    }

    /**
     * Retrieves the number of iterations of the latest policy iteration (policy evaluation + policy improbement = 1
     * iteration).
     * 
     * @return The number of iterations
     */
    public int getPolicyIterationIterations() {
        return mPolicyIterationIterations;
    }

}
