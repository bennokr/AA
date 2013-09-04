package com.uva.aa;

import java.util.List;

import com.uva.aa.model.Agent;
import com.uva.aa.model.PreyAgent;
import com.uva.aa.model.PredatorAgent;
import com.uva.aa.model.Environment;

public class Game {

	private final Environment mEnvironment;
	private GameState mState = GameState.PREPARATION;
	
	public Game(final int width, final int height) {
		mEnvironment = new Environment(this, 11, 11);
	}
	
	public void addPrey(final int x, final int y) {
		final PreyAgent prey = new PreyAgent(mEnvironment, x, y);
		mEnvironment.addAgent(prey);
	}
	
	public void addPredator(final int x, final int y) {
		final PredatorAgent predator = new PredatorAgent(mEnvironment, x, y);
		mEnvironment.addAgent(predator);
	}
	
	public void start() {
		final List<Agent> agents = mEnvironment.getAgents();
		
		mState = GameState.RUNNING;
		
		// The iterator for the current agent
		int i = 0;
		while (mState == GameState.RUNNING) {
			agents.get(i).performAction();

			// Prepare for the next agent
			++i;
			
			// Check if we're at the end of the round
			if (agents.size() == i) {
				i = 0;
			}
			
			mEnvironment.print();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
	
}
