package com.uva.aa;

import com.uva.aa.testers.PolicyEvaluationTester;
import com.uva.aa.testers.SimpleGameTester;
import com.uva.aa.testers.Tester;
import com.uva.aa.testers.PolicyIterationGameTester;
import com.uva.aa.testers.ValueIterationGameTester;

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
        final Tester simpleTester = new SimpleGameTester();
        simpleTester.runTests(10000);
        
        // task 1.2: iterative policy evaluation
//        new PolicyEvaluationTester();
        
//        final Tester policyIterationTester = new PolicyIterationGameTester();
//        policyIterationTester.runTests(100000);
       
//        final Tester valueIterationTester = new ValueIterationGameTester();
//        valueIterationTester.runTests(100000);
    }

}
