package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with a Q-Learning Softmax predator and prey behaviour.
 */
public class SarsaGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);

        // Adds the two required agents
        game.addPrey(5, 5);
        game.addSarsaPredator(0, 0);

        return game;
    }
}
