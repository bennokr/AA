package com.uva.aa;

import java.util.ArrayList;

import com.uva.aa.enums.Action;

public class Episode {
	
	private ArrayList<State>  states;
	private ArrayList<Action>  actions;
	private ArrayList<Double> rewards;
	
	public Episode() {
		states = new ArrayList<State>();
		actions = new ArrayList<Action>();
		rewards = new ArrayList<Double>();		
	}
	public void clear() {
		states.clear();
		actions.clear();
		rewards.clear();
	}

	public void addState(State currentState) {
		states.add(currentState);
	}

	public void addAction(Action nextAction) {
		actions.add(nextAction);
	}

	public void addReward(double immediateReward) {
		rewards.add(immediateReward);
	}
	
	public int getLength() {
		return actions.size();
	}

	public Double getReward(int t) {
		return rewards.get(t);
	}

	public State getState(int t) {
		return states.get(t);
	}

	public Action getAction(int t) {
		return actions.get(t);
	}

}
