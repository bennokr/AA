package com.uva.aa.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.uva.aa.Location;
import com.uva.aa.agents.Agent;
import com.uva.aa.agents.Environment;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.enums.Action;

/**
 * Iterative Policy Evaluation after Sutton, Barto, Chapter 4.1. An agent in an MDP-environment which is fully known to
 * him can evaluate his current policy and thus estimate the true value function for that policy
 * 
 * An estimation of the value function V which maps a value to each state that the agent can be in will be updated
 * incrementally using the Bellman equation: V(s)<-sum_{a}[policy(s,a)*sum_{s'}P_{s,s'}^{a}*(R_{s,s'}^{a}+gamma*V(s'))]
 * where the first sum is over all possible actions in state s, policy(s,a) gives the probability for taking action a in
 * state s due to that policy, P_{s,s'}^{a} is the transition function (see environment.getTransitionProbability),
 * R_{s,s'}^{a} is the immediate reward function (see environment.getImmediateReward), gamma is the discount factor of
 * the Bellman equation and V is our (estimation of the) value function
 * 
 */
public class IterativePolicyEvaluation {

    private final Agent mAgent;
    private final Environment mEnvironment;
    private final List<PreyAgent> mPreys;
    private final List<State> mPossibleStatesExclTerminal;
    private final List<State> mPossibleStatesInclTerminal;
    /** counts the number of iterations */
    private int mIterations;
    // this threshold will determine at what point we stop our algorithm
    private static final double ERR_THRESHOLD_THETA = 0.00001;
    // the discount factor of the bellman equation
    private static final double DISCOUNT_FACTOR_GAMMA = 0.8;

    /**
     * Creates a new Iterative Policy Evaluation
     * 
     * @param agent
     *            the agent who holds a policy that he wants to evaluate
     * @param possibleStatesExclTerminal
     *            all the possible states the agent can be in, excluding the terminal states
     * @param possibleStatesInclTerminal
     *            all the possible states the agent can be in, including the terminal states
     */
    public IterativePolicyEvaluation(final Agent agent, final List<State> possibleStatesExclTerminal,
            final List<State> possibleStatesInclTerminal) {
        mAgent = agent;
        mEnvironment = agent.getEnvironment();
        mPreys = mEnvironment.getPreys();
        mPossibleStatesExclTerminal = possibleStatesExclTerminal;
        mPossibleStatesInclTerminal = possibleStatesInclTerminal;
    }

    /**
     * Compute the real value function to a given policy with the iterative polic evaluation
     * 
     * @param policy
     *            the policy for which the value function should be estimated
     */
    public void estimateValueFunction(final Policy policy) {
        // initialize the number of iterations
        mIterations = 0;

        // initialize the value of each state to be 0 (including the terminal states)
        for (final State state : mPossibleStatesInclTerminal) {
            policy.setStateValue(state, 0.0);
        }

        // we use this variable to determine the changes we have made during a loop
        double maxValErrDelta = 0;

        // update the value function until it converges...
        do {
            // Reset the delta for this update
            maxValErrDelta = 0;

            // ... by sweeping through the state space
            for (final State state : mPossibleStatesExclTerminal) {

                // save current estimate of the value of the current state (for later comparison)
                final double previousStateValue = policy.getStateValue(state);

                // we replace the old values in place (like suggested in Sutton, Barto, Chapter 4.1)
                final double updatedStateValue = getNextStateValue(policy, state);
                policy.setStateValue(state, updatedStateValue);

                // update the maximum error we have
                maxValErrDelta = Math.max(maxValErrDelta, Math.abs(previousStateValue - updatedStateValue));

                ++mIterations;
            }
        } while (maxValErrDelta > ERR_THRESHOLD_THETA);

    }

    /**
     * Returns the next estimation of the state-value based on the Bellmann equation
     * 
     * @param policy
     *            the policy that we are following
     * @param state
     *            the state for which we want to estimate the value
     * @return outerSum the (next) estimation of the value of the given state
     */
    private double getNextStateValue(Policy policy, State state) {

        // locations of the agents
        final Location predatorCurrLocation = state.getAgentLocation(mAgent);
        final Location preyCurrLocation = state.getAgentLocation(mPreys.get(0));

        // in the outer summation: iterate over all possible actions the predator can take
        double outerSum = 0;
        for (Action actionPredator : Action.values()) {

            // determine the states (including terminal states) can follow the current state
            List<State> possibleNextStates = new ArrayList<State>();
            // first, get the new location of the predator after performing the action
            final Location predatorNewLocation = actionPredator.getLocation(predatorCurrLocation);
            // if the predator catches the prey with its action, there is only only one possible next state
            if (predatorNewLocation == preyCurrLocation) {
                final Map<Agent, Location> nextPredatorStateMap = new HashMap<Agent, Location>();
                nextPredatorStateMap.put(mAgent, predatorNewLocation);
                possibleNextStates.add(new State(nextPredatorStateMap));
            }
            // else, there are five possible actions we have to iterate over (if the prey cannot make
            // an action, the possibility of this will be 0, so we will not care about this here)
            else {
                for (Action actionPrey : Action.values()) {
                    final Location preyNewLocation = actionPrey.getLocation(preyCurrLocation);
                    final Map<Agent, Location> nextPreyStateMap = new HashMap<Agent, Location>();
                    nextPreyStateMap.put(mAgent, predatorNewLocation);
                    nextPreyStateMap.put(mPreys.get(0), preyNewLocation);
                    possibleNextStates.add(new State(nextPreyStateMap));
                }
            }

            // in the inner sum: iterate over all the possible next states
            double innerSum = 0;
            for (State nextState : possibleNextStates) {
                double transitionProbability = mAgent.getTransitionProbability(state, nextState, actionPredator);
                double immediateReward = mAgent.getImmediateReward(state, nextState, actionPredator);
                double nextStateValue = policy.getStateValue(nextState);
                innerSum += transitionProbability * (immediateReward + DISCOUNT_FACTOR_GAMMA * nextStateValue);
            }

            // get pi(s,a)
            double actionProb = policy.getActionProbability(state, actionPredator);
            // outer sum of the Bellman equation
            outerSum += actionProb * innerSum;
        }
        return outerSum;
    }

    /**
     * Retrieves the number of iterations performed in the last evaluation.
     * 
     * @return The number of iterations
     */
    public int getNumberOfIterations() {
        return mIterations;
    }

}
