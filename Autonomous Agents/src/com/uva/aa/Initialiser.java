package com.uva.aa;

public class Initialiser {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Game game = new Game(11, 11);

		game.addPrey(5, 5);
		game.addPredator(0, 0);
		
		game.start();
	}

}
