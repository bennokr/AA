package com.uva.aa.agents;

import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.State;

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
     * Initialize the random policy: for each state the predator can be in, every possible action will be chosen with
     * the same probability.
     */
    public void prepare() {
        final List<State> possibleStatesExclTerminal = getEnvironment().getPossibleStates(false);

        for (final State state : possibleStatesExclTerminal) {
            for (final Action action : Action.values()) {
                mPolicy.setActionProbability(state, action, 1.0 / Action.values().length);
            }
        }
    }

    /**
     * The transition probability that accounts for the prey movement
     */
    public double getTransitionProbability(final State initialState, final State resultingState, final Action action) {
        // The moves should be deterministic
        if (resultingState.getAgentLocation(this).equals(action.getLocation(initialState.getAgentLocation(this)))) {
            // We assume one prey and one predator!
            PreyAgent prey = getEnvironment().getPreys().get(0);
            // Check if the prey is alive
            if (resultingState.getAgentLocation(prey) != null) {
                return prey.getTransitionProbability(initialState, resultingState, action);
            } else {
                return 1.0;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * The deterministic immediate reward
     */
    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
        // The reward does not depend on the action, only on the locations
        // The prey moves 'after' the predator
        // We assume one prey and one predator!
        PreyAgent prey = getEnvironment().getPreys().get(0);
        if (resultingState.getAgentLocation(this).equals(initialState.getAgentLocation(prey))) {
            return 10.0;
        } else {
            return 0.0;
        }
    }
}
