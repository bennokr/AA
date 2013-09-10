package com.uva.aa;

import java.util.List;

import com.uva.aa.enums.GameState;
import com.uva.aa.model.Agent;
import com.uva.aa.model.PredatorAgent;
import com.uva.aa.model.PreyAgent;
import com.uva.aa.model.SmartPredatorAgent;
import com.uva.aa.model.Environment;

/**
 * The game which maintains the environment and agents.
 */
public class Game {
    
    /** The amount of time between turns in ms */
    private final static int TURN_DELAY = 50;

    /** The environment for this game */
    private final Environment mEnvironment;

    /** The state of the game which indicates if it's running */
    private GameState mState = GameState.PREPARATION;
    
    /** The amount of turns taken in the game */
    private int mTurnsTaken = 0;

    /**
     * Creates a new game with an environment with the specified dimensions.
     * 
     * @param width
     *            The width of the game's environment
     * @param height
     *            The height of the game's environment
     */
    public Game(final int width, final int height) {
        mEnvironment = new Environment(this, 11, 11);
    }

    /**
     * Adds a prey to the environment at the specified coordinates.
     * 
     * @param x
     *            The x coordinate where the prey is located at
     * @param y
     *            The y coordinate where the prey is located at
     */
    public void addPrey(final int x, final int y) {
        mEnvironment.addAgent(new PreyAgent(new Location(mEnvironment, x, y)));
    }

    /**
     * Adds a predator to the environment at the specified coordinates.
     * 
     * @param x
     *            The x coordinate where the predator is located at
     * @param y
     *            The y coordinate where the predator is located at
     */
    public void addPredator(final int x, final int y) {
        mEnvironment.addAgent(new PredatorAgent(new Location(mEnvironment, x, y)));
    }

    /**
     * Checks whether or not the game is currently running.
     * 
     * @return True of the game is running, false otherwise
     */
    public boolean isRunning() {
        return mState == GameState.RUNNING;
    }

    /**
     * Finishes the game.
     */
    public void finish() {
        mState = GameState.FINISHED;

        System.out.println("Game finished!");
        System.out.println("Amount of turns taken: " + mTurnsTaken);
    }

    /**
     * Starts the game.
     */
    public void start() {
        // Makes sure that the game can be started
        if (mState != GameState.PREPARATION) {
            // Should throw a proper exception when the game can be started dynamically
            throw new RuntimeException("This game is already running or finished.");
        }

        // Mark the game as running
        mState = GameState.RUNNING;
        mTurnsTaken = 0;

        // Let each agent take turns in performing actions
        final List<Agent> agents = mEnvironment.getAgents();
        Agent activeAgent = agents.get(0);
        while (mState == GameState.RUNNING) {
            // Keep track of the turns taken
            ++mTurnsTaken;
            
            // Make a move
            activeAgent.performAction();

            // Show the current state of the environment
            mEnvironment.print();

            // Select the next agent, keeping in mind that the list of agents may be altered
            activeAgent = agents.get((agents.indexOf(activeAgent) + 1) % agents.size());

            // Make sure that humans can see the game's state changes develop
            try {
                Thread.sleep(TURN_DELAY);
            } catch (InterruptedException e) {}
        }
    }

}
