package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with R-Learning predator and prey behaviour.
 */
public class ParallelRLearningGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);
        game.setParallelActions(true);

        // Adds the two required agents
        game.addParallelRLearningPrey(5, 5);
        // game.addParallelPrey(5, 5);
        game.addParallelRLearningPredator(0, 0);
        game.addParallelRLearningPredator(10, 0);
        game.addParallelRLearningPredator(10, 10);
        game.addParallelRLearningPredator(0, 10);

        return game;
    }
}
