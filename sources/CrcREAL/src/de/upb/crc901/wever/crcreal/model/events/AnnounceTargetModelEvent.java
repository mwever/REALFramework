package de.upb.crc901.wever.crcreal.model.events;

import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;

public class AnnounceTargetModelEvent {

	private final FiniteAutomaton targetModel;

	public AnnounceTargetModelEvent(final FiniteAutomaton pTargetModel) {
		this.targetModel = pTargetModel;
	}

	public FiniteAutomaton getTargetModel() {
		return this.targetModel;
	}

}
