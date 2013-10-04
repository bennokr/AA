package com.uva.aa.agents;

import java.util.HashMap;
import java.util.Map;

import com.uva.aa.Config;
import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class OffPolicyMCPredatorAgent extends MCPredatorAgent {

    private HashMap<State, HashMap<Action, Double>> Qn; // Numerator
    private HashMap<State, HashMap<Action, Double>> Qd; // Denominator

    private boolean mTraining = true;

    public OffPolicyMCPredatorAgent(Location location) {
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

    public void setTraining(final boolean isTraining) {
        mTraining = isTraining;
    }

    @Override
    protected Action getActionToPerform(State state) {
        if (mTraining) {
            // random action
            return Action.values()[(int) (Math.random() * Action.values().length)];
        } else {
            return mPolicy.getActionBasedOnProbability(state);
        }
    }

    private double getPerformanceProbability(State state, Action action) {
        return 1.0 / Action.values().length;
    }

    @Override
    protected void updatePolicyFromEpisode(Episode episode) {
        if (!mTraining) {
            return;
        }

        // tau is the last moment where $a_\tau \neq \pi(s_\tau)$
        int tau = episode.getLength() - 1;
        while (tau > 0 && mPolicy.getActionsBasedOnProbability(episode.getState(tau)).contains(episode.getAction(tau))) {
            tau--;
        }

        for (int i = tau; i < episode.getLength(); i++) {
            State state = episode.getState(i);
            Action action = episode.getAction(i);

            // t is the time of first occurrence of (s,a) such that t >= tau
            int t = tau;
            while (!episode.getState(t).equals(state) || !episode.getAction(t).equals(action)) {
                ++t;
            }

            double w = 1.0;
            for (int k = t + 1; k < episode.getLength() - 1; k++) {
                // Look at the off-policy
                w /= getPerformanceProbability(episode.getState(k), episode.getAction(k));
            }
            // System.out.println(w);
            // Update Numerator: $N_{sa} += w * R_t$
            Qn.get(state).put(action, Qn.get(state).get(action) + w * getDiscountedReward(episode, t));
            // Update Denominator: $D_{sa} += w$
            Qd.get(state).put(action, Qd.get(state).get(action) + w);
            // Update Q
            double Q = Qn.get(state).get(action) / Qd.get(state).get(action);
            if (Double.isNaN(Q)) {
                Q = 0;
            }
            mPolicy.setActionValue(state, action, Q);
            System.out.println(Q);
        }

        // Make the policy greedy wrt Q
        for (State state : getEnvironment().getPossibleStates(false)) {
            double bestActionValue = Integer.MIN_VALUE;
            double countBest = 0;
            for (final Action action : Action.values()) {
                double value = mPolicy.getActionValue(state, action);
                if (value > bestActionValue) {
                    bestActionValue = value;
                    countBest = 1;
                } else if (value == bestActionValue) {
                    ++countBest;
                }
            }

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
