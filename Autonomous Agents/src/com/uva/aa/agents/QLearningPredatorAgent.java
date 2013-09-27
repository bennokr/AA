package com.uva.aa.agents;

import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy using
 * Q-Learning.
 */
public abstract class QLearningPredatorAgent extends LearningPredatorAgent {

    /** The epsilon for the epsilon-greedy manner */
    private final static double STEP_SIZE_ALPHA = 0.1;

    /** The discount factor of the bellman equation */
    public static final double DISCOUNT_FACTOR_GAMMA = 0.7;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public QLearningPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    protected void postActionCallback(final State initialState, final State resultingState,
            final Action previousAction, final Action nextAction) {
        // Target our prey
        final List<PreyAgent> preys = getEnvironment().getPreys();
        final PreyAgent prey = (!preys.isEmpty() ? preys.get(0) : null);

        // Determine the transation's details
        final State immediateNextState = State.buildState(this, resultingState.getAgentLocation(this), prey,
                initialState.getAgentLocation(prey));
        final double reward = getImmediateReward(initialState, immediateNextState, previousAction);
        final double initialActionValue = mPolicy.getActionValue(initialState, previousAction);

        // Find the value of the best possible next action
        double bestResultingActionValue = 0;
        for (double actionValue : mPolicy.getProperties(resultingState).getActionValues().values()) {
            bestResultingActionValue = Math.max(bestResultingActionValue, actionValue);
        }

        // Update the value for the action we previously took
        mPolicy.setActionValue(initialState, previousAction, initialActionValue + STEP_SIZE_ALPHA
                * (reward + DISCOUNT_FACTOR_GAMMA * bestResultingActionValue - initialActionValue));
    }

    /**
     * {@inheritDoc}
     */
    protected boolean shouldPickActionBeforeCallback() {
        return false;
    }
}
