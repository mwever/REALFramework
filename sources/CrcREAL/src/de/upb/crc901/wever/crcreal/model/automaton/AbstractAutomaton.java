package de.upb.crc901.wever.crcreal.model.automaton;

public class AbstractAutomaton {
	
	private AbstractTransitionFunction transitionFunction;
	
	protected AbstractAutomaton(final AbstractTransitionFunction pTransitionFunction) {
		this.transitionFunction = pTransitionFunction;
	}
	
	public AbstractTransitionFunction getTransitionFunction() {
		return this.transitionFunction;
	}

}
