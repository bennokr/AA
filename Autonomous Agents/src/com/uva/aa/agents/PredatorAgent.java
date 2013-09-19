package com.uva.aa.agents;

import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will randomly move, hoping to catch a prey.
 */
public class PredatorAgent extends Agent {

    /** The reward for killing a prey */
    public static final double KILL_REWARD = 10.0;

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
     * Initialize the random policy: for each state the predator can be in, every possible action will be chosen with
     * the same probability.
     */
    public void prepare() {
        for (final State state : getEnvironment().getPossibleStates(false)) {
            for (final Action action : Action.values()) {
                mPolicy.setActionProbability(state, action, 1.0 / Action.values().length);
            }
        }
    }

    /**
     * The transition probability that accounts for the prey movement.
     */
    public double getTransitionProbability(final State initialState, final State resultingState, final Action action) {
        final Location nextPredatorLocation = resultingState.getAgentLocation(this);

        // Verify that the action is possible
        if (nextPredatorLocation.equals(action.getLocation(initialState.getAgentLocation(this)))) {
            // We assume one prey and one predator!
            final PreyAgent prey = getEnvironment().getPreys().get(0);

            // Check if the prey is alive
            if (resultingState.getAgentLocation(prey) != null) {
                // Return a probability based on the prey's movement
                final Location initialPreyLocation = initialState.getAgentLocation(prey);
                final State initialPreyState = State.buildState(this, nextPredatorLocation, prey, initialPreyLocation);
                return prey.getTransitionProbability(initialPreyState, resultingState, null);
            } else {
                // We'll catch the prey with this move
                return 1.0;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * The deterministic immediate reward based on if a prey is caught.
     */
    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
        // The reward does not depend on the action, only on the locations
        // The prey moves 'after' the predator
        // We assume one prey and one predator!
        PreyAgent prey = getEnvironment().getPreys().get(0);
        if (resultingState.getAgentLocation(this).equals(initialState.getAgentLocation(prey))) {
            return KILL_REWARD;
        } else {
            return 0.0;
        }
    }
}
