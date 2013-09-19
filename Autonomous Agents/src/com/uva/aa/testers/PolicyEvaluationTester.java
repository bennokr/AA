package com.uva.aa.testers;

import com.uva.aa.Environment;
import com.uva.aa.Game;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.PolicyManager;

/**
 * Runs a simple test with the default predator and prey behaviour.
 */
public class PolicyEvaluationTester {

    /** The environment of the game */
    final Environment mEnvironment;

    /** The predator within the game */
    final PredatorAgent mPredator;

    /** The prey within the game */
    final PreyAgent mPrey;

    /** The policy of the predator */
    final Policy mPolicy;

    /**
     * Prepares a new policy evaluation test by creating a game and setting up the agents.
     */
    public PolicyEvaluationTester() {
        // Creates a game
        final Game game = new Game(11, 11);

        // To test the policy of a predator, we have to create all the agents that take part in the game
        // Adds a prey
        game.addPrey(5, 5);
        // Add a predator (this one has a random policy!)
        game.addPredator(0, 0);

        mEnvironment = game.getEnvironment();
        mPredator = mEnvironment.getPredators().get(0);
        mPrey = mEnvironment.getPreys().get(0);

        mPrey.prepare();
        mPredator.prepare();

        mPolicy = mPredator.getPolicy();
    }

    public void performTest() {
        // Evaluate the (random) policy of the predator
        final PolicyManager policyManager = new PolicyManager(mPolicy, mEnvironment);
        policyManager.evaluatePolicy();

        // Print some state-values
        printStateValues(new Location(mEnvironment, 0, 0), new Location(mEnvironment, 5, 5));
        printStateValues(new Location(mEnvironment, 2, 3), new Location(mEnvironment, 5, 4));
        printStateValues(new Location(mEnvironment, 2, 10), new Location(mEnvironment, 10, 0));
        printStateValues(new Location(mEnvironment, 10, 10), new Location(mEnvironment, 0, 0));
        System.out.println("The amount of iterations of the policy evaluation taken is " + policyManager.getPolicyUpdateStateValueIterations());
    }

    /**
     * Prints the state value for a given predator-location and prey-location
     * 
     * @param predatorLocation
     *            The location where the predator is
     * @param preyLocation
     *            The location where the prey is
     */
    private void printStateValues(final Location predatorLocation, final Location preyLocation) {
        // Create the state that holds the predator and the prey in the locations that we set in the agent
        final State state = State.buildState(mPredator, predatorLocation, mPrey, preyLocation);

        // Since we have evaluated the policy, we can now ask for the state value
        final double stateValue = mPolicy.getStateValue(state);

        System.out.println("The value for the state " + state + " is " + stateValue);
    }
}
