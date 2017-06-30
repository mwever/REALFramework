package de.upb.crc901.wever.crcreal.model.events;

public class ModelPopulationEvaluationEvent {

	private final ModelPopulationEvent modelPopulationEvent;

	public ModelPopulationEvaluationEvent(final ModelPopulationEvent pModelPopulationEvent) {
		this.modelPopulationEvent = pModelPopulationEvent;
	}

	public ModelPopulationEvent getModelList() {
		return this.modelPopulationEvent;
	}

}
