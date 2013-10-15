package com.uva.aa.agents;

import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class ParallelMinimaxQPredator extends ParallelLearningPredatorAgent {

	/**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ParallelMinimaxQPredator(final Location location) {
        super(location);
        
        // initialize Q(s,a,o)
    }

	@Override
	protected void postActionCallback(State initialState, State resultingState,
			Action previousAction, Action nextAction) {
		// TODO Learn:
		// Q(s,a,o) = (1-alpha) * Q(s,a,o) + alpha * (r + gamma * V(s'))
		
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
