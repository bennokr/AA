package com.uva.aa.enums;

import com.uva.aa.Location;
import com.uva.aa.agents.Agent;

/**
 * Denotes the different possible actions an agent can perform.
 */
public enum Action {

    WAIT(0, 0),
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    /** The relative location that this direction goes to */
    final Location mLocation;

    /**
     * Prepares an action with the specified coordinates.
     * 
     * @param x
     *            The change in the x coordinate after going in this direction
     * @param y
     *            The change in the y coordinate after going in this direction
     */
    Action(final int x, final int y) {
        mLocation = new Location(null, x, y);
    }

    /**
     * Retrieves the new location for the specified agent performing this action.
     * 
     * @param agent
     *            The agent performing this action
     *            
     * @return The new location of the agent
     */
    public Location getLocation(final Agent agent) {
        return getLocation(agent.getLocation());
    }

    /**
     * Retrieves the new location for the specified location after performing this action.
     * 
     * @param location
     *            The the location before performing the action
     *            
     * @return The new location
     */
    public Location getLocation(final Location location) {
        return mLocation.add(location);
    }
}
