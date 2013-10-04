package com.uva.aa.agents;

import java.util.HashMap;

import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

/**
 * An agent that acts as a predator within the environment. Will learn about the prey by following Off-Policy Monte
 * Carlo.
 */
public class OffPolicyMCPredatorAgent extends MCPredatorAgent {

    /** The values weighed according to discounted returns mapped to state-action pairs */
    private HashMap<State, HashMap<Action, Double>> Qn;

    /** The values mapped to state-action pairs */
    private HashMap<State, HashMap<Action, Double>> Qd;

    private boolean mTraining = true;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public OffPolicyMCPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * Initialize Numerator and Denominator.
     */
    @Override
    public void prepare() {
        super.prepare();

        Qn = new HashMap<State, HashMap<Action, Double>>();
        Qd = new HashMap<State, HashMap<Action, Double>>();
        for (State state : getEnvironment().getPossibleStates(false)) {
            Qn.put(state, new HashMap<Action, Double>());
            Qd.put(state, new HashMap<Action, Double>());
            for (Action action : Action.values()) {
                Qn.get(state).put(action, 0.0);
                Qd.get(state).put(action, 0.0);
            }
        }
    }

    /**
     * Determines whether or not the predator should train or use the trained policy.
     * 
     * @param isTraining
     *            True if we should explore, false to exploit
     */
    public void setTraining(final boolean isTraining) {
        mTraining = isTraining;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Action getActionToPerform(final State state) {
        if (mTraining) {
            // Follow a random policy when training
            return Action.values()[(int) (Math.random() * Action.values().length)];
        } else {
            // Exploit the trained policy
            return mPolicy.getActionBasedOnProbability(state);
        }
    }

    /**
     * Gets the probability of an action for a state in the off-policy. Will be an equally divided chance as we're
     * following a random policy.
     * 
     * @param state
     *            The state to check for
     * @param action
     *            The action to check for probability
     * 
     * @return The chance of the action occuring
     */
    private double getPerformanceProbability(final State state, final Action action) {
        return 1.0 / Action.values().length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updatePolicyFromEpisode(final Episode episode) {
        // Don't u[pdate if we weren't training
        if (!mTraining) {
            return;
        }

        // tau is the last timestep where our taken action differed from the trained policy's action
        int tau = episode.getLength() - 1;
        while (tau > 0 && mPolicy.getActionsBasedOnProbability(episode.getState(tau)).contains(episode.getAction(tau))) {
            tau--;
        }

        // Update Q(s,a) for each timestamp following tau
        for (int i = tau; i < episode.getLength(); i++) {
            final State state = episode.getState(i);
            final Action action = episode.getAction(i);

            // t is the time of first occurrence of (s,a) given t >= tau
            int t = tau;
            while (!episode.getState(t).equals(state) || !episode.getAction(t).equals(action)) {
                ++t;
            }

            // Find the weight based on the probability
            double w = 1.0;
            for (int k = t + 1; k < episode.getLength() - 1; k++) {
                // Look at the off-policy
                w /= getPerformanceProbability(episode.getState(k), episode.getAction(k));
            }

            // Update Numerator: $N_{sa} += w * R_t$
            Qn.get(state).put(action, Qn.get(state).get(action) + w * getDiscountedReturn(episode, t));

            // Update Denominator: $D_{sa} += w$
            Qd.get(state).put(action, Qd.get(state).get(action) + w);

            // Update Q(s,a)
            double Q = Qn.get(state).get(action) / Qd.get(state).get(action);
            if (Double.isNaN(Q)) {
                // This may happen for extreme values of either Qn or Qd
                Q = 0;
            }
            mPolicy.setActionValue(state, action, Q);
        }

        // Make the policy greedy with respect to Q
        for (State state : getEnvironment().getPossibleStates(false)) {
            double bestActionValue = Integer.MIN_VALUE;
            double countBest = 0;

            // Find the best action value
            for (final Action action : Action.values()) {
                double value = mPolicy.getActionValue(state, action);
                if (value > bestActionValue) {
                    bestActionValue = value;
                    countBest = 1;
                } else if (value == bestActionValue) {
                    ++countBest;
                }
            }

            // Update the policy based on the best action values
            for (final Action action : Action.values()) {
                if (mPolicy.getActionValue(state, action) == bestActionValue) {
                    mPolicy.setActionProbability(state, action, 1 / countBest);
                } else {
                    mPolicy.setActionProbability(state, action, 0);
                }
            }
        }
    }
}
