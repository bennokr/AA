package com.uva.aa.agents;

import com.uva.aa.Location;
import com.uva.aa.policies.PolicyManager;

/**
 * An agent that acts as a predator within the environment. Will move towards the prey according to an evaluated policy.
 */
public class PolicyIteratingPredatorAgent extends PredatorAgent {

    /** The policy manager improving this agent's policy */
    final PolicyManager mPolicyManager;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public PolicyIteratingPredatorAgent(final Location location) {
        super(location);

        mPolicyManager = new PolicyManager(mPolicy, getEnvironment());
    }

    /**
     * Starts with a random policy but will evaluate that to improve.
     */
    @Override
    public void prepare() {
        super.prepare();

        mPolicyManager.iteratePolicy();
    }

    /**
     * Retrieves The policy manager improving this agent's policy
     * 
     * @return The agent's policy manager
     */
    public PolicyManager getPolicyManager() {
        return mPolicyManager;
    }
}
