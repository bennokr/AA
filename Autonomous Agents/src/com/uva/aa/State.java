package com.uva.aa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.uva.aa.agents.Agent;
import com.uva.aa.agents.PredatorAgent;
import com.uva.aa.agents.PreyAgent;

/**
 * A description of the state within an environment.
 */
public class State {

    /** The map of agents with their locations */
    private final Map<Agent, Location> mAgentLocations;

    /** The map of agents with their locations relative to the most top-left location */
    private final Map<Agent, Location> mRelativeAgentLocations = new HashMap<Agent, Location>();

    /**
     * Builds a state based on a predator and a prey, both with a location. Only supports one predator and one prey.
     * 
     * @param predator
     *            The predator in the state
     * @param predatorLocation
     *            The predator's location in the state
     * @param prey
     *            The prey in the state
     * @param preyLocation
     *            The prey's location in the state
     * 
     * @return The new state for the given arguments
     */
    public static State buildState(final PredatorAgent predator, final Location predatorLocation, final PreyAgent prey,
            final Location preyLocation) {
        final Map<Agent, Location> nextStateMap = new LinkedHashMap<Agent, Location>();
        nextStateMap.put(predator, predatorLocation);
        if (preyLocation != null && !predatorLocation.equals(preyLocation)) {
            nextStateMap.put(prey, preyLocation);
        }
        return new State(nextStateMap);
    }

    /**
     * Creates a new state with the given mapping.
     * 
     * @param agentLocations
     *            The agents mapped to their locations
     */
    public State(final Map<Agent, Location> agentLocations) {
        mAgentLocations = agentLocations;

        // Set relative locations based on the most top-left agent for reducing the state-space
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (final Location location : mAgentLocations.values()) {
            minX = Math.min(minX, location.getX());
            minY = Math.min(minY, location.getY());
        }

        for (final Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
            final Location location = agentLocation.getValue();
            final Location relativeLocation = new Location(null, location.getX() - minX, location.getY() - minY);
            mRelativeAgentLocations.put(agentLocation.getKey(), relativeLocation);
        }
    }

    /**
     * Retrieves the mapping of agents to their locations.
     * 
     * @return The mapped agents with locations
     */
    public Map<Agent, Location> getAgentLocations() {
        return mAgentLocations;
    }

    /**
     * Retrieves the mapping of agents to their relative locations.
     * 
     * @return The mapped agents with relative locations
     */
    public Map<Agent, Location> getRelativeAgentLocations() {
        return mRelativeAgentLocations;
    }

    /**
     * Returns the location of the agent.
     * 
     * @param agent
     *            The agent to find the location for
     * @return The agent's location or null if the agent is not in the state
     */
    public Location getAgentLocation(final Agent agent) {
        return mAgentLocations.get(agent);
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
    public Agent getOccupyingAgent(final Location location) {
        for (final Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
            if (location.equals(agentLocation.getValue())) {
                return agentLocation.getKey();
            }
        }

        return null;
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
        return (getOccupyingAgent(location) != null);
    }

    /**
     * Retrieves whether or not the state space should be reduced.
     * 
     * @return True for a reduced state-space, false for the full size
     */
    public boolean hasReducedStateSpace() {
        if (mAgentLocations.isEmpty()) {
            return false;
        }

        return mAgentLocations.entrySet().iterator().next().getKey().getEnvironment().hasReducedStateSpace();
    }

    /**
     * Prints this state to the console in a human readable way.
     */
    public void print() {
        for (final Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
            final Agent agent = agentLocation.getKey();
            final Location location = agentLocation.getValue();
            System.out.print(agent.getClass().getSimpleName() + "-" + agent.hashCode() + "(" + location.getX() + ","
                    + location.getY() + ") ");
        }
        System.out.println();
    }

    /**
     * Checks if this state matches the given one.
     * 
     * @param state
     *            The state to compare
     * 
     * @return True of the contents of the state are the same, false otherwise
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof State)) {
            return false;
        }

        final State state = (State) other;

        final boolean reducedStateSpace = hasReducedStateSpace();

        final Map<Agent, Location> agentLocations = (reducedStateSpace ? mRelativeAgentLocations : mAgentLocations);
        final Map<Agent, Location> otherAgentLocations = (reducedStateSpace ? state.getRelativeAgentLocations() : state
                .getAgentLocations());

        // Verify if the number of agents is the same, so as to not miss any after checking each agent from this state's
        // point of view
        if (agentLocations.size() != otherAgentLocations.size()) {
            return false;
        }

        // Checks if all agents and their locations match with those in the other set
        for (final Map.Entry<Agent, Location> agentLocation : agentLocations.entrySet()) {
            if (!agentLocation.getValue().equals(otherAgentLocations.get(agentLocation.getKey()))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generates a hashcode for this state based on the hashcodes of the agent location mapping. This makes sure that
     * different states with the same contents will have matching hashes, even when the agents in the mapping are
     * ordered differently or have different objects.
     * 
     * @return The hashcode for the state
     */
    @Override
    public int hashCode() {
        return (hasReducedStateSpace() ? mRelativeAgentLocations : mAgentLocations).hashCode();
    }
}