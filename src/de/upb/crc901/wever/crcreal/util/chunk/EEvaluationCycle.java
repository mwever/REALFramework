package de.upb.crc901.wever.crcreal.util.chunk;

public enum EEvaluationCycle {
	ROUND("round"), GENERATION("generation"), LAST("last");

	private final String representation;

	private EEvaluationCycle(final String pRepresentaiton) {
		this.representation = pRepresentaiton;
	}

	@Override
	public String toString() {
		return this.representation;
	}
}
