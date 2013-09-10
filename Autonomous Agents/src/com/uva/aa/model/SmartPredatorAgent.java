package com.uva.aa.model;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.enums.Direction;

/**
 * An agent that acts as a predator within the environment. Will target a prey and try to hunt it down.
 */
public class SmartPredatorAgent extends PredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public SmartPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     * 
     * Will target a prey and try to move towards it.
     */
    @Override
    public void performAction() {
        final Environment environment = getEnvironment();
        final int environmentHalfWidth = environment.getHalfWidth();
        final int environmentHalfHeight = environment.getHalfHeight();

        // Check which directions are invalid moves (i.e., have a predator on the resulting location)
        final List<Direction> excludedDirections = new LinkedList<Direction>();
        for (final Direction direction : Direction.values()) {
            if (environment.isOccupiedByPredator(direction.getLocation(this))) {
                excludedDirections.add(direction);
            }
        }

        // Find the differences and distances between the locations
        final Location predatorLocation = getLocation();
        final Location preyLocation = environment.getPreys().get(0).getLocation();
        final int diffX = preyLocation.getX() - predatorLocation.getX();
        final int diffY = preyLocation.getY() - predatorLocation.getY();
        final int distX = Math.abs(diffX);
        final int distY = Math.abs(diffY);

        // Determine the best horizontal and vertical directions, based on the distances
        final boolean goLeft = (distX < environmentHalfWidth == diffX < 0);
        final boolean goUp = (distY < environmentHalfHeight == diffY < 0);
        final Direction bestX = (goLeft ? Direction.LEFT : Direction.RIGHT);
        final Direction worstX = (goLeft ? Direction.RIGHT : Direction.LEFT);
        final Direction bestY = (goUp ? Direction.UP : Direction.DOWN);
        final Direction worstY = (goUp ? Direction.DOWN : Direction.UP);

        // Check whether moving horizontal or vertical is preferred
        final Direction[] directions = new Direction[4];
        if (distX % (environmentHalfWidth + 1) > distY % (environmentHalfHeight + 1)) {
            directions[0] = bestX;
            directions[1] = bestY;
            directions[2] = worstY;
            directions[3] = worstX;
        } else {
            directions[0] = bestY;
            directions[1] = bestX;
            directions[2] = worstX;
            directions[3] = worstY;
        }

        // Try to return the best allowed direction
        Direction targetDirection = null;
        for (final Direction direction : directions) {
            if (!excludedDirections.contains(direction)) {
                targetDirection = direction;
                break;
            }
        }

        // Make sure there's a direction available
        if (targetDirection == null) {
            return;
        }

        // Perform the movement
        moveTo(targetDirection.getLocation(this));
    }
}
