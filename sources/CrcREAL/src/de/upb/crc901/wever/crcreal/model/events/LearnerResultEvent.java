package de.upb.crc901.wever.crcreal.model.events;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;

public class LearnerResultEvent {

	private final List<CandidateModel> solutionSet;

	public LearnerResultEvent(final List<CandidateModel> pSolutionSet) {
		this.solutionSet = pSolutionSet;
	}

	public List<CandidateModel> getSolutionSet() {
		return this.solutionSet;
	}

}
