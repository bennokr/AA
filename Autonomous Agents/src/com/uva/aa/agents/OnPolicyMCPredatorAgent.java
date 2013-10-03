package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class OnPolicyMCPredatorAgent extends MCPredatorAgent {

	public OnPolicyMCPredatorAgent(Location location) {
		super(location);
	}

	@Override
	protected Action getActionToPerform(State state) {
		return mPolicy.getActionBasedOnProbability(state);
	}

	@Override
	protected void updatePolicyFromEpisode(Episode episode) {
		// iterate over every (s,a)
		for (int t=0; t < episode.getLength(); t++) {
			// Set Q to the discounted reward R
			mPolicy.setActionValue(episode.getState(t), episode.getAction(t), getDiscountedReward(episode, t));
		}
		
		for (int t=0; t < episode.getLength(); t++) {
			State loopState = episode.getState(t);
			//update epsilon-soft policy
			Action bestAction = null;
			for (final Action action : Action.values()) {
				if (bestAction != null) {
					double Qloop = mPolicy.getActionValue(loopState, action);
					double Qbest = mPolicy.getActionValue(loopState, bestAction);
					if (Qloop > Qbest) {
						bestAction = action;
					}
				} else {
					bestAction = action;
				}
			}
			for (final Action action : Action.values()) {
				double p = (Config.EPSILON / Action.values().length)
						+ (action.equals(bestAction)? (1 - Config.EPSILON) : 0);
				mPolicy.setActionProbability(loopState,	action, p);
			}
		}
	}

}
