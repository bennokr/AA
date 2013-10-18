package com.uva.aa;

import com.uva.aa.testers.GameTester;
import com.uva.aa.testers.OffPolicyMCGameTester;
import com.uva.aa.testers.OnPolicyMCGameTester;
import com.uva.aa.testers.ParallelGameTester;
import com.uva.aa.testers.ParallelMinimaxQGameTester;
import com.uva.aa.testers.ParallelQLearningEGreedyGameTester;
import com.uva.aa.testers.ParallelRLearningGameTester;
import com.uva.aa.testers.PolicyEvaluationTester;
import com.uva.aa.testers.PolicyIterationStateValueTester;
import com.uva.aa.testers.QLearningEGreedyGameTester;
import com.uva.aa.testers.QLearningSoftmaxGameTester;
import com.uva.aa.testers.SarsaGameTester;
import com.uva.aa.testers.SimpleGameTester;
import com.uva.aa.testers.StateSpaceTester;
import com.uva.aa.testers.StateValueTester;
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

    private static boolean sTestQLearningEGreedyGame = false;
    private static boolean sTestQLearningSoftmaxGame = false;
    private static boolean sTestSarsaGame = false;
    private static boolean sTestOnPolicyMCGame = false;
    private static boolean sTestOffPolicyMCGame = false;

    private static boolean sTestParallelGame = false;
    private static boolean sTestParallelQLearningEGreedyGame = false;
    private static boolean sTestParallelRLearningGame = false;
    private static boolean sTestParallelMinimaxQGame = true;

    /**
     * Sets everything in motion.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {

        // Task 1.1: Random policy
        if (sTestRandomPolicy) {
            final GameTester simpleTester = new SimpleGameTester();
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
            final GameTester valueIterationGameTester = new ValueIterationGameTester();
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

        // Task 2.1: Q-Learning e-Greedy
        if (sTestQLearningEGreedyGame) {
            final GameTester qLearningEGreedyGameTester = new QLearningEGreedyGameTester();
            qLearningEGreedyGameTester.runTests(100000);
        }

        // Task 2.3: Q-Learning Softmax
        if (sTestQLearningSoftmaxGame) {
            final GameTester qLearningSoftmaxGameTester = new QLearningSoftmaxGameTester();
            qLearningSoftmaxGameTester.runTests(10000);
        }

        // Task 2.4: Sarsa
        if (sTestSarsaGame) {
            final GameTester sarsaGameTester = new SarsaGameTester();
            sarsaGameTester.runTests(100000);
        }

        // Task 2.4(2): On-policy MC
        if (sTestOnPolicyMCGame) {
            final GameTester onPolicyMCGameTester = new OnPolicyMCGameTester();
            onPolicyMCGameTester.runTests(10000);
        }

        // Task 2.4(3): Off-policy MC
        if (sTestOffPolicyMCGame) {
            final GameTester offPolicyMCGameTester = new OffPolicyMCGameTester();
            offPolicyMCGameTester.runTests(1000);
        }

        // Task 3.1: Parallel
        if (sTestParallelGame) {
            final GameTester parallelTester = new ParallelGameTester();
            parallelTester.runTests(1000);
        }

        // Task 3.2(1): Parallel Q-Learning with Epsilon-Greedy action selection
        if (sTestParallelQLearningEGreedyGame) {
            final GameTester parallelQLearningEGreedyTester = new ParallelQLearningEGreedyGameTester();
            parallelQLearningEGreedyTester.runTests(10000);
        }

        // Task 3.3: Parallel R-Learning
        if (sTestParallelRLearningGame) {
            final GameTester parallelRLearningTester = new ParallelRLearningGameTester();
            parallelRLearningTester.runTests(1000);
        }

        // Task 3.2(2): Minimax-Q
        if (sTestParallelMinimaxQGame) {
            final GameTester parallelMinimaxQTester = new ParallelMinimaxQGameTester();
            parallelMinimaxQTester.runTests(1000);
        }
    }

}
