package com.uva.aa.model;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.enums.Direction;

/**
 * An agent that acts as a predator within the environment. Will randomly move, hoping to catch a prey.
 */
public class PredatorAgent extends Agent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public PredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     * 
     * Will randomly move, hoping to catch a prey.
     */
    @Override
    public void performAction() {
        // Check which directions are valid moves (i.e., have no other agent on the resulting location)
        final List<Location> possibleLocations = new LinkedList<Location>();
        for (final Direction direction : Direction.values()) {
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
        final double moveProbability = 1 - (1.0 / (possibleLocations.size() + 1));
        final double decision = Math.random();
        if (decision < moveProbability) {
            moveTo(possibleLocations.get((int) Math.floor(decision / moveProbability * possibleLocations.size())));
        }
    }
}
