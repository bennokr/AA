package com.uva.aa;

import java.util.ArrayList;

import com.uva.aa.enums.Action;

/**
 * An episode for a game for a single predator.
 */
public class Episode {

    /** The states we were in */
    private ArrayList<State> states;

    /** The actions we performed */
    private ArrayList<Action> actions;

    /** The rewards we received */
    private ArrayList<Double> rewards;

    /**
     * Prepares a new episode for the start of a game.
     */
    public Episode() {
        states = new ArrayList<State>();
        actions = new ArrayList<Action>();
        rewards = new ArrayList<Double>();
    }

    /**
     * Clears the episode for a new game.
     */
    public void clear() {
        states.clear();
        actions.clear();
        rewards.clear();
    }

    /**
     * Adds a state to the episode.
     * 
     * @param currentState
     *            The state to add
     */
    public void addState(final State currentState) {
        states.add(currentState);
    }

    /**
     * Adds an action to the episode.
     * 
     * @param currentState
     *            The action to add
     */
    public void addAction(final Action nextAction) {
        actions.add(nextAction);
    }

    /**
     * Adds a reward to the episode.
     * 
     * @param currentState
     *            The reward to add
     */
    public void addReward(final double immediateReward) {
        rewards.add(immediateReward);
    }

    /**
     * Retrieves the amount of actions taken.
     * 
     * @return The length of the episode
     */
    public int getLength() {
        return actions.size();
    }

    /**
     * Retrieves the reward of a certain point in the episode
     * 
     * @param timestep
     *            The timestep to look in the episode at
     * 
     * @return The reward at the timestep
     */
    public Double getReward(final int timestep) {
        return rewards.get(timestep);
    }

    /**
     * Retrieves the state of a certain point in the episode
     * 
     * @param timestep
     *            The timestep to look in the episode at
     * 
     * @return The state at the timestep
     */
    public State getState(final int timestep) {
        return states.get(timestep);
    }

    /**
     * Retrieves the action of a certain point in the episode
     * 
     * @param timestep
     *            The timestep to look in the episode at
     * 
     * @return The action at the timestep
     */
    public Action getAction(final int timestep) {
        return actions.get(timestep);
    }

}
