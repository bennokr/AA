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
    	// initialize minimaxQ without opponent
        minimaxQ = new MinimaxQ(1, 0.999999);
    }
    
    @Override
    public void prepare() {
    	super.prepare();
    	// set the opponent
        minimaxQ.setOpponent(getEnvironment().getPredators().get(0));
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
	
	/**
     * Performs an action during the agent's turn based on the policy, but tripping with a chance of 0.2!
     * Tripping is performing the WAIT action, but storing the chosen action.
     * 
     * @param roundStartState The state that we act for, or null to act for the environment's current state
     */
	@Override
    public void performAction(final State roundStartState) {
        // Move to a location based on an action determined by the policy
    	lastAction = mPolicy.getActionBasedOnProbability(
                roundStartState != null ? roundStartState : getEnvironment().getState());
    	
    	// Trip with a chance of 0.2
    	final double decision = Math.random();
        if (decision <= 0.2) {
        	moveTo(Action.WAIT.getLocation(this));
        } else {
        	moveTo(lastAction.getLocation(this));
        }
    }

	@Override
	protected boolean shouldPickActionBeforeCallback() {
		return false;
	}
}
