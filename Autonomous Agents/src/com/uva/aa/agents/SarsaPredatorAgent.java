package com.uva.aa.agents;

import java.util.List;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy using
 * Sarsa
 */
public class SarsaPredatorAgent extends LearningPredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public SarsaPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    protected Action getActionToPerform(final State state) {
        return mPolicy.getActionBasedOnValueEpsilonGreedy(state, Config.EPSILON);
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
        final double nextActionValue = mPolicy.getActionValue(resultingState, nextAction);

        // Update the value for the action we previously took
        mPolicy.setActionValue(initialState, previousAction, initialActionValue + Config.STEP_SIZE_ALPHA
                * (reward + Config.DISCOUNT_FACTOR_GAMMA * nextActionValue - initialActionValue));
    }

    /**
     * {@inheritDoc}
     */
    protected boolean shouldPickActionBeforeCallback() {
        return true;
    }
}
