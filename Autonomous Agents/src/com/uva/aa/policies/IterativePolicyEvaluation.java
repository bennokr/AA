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

public class IterativePolicyEvaluation {

    private final Agent mAgent;
    private final Environment mEnvironment;
    private final List<PreyAgent> mPreys;
    private final List<State> mPossibleStatesExclTerminal;
    private final List<State> mPossibleStatesInclTerminal;

    private int mIter;

    private static final double ERR_THRESHOLD_THETA = 0.00001;
    private static final double DISCOUNT_FACTOR_GAMMA = 0.8;

    /**
     * The Iterative Policy Evaluation (Sutton, Barto, Chapter 4.1) determines the value function to a given policy
     * 
     * @param agent
     * @param policy
     * @param possibleStatesExclTerminal
     * @param possibleStatesInclTerminal
     */
    public IterativePolicyEvaluation(Agent agent, List<State> possibleStatesExclTerminal,
            List<State> possibleStatesInclTerminal) {

        mAgent = agent;
        mEnvironment = agent.getEnvironment();
        mPreys = mEnvironment.getPreys();
        mPossibleStatesExclTerminal = possibleStatesExclTerminal;
        mPossibleStatesInclTerminal = possibleStatesInclTerminal;

    }

    /**
     * Updates the value function until it converges
     */
    public void estimateValueFunction(Policy policy) {

        mIter = 0;

        // initialize the value of each state to be 0 (including the terminal states)
        for (State state : mPossibleStatesInclTerminal) {
            policy.setStateValue(state, 0.0);
        }

        // we use this variable to determine the changes we have made during a loop
        double maxValErrDelta = 0;

        // update the value function until it converges...
        while (maxValErrDelta > ERR_THRESHOLD_THETA) {

            // ... by sweeping through the state space
            for (State state : mPossibleStatesExclTerminal) {

                // save current estimate of the value of the current state (for later comparison)
                double previousStateValue = policy.getStateValue(state);

                // now: use the bellman equation to update the estimate of the state-values
                double updatedStateValue = getNextStateValue(policy, state);
                policy.setStateValue(state, updatedStateValue);

                // update the maximum error we have
                maxValErrDelta = Math.max(maxValErrDelta, Math.abs(previousStateValue - updatedStateValue));

                mIter++;
            }
        }

    }

    /**
     * Returns an updated estimation of the state-value based on the bellmann equation
     * 
     * @param state
     * @return
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
                double transitionProbability = mEnvironment.getTransitionProbability(state, nextState, actionPredator);
                double immediateReward = mEnvironment.getImmediateReward(state, nextState, actionPredator);
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

    public int getNumberOfIterations() {
        return mIter;
    }

}
