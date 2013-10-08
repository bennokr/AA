package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with a Q-Learning e-Greedy predator and prey behaviour.
 */
public class ParallelGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);
        game.setParallelActions(true);

        // Adds the two required agents
        game.addParallelPrey(5, 5);
        game.addParallelPredator(0, 0);
        game.addParallelPredator(10, 0);
        game.addParallelPredator(10, 10);
        game.addParallelPredator(0, 10);

        return game;
    }
}
