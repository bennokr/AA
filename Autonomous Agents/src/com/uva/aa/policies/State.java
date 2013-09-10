package com.uva.aa.policies;

import java.util.Map;

import com.uva.aa.Location;
import com.uva.aa.agents.Agent;

public class State {

    private final Map<Agent, Location> mAgentLocations;
    
    public State(final Map<Agent, Location> agentLocations) {
    	mAgentLocations = agentLocations;
    }
    
    public Map<Agent, Location> getAgentLocations() {
		return mAgentLocations;
	}

	public boolean equals(final State state) {
		final Map<Agent, Location> otherAgentLocations = state.getAgentLocations();
		
    	if (mAgentLocations.size() != otherAgentLocations.size()) {
    		return false;
    	}
    	
    	for (Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
    		final Location otherAgentLocation = otherAgentLocations.get(agentLocation.getKey());
    		if (otherAgentLocation == null) {
    			return false;
    		}
    		if (!agentLocation.equals(otherAgentLocation)) {
    			return false;
    		}
    	}
    	
    	return true;
    }
	
	public void print() {
    	for (Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
    		final Location location = agentLocation.getValue();
    		System.out.print(agentLocation.getKey() + "(" + location.getX() + "," + location.getY() + ") ");
    	}
    	System.out.println();
	}
}
