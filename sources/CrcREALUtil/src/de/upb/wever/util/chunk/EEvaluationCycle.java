package de.upb.wever.util.chunk;

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
