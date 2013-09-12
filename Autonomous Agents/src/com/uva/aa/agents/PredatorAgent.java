package com.uva.aa.agents;

import java.util.LinkedList;
import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.IterativePolicyEvaluation;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.State;

/**
 * An agent that acts as a predator within the environment. Will randomly move, hoping to catch a prey.
 */
public class PredatorAgent extends Agent {

    /** The policy the predator follows */
    private Policy mPolicy;
    private List<State> mPossibleStatesExclTerminal;
    private List<State> mPossibleStatesInclTerminal;

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public PredatorAgent(final Location location) {
        super(location);

        initPolicy();
    }

    /**
     * Initialize the random policy: for each state the predator can be in, every possible action will be chosen with
     * the same probability
     */
    public void initPolicy() {
        mPolicy = new Policy();
        mPossibleStatesExclTerminal = getEnvironment().getPossibleStates(false);
        mPossibleStatesInclTerminal = getEnvironment().getPossibleStates(true);
        // TODO: Remove console output when true
        System.out.println("This should be 14.520" + mPossibleStatesExclTerminal.size());
        System.out.println("This should be 14.641" + mPossibleStatesExclTerminal.size());
        for (final State state : mPossibleStatesExclTerminal) {
            for (final Action action : Action.values()) {
                mPolicy.setActionProbability(state, action, 1 / Action.values().length);
            }
        }
    }

    /**
     * Evaluates the policy of the predator
     * 
     */
    public void evaluatePolicy() {
        IterativePolicyEvaluation iterativePolicyEvaluation = new IterativePolicyEvaluation(this,
                mPossibleStatesExclTerminal, mPossibleStatesInclTerminal);
        iterativePolicyEvaluation.estimateValueFunction(mPolicy);
    }
    
    /** 
     * The transition probability that accounts for the prey movement
     */
    public double getTransitionProbability(final State initialState, final State resultingState, final Action action) {
    	// The moves should be deterministic
    	if (resultingState.getAgentLocation(this).equals( action.getLocation(initialState.getAgentLocation(this)) )) {
    		// We assume one prey and one predator!
    		PreyAgent prey = getEnvironment().getPreys().get(0);
    		// Check if the prey is alive
    		if (resultingState.getAgentLocation( prey ) != null) {
    			// Return the probability of the prey moving to its location in the resulting state from where it is in the initial state
    			for (final Action direction : Action.values()) {
    				// check if the prey moved in this direction
    				if (direction.getLocation(initialState.getAgentLocation( prey )).equals( resultingState.getAgentLocation( prey ) )) {
    					// return the chance we moved here
    					return prey.getPolicy().getActionProbability(initialState, direction);
    				}
    			}
    			// Should be unreachable!
    			return 0.0;
    		} else {
    			return 1.0;
    		}
    	} else {
    		return 0.0;
    	}
    }
    
    /** 
     * The deterministic immediate reward
     */
    public double getImmediateReward(final State initialState, final State resultingState, final Action action) {
    	// The reward does not depend on the action, only on the locations
    	// The prey moves 'after' the predator
    	// We assume one prey and one predator!
		PreyAgent prey = getEnvironment().getPreys().get(0);
    	if (resultingState.getAgentLocation(this).equals( initialState.getAgentLocation(prey) )) {
    		return 10.0;
    	} else {
    		return 0.0;
    	}
    }

    /**
     * {@inheritDoc}
     * 
     * Will randomly move, hoping to catch a prey.
     */
    @Override
    public void performAction() {

        // TODO: Follow policy instead of custom logic

        // Check which directions are valid moves (i.e., have no other agent on the resulting location)
        final List<Location> possibleLocations = new LinkedList<Location>();
        for (final Action direction : Action.values()) {
            final Location possibleLocation = direction.getLocation(this);
            if (!getEnvironment().isOccupiedByPredator(possibleLocation)) {
                possibleLocations.add(possibleLocation);
            }
        }

        // If there are no locations to move to, wait
        if (possibleLocations.isEmpty()) {
            return;
        }

        // Choose whether to wait or move to a random (available) location
        moveTo(possibleLocations.get((int) Math.floor(Math.random() * possibleLocations.size())));
    }
}
