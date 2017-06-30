package de.upb.crc901.wever.crcreal.model.events;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;

public class ModelPopulationEvent {

	private int roundCounter = -1;
	private final int generationCounter;
	private final List<CandidateModel> population;

	public ModelPopulationEvent(final int roundCounter, final int generationCounter, final List<CandidateModel> pCurrentPopulation) {
		this.population = pCurrentPopulation;
		this.roundCounter = roundCounter;
		this.generationCounter = generationCounter;
	}

	public List<CandidateModel> getPopulation() {
		return this.population;
	}

	public int getRoundCounter() {
		return this.roundCounter;
	}

	public int getGenerationCounter() {
		return this.generationCounter;
	}

}
