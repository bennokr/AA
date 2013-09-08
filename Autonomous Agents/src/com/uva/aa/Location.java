package com.uva.aa;

import com.uva.aa.model.Environment;

/**
 * A location within an environment.
 */
public class Location {

    /** The environment this location is within */
    private Environment mEnvironment;

    /** The x coordinate for this location */
    private int mX;

    /** The y coordinate for this location */
    private int mY;

    /**
     * Creates a new location for the specified coordinates, optionally within an environment.
     * 
     * @param environment
     *            The environment the location is within or null if it's not in any specific environment
     * @param x
     *            The x coordinate within the environment
     * @param y
     *            The y coordinate within the environment
     */
    public Location(final Environment environment, final int x, final int y) {
        mEnvironment = environment;

        if (environment == null) {
            // If there's no specific environment, just keep the coordinates
            mX = x;
            mY = y;

        } else {
            // When there's an environment, make sure the coordinates fit within it

            final int width = environment.getWidth();
            final int height = environment.getHeight();

            mX = x % width;
            mY = y % height;

            if (mX < 0) {
                mX += width;
            }
            if (mY < 0) {
                mY += height;
            }
        }
    }

    /**
     * Retrieves the environment that the location is in.
     * 
     * @return The location's environment
     */
    public Environment getEnvironment() {
        return mEnvironment;
    }

    /**
     * Retrieves the x coordinate within the location's environment.
     * 
     * @return The x coordinate
     */
    public int getX() {
        return mX;
    }

    /**
     * Retrieves the y coordinate within the location's environment.
     * 
     * @return The y coordinate
     */
    public int getY() {
        return mY;
    }

    /**
     * Checks if this location has the same coordinates as the given one.
     * 
     * @param location
     *            The location to compare
     * 
     * @return True of the coordinates are the same, false otherwise
     */
    public boolean equals(final Location location) {
        return mX == location.getX() && mY == location.getY();
    }

    /**
     * Adds the x and y values of two locations together within a certain environment.
     * 
     * @param location
     *            The location to add to the current one
     * 
     * @return A location with the combined coordinates within the environment
     * 
     * @throws RuntimeException
     *             Thrown when the locations' environments are both set but don't match
     */
    public Location add(final Location location) {
        final Environment targetEnv = location.getEnvironment();
        if (mEnvironment != null && targetEnv != null && mEnvironment != targetEnv) {
            // Should throw a proper exception when multiple running environments will actually occur
            throw new RuntimeException("Environments to not match.");
        }

        return new Location(mEnvironment != null ? mEnvironment : targetEnv, mX + location.getX(), mY + location.getY());
    }
}
