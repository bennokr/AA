package com.uva.aa.agents;

import com.uva.aa.Config;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following its policy in an
 * epsilon-greedy manner using Q-Learning.
 */
public class QLearningEGreedyPredatorAgent extends QLearningPredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public QLearningEGreedyPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    protected Action getActionToPerform(final State state) {
        return mPolicy.getActionBasedOnValueEpsilonGreedy(state, Config.EPSILON);
    }
}
