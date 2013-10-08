package com.uva.aa;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.uva.aa.agents.Agent;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;

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

    /** Whether or not the state-space should exclude relative duplicates to reduce the state-space */
    private boolean mReducedStateSpace = true;

    /** The possible states that can occur, excluding terminal state */
    private Set<State> mNonTerminalPossibleStates;

    /** The possible states that can occur, including terminal state */
    private Set<State> mAllPossibleStates;

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
     * Removes all agents from this environment.
     */
    public void clearAgents() {
        mAgents.clear();
        mPreys.clear();
        mPredators.clear();
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
    public Set<State> getPossibleStates(final boolean includeTerminal) {
        Set<State> possibleStates = (includeTerminal ? mAllPossibleStates : mNonTerminalPossibleStates);

        if (possibleStates == null) {
            possibleStates = new HashSet<State>();
            final PredatorAgent predator = mPredators.get(0);
            final PreyAgent prey = mPreys.get(0);

            // Loop over the possible locations of the predator
            for (int xPred = 0; xPred < mWidth; ++xPred) {
                for (int yPred = 0; yPred < mHeight; ++yPred) {
                    final Location predatorLocation = new Location(this, xPred, yPred);

                    // Loop over the possible locations of the prey
                    for (int xPrey = 0; xPrey < mWidth; ++xPrey) {
                        for (int yPrey = 0; yPrey < mHeight; ++yPrey) {
                            final Location preyLocation = new Location(this, xPrey, yPrey);

                            // Only include the state if it's not terminal or if terminal states should be included
                            if (includeTerminal || !predatorLocation.equals(preyLocation)) {
                                possibleStates.add(State.buildState(predator, predatorLocation, prey, preyLocation));
                            }
                        }
                    }
                }
            }

            if (includeTerminal) {
                mAllPossibleStates = possibleStates;
            } else {
                mNonTerminalPossibleStates = possibleStates;
            }
        }

        return possibleStates;
    }

    /**
     * Retrieves the state that the environment is currently in.
     * 
     * @return The environment's current state
     */
    public State getState() {
        final Map<Agent, Location> stateMap = new LinkedHashMap<Agent, Location>();

        for (final Agent agent : mAgents) {
            stateMap.put(agent, agent.getLocation());
        }

        return new State(stateMap);
    }

    /**
     * Modifies the environment and its agents so that it matches the given state.
     * 
     * @param state
     *            The new state of the environment
     */
    public void setState(final State state) {
        clearAgents();

        for (final Entry<Agent, Location> agentLocation : state.getAgentLocations().entrySet()) {
            final Agent agent = agentLocation.getKey();
            agent.moveTo(agentLocation.getValue());
            addAgent(agent);
        }
    }

    /**
     * Sets whether or not the state-space should be reduced.
     * 
     * @param reducedStateSpace
     *            True for a reduced state-space, false for the full size
     */
    public void setReducedStateSpace(final boolean reducedStateSpace) {
        mReducedStateSpace = reducedStateSpace;
    }

    /**
     * Retrieves whether or not the state space should be reduced.
     * 
     * @return True for a reduced state-space, false for the full size
     */
    public boolean hasReducedStateSpace() {
        return mReducedStateSpace;
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

    /**
     * Called at the end of a round in a parallel running game. Updates the environment's state and the game's state
     * when needed.
     */
    public void updateParallelActionState() {
        // Check all the predator locations
        final List<Location> predatorLocations = new LinkedList<Location>();
        for (final PredatorAgent predator : mPredators) {
            final Location location = predator.getLocation();
            if (predatorLocations.contains(location)) {
                // The predators lose when they end up in the same location
                mGame.finish();
                return;
            }
            predatorLocations.add(location);
        }

        // Kill all preys that are on the spots of the predators
        for (final PreyAgent prey : mPreys) {
            if (predatorLocations.contains(prey.getLocation())) {
                prey.die();
            }
        }

        // The predators win when all preys are dead
        if (mPreys.isEmpty()) {
            mGame.finish();
            return;
        }
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
