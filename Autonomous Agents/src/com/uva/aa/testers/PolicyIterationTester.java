package com.uva.aa.testers;

import com.uva.aa.Environment;
import com.uva.aa.Game;
import com.uva.aa.Location;
import com.uva.aa.agents.Agent;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.PolicyManager;

/**
 * Runs a simple test with the default predator and prey behaviour.
 */
public class PolicyIterationTester {

    final Environment mEnvironment;
    final Agent mPredator;
    final Agent mPrey;
    final Policy mPolicy;
    
    public PolicyIterationTester() {
        
        // Creates a game
        final Game game = new Game(11, 11);

        // Adds the two required agents - the predator will be using policy iteration
        game.addPrey(5, 5);
        game.addPolicyIteratingPredator(0, 0);

        mEnvironment = game.getEnvironment();
        mPredator = mEnvironment.getPredators().get(0);
        mPrey = mEnvironment.getPreys().get(0);
        
        mPrey.prepare();
        mPredator.prepare();
        
        mPolicy = mPredator.getPolicy();
        
    }
}
