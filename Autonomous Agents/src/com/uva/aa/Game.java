package com.uva.aa;

import java.util.List;

import com.uva.aa.agents.Agent;
import com.uva.aa.agents.EvaluatingPredatorAgent;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;
import com.uva.aa.enums.GameState;

/**
 * The game which maintains the environment and agents.
 */
public class Game {

    /** The amount of time between turns in ms when performing a human test */
    private final static int TURN_DELAY = 75;

    /** The environment for this game */
    private final Environment mEnvironment;

    /** The state of the game which indicates if it's running */
    private GameState mGameState = GameState.PREPARATION;

    /** The state as it was before the game started */
    private State mInitialState;

    /** The amount of rounds played in the game */
    private int mRoundsPlayed = 0;

    /** The amount of turns taken in the game */
    private int mTurnsPlayed = 0;

    /** True if we should see the states of the game, false otherwise */
    private boolean mHumanTest = true;

    /** Whether to print the UI or just a simple textual state when performing a human test */
    private boolean mPrintUi = true;

    /**
     * Creates a new game with an environment with the specified dimensions.
     * 
     * @param width
     *            The width of the game's environment
     * @param height
     *            The height of the game's environment
     */
    public Game(final int width, final int height) {
        mEnvironment = new Environment(this, width, height);
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
     * Adds a policy-evaluating predator to the environment at the specified coordinates.
     * 
     * @param x
     *            The x coordinate where the predator is located at
     * @param y
     *            The y coordinate where the predator is located at
     */
    public void addEvaluatingPredator(final int x, final int y) {
        mEnvironment.addAgent(new EvaluatingPredatorAgent(new Location(mEnvironment, x, y)));
    }

    /**
     * Checks whether or not moves taken should be printed.
     * 
     * @return True of moves should be printed, false otherwise
     */
    public boolean shouldPrintMoves() {
        return mHumanTest;
    }

    /**
     * Checks whether or not the game is currently running.
     * 
     * @return True of the game is running, false otherwise
     */
    public boolean isRunning() {
        return mGameState == GameState.RUNNING;
    }

    /**
     * Retrieves the number of rounds played.
     * 
     * @return The number of rounds played
     */
    public int getRoundsPlayed() {
        return mRoundsPlayed;
    }

    /**
     * Finishes the game if it's currently running.
     */
    public void finish() {
        if (mGameState == GameState.RUNNING) {
            mGameState = GameState.FINISHED;

            if (mHumanTest) {
                System.out.println("Game finished!");
            }
        }
    }

    /**
     * Starts the game.
     */
    public void start() {
        // Makes sure that the game can be started and prepare it if needed
        switch (mGameState) {
            case PREPARATION:
                // The preys must be prepared first as the predators depend on them
                for (final Agent agent : mEnvironment.getPreys()) {
                    agent.prepare();
                }
                for (final Agent agent : mEnvironment.getPredators()) {
                    agent.prepare();
                }
                break;

            case RESET:
                break;

            case RUNNING:
            case FINISHED:
                // Should throw a proper exception when the game can be started dynamically
                throw new RuntimeException("This game is already running or finished.");
        }

        mInitialState = mEnvironment.getState();

        // Mark the game as running
        mGameState = GameState.RUNNING;
        mRoundsPlayed = 0;
        mTurnsPlayed = 0;

        // Let each agent take turns in performing actions
        final List<Agent> agents = mEnvironment.getAgents();
        Agent activeAgent = agents.get(0);
        while (mGameState == GameState.RUNNING) {
            // Keep track of the turns taken
            ++mTurnsPlayed;

            // Make a move
            activeAgent.performAction();

            // Show the current state of the environment
            if (mHumanTest) {
                if (mPrintUi) {
                    mEnvironment.printUi();
                } else {
                    mEnvironment.printSimple();
                }
            }

            // Select the next agent, keeping in mind that the list of agents may be altered
            int nextAgent = (agents.indexOf(activeAgent) + 1);
            if (nextAgent == agents.size()) {
                ++mRoundsPlayed;
                nextAgent = 0;
            }
            activeAgent = agents.get(nextAgent);

            // Make sure that humans can see the game's state changes develop
            if (mHumanTest) {
                try {
                    Thread.sleep(TURN_DELAY);
                } catch (InterruptedException e) {}
            }
        }

        System.out.println(mRoundsPlayed + " rounds played with a total of " + mTurnsPlayed + " turns.");
        System.out.println();
    }

    /**
     * Resets the game so that it may be ran again.
     */
    public void resetGame() {
        switch (mGameState) {
            case PREPARATION:
            case RESET:
                // There's nothing to reset
                return;

            case FINISHED:
                break;

            case RUNNING:
                // Should throw a proper exception when the game can be reset dynamically
                throw new RuntimeException("This game is currently running.");
        }

        mEnvironment.setState(mInitialState);
        mGameState = GameState.RESET;
    }

    /**
     * Sets whether or not a human test is performed or if a tester runs the game. Changes what output is shown.
     * 
     * @param humanTest
     *            True for a human test, false for a tester run
     */
    public void setHumanTest(final boolean humanTest) {
        mHumanTest = humanTest;
    }

    /**
     * Returns the environment of the game.
     * 
     * @return
     *      The environment for this game
     */
    public Environment getEnvironment() {
        return mEnvironment;
    }
    
}
