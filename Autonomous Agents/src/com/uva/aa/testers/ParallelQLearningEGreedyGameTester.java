package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with a On-Policy Monte-Carlo game tester.
 */
public class ParallelQLearningEGreedyGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);
        game.setParallelActions(true);

        // Adds the required agents
        game.addParallelQLearningEGreedyPrey(5, 5);
        //game.addParallelPrey(5, 5);
        game.addParallelQLearningEGreedyPredator(0, 0);
        game.addParallelQLearningEGreedyPredator(10, 0);
        //game.addParallelQLearningEGreedyPredator(10, 10);
        //game.addParallelQLearningEGreedyPredator(0, 10);
        
        return game;
    }
}
