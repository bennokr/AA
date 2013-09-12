package com.uva.aa.testers;

import java.util.List;
import java.util.LinkedList;

import com.uva.aa.Game;

/**
 * A tester class that performs multiple tests and finds the mean average.
 */
public abstract class Tester {
	
	/**
	 * Performs the test once and returns the resulting game after this has finished.
	 * 
	 * @return The finished game
	 */
	public abstract Game performSingleTest();

	/**
	 * Performs the test <code>numRuns</code> times and prints the mean average.
	 * 
	 * @param numRuns The amount of tests to perform
	 */
	public void runTests(final int numRuns) {
		final List<Integer> results = new LinkedList<Integer>();
		
		// Run the tests
		for (int i = 0; i < numRuns; ++i) {
			final Game game = performSingleTest();
			results.add(game.getRoundsPlayed());
		}
		
		// Find the mean average of the results
		int resultSum = 0;
		for (int result : results) {
			resultSum += result;
		}
		final int mean = resultSum / results.size();
		
		System.out.println("Average result: " + mean);
	}
}
