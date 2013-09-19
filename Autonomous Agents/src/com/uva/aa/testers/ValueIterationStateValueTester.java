package com.uva.aa.testers;

import com.uva.aa.policies.PolicyManager;

/**
 * Tests the state values for value iteration.
 */
public class ValueIterationStateValueTester extends StateValueTester {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void improvePolicy(final PolicyManager policyManager) {
        policyManager.iterateValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getIterations(final PolicyManager policyManager) {
        return policyManager.getUpdateStateValueIterations();
    }
}
