package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * A tester class that performs multiple tests and finds the mean average.
 */
public abstract class GameTester extends Tester {

    /**
     * Performs the test once and returns the resulting game after this has finished.
     * 
     * @return The finished game
     */
    public abstract Game performSingleGameTest(int numRuns);

    /**
     * Performs the test <code>numRuns</code> times and prints the mean average.
     * 
     * @param numRuns
     *            The amount of tests to perform
     */
    public int performSingleTest(final int numRuns) {
		return performSingleGameTest(numRuns).getRoundsPlayed();
	}
}