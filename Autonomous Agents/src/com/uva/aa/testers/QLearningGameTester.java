package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with a Q-Learning predator and prey behaviour.
 */
public class QLearningGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);

        // Adds the two required agents
        game.addPrey(5, 5);
        game.addQLearningPredator(0, 0);

        return game;
    }
}
