package testers;

import com.uva.aa.Game;

/**
 * Runs a simple test with the default predator and prey behaviour.
 */
public class SimpleTester extends Tester {

	/**
	 * {@inheritDoc}
	 */
	public Game performSingleTest() {
		// Creates a sample game
		final Game game = new Game(11, 11);

		// Adds the two required agents
		game.addPrey(5, 5);
		game.addPredator(0, 0);

		// Perform the test
		game.start();

		return game;
	}
}
