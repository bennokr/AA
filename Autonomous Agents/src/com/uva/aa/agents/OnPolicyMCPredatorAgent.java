package com.uva.aa.agents;

import com.uva.aa.Episode;
import com.uva.aa.Location;
import com.uva.aa.State;
import com.uva.aa.enums.Action;

public class OnPolicyMCPredatorAgent extends MCPredatorAgent {

	public OnPolicyMCPredatorAgent(Location location) {
		super(location);
	}

	@Override
	protected Action getActionToPerform(State state) {
		// TODO get epsilon-soft action
		return null;
	}

	@Override
	protected void updatePolicyFromEpisode(Episode episode) {
		// TODO Implement this:
		// iterate over every (s,a)
		//  get reward *(discounted or end)*
		//  policy.setActionValue(State, Action, double)
		// iterate over states
		//  update epsilon-soft policy
	}

}
