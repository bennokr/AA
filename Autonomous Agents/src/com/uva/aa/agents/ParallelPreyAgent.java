package com.uva.aa.agents;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.StatePolicyProperties;

/**
 * An agent that acts as a prey within the environment.
 */
public class ParallelPreyAgent extends PreyAgent {

    /**
     * Creates a new prey on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the prey at
     */
    public ParallelPreyAgent(final Location location) {
        super(location);
        mPolicy.setTargetAgent(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        // Prepare the default properties instead of the properties for each state
        final StatePolicyProperties defaultProperties = mPolicy.getDefaultProperties();

        final List<Action> possibleActions = new LinkedList<Action>();
        for (final Action action : Action.values()) {
            if (action == Action.WAIT) {
                continue;
            }
            possibleActions.add(action);
        }

        // Determine the chance to wait
        defaultProperties.setActionProbability(Action.WAIT, 1 - MOVE_PROBABILITY);

        // Assign the changes to move to a different location
        final double moveProbability = MOVE_PROBABILITY / possibleActions.size();
        for (final Action action : possibleActions) {
            defaultProperties.setActionProbability(action, moveProbability);
        }
    }

    /**
     * The deterministic immediate reward based on if a prey is caught by any predator. Will be rewarded if predators
     * collide and punished if it is caught by a predator.
     */
    @Override
    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
        // Check all the predator locations
        final List<Location> predatorLocations = new LinkedList<Location>();
        for (final PredatorAgent predator : initialState.getPredators()) {
            final Location location = resultingState.getAgentLocation(predator);
            if (predatorLocations.contains(location)) {
                // The prey gets rewarded when prey end up in the same location
                return Config.PREY_ESCAPE_REWARD;
            }
            predatorLocations.add(location);
        }

        // If a prey from the intial state is caught, it gets a negative reward
        for (final PreyAgent prey : initialState.getPreys()) {
            final Location location = resultingState.getAgentLocation(prey);
            if (location == null || predatorLocations.contains(location)) {
                return Config.PREY_DIE_REWARD;
            }
        }

        return 0.0;
    }
}
