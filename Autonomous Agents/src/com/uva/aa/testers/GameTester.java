package com.uva.aa.testers;

import com.uva.aa.Game;

/**
 * A tester class that performs multiple tests and finds the mean average.
 */
public abstract class GameTester extends Tester {
    
    /** The game to test */
    final Game mGame;
    
    /**
     * Prepares the game tester.
     */
    public GameTester() {
        mGame = getGame();
        mGame.setHumanTest(false);
    }

    /**
     * Returns the game that should be tested.
     * 
     * @return The game to test
     */
    public abstract Game getGame();

    /**
     * {@inheritDoc}
     */
    public int performSingleTest(final int numRun) {
        // Make sure that the game is ready
        mGame.resetGame();
        
        // Perform the test
        mGame.start();
        
        // Return the amount of rounds it took to finish the game as a result
        return mGame.getRoundsPlayed();
    }
}