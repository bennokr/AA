package com.uva.aa;

/**
 * Prepares the game(s) and starts the required actions.
 */
public class Initialiser {

    /**
     * Sets everything in motion
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        // Creates a sample game
        final Game game = new Game(11, 11);

        // Adds the two required agents
        game.addPrey(5, 5);
        game.addPredator(0, 0);

        // Enable this for a simple multi-agent demonstration
//        game.addPrey(5, 6);
//        game.addPrey(6, 6);
//        game.addPrey(6, 5);
//        game.addPredator(0, 10);
//        game.addPredator(10, 10);
//        game.addPredator(10, 0);

        game.start();
    }

}
