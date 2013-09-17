package com.uva.aa.testers;

import java.util.HashMap;
import java.util.Map;

import com.uva.aa.Environment;
import com.uva.aa.Game;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.agents.Agent;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.PolicyManager;

/**
 * Runs a simple test with the default predator and prey behaviour.
 */
public class PolicyEvaluationTester {

    final Environment mEnvironment;
    final Agent mPredator;
    final Agent mPrey;
    final Policy mPolicy;

    final PolicyManager policyIterater;

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

        // create a policy iterater, who can evaluate policies
        policyIterater = new PolicyManager(mPolicy, mEnvironment);

        // evaluate the (random) policy of the predator
        policyIterater.evaluatePolicy();

        // print some state-values
        printStateValues(new Location(mEnvironment, 0, 0), new Location(mEnvironment, 5, 5));
        printStateValues(new Location(mEnvironment, 2, 3), new Location(mEnvironment, 5, 4));
        printStateValues(new Location(mEnvironment, 2, 10), new Location(mEnvironment, 10, 0));
        printStateValues(new Location(mEnvironment, 10, 10), new Location(mEnvironment, 0, 0));
    }

    /**
     * Prints the state value for a given predator-location and prey-location
     * 
     * @param predatorLocation
     *            the location in which the predator is
     * @param preyLocation
     *            the location in which the prey is
     */
    private void printStateValues(Location predatorLocation, Location preyLocation) {
        // create an map which holds the agents and their locations
        Map<Agent, Location> agentMap = new HashMap<Agent, Location>();
        agentMap.put(mPrey, preyLocation);
        agentMap.put(mPredator, predatorLocation);

        // create the state that holds the predator and the prey in the locations that we set in the agentMao
        State state = new State(agentMap);

        // since we have evaluated the policy, we can now ask for the state value
        double stateValue = mPolicy.getStateValue(state);
        
        System.out.println("The value for the state Predator("+predatorLocation.getX()+","
                +predatorLocation.getY()+"), Prey("+preyLocation.getX()+","+preyLocation.getY()
                +") is "+stateValue);
    }
}
