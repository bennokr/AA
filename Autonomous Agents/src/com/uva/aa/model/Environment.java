package com.uva.aa.model;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.uva.aa.Game;

public class Environment {

	private final int mWidth;
	private final int mHeight;

	private final Game mGame;

	private final List<Agent> mAgents = new ArrayList<Agent>();
	private final List<PreyAgent> mPreys = new ArrayList<PreyAgent>();
	private final List<PredatorAgent> mPredators = new ArrayList<PredatorAgent>();

	public Environment(final Game game, final int width, final int height) {
		mGame = game;
		mWidth = width;
		mHeight = height;
	}

	public void addAgent(final PreyAgent prey) {
		mAgents.add(prey);
		mPreys.add(prey);
	}

	public void addAgent(final PredatorAgent predator) {
		mAgents.add(predator);
		mPredators.add(predator);
	}

	public List<Agent> getAgents() {
		return mAgents;
	}

	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}

	public boolean isOccupied(final int x, final int y, final List<Agent> agents) {
		for (Agent agent : agents) {
			if (agent.getX() == x && agent.getY() == y) {
				return true;
			}
		}

		return false;
	}

	public boolean isOccupied(final int x, final int y) {
		return isOccupied(x, y, mAgents);
	}

	@SuppressWarnings("unchecked")
	public boolean isOccupiedByPrey(final int x, final int y) {
		return isOccupied(x, y, (List<Agent>)(List<?>) mPreys);
	}

	@SuppressWarnings("unchecked")
	public boolean isOccupiedByPredator(final int x, final int y) {
		return isOccupied(x, y, (List<Agent>)(List<?>) mPredators);
	}

	public void print() {
		final PrintStream out = System.out;
		
		// Print the top border
		out.print(" ");
		for (int x = 0; x < mWidth; ++x) {
			out.print("-");
		}
		out.print(" ");
		out.println();

		// Print each row
		for (int y = 0; y < mHeight; ++y) {
			out.print("|");

			// Print each column
			for (int x = 0; x < mWidth; ++x) {
				if (isOccupiedByPredator(x, y)) {
					out.print("X");
				} else if (isOccupiedByPrey(x, y)) {
					out.print("O");
				} else {
					out.print(" ");
				}
			}

			out.print("|");
			out.println();
		}

		// Print the bottom border
		out.print(" ");
		for (int x = 0; x < mWidth; ++x) {
			out.print("-");
		}
		out.print(" ");
		out.println();
	}
}
