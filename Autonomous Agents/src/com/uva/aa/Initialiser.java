package com.uva.aa;

import com.uva.aa.testers.PolicyEvaluationTester;
import com.uva.aa.testers.PolicyIterationTester;
import com.uva.aa.testers.SimpleGameTester;
import com.uva.aa.testers.StateSpaceTester;
import com.uva.aa.testers.Tester;
import com.uva.aa.testers.ValueIterationGameTester;
import com.uva.aa.testers.ValueIterationStateTester;

/**
 * Prepares the game(s) and starts the required actions.
 */
public class Initialiser {

    /**
     * Sets everything in motion.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        // Task 1.1: Random policy
//        final Tester simpleTester = new SimpleGameTester();
//        simpleTester.runTests(10000);
        
        // Task 1.2: Iterative policy evaluation
//        final PolicyEvaluationTester policyEvaluationTester = new PolicyEvaluationTester();
//        policyEvaluationTester.performTest();
        
        // Task 1.3: Policy iteration
//      final Tester policyIterationTester = new PolicyIterationGameTester();
//      policyIterationTester.runTests(100000);
     
        // Task 1.4: Value iteration
//      final Tester valueIterationGameTester = new ValueIterationGameTester();
//      valueIterationGameTester.runTests(100000);
        final ValueIterationStateTester valueIterationTester = new ValueIterationStateTester();
        valueIterationTester.performTest();
        
        // Task 1.5: State-space reduction
//        final StateSpaceTester stateSpaceTester = new StateSpaceTester();
//        stateSpaceTester.performTest();
    }

}
