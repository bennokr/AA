package com.uva.aa.agents;

import com.uva.aa.Location;
import com.uva.aa.policies.PolicyManager;

/**
 * An agent that acts as a predator within the environment. Will move towards the prey according to an evaluated policy.
 */
public class ValueIteratingPredatorAgent extends PredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public ValueIteratingPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * Starts with a random policy but will evaluate that to improve.
     */
    @Override
    public void prepare() {
        super.prepare();

        final PolicyManager policyEvaluator = new PolicyManager(mPolicy, getEnvironment());
        policyEvaluator.iterateValues();
    }
}
