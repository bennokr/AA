package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with a Minimax-Q game tester.
 */
public class ParallelMinimaxQGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);
        game.setParallelActions(true);

        // Adds the required agents
        game.addParallelMinimaxQPrey(5, 5);
        game.addParallelMinimaxQPredator(0, 0);

        return game;
    }
}
