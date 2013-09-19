package com.uva.aa.testers;

import com.uva.aa.policies.PolicyManager;

/**
 * Tests the state values for policy iteration.
 */
public class PolicyIterationStateValueTester extends StateValueTester {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void improvePolicy(final PolicyManager policyManager) {
        policyManager.iteratePolicy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getIterations(final PolicyManager policyManager) {
        return policyManager.getPolicyUpdateStateValueIterations();
    }
}
