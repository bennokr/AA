package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.Episode;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy.
 */
public abstract class MCPredatorAgent extends PredatorAgent {

    private Episode episode;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public MCPredatorAgent(final Location location) {
        super(location);
        episode = new Episode();
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
     * Performs an action during the agent's turn based on the policy.
     */
    public void performAction() {
        // Log state
        final State currentState = getEnvironment().getState();
        episode.addState(currentState);
        
        // Log action
        Action nextAction = getActionToPerform(currentState);
        episode.addAction(nextAction);
        
        // Move to a location based on an action determined by the policy
        moveTo(nextAction.getLocation(this));
        
        episode.addReward(getImmediateReward(currentState, getEnvironment().getState(), nextAction));
    }

    /**
     * {@inheritDoc}
     */
    public void postGameCallback() {
        // Log the last state
        episode.addState(getEnvironment().getState());
        
        // Run update stuff
        updatePolicyFromEpisode(episode);
        
        // Clear episode logging
        episode.clear();
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
     * @param episode The episode to base our update on
     */
    protected abstract void updatePolicyFromEpisode(Episode episode);

}
