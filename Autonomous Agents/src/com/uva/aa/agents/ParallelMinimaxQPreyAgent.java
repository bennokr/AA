package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class ParallelMinimaxQPreyAgent extends ParallelLearningPreyAgent {
	
	private MinimaxQ minimaxQ;

	/**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ParallelMinimaxQPreyAgent(final Location location) {
        super(location);
    }
    
    @Override
    public void prepare() {
    	super.prepare();
    	// initialize minimaxQ with opponent
        minimaxQ = new MinimaxQ(getEnvironment().getPredators().get(0), 1, 0.99);
    }

	@Override
	protected void postActionCallback(State initialState, State resultingState,
			Action previousAction, Action nextAction) {	
		double r = getImmediateReward(initialState, resultingState, previousAction);
		minimaxQ.learn(initialState, resultingState, previousAction, r, mPolicy);
	}
	
	@Override
	protected Action getActionToPerform(State state) {
        return mPolicy.getActionBasedOnValueEpsilonGreedy(state, Config.EPSILON);
	}

	@Override
	protected boolean shouldPickActionBeforeCallback() {
		return false;
	}
}
