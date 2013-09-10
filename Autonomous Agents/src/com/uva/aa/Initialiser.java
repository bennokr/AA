package com.uva.aa;

import testers.SimpleTester;
import testers.Tester;

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
        final Tester simpleTester = new SimpleTester();
        simpleTester.runTests(10);
    }

}
