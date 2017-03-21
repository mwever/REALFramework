package de.upb.crc901.wever.crcreal.model.config;

public enum EEvaluationCycle {
	ROUND("round"), GENERATION("generation"), LAST("last");

	private String name;

	private EEvaluationCycle(final String pName) {
		this.name = pName;
	}

	public String getName() {
		return this.name;
	}

}
