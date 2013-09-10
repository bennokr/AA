package com.uva.aa.model;

import com.uva.aa.Location;

/**
 * An agent that will act within the environment.
 */
public abstract class Agent {

    /** The current location of the agent within the environment */
    private Location mLocation;

    /**
     * Creates a new agent on the specified location.
     * 
     * @param location
     *            The location to place the agent at
     */
    public Agent(final Location location) {
        mLocation = location;
    }

    /**
     * Performs an action during the agent's turn, often involving movement.
     */
    public abstract void performAction();

    /**
     * Moves the agent to the specified location within the environment. If there is an agent already occupying the
     * location, the occupying agent will be killed.
     * 
     * @param location
     *            The location to move to
     */
    public void moveTo(final Location location) {
    	// Print the move taken if needed
    	if (getEnvironment().getGame().shouldPrintMoves()) {
	        System.out.println(getClass().getSimpleName() + " moves from (" + mLocation.getX() + ", " + mLocation.getY()
	                + ") to (" + location.getX() + ", " + location.getY() + ")");
    	}

        // Kills the agent present at the location, if any
        final Agent occupyingAgent = getEnvironment().getOccupyingAgent(location);
        if (occupyingAgent != null) {
            occupyingAgent.die();
        }

        mLocation = location;
    }

    /**
     * Kills the agent, removing it from the environment.
     */
    public void die() {
        getEnvironment().removeAgent(this);
    }

    /**
     * Retrieves the environment that the agent is in.
     * 
     * @return The agent's environment
     */
    public Environment getEnvironment() {
        return mLocation.getEnvironment();
    }

    /**
     * Retrieves the location that the agent is at within its environment.
     * 
     * @return The agent's location
     */
    public Location getLocation() {
        return mLocation;
    }
}
