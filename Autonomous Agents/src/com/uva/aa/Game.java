package com.uva.aa;

import java.util.ArrayList;
import java.util.List;

import com.uva.aa.model.Agent;
import com.uva.aa.model.PreyAgent;
import com.uva.aa.model.PredatorAgent;
import com.uva.aa.model.Environment;

public class Game {

	private final Environment mEnvironment;
	private final List<Agent> mAgents = new ArrayList<Agent>();
	private GameState mState = GameState.PREPARATION;
	
	public Game(final int width, final int height) {
		mEnvironment = new Environment(this, 11, 11);
	}

	private void addAgent(final Agent agent, final int x, final int y) {
		agent.setLocation(x, y);
		mAgents.add(agent);
	}
	
	public void addPrey(final int x, final int y) {
		final PreyAgent prey = new PreyAgent(mEnvironment);
		mEnvironment.addAgent(prey);
		addAgent((Agent) prey, x, y);
	}
	
	public void addPredator(final int x, final int y) {
		final PredatorAgent predator = new PredatorAgent(mEnvironment);
		mEnvironment.addAgent(predator);
		addAgent((Agent) predator, x, y);
	}
	
	public void start() {
		mState = GameState.RUNNING;
		
		// The iterator for the current agent
		int i = 0;
		while (mState == GameState.RUNNING) {
			mAgents.get(i).performAction();

			// Prepare for the next agent
			++i;
			
			// Check if we're at the end of the round
			if (mAgents.size() == i) {
				i = 0;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public List<Agent> getAgents() {
		return mAgents;
	}
	
}
