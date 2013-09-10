package com.uva.aa.policyImpovement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.uva.aa.Location;
import com.uva.aa.agents.Agent;
import com.uva.aa.agents.Environment;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.State;

public class IterativePolicyEvaluation {

    private final Agent mAgent;
    private final Environment mEnvironment;
    private final List<Agent> mOtherAgents;
    private final Policy mPolicy;
    private final List<State> mPossibleStatesExclTerminal;
    private final List<State> mPossibleStatesInclTerminal;
    
    /**
     * The Iterative Policy Evaluation (Sutton, Barto, Chapter 4.1) determines
     * the value function to a given policy
     * 
     * @param agent
     * @param policy
     * @param possibleStatesExclTerminal
     * @param possibleStatesInclTerminal
     */
    public IterativePolicyEvaluation(Agent agent, Policy policy, List<State> possibleStatesExclTerminal, List<State> possibleStatesInclTerminal) {

        mAgent = agent;
        mEnvironment = agent.getEnvironment();
        mOtherAgents = mEnvironment.getAgents();
        mOtherAgents.remove(agent);
        mPolicy = policy;
        mPossibleStatesExclTerminal = possibleStatesExclTerminal;
        mPossibleStatesInclTerminal = possibleStatesInclTerminal;
        
        // initialize the value of each state to be 0 (including the terminal states)
        for (State state : mPossibleStatesInclTerminal) {
            mPolicy.setStateValue(state, 0.0);
        }

        // start the actual loop
        estimateValueFunction();
    }
    
    /**
     * Updates the value function until it converges
     */
    private void estimateValueFunction() {
        
        // these determine at what point the algorithm should stop
        double maxValErrDelta = 100.0;
        double errBoundTheta = 0.0000001;
        
        // update the value function until it converges...
        while (maxValErrDelta > errBoundTheta) {

            // ... by sweeping through the state space
            for (State state : mPossibleStatesExclTerminal) {

                // save current estimate of the value of the current state (for later comparison)
                double previousStateValue = mPolicy.getStateValue(state);

                // now: use the bellman equation to update the estimate of the state-values
                double updatedStateValue = updateStateValue(state);
                mPolicy.setStateValue(state, updatedStateValue);
 
                maxValErrDelta = Math.max(maxValErrDelta, Math.abs(previousStateValue-updatedStateValue));
            }
        }

    }
    
    /**
     * Returns an updated estimation of the state-value based on the bellmann equation
     * 
     * @param state
     * @return
     */
    private double updateStateValue(State state) {
 
        // discount factor of the bellman equation
        double discountFactorGamma = 0.8;
        
        // locations of the agents
        final Location predatorCurrLocation = getPredatorLocation(state);
        final Location preyCurrLocation = getPreyLocation(state);
        
        // in the outer summation: iterate over all possible actions the predator can take
        double outerSum = 0;
        for (Action actionPredator : Action.values()) {

            // get pi(s,a)
            double actionProb = mPolicy.getActionProbability(state, actionPredator);

            // determine the states (including terminal states) can follow the current state
            List<State> possibleNextStates = new ArrayList<State>();
            // first, get the new location of the predator after performing the action
            final Location predatorNewLocation = actionPredator.getLocation(predatorCurrLocation);
            // if the predator catches the prey with its action, there is only only one possible next state
            if (predatorNewLocation == preyCurrLocation) {
                final Map<Agent, Location> nextPredatorStateMap = new HashMap<Agent, Location>();
                nextPredatorStateMap.put(mAgent, predatorNewLocation);
                nextPredatorStateMap.put(mEnvironment.getPredators().get(0), preyCurrLocation);
                possibleNextStates.add(new State(nextPredatorStateMap));
            } else {
                for (Action actionPrey : Action.values()) {
                    final Location preyNewLocation = actionPrey.getLocation(preyCurrLocation);
                    final Map<Agent, Location> nextPreyStateMap = new HashMap<Agent, Location>();
                    nextPreyStateMap.put(mAgent, predatorNewLocation);
                    nextPreyStateMap.put(mEnvironment.getPreys().get(0), preyNewLocation);
                    possibleNextStates.add(new State(nextPreyStateMap));
                }
            }
            

            // in the inner sum: iterate over all the possible next states
            double innerSum = 0;
            for (State nextState : possibleNextStates) {
                double transitionProbability = mEnvironment.getTransitionProbability(state, nextState,
                        actionPredator);
                double immediateReward = mEnvironment.getImmediateReward(state, nextState, actionPredator);
                double nextStateValue = mPolicy.getStateValue(nextState);
                innerSum += transitionProbability * (immediateReward + discountFactorGamma * nextStateValue);
            }
            
            // outer sum
            outerSum += actionProb * innerSum;
        }
        return outerSum;
    }
    
    /**
     * Returns the location of the predator in the given state
     * Does NOT work for several predators yet!
     * 
     * @param state
     * @return
     */
    private Location getPredatorLocation(State state) {
        Location predatorLocation = null;
        Map<Agent, Location> agentLocations = state.getAgentLocations();
        for (Map.Entry<Agent, Location> mapping : agentLocations.entrySet()) {
            if (PredatorAgent.class.isInstance(mapping.getKey())) {
                predatorLocation = mapping.getValue();
            }
        }
        return predatorLocation;
    }
    
    /**
     * Returns the location of the prey in the given state
     * Does NOT work for several preys yet!
     * 
     * @param state
     * @return
     */
    private Location getPreyLocation(State state) {
        Location preyLocation = null;
        Map<Agent, Location> agentLocations = state.getAgentLocations();
        for (Map.Entry<Agent, Location> mapping : agentLocations.entrySet()) {
            if (PreyAgent.class.isInstance(mapping.getKey())) {
                preyLocation = mapping.getValue();
            }
        }
        return preyLocation;
    }
    
}
