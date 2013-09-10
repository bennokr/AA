package com.uva.aa.policies;

import java.util.HashMap;
import java.util.Map;

import com.uva.aa.enums.Action;

/**
 * Holds the map for the possible actions with their probabilities to all available state.
 */
public class Policy {
	
	/** The map holding the actions with probabilities for their state */
	private Map<State, HashMap<Action, Double>> mMap = new HashMap<State, HashMap<Action, Double>>();
	
	/**
	 * Sets the actions with their probabilities for a state.
	 * 
	 * @param state The state to set the actions with probabilities for
	 * @param actionProbs The actions and probabilities
	 */
	public void setActionProbs(final State state, final HashMap<Action, Double> actionProbs) {
		mMap.put(state, actionProbs);
	}
	
	/**
	 * Returns the mapped actions with probabilities for their state.
	 * 
	 * @return The map of states to action with probabilities
	 */
	public Map<State, HashMap<Action, Double>> getMap() {
		return mMap;
	}
	
	/**
	 * Prints out the full mapped contents.
	 */
	public void print() {
    	for (Map.Entry<State, HashMap<Action, Double>> stateActionProbs : mMap.entrySet()) {
    		// Print the state
    		stateActionProbs.getKey().print();

    		// Print the actions with their probability
    		final HashMap<Action, Double> actionProbs = stateActionProbs.getValue();
        	for (Map.Entry<Action, Double> actionProb : actionProbs.entrySet()) {
        		System.out.println("    " + actionProb.getKey() + " = " + actionProb.getValue());
        	}
    	}
    	System.out.println();
	}
}
