package com.uva.aa.agents;

import java.text.DecimalFormat;
import java.util.HashMap;

import com.uva.aa.Config;
import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class OnPolicyMCPredatorAgent extends MCPredatorAgent {
	
	PreyAgent somePrey;
	private HashMap<State,HashMap<Action, Integer>> countReturn;
	
	public OnPolicyMCPredatorAgent(Location location) {
		super(location);
		countReturn = new HashMap<State, HashMap<Action, Integer>>();
	}

	@Override
	protected Action getActionToPerform(State state) {
		// save our prey before it gets eaten and lost (don't ask)
		somePrey = getEnvironment().getPreys().get(0);
		
		return mPolicy.getActionBasedOnProbability(state);
	}

	@Override
	protected void updatePolicyFromEpisode(Episode episode) {
		// iterate over every (s,a)
		for (int t=0; t < episode.getLength(); t++) {
			State s = episode.getState(t);
			Action a = episode.getAction(t);
			
			// make sure the counter is initialized
			if (!countReturn.containsKey(s)) {
				countReturn.put(s, new HashMap<Action, Integer>());
			}
			if (!countReturn.get(s).containsKey(a)) {
				countReturn.get(s).put(a, 0);
			}

			// Set Q to the average discounted reward R
			// updated incrementally
			double i = (double) countReturn.get(s).get(a) + 1;
			double q = (1-(1/i))*mPolicy.getActionValue(s, a) + (1/i)*getDiscountedReward(episode, t);
			mPolicy.setActionValue(episode.getState(t), episode.getAction(t), q);
			countReturn.get(s).put(a, (int) i);
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
				double p = (Config.EPSILON / (double) Action.values().length)
						+ (action.equals(bestAction)? (1 - Config.EPSILON) : 0);
				mPolicy.setActionProbability(loopState,	action, p);
			}
		}
				
//			System.out.println("Q for prey at 0,0:");
//			for (int y=0; y<10; y++) {
//				for (int x=0; x<10; x++) {
//					double v = mPolicy.getActionValue(
//							State.buildState(
//									this, 
//									new Location(getEnvironment(), x, y), 
//									somePrey, 
//									new Location(getEnvironment(), 0, 0)), 
//							Action.UP);
//					DecimalFormat df = new DecimalFormat("00.0000");
//			        System.out.print(" " + df.format(v));
//				}
//				System.out.println();
//			}
	}

}
