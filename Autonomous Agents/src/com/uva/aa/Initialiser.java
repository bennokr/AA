package com.uva.aa;

import tester.SimpleTester;
import tester.Tester;

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
        simpleTester.runTests(100000);
    }

}
