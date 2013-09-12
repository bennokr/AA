package com.uva.aa.agents;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.uva.aa.Location;
import com.uva.aa.enums.Action;
import com.uva.aa.policies.IterativePolicyEvaluation;
import com.uva.aa.policies.Policy;
import com.uva.aa.policies.State;
import com.uva.aa.policies.StatePolicyProperties;

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
