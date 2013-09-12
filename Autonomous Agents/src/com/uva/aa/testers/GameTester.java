package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * A tester class that performs multiple tests and finds the mean average.
 */
public abstract class GameTester extends Tester {

    /**
     * Performs the test once and returns the resulting game after this has finished.
     * 
     * @param numRun
     *            The number for the run, increments each time
     * 
     * @return The finished game
     */
    public abstract Game performSingleGameTest(int numRun);

    /**
     * {@inheritDoc}
     */
    public int performSingleTest(final int numRun) {
        return performSingleGameTest(numRun).getRoundsPlayed();
    }
}