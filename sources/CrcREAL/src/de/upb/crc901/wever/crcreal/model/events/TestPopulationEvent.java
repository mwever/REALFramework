package de.upb.crc901.wever.crcreal.model.events;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.word.CandidateTest;

public class TestPopulationEvent {

	private final int currentRoundNumber;
	private final int generationCounter;
	private final List<CandidateTest> testPopulation;

	public TestPopulationEvent(final int currentRoundNumber, final int generationNumber, final List<CandidateTest> pTestPopulation) {
		this.testPopulation = pTestPopulation;
		this.generationCounter = generationNumber;
		this.currentRoundNumber = currentRoundNumber;
	}

	public List<CandidateTest> getPopulation() {
		return this.testPopulation;
	}

	public int getRoundCounter() {
		return this.currentRoundNumber;
	}

	public int getGenerationCounter() {
		return this.generationCounter;
	}

}
