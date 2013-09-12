package com.uva.aa.agents;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.Policy;

/**
 * An agent that acts as a prey within the environment. Will randomly move and won't kill other agents.
 */
public class PreyAgent extends Agent {

    /** The probability that the prey will move instead of wait */
    private final static double MOVE_PROBABILITY = 0.2;

    /**
     * Creates a new prey on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the prey at
     */
    public PreyAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The prey will often wait and otherwise make a random movement to an available location.
     * </p>
     */
    @Override
    public void performAction() {
        // Check which directions are valid moves (i.e., have no other agent on the resulting location)
        final List<Location> possibleLocations = new LinkedList<Location>();
        for (final Action direction : Action.values()) {
            final Location possibleLocation = direction.getLocation(this);
            if (!getEnvironment().isOccupied(possibleLocation)) {
                possibleLocations.add(possibleLocation);
            }
        }

        // If there are no locations to move to, wait
        if (possibleLocations.isEmpty()) {
            return;
        }

        // Choose whether to wait or move to a random (available) location
        final double decision = Math.random();
        if (decision < MOVE_PROBABILITY) {
            moveTo(possibleLocations.get((int) Math.floor(decision / MOVE_PROBABILITY * possibleLocations.size())));
        }
    }
}
