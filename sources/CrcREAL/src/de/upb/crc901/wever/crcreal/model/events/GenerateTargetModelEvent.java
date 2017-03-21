package de.upb.crc901.wever.crcreal.model.events;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;

public class GenerateTargetModelEvent {

	private final int numberOfStates;
	private final Alphabet alphabet;
	private final int trainingSetSize;

	public GenerateTargetModelEvent(final int pNumberOfStates, final Alphabet pAlphabet, final int pTrainingSetSize) {
		this.numberOfStates = pNumberOfStates;
		this.alphabet = pAlphabet;
		this.trainingSetSize = pTrainingSetSize;
	}

	public int getNumberOfStates() {
		return this.numberOfStates;
	}

	public Alphabet getAlphabet() {
		return this.alphabet;
	}

	public int getTrainingSetSize() {
		return this.trainingSetSize;
	}
}
