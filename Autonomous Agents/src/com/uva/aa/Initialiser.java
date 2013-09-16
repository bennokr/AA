package com.uva.aa;

import com.uva.aa.testers.SimpleGameTester;
import com.uva.aa.testers.Tester;
import com.uva.aa.testers.EvaluatingGameTester;

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
//        final Tester simpleTester = new SimpleGameTester();
//        simpleTester.runTests(10);
        
        final Tester valueIterationTester = new EvaluatingGameTester();
        valueIterationTester.runTests(100000);
    }

}
