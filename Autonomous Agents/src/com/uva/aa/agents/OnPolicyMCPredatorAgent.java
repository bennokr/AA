package com.uva.aa.agents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Config;
import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following On-Policy Monte
 * Carlo.
 */
public class OnPolicyMCPredatorAgent extends MCPredatorAgent {

    /** The mapped rewards for each state-action pair */
    private final HashMap<State, HashMap<Action, Integer>> countReturn = new HashMap<State, HashMap<Action, Integer>>();

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public OnPolicyMCPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Action getActionToPerform(State state) {
        return mPolicy.getActionBasedOnProbability(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updatePolicyFromEpisode(Episode episode) {
        // Iterate over every (s,a)
        for (int timestep = 0; timestep < episode.getLength(); timestep++) {
            final State state = episode.getState(timestep);
            final Action action = episode.getAction(timestep);

            // Make sure the counter is initialized
            if (!countReturn.containsKey(state)) {
                countReturn.put(state, new HashMap<Action, Integer>());
            }
            if (!countReturn.get(state).containsKey(action)) {
                countReturn.get(state).put(action, 0);
            }

            // Set Q to the average discounted reward R
            // updated incrementally
            double i = countReturn.get(state).get(action) + 1.0;
            double q = (1 - (1 / i)) * mPolicy.getActionValue(state, action) + getDiscountedReturn(episode, timestep)
                    / i;
            mPolicy.setActionValue(episode.getState(timestep), episode.getAction(timestep), q);
            countReturn.get(state).put(action, (int) i);
        }

        // Update epsilon-soft policy
        for (int timestep = 0; timestep < episode.getLength(); timestep++) {
            final State loopState = episode.getState(timestep);
            final List<Action> bestActions = new LinkedList<Action>();
            double bestValue = Double.MIN_VALUE;

            for (final Action action : Action.values()) {
                double loopValue = mPolicy.getActionValue(loopState, action);
                if (loopValue > bestValue) {
                    bestValue = loopValue;
                    bestActions.clear();
                }
                if (loopValue >= bestValue) {
                    bestActions.add(action);
                }
            }

            for (final Action action : Action.values()) {
                double p = (Config.EPSILON / Action.values().length)
                        + (bestActions.contains(action) ? (1 - Config.EPSILON) / bestActions.size() : 0);
                mPolicy.setActionProbability(loopState, action, p);
            }
        }
    }

}
