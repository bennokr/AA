package com.uva.aa.testers;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.uva.aa.Game;
import com.uva.aa.agents.OffPolicyMCPredatorAgent;
import com.uva.aa.agents.PredatorAgent;

/**
 * Runs simple test with a On-Policy Monte-Carlo game tester.
 */
public class OffPolicyMCGameTester extends GameTester {
    
    private OffPolicyMCPredatorAgent predator;

    /**
     * {@inheritDoc}
     */
    public Game getGame() {
        // Creates a sample game
        final Game game = new Game(11, 11);

        // Adds the two required agents
        game.addPrey(5, 5);
        game.addOffPolicyMCPredator(0, 0);
        
        predator = (OffPolicyMCPredatorAgent) game.getEnvironment().getPredators().get(0);

        return game;
    }
    
    /**
     * {@inheritDoc}
     */
    public int performSingleTest(final int numRun) {
//        PrintStream out = System.out;
        
        predator.setTraining(true);
//        try {
//            System.setOut(new PrintStream(new FileOutputStream("NUL:")));
//        } catch (FileNotFoundException e) {}
        
        // Make sure that the game is ready
        mGame.resetGame();
        
        // Perform the test
        mGame.start();

        predator.setTraining(false);
        
//        System.setOut(out);
        
        // Return the amount of rounds it took to finish the game as a result
        return super.performSingleTest(numRun);
    }
}
