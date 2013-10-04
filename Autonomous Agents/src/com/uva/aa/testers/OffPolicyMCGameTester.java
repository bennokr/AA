package com.uva.aa.testers;

import com.uva.aa.Game;
import com.uva.aa.agents.OffPolicyMCPredatorAgent;

/**
 * Runs simple test with a On-Policy Monte-Carlo game tester.
 */
public class OffPolicyMCGameTester extends GameTester {

    /** The predator that performs the tests */
    private OffPolicyMCPredatorAgent predator;

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);

        // Adds the two required agents
        game.addPrey(5, 5);
        game.addOffPolicyMCPredator(0, 0);

        predator = (OffPolicyMCPredatorAgent) game.getEnvironment().getPredators().get(0);

        return game;
    }

    /**
     * {@inheritDoc}
     */
    public int performSingleTest(final int numRun) {
        // Use Off-Policy Monte Carlo to train before following the trained policy
        predator.setTraining(true);

        // Make sure that the game is ready
        mGame.resetGame();

        // Perform the test
        mGame.start();

        // Make the predator follow the trained policy
        predator.setTraining(false);

        // Return the amount of rounds it took to finish the game as a result
        return super.performSingleTest(numRun);
    }
}
