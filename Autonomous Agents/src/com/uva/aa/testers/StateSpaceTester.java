package com.uva.aa.testers;

import com.uva.aa.Config;
import com.uva.aa.Environment;
import com.uva.aa.Game;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.PolicyManager;

/**
 * Performs several tests to demonstrate the difference with state-space sizes.
 */
public class StateSpaceTester {

    /**
     * Performs several tests to demonstrate the difference with state-space sizes.
     */
    public void performTest() {
        performIterateValuesTest(false, true);
        performIterateValuesTest(true, true);
        performIterateValuesTest(false, false);
        performIterateValuesTest(true, false);
    }

    /**
     * Prepares a game and times the value iteration done for it. Prints out the test duration in seconds and
     * iterations.
     * 
     * @param reducedStateSpace
     *            Whether or not to use a reduced state-space
     * @param policyIterationInsteadOfValue
     *            True to use policy iteration, false for value iteration
     */
    private void performIterateValuesTest(final boolean reducedStateSpace, final boolean policyIterationInsteadOfValue) {
        // Creates a game
        final Game game = new Game(11, 11);

        // To test the policy of a predator, we have to create all the agents that take part in the game
        // Adds a prey
        game.addPrey(5, 5);
        // Add a predator (this one has a random policy!)
        game.addPredator(0, 0);

        final Environment environment = game.getEnvironment();
        environment.setReducedStateSpace(reducedStateSpace);
        final PreyAgent prey = environment.getPreys().get(0);
        final PredatorAgent predator = environment.getPredators().get(0);

        prey.prepare();
        predator.prepare();

        final Policy policy = predator.getPolicy();
        final PolicyManager policyManager = new PolicyManager(policy, environment);

        final double startTime = System.nanoTime();
        if (policyIterationInsteadOfValue) {
            policyManager.iteratePolicy();
        } else {
            policyManager.iterateValues();
        }
        final double testDuration = (System.nanoTime() - startTime) / Math.pow(10, 9);

        System.out.println((policyIterationInsteadOfValue ? "Policy" : "Value")
                + " iteration with theta "
                + Config.ERROR_THRESHOLD_THETA
                + " and "
                + (reducedStateSpace ? "reduced" : "full")
                + " state-space: "
                + testDuration
                + "s for "
                + (policyIterationInsteadOfValue ? policyManager.getPolicyUpdateStateValueIterations() : policyManager
                        .getUpdateStateValueIterations()) + " iterations");
    }
}
