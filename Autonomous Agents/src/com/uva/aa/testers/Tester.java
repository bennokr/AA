package com.uva.aa.testers;

import java.util.List;
import java.util.LinkedList;

/**
 * A tester class that performs multiple tests and finds the mean average.
 */
public abstract class Tester {

    /**
     * Performs the test once and returns the resulting game after this has finished.
     * 
     * @param numRun
     *            The number for the run, increments each time
     * 
     * @return The finished game
     */
	public abstract int performSingleTest(int numRun);

	/**
	 * Performs the test <code>numRuns</code> times and prints the mean average.
	 * 
	 * @param numRuns The amount of tests to perform
	 */
	public void runTests(final int numRuns) {
		final List<Integer> results = new LinkedList<Integer>();
		
		// Run the tests
		for (int i = 0; i < numRuns; ++i) {
			results.add(performSingleTest(i));
		}
		
		// Find the mean average of the results
		int resultSum = 0;
		for (final int result : results) {
			resultSum += result;
		}
		final double mean = ((double)resultSum) / results.size();
		
		System.out.println("Average result: " + mean);
	}
}