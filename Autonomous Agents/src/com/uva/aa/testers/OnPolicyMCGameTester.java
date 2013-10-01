package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * Runs simple test with a On-Policy Monte-Carlo game tester.
 */
public class OnPolicyMCGameTester extends GameTester {

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);

        // Adds the two required agents
        game.addPrey(5, 5);
        game.addOnPolicyMCPredator(0, 0);

        return game;
    }
}
