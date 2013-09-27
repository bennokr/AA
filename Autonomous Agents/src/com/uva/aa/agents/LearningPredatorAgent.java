package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy.
 */
public abstract class LearningPredatorAgent extends PredatorAgent {

    private State mLastState;
    private Action mLastAction;

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
                mPolicy.setActionValue(state, action, Config.DEFAULT_ACTION_VALUE);
            }
        }
    }

    /**
     * Performs an action during the agent's turn based on the policy.
     */
    public void performAction() {
        final State currentState = getEnvironment().getState();
        Action nextAction = null;

        // Pick an action if it should be picked before the learning step
        if (shouldPickActionBeforeCallback()) {
            nextAction = getActionToPerform(currentState);
        }

        // Allow the learning algorithm to update values
        if (mLastState != null) {
            postActionCallback(mLastState, currentState, mLastAction, nextAction);
        }

        // Pick the action if it wasn't picked before the callback
        if (nextAction == null) {
            nextAction = getActionToPerform(currentState);
        }

        // Move to a location based on an action determined by the policy
        moveTo(nextAction.getLocation(this));

        mLastAction = nextAction;
        mLastState = currentState;
    }

    /**
     * {@inheritDoc}
     */
    public void postGameCallback() {
        if (mLastState != null) {
            postActionCallback(mLastState, getEnvironment().getState(), mLastAction, null);
        }
    }

    /**
     * Decides what action should be performed for this agent's turn.
     * 
     * @param state
     *            The state that we're currently in
     * 
     * @return The action to perform
     */
    protected abstract Action getActionToPerform(State state);

    /**
     * Called after an action when the next state right before the next action is known.
     * 
     * @param initialState
     *            The state before performing the action
     * @param resultingState
     *            The state after performing the action and the other agents have taken turns
     * @param previousAction
     *            The action executed at the initial state
     * @param nextAction
     *            The action about to be executed in the resulting state
     */
    protected abstract void postActionCallback(State initialState, State resultingState, Action previousAction,
            Action nextAction);

    /**
     * Should be overridden to indicate whether the next action should before allowing the learning algorithm to update
     * values or after it.
     * 
     * @return True if an action should be picked first, false if it should be picked after calling the callback
     */
    protected abstract boolean shouldPickActionBeforeCallback();

}
