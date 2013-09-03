package com.uva.aa.model;

public abstract class Agent {

	private int mX;
	private int mY;
	protected final Environment mEnvironment;
	
	public Agent(final Environment environment) {
		mEnvironment = environment;
	}

	public abstract void performAction(); 
	
	public void setLocation(final int x, final int y) {
		mX = x;
		mY = y;
		
		System.out.println(getClass().getSimpleName() + " moves to (" + x + ", " + y + ")");
	}

	public int getX() {
		return mX;
	}
	
	public int getY() {
		return mY;
	}
}
