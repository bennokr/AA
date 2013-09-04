package com.uva.aa.model;

public abstract class Agent {

	private int mX;
	private int mY;
	protected final Environment mEnvironment;
	
	public Agent(final Environment environment, final int x, final int y) {
		mEnvironment = environment;
		setLocation(x, y);
	}

	public abstract void performAction(); 
	
	public void setLocation(int x, int y) {
		final int width = mEnvironment.getWidth();
		final int height = mEnvironment.getHeight();
		
		mX = x % width;
		mY = y % height;

		if (mX < 0) {
			mX += width;
		}
		if (mY < 0) {
			mY += height;
		}
		
		System.out.println(getClass().getSimpleName() + " moves to (" + x + ", " + y + ")");
	}

	public int getX() {
		return mX;
	}
	
	public int getY() {
		return mY;
	}
}
