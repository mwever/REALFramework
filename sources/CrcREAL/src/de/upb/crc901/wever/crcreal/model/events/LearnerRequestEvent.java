package de.upb.crc901.wever.crcreal.model.events;

import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class LearnerRequestEvent {

	private final int numberOfStates;
	private final TrainingSet trainingData;

	public LearnerRequestEvent(final int pNumberOfStates, final TrainingSet pTrainingData) {
		this.trainingData = pTrainingData;
		this.numberOfStates = pNumberOfStates;
	}

	public TrainingSet getTrainingData() {
		return this.trainingData;
	}

	public int getNumberOfStates() {
		return this.numberOfStates;
	}

}
