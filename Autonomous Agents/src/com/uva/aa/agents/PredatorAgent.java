package com.uva.aa.agents;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.Policy;

/**
 * An agent that acts as a predator within the environment. Will randomly move, hoping to catch a prey.
 */
public class PredatorAgent extends Agent {
	
    /** The policy the predator follows */
	private Policy mPolicy;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public PredatorAgent(final Location location) {
        super(location);
        
        initPolicy();
    }
    
    public void initPolicy() {
    	// TODO: Luisa will do this
    }
    
    public void evaluatePolicy() {
    	// TODO: Luisa will do this
    }

    /**
     * {@inheritDoc}
     * 
     * Will randomly move, hoping to catch a prey.
     */
    @Override
    public void performAction() {
    	
    	// TODO: Follow policy instead of custom logic
    	
        // Check which directions are valid moves (i.e., have no other agent on the resulting location)
        final List<Location> possibleLocations = new LinkedList<Location>();
        for (final Action direction : Action.values()) {
            final Location possibleLocation = direction.getLocation(this);
            if (!getEnvironment().isOccupiedByPredator(possibleLocation)) {
                possibleLocations.add(possibleLocation);
            }
        }

        // If there are no locations to move to, wait
        if (possibleLocations.isEmpty()) {
            return;
        }

        // Choose whether to wait or move to a random (available) location
        moveTo(possibleLocations.get((int) Math.floor(Math.random() * possibleLocations.size())));
    }
}
