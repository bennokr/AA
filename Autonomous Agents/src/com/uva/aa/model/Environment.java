package com.uva.aa.model;

import java.util.ArrayList;
import java.util.List;

import com.uva.aa.Game;

public class Environment {

	private final int mWidth;
	private final int mHeight;
	
	private final Game mGame;
	
	private final List<PreyAgent> mPreys = new ArrayList<PreyAgent>();
	private final List<PredatorAgent> mPredators = new ArrayList<PredatorAgent>();
	
	public Environment(final Game game, final int width, final int height) {
		mGame = game;
		mWidth = width;
		mHeight = height;
	}
	
	public void addAgent(final PreyAgent prey) {
		mPreys.add(prey);
	}
	
	public void addAgent(final PredatorAgent predator) {
		mPredators.add(predator);
	}
	
	public boolean isOccupied(final int x, final int y) {
		for (Agent agent : mGame.getAgents()) {
			if (agent.getX() == x && agent.getY() == y) {
				return true;
			}
		}
		
		return false;
	}
}
