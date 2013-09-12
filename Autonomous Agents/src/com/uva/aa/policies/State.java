package com.uva.aa.policies;

import java.util.Map;

import com.uva.aa.Location;
import com.uva.aa.agents.Agent;
import com.uva.aa.agents.PreyAgent;

/**
 * A description of the state within an environment.
 */
public class State {

    /** The map of agents with their locations */
    private final Map<Agent, Location> mAgentLocations;

    /**
     * Creates a new state with the given mapping.
     * 
     * @param agentLocations
     *            The agents mapped to their locations
     */
    public State(final Map<Agent, Location> agentLocations) {
        mAgentLocations = agentLocations;
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
     * Prints this state to the console in a human readable way.
     */
    public void print() {
        for (final Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
            final Agent agent = agentLocation.getKey();
            final Location location = agentLocation.getValue();
            System.out.print(agent.getClass().getSimpleName() + "-" + agent.hashCode() + "(" + location.getX() + "," + location.getY() + ") ");
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
    public boolean equals(final State state) {
        final Map<Agent, Location> otherAgentLocations = state.getAgentLocations();

        // Verify if the number of agents is the same, so as to not miss any after checking each agent from this state's
        // point of view
        if (mAgentLocations.size() != otherAgentLocations.size()) {
            return false;
        }

        // Checks if all agents and their locations match with those in the other set
        for (final Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
            final Location otherAgentLocation = otherAgentLocations.get(agentLocation.getKey());
            if (otherAgentLocation == null) {
                return false;
            }
            if (!agentLocation.equals(otherAgentLocation)) {
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
        int agentHash = 0;
        for (final Map.Entry<Agent, Location> agentLocation : mAgentLocations.entrySet()) {
            agentHash += agentLocation.hashCode();
        }

        return agentHash;
    }

    /**
     * Returns the location of the agent
     * 
     * @param state
     * @return
     */
    public Location getAgentLocation(Agent agent) {
        return mAgentLocations.get(agent);
    }
}
