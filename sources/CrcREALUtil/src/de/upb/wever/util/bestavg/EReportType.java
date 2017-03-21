package de.upb.wever.util.bestavg;

public enum EReportType {
	CANDIDATE_MODEL("model"), CANDIDATE_TEST("test");

	private final String name;

	private EReportType(final String pName) {
		this.name = pName;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(this.getName());

		return sb.toString();
	}
}
