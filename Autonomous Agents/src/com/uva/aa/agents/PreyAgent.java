package com.uva.aa.agents;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a prey within the environment. Will randomly move and won't kill other agents.
 */
public class PreyAgent extends Agent {

    /** The probability that the prey will move instead of wait */
    protected final static double MOVE_PROBABILITY = 0.2;

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
     */
    @Override
    public void prepare() {
        for (final State state : getEnvironment().getPossibleStates(false)) {
            // Check which actions are valid moves (i.e., have no other agent on the resulting location)
            final List<Action> possibleActions = new LinkedList<Action>();
            for (final Action action : Action.values()) {
                final Location newLocation = action.getLocation(state.getAgentLocation(this));
                if (!state.isOccupied(newLocation)) {
                    // Action.WAIT cannot get here as the prey occupies itself, this is wanted behaviour
                    possibleActions.add(action);
                }
            }

            // Determine the chance to wait
            if (possibleActions.isEmpty()) {
                mPolicy.setActionProbability(state, Action.WAIT, 1);
            } else {
                mPolicy.setActionProbability(state, Action.WAIT, 1 - MOVE_PROBABILITY);
            }

            // Assign the changes to move to a different location
            final double moveProbability = MOVE_PROBABILITY / possibleActions.size();
            for (final Action action : possibleActions) {
                mPolicy.setActionProbability(state, action, moveProbability);
            }
        }
    }

    /**
     * Has a 0.8 chance of waiting, and a spread chance of moving to a free adjacent space.
     */
    @Override
    public double getTransitionProbability(final State initialState, final State resultingState, final Action action) {
        final Location resultingPreyLocation = resultingState.getAgentLocation(this);
        // Return the probability of the prey moving to its location in the resulting state from where it is in
        // the initial state
        for (final Action possibleAction : Action.values()) {
            // Check if the prey moved in this direction
            final Location newLocation = possibleAction.getLocation(initialState.getAgentLocation(this));
            if (newLocation.equals(resultingPreyLocation)) {
                // Return the chance we moved here
                return mPolicy.getActionProbability(initialState, possibleAction);
            }
        }

        // Only reached when the state change cannot be reached with one action
        return 0.0;
    }

    /**
     * Preys don't get rewards, thus this always returns 0.
     */
    @Override
    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
        return 0;
    }
}
