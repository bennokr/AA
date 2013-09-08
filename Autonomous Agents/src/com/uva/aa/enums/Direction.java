package com.uva.aa.enums;

import com.uva.aa.Location;
import com.uva.aa.model.Agent;

/**
 * Denotes the different possible directions an agents can go to.
 */
public enum Direction {

    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    /** The relative location that this direction goes to */
    final Location mLocation;

    /**
     * Prepares a direction with the specified coordinates.
     * 
     * @param x
     *            The change in the x coordinate after going in this direction
     * @param y
     *            The change in the y coordinate after going in this direction
     */
    Direction(final int x, final int y) {
        mLocation = new Location(null, x, y);
    }

    /**
     * Retrieves the new location for the specified agent going in this direction.
     * 
     * @param agent
     *            The agent going in this direction
     *            
     * @return The new location of the agent
     */
    public Location getLocation(final Agent agent) {
        return mLocation.add(agent.getLocation());
    }
}
