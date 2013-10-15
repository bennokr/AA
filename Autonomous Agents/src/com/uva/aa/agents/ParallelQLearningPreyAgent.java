package com.uva.aa.agents;

import java.util.List;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a prey within the environment. Will learn about the predator by following its policy using
 * Q-Learning.
 */
public abstract class ParallelQLearningPreyAgent extends ParallelLearningPreyAgent {

    /**
     * Creates a new prey on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the prey at
     */
    public ParallelQLearningPreyAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    protected void postActionCallback(final State initialState, final State resultingState,
            final Action previousAction, final Action nextAction) {
        
        // Determine the transation's details
        final double reward = getImmediateReward(initialState, resultingState, previousAction);
        final double initialActionValue = mPolicy.getActionValue(initialState, previousAction);

        // Find the value of the best possible next action
        double bestResultingActionValue = 0;
        for (double actionValue : mPolicy.getProperties(resultingState).getActionValues().values()) {
            bestResultingActionValue = Math.max(bestResultingActionValue, actionValue);
        }

        // Update the value for the action we previously took
        mPolicy.setActionValue(initialState, previousAction, initialActionValue + Config.PREY_STEP_SIZE_ALPHA
                * (reward + Config.PREY_DISCOUNT_FACTOR_GAMMA * bestResultingActionValue - initialActionValue));
    }

    /**
     * {@inheritDoc}
     */
    protected boolean shouldPickActionBeforeCallback() {
        return false;
    }
}
