package com.uva.aa;

import com.uva.aa.testers.PolicyEvaluationTester;
import com.uva.aa.testers.PolicyIterationStateValueTester;
import com.uva.aa.testers.QLearningGameTester;
import com.uva.aa.testers.SimpleGameTester;
import com.uva.aa.testers.StateSpaceTester;
import com.uva.aa.testers.StateValueTester;
import com.uva.aa.testers.MeanTester;
import com.uva.aa.testers.ValueIterationGameTester;
import com.uva.aa.testers.ValueIterationStateValueTester;

/**
 * Prepares the game(s) and starts the required actions.
 */
public class Initialiser {

    private static boolean sTestRandomPolicy = false;
    private static boolean sTestPolicyEvaluation = false;
    private static boolean sTestPolicyStateValueIteration = false;
    private static boolean sTestValueIterationGame = false;
    private static boolean sTestValueIterationStateValue = false;
    private static boolean sTestStateSpace = false;

    private static boolean sTestQLearningGame = true;

    /**
     * Sets everything in motion.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        // Task 1.1: Random policy
        if (sTestRandomPolicy) {
            final MeanTester simpleTester = new SimpleGameTester();
            simpleTester.runTests(100);
        }

        // Task 1.2: Iterative policy evaluation
        if (sTestPolicyEvaluation) {
            final PolicyEvaluationTester policyEvaluationTester = new PolicyEvaluationTester();
            policyEvaluationTester.performTest();
        }

        // Task 1.3: Policy iteration
        if (sTestPolicyStateValueIteration) {
            final StateValueTester policyIterationStateValueTester = new PolicyIterationStateValueTester();
            policyIterationStateValueTester.performTest();
        }

        // Task 1.4: Value iteration
        if (sTestValueIterationGame) {
            final MeanTester valueIterationGameTester = new ValueIterationGameTester();
            valueIterationGameTester.runTests(100);
        }
        if (sTestValueIterationStateValue) {
            final StateValueTester valueIterationTester = new ValueIterationStateValueTester();
            valueIterationTester.performTest();
        }

        // Task 1.5: State-space reduction
        if (sTestStateSpace) {
            final StateSpaceTester stateSpaceTester = new StateSpaceTester();
            stateSpaceTester.performTest();
        }

        // Task 2.1: Q-Learning
        if (sTestQLearningGame) {
            final QLearningGameTester qLearningGameTester = new QLearningGameTester();
            qLearningGameTester.runTests(10000);
        }
    }

}
