package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy using
 * R-Learning.
 */
public class ParallelRLearningPredatorAgent extends ParallelLearningPredatorAgent {

    /** The rho used for updating action values */
    private double mRho;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ParallelRLearningPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

        // Find the value used for updating the action and rho
        final double actionValueUpdate = reward - mRho + bestResultingActionValue - initialActionValue;

        // Update the value for the action we previously took
        mPolicy.setActionValue(initialState, previousAction, initialActionValue + Config.STEP_SIZE_ALPHA
                * actionValueUpdate);

        // Update rho
        if (bestResultingActionValue == initialActionValue) {
            mRho += Config.STEP_SIZE_BETA * actionValueUpdate;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldPickActionBeforeCallback() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Action getActionToPerform(final State state) {
        final Action action = mPolicy.getActionBasedOnValueEpsilonGreedy(state, Config.EPSILON);
//        System.out.println(action);
        return action;
    }
}
