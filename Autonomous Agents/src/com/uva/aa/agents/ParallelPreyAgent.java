package com.uva.aa.agents;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will randomly move, hoping to catch a prey.
 */
public class ParallelPreyAgent extends PredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ParallelPreyAgent(final Location location) {
        super(location);
    }

    /**
     * The deterministic immediate reward based on if a prey is caught by any predator. Will punish for colliding with
     * other predators.
     */
    @Override
    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
        // Check all the predator locations
        final List<Location> predatorLocations = new LinkedList<Location>();
        for (final PredatorAgent predator : initialState.getPredators()) {
            final Location location = resultingState.getAgentLocation(predator);
            if (predatorLocations.contains(location)) {
                // The prey gets rewarded when predators end up in the same location
                return -Config.COLLISION_REWARD;
            }
            predatorLocations.add(location);
        }

        // If a prey from the intial state is caught, it gets a negative reward
        for (final PreyAgent prey : initialState.getPreys()) {
            final Location location = resultingState.getAgentLocation(prey);
            if (location == null || predatorLocations.contains(location)) {
                return -Config.KILL_REWARD;
            }
        }

        return 0.0;
    }
}
