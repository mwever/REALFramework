package de.upb.wever.util.chunk;

public enum EEvaluationType {
	ELITE("elite"), ALL("all");

	private final String representation;

	private EEvaluationType(final String pRepresentation) {
		this.representation = pRepresentation;
	}

	@Override
	public String toString() {
		return this.representation;
	}
}
