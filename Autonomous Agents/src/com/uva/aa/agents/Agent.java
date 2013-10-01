package com.uva.aa.agents;

import com.uva.aa.Environment;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.Policy;

/**
 * An agent that will act within the environment.
 */
public abstract class Agent {

    /** The policy that the agent should follow */
    protected final Policy mPolicy = new Policy();

    /** The current location of the agent within the environment */
    private Location mLocation;

    /**
     * Creates a new agent on the specified location.
     * 
     * @param location
     *            The location to place the agent at
     */
    public Agent(final Location location) {
        mLocation = location;
    }

    /**
     * Performs an action during the agent's turn based on the policy.
     */
    public void performAction() {
        // Move to a location based on an action determined by the policy
        moveTo(mPolicy.getActionBasedOnProbability(getEnvironment().getState()).getLocation(this));
    }

    /**
     * Moves the agent to the specified location within the environment. If there is an agent already occupying the
     * location, the occupying agent will be killed.
     * 
     * @param location
     *            The location to move to
     */
    public void moveTo(final Location location) {
        // Print the move taken if needed
        if (getEnvironment().getGame().shouldPrintMoves()) {
            System.out.println(getClass().getSimpleName() + " moves from (" + mLocation.getX() + ", "
                    + mLocation.getY() + ") to (" + location.getX() + ", " + location.getY() + ")");
        }

        // Kills the agent present at the location, if any
        final Agent occupyingAgent = getEnvironment().getOccupyingAgent(location);
        if (occupyingAgent != null && occupyingAgent != this) {
            occupyingAgent.die();
        }

        mLocation = location;
    }

    /**
     * Kills the agent, removing it from the environment.
     */
    public void die() {
        getEnvironment().removeAgent(this);
    }

    /**
     * Retrieves the environment that the agent is in.
     * 
     * @return The agent's environment
     */
    public Environment getEnvironment() {
        return mLocation.getEnvironment();
    }

    /**
     * Retrieves the location that the agent is at within its environment.
     * 
     * @return The agent's location
     */
    public Location getLocation() {
        return mLocation;
    }

    /**
     * Prepares the agent before starting a game.
     */
    public abstract void prepare();

    /**
     * Retrieves the probability of going from initialState to resultingState when this agent performs action.
     * 
     * @param initialState
     *            The initial state
     * @param resultingState
     *            The resulting state
     * @param action
     *            This agent's action
     * 
     * @return The probability of the action getting to the resulting state
     */
    public abstract double getTransitionProbability(final State initialState, final State resultingState,
            final Action action);

    /**
     * Retrieves this agents immediate reward for going from initialState to resultingState when this agent performs
     * action.
     * 
     * @param initialState
     *            The initial state
     * @param resultingState
     *            The resulting state
     * @param action
     *            This agent's action
     * 
     * @return The reward of moving states through the given action
     */
    public abstract double getImmediateReward(final State initialState, final State resultingState, final Action action);

    /**
     * Retrieves the policy that the agent is following.
     * 
     * @return The agent's policy
     */
    public Policy getPolicy() {
        return mPolicy;
    }

    /**
     * Called after the game has finished to allow the agent to make final updates to its policy.
     */
    public void postGameCallback() {}

    @Override
    public String toString() {
        final String className = getClass().getSimpleName();
        return className.substring(0, className.length() - 5);
    }
}
