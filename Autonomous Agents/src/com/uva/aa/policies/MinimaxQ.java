package com.uva.aa.policies;

import java.util.HashMap;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import com.uva.aa.Config;
import com.uva.aa.State;
import com.uva.aa.agents.Agent;
import com.uva.aa.enums.Action;

public class MinimaxQ {

    /** The zero-sum game payoff matrix per state or Double Q[s][o][a] or Q :: s -> o -> a -> Double */
    private final HashMap<State, HashMap<Action, HashMap<Action, Double>>> stateGameValues;

    /** The zero-sum game theoretical opponent */
    private Agent mOpponent;

    double mAlpha;
    double mDecay;

    public MinimaxQ(double alpha, double decay) {
        // initialize Q(s,o,a)
        stateGameValues = new HashMap<State, HashMap<Action, HashMap<Action, Double>>>();
        this.mAlpha = alpha;
        this.mDecay = decay;
    }

    public void setOpponent(Agent opponent) {
        this.mOpponent = opponent;
    }

    /*
     * Update policy with Minimax-Q
     */
    public void learn(State initialState, State resultingState, Action previousAction, double reward, Policy policy) {
        // Init action value map
        if (!stateGameValues.containsKey(initialState)) {
            stateGameValues.put(initialState, new HashMap<Action, HashMap<Action, Double>>());
        }
        if (!stateGameValues.get(initialState).containsKey(mOpponent.getLastAction())) {
            stateGameValues.get(initialState).put(mOpponent.getLastAction(), new HashMap<Action, Double>());
        }

        // Q(s,a,o) = (1-alpha) * Q(s,a,o) + alpha * (r + gamma * V(s'))
        double Q = Config.DEFAULT_ACTION_VALUE;
        if (stateGameValues.get(initialState).get(mOpponent.getLastAction()).containsKey(previousAction)) {
            Q = stateGameValues.get(initialState).get(mOpponent.getLastAction()).get(previousAction);
        }
        stateGameValues
                .get(initialState)
                .get(mOpponent.getLastAction())
                .put(previousAction,
                        (1 - mAlpha) * Q + mAlpha
                                * (reward + Config.DISCOUNT_FACTOR_GAMMA * policy.getStateValue(resultingState)));

        try {
            // With 1 extra parameter for the value of the minimization
            LpSolve solver = LpSolve.makeLp(0, Action.values().length + 1);
            solver.setVerbose(1);
            solver.setMaxim();
            // Sum of probability distribution pi should be 1
            solver.addConstraint(new double[] { 0, 1, 1, 1, 1, 1, 0 }, LpSolve.EQ, 1);
            // v has no lower boundary
            solver.setLowbo(Action.values().length + 1, Double.NEGATIVE_INFINITY);

            for (Action a : Action.values()) {
                solver.setColName(a.ordinal() + 1, a.name());
            }

            for (Action o : Action.values()) {
                // Init action value map if not present
                if (!stateGameValues.get(initialState).containsKey(o)) {
                    stateGameValues.get(initialState).put(o, new HashMap<Action, Double>());
                }

                // Java wants us to loop so we loop-de-loop
                double[] minimizationConstraint = new double[Action.values().length + 2];
                for (Action a : Action.values()) {
                    // Init action value if not present
                    if (!stateGameValues.get(initialState).get(o).containsKey(a)) {
                        stateGameValues.get(initialState).get(o).put(a, Config.DEFAULT_ACTION_VALUE);
                    }
                    minimizationConstraint[a.ordinal() + 1] = -stateGameValues.get(initialState).get(o).get(a);
                }
                minimizationConstraint[Action.values().length + 1] = 1;
                solver.addConstraint(minimizationConstraint, LpSolve.LE, 0);
            }

            // Maximize
            solver.setObjFn(new double[] { 0, 0, 0, 0, 0, 0, 1 });
            if (mOpponent.getEnvironment().getPreys().isEmpty()) {
                solver.printLp();
            }
            solver.solve();

            // Update
            double R = solver.getObjective();
            double sum = 0;
            for (Action a : Action.values()) {
                double p = solver.getPtrVariables()[a.ordinal()];
                // System.out.print(p + " ");
                policy.setActionProbability(initialState, a, p);
                sum += p;
            }
            
            if (1 - sum > 0.0001) {
                System.err.println("bad sum!");
            }

            policy.setStateValue(resultingState, R);
            mAlpha = mAlpha * mDecay;
            solver.deleteLp();

        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }
}
