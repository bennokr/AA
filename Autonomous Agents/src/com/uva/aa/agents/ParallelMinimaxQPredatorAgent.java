package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class ParallelMinimaxQPredatorAgent extends ParallelLearningPredatorAgent {
	
	private MinimaxQ minimaxQ;

	/**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ParallelMinimaxQPredatorAgent(final Location location) {
        super(location);
    	// initialize minimaxQ without opponent
        minimaxQ = new MinimaxQ( 1, 0.999999);
    }
    
    @Override
    public void prepare() {
    	super.prepare();
    	minimaxQ.setOpponent(getEnvironment().getPreys().get(0));
    }

	@Override
	protected void postActionCallback(State initialState, State resultingState,
			Action previousAction, Action nextAction) {	
		double r = getImmediateReward(initialState, resultingState, previousAction);
		minimaxQ.learn(initialState, resultingState, previousAction, r, mPolicy);
	}
	
	@Override
	protected Action getActionToPerform(State state) {
        return mPolicy.getActionBasedOnPolicyOrRandom(state, Config.EPSILON);
	}

	@Override
	protected boolean shouldPickActionBeforeCallback() {
		return false;
	}
}
