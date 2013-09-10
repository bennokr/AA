package com.uva.aa.agents;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.uva.aa.Game;
import com.uva.aa.Location;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.State;

/**
 * The environment which holds the agents and state.
 */
public class Environment {

    /** The width of the environment */
    private final int mWidth;

    /** The height of the environment */
    private final int mHeight;

    /** The game in which this environment is located */
    private final Game mGame;

    /** The list of agents within the environment, both preys and predators, in the order in which they were added */
    private final List<Agent> mAgents = new ArrayList<Agent>();

    /** The list of preys in the order in which they were added */
    private final List<PreyAgent> mPreys = new ArrayList<PreyAgent>();

    /** The list of predators in the order in which they were added */
    private final List<PredatorAgent> mPredators = new ArrayList<PredatorAgent>();

    /**
     * Creates a new environment within the specified game with the given dimensions.
     * 
     * @param game
     *            The game which created the environment
     * @param width
     *            The width of environment
     * @param height
     *            The height of environment
     */
    public Environment(final Game game, final int width, final int height) {
        mGame = game;
        mWidth = width;
        mHeight = height;
    }

    /**
     * Adds an agent to this environment.
     * 
     * @param agent
     *            The agent to add
     * 
     * @throws RuntimeException
     *             Thrown when the agent's location is already occupied
     */
    public void addAgent(final Agent agent) {
        // Make sure the agent's location isn't already occupied
        if (isOccupied(agent.getLocation())) {
            // Should throw a proper exception when agents can be dynamically added and this is a viable scenario
            throw new RuntimeException("This location is already occupied by another agent.");
        }

        mAgents.add(agent);

        if (PreyAgent.class.isInstance(agent)) {
            mPreys.add((PreyAgent) agent);
        } else if (PredatorAgent.class.isInstance(agent)) {
            mPredators.add((PredatorAgent) agent);
        }
    }

    /**
     * Removes an agent from this environment and updates the game state if needed.
     * 
     * @param agent
     *            The agent to remove
     */
    public void removeAgent(final Agent agent) {
        mAgents.remove(agent);

        if (PreyAgent.class.isInstance(agent)) {
            mPreys.remove(agent);
        } else if (PredatorAgent.class.isInstance(agent)) {
            mPredators.remove(agent);
        }

        updateGameState();
    }

    /**
     * Retrieves the list of agents within the environment, both preys and predators, in the order in which they were
     * added.
     * 
     * @return The list of agents
     */
    public List<Agent> getAgents() {
        return mAgents;
    }

    /**
     * Retrieves the list of preys in the order in which they were added.
     * 
     * @return The list of preys
     */
    public List<PreyAgent> getPreys() {
        return mPreys;
    }

    /**
     * Retrieves the list of all possible states that the environment can be in, considering all the agents that are
     * currently in it.
     * 
     * This method does NOT support more than one predator or prey yet.
     * 
     * @param includeTerminal
     *            Whether or not the terminal states should also be included in the list
     * 
     * @return The possible states
     */
    public List<State> getPossibleStates(final boolean includeTerminal) {
        final List<State> possibleStates = new ArrayList<State>();

        // Loop over the possible locations of the predator
        for (int xPred = 0; xPred < mWidth; ++xPred) {
            for (int yPred = 0; yPred < mHeight; ++yPred) {
                final Location locPred = new Location(this, xPred, yPred);

                // Loop over the possible locations of the prey
                for (int xPrey = 0; xPrey < mWidth; ++xPrey) {
                    for (int yPrey = 0; yPrey < mHeight; ++yPrey) {
                        final Location locPrey = new Location(this, xPrey, yPrey);

                        // Only include the state if it's not terminal or if terminal states should be included
                        if (includeTerminal || !locPred.equals(locPrey)) {
                            final HashMap<Agent, Location> stateMap = new HashMap<Agent, Location>();
                            stateMap.put(mPredators.get(0), locPred);
                            stateMap.put(mPreys.get(0), locPrey);
                            possibleStates.add(new State(stateMap));
                        }
                    }
                }

            }
        }

        return possibleStates;
    }

    /**
     * Retrieves the list of predators in the order in which they were added.
     * 
     * @return The list of predators
     */
    public List<PredatorAgent> getPredators() {
        return mPredators;
    }

    /**
     * Returns the game containing the environment.
     * 
     * @return The game containing the environment
     */
    public Game getGame() {
        return mGame;
    }

    /**
     * Returns the width of the environment.
     * 
     * @return The width of the environment
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Returns the rounded-up width of the environment. Useful for direction calculations.
     * 
     * @return The rounded-up width of the environment
     */
    public int getHalfWidth() {
        return (int) Math.ceil(mWidth / 2.0);
    }

    /**
     * Returns the height of the environment.
     * 
     * @return The height of the environment
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Returns the rounded-up height of the environment. Useful for direction calculations.
     * 
     * @return The rounded-up height of the environment
     */
    public int getHalfHeight() {
        return (int) Math.ceil(mWidth / 2.0);
    }

    /**
     * Retrieves the agent occupying the given location, if any, from the specified list of agents.
     * 
     * @param location
     *            The location to find an agent at
     * @param agents
     *            The lost of agents to check
     * 
     * @return The occupying agent or null if none on the location
     */
    public Agent getOccupyingAgent(final Location location, final List<Agent> agents) {
        for (Agent agent : agents) {
            if (location.equals(agent.getLocation())) {
                return agent;
            }
        }

        return null;
    }

    /**
     * Retrieves the agent occupying the given location, if any.
     * 
     * @param location
     *            The location to find an agent at
     * 
     * @return The occupying agent or null if none on the location
     */
    public Agent getOccupyingAgent(final Location location) {
        return getOccupyingAgent(location, mAgents);
    }

    /**
     * Checks if an agent from the specified list is occupying the given location.
     * 
     * @param location
     *            The location to find an agent at
     * @param agents
     *            The lost of agents to check
     * 
     * @return True if an agent is at the location, false otherwise
     */
    public boolean isOccupied(final Location location, final List<Agent> agents) {
        return getOccupyingAgent(location, agents) != null;
    }

    /**
     * Checks if an agent is occupying the given location.
     * 
     * @param location
     *            The location to find an agent at
     * 
     * @return True if an agent is at the location, false otherwise
     */
    public boolean isOccupied(final Location location) {
        return isOccupied(location, mAgents);
    }

    /**
     * Checks if a prey occupying the given location.
     * 
     * @param location
     *            The location to find a prey at
     * 
     * @return True if a prey is at the location, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean isOccupiedByPrey(final Location location) {
        return isOccupied(location, (List<Agent>) (List<?>) mPreys);
    }

    /**
     * Checks if a predator occupying the given location.
     * 
     * @param location
     *            The location to find a predator at
     * 
     * @return True if a predator is at the location, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean isOccupiedByPredator(final Location location) {
        return isOccupied(location, (List<Agent>) (List<?>) mPredators);
    }

    /**
     * Updates the game state if needed. When no preys are left, the game is finished.
     */
    public void updateGameState() {
        // When there are no preys left, end the game
        if (mPreys.isEmpty() && mGame.isRunning()) {
            mGame.finish();
        }
    }

    public double getTransitionProbability(final State initialState, final State resultingState, final Action action) {
        // TODO: Benno will do this
        return 0.0;
    }

    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
        // TODO: Benno will do this
        return 0.0;
    }

    /**
     * Prints the current environment state to the console.
     */
    public void printUi() {
        final PrintStream out = System.out;

        // Print the top border
        out.print(" ");
        for (int x = 0; x < mWidth; ++x) {
            out.print("-");
        }
        out.print(" ");
        out.println();

        // Print each row
        for (int y = 0; y < mHeight; ++y) {
            out.print("|");

            // Print each column
            for (int x = 0; x < mWidth; ++x) {
                final Location location = new Location(null, x, y);
                if (isOccupiedByPredator(location)) {
                    out.print("X");
                } else if (isOccupiedByPrey(location)) {
                    out.print("O");
                } else {
                    out.print(" ");
                }
            }

            out.print("|");
            out.println();
        }

        // Print the bottom border
        out.print(" ");
        for (int x = 0; x < mWidth; ++x) {
            out.print("-");
        }
        out.print(" ");
        out.println();
    }

    /**
     * Prints the current environment state to the console.
     */
    public void printSimple() {
        for (final PredatorAgent predator : mPredators) {
            final Location location = predator.getLocation();
            System.out.print("Predator(" + location.getX() + "," + location.getY() + ") ");
        }
        for (final PreyAgent prey : mPreys) {
            final Location location = prey.getLocation();
            System.out.print("Prey(" + location.getX() + "," + location.getY() + ") ");
        }
        System.out.println();
    }
}
