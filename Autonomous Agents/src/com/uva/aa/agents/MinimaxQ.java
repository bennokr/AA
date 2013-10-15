package com.uva.aa.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import com.uva.aa.Config;
import com.uva.aa.State;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.Policy;

public class MinimaxQ {
	
	/* The zero-sum game payoff matrix per state
	 * or Double Q[s][o][a]
	 * or Q :: s -> o -> a -> Double
	 */
	private final HashMap<State, HashMap<Action, HashMap<Action, Double>>> stateGameValues;
	
	/*
	 * The zero-sum game theoretical opponent 
	 */
	private Agent opponent;
	
	double alpha;
	double decay;
	
	protected MinimaxQ(Agent opponent, double alpha, double decay) {
        // initialize Q(s,a,o)
        stateGameValues = new HashMap<State, HashMap<Action, HashMap<Action, Double>>>();
        this.alpha = alpha;
        this.decay = decay;
        this.opponent = opponent;
	}
	
	/*
	 * Update policy with Minimax-Q
	 * 
	 */
	protected void learn(State initialState, State resultingState, Action previousAction, double reward, Policy policy) {	
//		for(Action a : Action.values()) {
//			System.out.print(policy.getActionProbability(initialState, a) + " ");
//		}
//		System.out.println();
		
		
		
		// Init Q
		if (!stateGameValues.containsKey(initialState)) {
			stateGameValues.put(initialState, new HashMap<Action, HashMap<Action, Double>>());
		}
		if (!stateGameValues.get(initialState).containsKey(opponent.getLastAction())) {
			stateGameValues.get(initialState).put(opponent.getLastAction(), new HashMap<Action, Double>());
		}
		
		// Q(s,a,o) = (1-alpha) * Q(s,a,o) + alpha * (r + gamma * V(s'))
		double Q = 0;
		if (stateGameValues.get(initialState).get(opponent.getLastAction()).containsKey(previousAction)) {
			Q = stateGameValues.get(initialState).get(opponent.getLastAction()).get(previousAction);
		}
		stateGameValues.get(initialState).get(opponent.getLastAction()).put(previousAction, 
				(1-alpha) * Q 
				+ alpha * (reward + Config.DISCOUNT_FACTOR_GAMMA * policy.getStateValue(resultingState)));
		
		// Minimize, starting with infinity
		double R = Double.POSITIVE_INFINITY;
		double[] pi = new double[Action.values().length];
		for(Action o : Action.values()) {
			if (!stateGameValues.get(initialState).containsKey(o)) {
				stateGameValues.get(initialState).put(o, new HashMap<Action, Double>());
			}
			try {
				// Initialize immense overcapabilities
				LpSolve solver = LpSolve.makeLp(0, Action.values().length);
				solver.setVerbose(0);
				// Sum of probability distribution pi should be 1
				solver.addConstraint(new double[]{1, 1, 1, 1, 1}, LpSolve.EQ, 1);
				// Java wants us to loop so we loop-de-loop
				double[] Qs = new double[Action.values().length];
				for (Action a : Action.values()) {
					// Init action value if not present
					if (!stateGameValues.get(initialState).get(o).containsKey(a)) {
						stateGameValues.get(initialState).get(o).put(a, Config.DEFAULT_ACTION_VALUE);
					}
					//solver.setColName(a.ordinal()+1, a.name());
					Qs[a.ordinal()] = stateGameValues.get(initialState).get(o).get(a);
				}
				// Maximize over innerproduct(Q(s,o), pi)
				solver.setObjFn(Qs);
				solver.setMaxim();
				solver.solve();
				
				// Minimize outer loop
				double newR = solver.getObjective();
				double[] newPi = solver.getPtrVariables();
				
				if (newR < R) {
					System.out.println(o.name());
					R = newR;
					pi = newPi.clone();
				} else { System.err.println(o.name()); }
			} catch (LpSolveException e) {
				e.printStackTrace();
			}
		}
		for(Action a : Action.values()) {
//			System.out.print(pi[a.ordinal()] + " ");
			policy.setActionProbability(initialState, a, pi[a.ordinal()]);
		}
//		System.out.println();
		policy.setStateValue(resultingState, R);
		alpha = alpha * decay;
	}
}
