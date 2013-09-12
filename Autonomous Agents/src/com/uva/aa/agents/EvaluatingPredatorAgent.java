package com.uva.aa.agents;

import java.util.List;

import com.uva.aa.Location;
import com.uva.aa.policies.IterativePolicyEvaluation;
import com.uva.aa.policies.State;

/**
 * An agent that acts as a predator within the environment. Will move towards the prey according to an evaluated policy.
 */
public class EvaluatingPredatorAgent extends PredatorAgent {

    /**
     * Creates a new predator on the specified coordinates within the environment.
     * 
     * @param location
     *            The location to place the predator at
     */
    public EvaluatingPredatorAgent(final Location location) {
        super(location);
    }

    /**
     * Starts with a random policy but will evaluate that to improve.
     */
    @Override
    public void prepare() {
        super.prepare();

        final List<State> possibleStatesExclTerminal = getEnvironment().getPossibleStates(false);
        final List<State> possibleStatesInclTerminal = getEnvironment().getPossibleStates(true);

        final IterativePolicyEvaluation ipe = new IterativePolicyEvaluation(this, possibleStatesExclTerminal,
                possibleStatesInclTerminal);
        ipe.estimateValueFunction(mPolicy);
    }
}
