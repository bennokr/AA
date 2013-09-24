package com.uva.aa.agents;

import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy.
 */
public abstract class LearningPredatorAgent extends PredatorAgent {

    /** The default value for any Q(s,a) in this agent's policy */
    private final static double DEFAULT_ACTION_VALUE = 15;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public LearningPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * Starts with a random policy but will evaluate that to improve.
     */
    @Override
    public void prepare() {
        for (final State state : getEnvironment().getPossibleStates(false)) {
            for (final Action action : Action.values()) {
                mPolicy.setActionValue(state, action, DEFAULT_ACTION_VALUE);
            }
        }
    }

    /**
     * Performs an action during the agent's turn based on the policy.
     */
    public void performAction() {
        final State initialState = getEnvironment().getState();

        // Move to a location based on an action determined by the policy
        final Action action = getActionToPerform();
        moveTo(action.getLocation(this));

        final State resultingState = getEnvironment().getState();

        postActionCallback(initialState, resultingState, action);
    }

    /**
     * Decides what action should be performed for this agent's turn.
     * 
     * @return The action to perform
     */
    protected abstract Action getActionToPerform();

    // TODO: document this
    /**
     * 
     * @param initialState
     * @param resultingState
     * @param action
     */
    protected abstract void postActionCallback(final State initialState, final State resultingState, final Action action);

}
