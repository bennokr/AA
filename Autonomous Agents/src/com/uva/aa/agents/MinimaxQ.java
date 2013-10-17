package com.uva.aa.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	LpSolve solver;

	protected MinimaxQ(Agent opponent, double alpha, double decay) {
		// initialize Q(s,a,o)
		stateGameValues = new HashMap<State, HashMap<Action, HashMap<Action, Double>>>();
		this.alpha = alpha;
		this.decay = decay;
		this.opponent = opponent;
		try {
			// Initialize immense overcapabilities
			solver = LpSolve.makeLp(0, Action.values().length);
			solver.setVerbose(0);
			solver.setMaxim();
			// Sum of probability distribution pi should be 1
			solver.addConstraint(new double[]{1, 1, 1, 1, 1}, LpSolve.EQ, 1);
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
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
		double minEndReward = Double.POSITIVE_INFINITY;
		Set<double[]> bestPolicyArrays = new HashSet<double[]>();
		try {
			for(Action o : Action.values()) {
				if (!stateGameValues.get(initialState).containsKey(o)) {
					stateGameValues.get(initialState).put(o, new HashMap<Action, Double>());
				}
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
				solver.solve();
				
				// Minimize outer loop
				double endReward = solver.getObjective();
				if (endReward <= minEndReward) {
					minEndReward = endReward;
					if (endReward < minEndReward) {
						bestPolicyArrays.clear();
					}
					bestPolicyArrays.add(solver.getPtrVariables());
				}
			} 
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
		for(Action a : Action.values()) {
			double prob = 0;
			for (double[] policyArray : bestPolicyArrays) {
				prob += policyArray[a.ordinal()] ;
			}
			policy.setActionProbability(initialState, a, prob);
		}
		policy.setStateValue(resultingState, minEndReward);
		alpha = alpha * decay;
	}
}
