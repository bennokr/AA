package com.uva.aa.agents;

import com.uva.aa.Location;
import com.uva.aa.policies.PolicyIterater;

/**
 * An agent that acts as a predator within the environment. Will move towards the prey according to an evaluated policy.
 */
public class PolicyIteratingPredatorAgent extends PredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public PolicyIteratingPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * Starts with a random policy but will evaluate that to improve.
     */
    @Override
    public void prepare() {
        super.prepare();

        final PolicyIterater policyEvaluator = new PolicyIterater(mPolicy, getEnvironment());
        policyEvaluator.iteratePolicy();
    }
}