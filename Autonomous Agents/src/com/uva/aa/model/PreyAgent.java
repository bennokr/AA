package com.uva.aa.model;

import java.util.LinkedList;
import java.util.List;

public class PreyAgent extends Agent {
	
	/** The probability that the prey will move instead of wait */
	private final static double MOVE_PROBABILITY = 0.2;
	
	public PreyAgent(final Environment environment, final int x, final int y) {
		super(environment, x, y);
	}

	@Override
	public void performAction() {
		final List<int[]> possiblePosition = new LinkedList<int[]>();
		
		// The possible positions that might be moved to
		final int[][] testPositions = {
				{getX() - 1, getY()},
				{getX() + 1, getY()},
				{getX(), getY() - 1},
				{getX(), getY() + 1}
		};
		
		// Check which position is actually a valid position to move to
		for (int[] position : testPositions) {
			if (!mEnvironment.isOccupied(position[0], position[1])) {
				possiblePosition.add(position);
			}
		}
		
		// If there are no positions to move to, wait
		if (possiblePosition.isEmpty()) {
			return;
		}

		// Choose whether to wait or move to a random (available) position
		final double value = Math.random();
		if (value < 0.2) {
			final int[] position = possiblePosition.get((int) Math.floor(value / MOVE_PROBABILITY * possiblePosition.size()));
			setLocation(position[0], position[1]);
		}
		
	}

}
