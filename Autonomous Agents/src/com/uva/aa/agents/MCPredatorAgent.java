package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.Episode;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following a Monte Carlo based
 * policy.
 */
public abstract class MCPredatorAgent extends PredatorAgent {

    /** The episode of the current game */
    private final Episode mEpisode = new Episode();

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public MCPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * Starts with a random policy but will evaluate that to improve.
     */
    @Override
    public void prepare() {
        for (final State state : getEnvironment().getPossibleStates(false)) {
            for (final Action action : Action.values()) {
                // initialize action values
                mPolicy.setActionValue(state, action, Config.DEFAULT_ACTION_VALUE);
                // initialize action probabilities
                mPolicy.setActionProbability(state, action, 1.0 / Action.values().length);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAction(final State roundStartState) {
        // Log state
        final State currentState = getEnvironment().getState();
        mEpisode.addState(currentState);

        // Log action
        Action nextAction = getActionToPerform(currentState);
        mEpisode.addAction(nextAction);

        // Move to a location based on an action determined by the policy
        moveTo(nextAction.getLocation(this));

        // Log the reward that was received by taking the action
        mEpisode.addReward(getImmediateReward(currentState, getEnvironment().getState(), nextAction));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postGameCallback() {
        // Log the last state
        mEpisode.addState(getEnvironment().getState());

        // Run update stuff
        updatePolicyFromEpisode(mEpisode);

        // Clear episode logging
        mEpisode.clear();
    }

    /**
     * Get the discounted return at this timestep in the episode
     * 
     * @param episode
     *            The episode to get the return from
     * @param timestep
     *            Which step the reward should be discounted for
     * 
     * @return A discounted return based on how many steps away we are
     */
    protected double getDiscountedReturn(final Episode episode, final int timestep) {
        double R = 0.0;
        for (int tempTimestep = timestep; tempTimestep < episode.getLength(); tempTimestep++) {
            R += Math.pow(Config.DISCOUNT_FACTOR_GAMMA, tempTimestep - timestep) * episode.getReward(tempTimestep);
        }
        return R;
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
     * Updates the current policy based on information from the episode we've run
     * 
     * @param episode
     *            The episode to base our update on
     */
    protected abstract void updatePolicyFromEpisode(Episode episode);

}
