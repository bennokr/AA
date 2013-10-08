package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a prey within the environment. Will learn about the predator by following its policy in an
 * epsilon-greedy manner using Q-Learning.
 */
public class ParallelQLearningEGreedyPreyAgent extends ParallelQLearningPreyAgent {

    /**
     * Creates a new prey on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the prey at
     */
    public ParallelQLearningEGreedyPreyAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    protected Action getActionToPerform(final State state) {
        final double decision = Math.random();
        if (decision <= 0.2) {
            return Action.WAIT;
        } else {
            return mPolicy.getActionBasedOnValueEpsilonGreedy(state, Config.PREY_EPSILON);
        }
    }
}
