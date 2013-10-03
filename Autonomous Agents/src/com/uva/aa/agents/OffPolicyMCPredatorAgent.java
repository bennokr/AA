package com.uva.aa.agents;

import java.util.HashMap;
import java.util.Map;

import com.uva.aa.Config;
import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class OffPolicyMCPredatorAgent extends MCPredatorAgent {
	
	private HashMap<State,HashMap<Action, Double>> Qn; // Numerator
	private HashMap<State,HashMap<Action, Double>> Qd; // Denominator
	
	public OffPolicyMCPredatorAgent(Location location) {
		super(location);
		
	}

	 /**
     * Initialize Numerator and Denominator.
     */
    @Override
    public void prepare() {
        Qn = new HashMap<State, HashMap<Action, Double>>();
        Qd = new HashMap<State, HashMap<Action, Double>>();
        for(State s : getEnvironment().getPossibleStates(true)) {
        	Qn.put(s, new HashMap<Action, Double>());
        	Qd.put(s, new HashMap<Action, Double>());
        	for (Action a : Action.values()) {
        		Qn.get(s).put(a, 1.0);
        		Qd.get(s).put(a, 1.0);
          	}
        }
        super.prepare();
        
    }
    
    @Override
	protected Action getActionToPerform(State state) {
    	// random action
    	return Action.values()[(int) (Math.random() * (double) (Action.values().length-1))];
    }
    
    private double getPerformanceProbability(State state, Action action) {
    	return 1/ (double) Action.values().length;
	}

	@Override
	protected void updatePolicyFromEpisode(Episode episode) {
		// tau is the last moment where $a_\tau \neq \pi(s_\tau)$
		int tau = episode.getLength()-1; // TODO: off-by-one??
		while (tau > 1 && mPolicy.getActionSetBasedOnProbability(episode.getState(tau)).contains(episode.getAction(tau))){
			tau --;
		}
		for (int i=tau; i < episode.getLength(); i++) {
			State s = episode.getState(i);
			Action a = episode.getAction(i);
			
			// t is the time of first occurrence of (s,a) such that t >= tau
			int t = 0;
			for (int j=tau; j < i; j++) {
				if (episode.getState(j).equals(s) && episode.getAction(j).equals(a)) {
					// We've seen this state-action pair before
					t = j;
				}
			}
			double w = 1.0;
			for (int k = t+1; k < episode.getLength(); k++) {
				// Look at the off-policy
				w = w * (1/getPerformanceProbability(episode.getState(k), episode.getAction(k)));
			}
			// Update Numerator: $N_{sa} += w * R_t$
			Qn.get(s).put(a, Qn.get(s).get(a) + (w * getDiscountedReward(episode, t)));
			// Update Denominator: $D_{sa} += w$
			Qd.get(s).put(a, Qd.get(s).get(a) + w);
			// Update Q
			mPolicy.setActionValue(episode.getState(t), episode.getAction(t), Qn.get(s).get(a)/Qd.get(s).get(a)); 
		}
		
		// Make the policy greedy wrt Q
		for (State s : getEnvironment().getPossibleStates(true)) {
			double bestActionValue = Double.MIN_VALUE;
			for (final Action action : Action.values()) {
				bestActionValue = Math.max(bestActionValue, mPolicy.getActionValue(s, action));
			}
			double countBest = 0;
			for (final Action action : Action.values()) {
				if (mPolicy.getActionValue(s,action) == bestActionValue) {
					countBest++;
				}
			}
			
			double total = 0;
			for (final Action action : Action.values()) {
				if (mPolicy.getActionValue(s,action) == bestActionValue) {
					mPolicy.setActionProbability(s,	action, 1/countBest);
					total += 1/countBest;
				} else {
					mPolicy.setActionProbability(s,	action, 0);
				}
			}
		}
	}

	

}
