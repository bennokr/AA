package com.uva.aa.agents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Config;
import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class OnPolicyMCPredatorAgent extends MCPredatorAgent {

    PreyAgent somePrey;
    private HashMap<State, HashMap<Action, Integer>> countReturn;

    public OnPolicyMCPredatorAgent(Location location) {
        super(location);
        countReturn = new HashMap<State, HashMap<Action, Integer>>();
    }

    @Override
    protected Action getActionToPerform(State state) {
        // save our prey before it gets eaten and lost (don't ask)
        somePrey = getEnvironment().getPreys().get(0);

        return mPolicy.getActionBasedOnProbability(state);
    }

    @Override
    protected void updatePolicyFromEpisode(Episode episode) {
        // iterate over every (s,a)
        for (int timestep = 0; timestep < episode.getLength(); timestep++) {
            State s = episode.getState(timestep);
            Action a = episode.getAction(timestep);

            // make sure the counter is initialized
            if (!countReturn.containsKey(s)) {
                countReturn.put(s, new HashMap<Action, Integer>());
            }
            if (!countReturn.get(s).containsKey(a)) {
                countReturn.get(s).put(a, 0);
            }

            // Set Q to the average discounted reward R
            // updated incrementally
            double i = countReturn.get(s).get(a) + 1.0;
            double q = (1 - (1 / i)) * mPolicy.getActionValue(s, a) + getDiscountedReward(episode, timestep) / i;
            mPolicy.setActionValue(episode.getState(timestep), episode.getAction(timestep), q);
            countReturn.get(s).put(a, (int) i);
        }

        for (int timestep = 0; timestep < episode.getLength(); timestep++) {
            State loopState = episode.getState(timestep);
            // update epsilon-soft policy
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
