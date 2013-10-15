package com.uva.aa.agents;

import java.util.HashMap;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class ParallelMinimaxQPredator extends ParallelLearningPredatorAgent {
	
	/* The zero-sum game payoff matrix per state
	 * or Double Q[s][o][a]
	 * or Q :: s -> o -> a -> Double
	 */
	private final HashMap<State, HashMap<Action, HashMap<Action, Double>>> stateGameValues;
	
	/*
	 * The zero-sum game theoretical opponent 
	 */
	private Agent opponent;
	
	double alpha;
	double decay;
	
	
	/**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ParallelMinimaxQPredator(final Location location) {
        super(location);
        
        // initialize Q(s,a,o)
        stateGameValues = new HashMap<State, HashMap<Action, HashMap<Action, Double>>>();
        
        // config
        alpha = 1;
        decay = 0.999;
    }
    
    @Override
    public void prepare() {
    	super.prepare();
    	// initialize opponent
    	opponent = getEnvironment().getPreys().get(0);
    }

	@Override
	protected void postActionCallback(State initialState, State resultingState,
			Action previousAction, Action nextAction) {	
		
		if (!stateGameValues.containsKey(initialState)) {
			stateGameValues.put(initialState, new HashMap<Action, HashMap<Action, Double>>());
		}
		if (!stateGameValues.get(initialState).containsKey(opponent.getLastAction())) {
			stateGameValues.get(initialState).put(opponent.getLastAction(), new HashMap<Action, Double>());
		}
		
		
		// TODO Learn:
		// Q(s,a,o) = (1-alpha) * Q(s,a,o) + alpha * (r + gamma * V(s'))
		double Q = 0;
		if (stateGameValues.get(initialState).get(opponent.getLastAction()).containsKey(previousAction)) {
			Q = stateGameValues.get(initialState).get(opponent.getLastAction()).get(previousAction);
		}
		stateGameValues.get(initialState).get(opponent.getLastAction()).put(previousAction, 
				(1-alpha) * Q 
				+ alpha * (
						getImmediateReward(initialState, resultingState, previousAction) 
						+ Config.DISCOUNT_FACTOR_GAMMA * mPolicy.getStateValue(resultingState)));
		
		// R = neg infinity
		// for each o:
		//   solve LP:
		//     add objective as each action-value Q(s,o)
		//     add constraint as sum pi = 1
		//     R'  = result objective
		//     pi' = result variables
		//   if R' < R:
		//     pi = pi'
		//     R  = R'
		
		// V(s) = R
		// alpha = alpha * decay
	}
	
	@Override
	protected Action getActionToPerform(State state) {
		// TODO With probability `explore`, return a random action, else return action from pi
		return null;
	}

	@Override
	protected boolean shouldPickActionBeforeCallback() {
		return false;
	}
}
