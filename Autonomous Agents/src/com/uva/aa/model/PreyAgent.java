package com.uva.aa.model;

import java.util.LinkedList;
import java.util.List;

public class PreyAgent extends Agent {
	
	private final static double MOVE_PROBABILITY = 0.2;
	
	public PreyAgent(final Environment environment) {
		super(environment);
	}

	@Override
	public void performAction() {
		final int[][] testPositions = {
				{getX() - 1, getY()},
				{getX() + 1, getY()},
				{getX(), getY() - 1},
				{getX(), getY() + 1}
		};
		final List<int[]> possiblePosition = new LinkedList<int[]>();
		
		for (int[] position : testPositions) {
			if (!mEnvironment.isOccupied(position[0], position[1])) {
				possiblePosition.add(position);
			}
		}
		
		if (possiblePosition.isEmpty()) {
			return;
		}

		final double value = Math.random();
		if (value < 0.2) {
			final int[] position = possiblePosition.get((int) Math.floor(value / MOVE_PROBABILITY * possiblePosition.size()));
			setLocation(position[0], position[1]);
		}
		
	}

}
