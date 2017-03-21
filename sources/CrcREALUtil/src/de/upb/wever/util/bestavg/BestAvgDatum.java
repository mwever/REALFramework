package de.upb.wever.util.bestavg;

public class BestAvgDatum {

	public static final String HEURISTIC_PREFIX = "heur-";
	public static final String REAL_PREFIX = "real-";

	private final String objectiveID;
	private final double best;
	private final double avg;

	public BestAvgDatum(final String objectiveID, final double best, final double avg) {
		this.objectiveID = objectiveID;
		this.best = best;
		this.avg = avg;
	}

	public String getObjectiveID() {
		return this.objectiveID;
	}

	public boolean isReal() {
		return this.objectiveID.startsWith(REAL_PREFIX);
	}

	public boolean isHeuristic() {
		return this.objectiveID.startsWith(HEURISTIC_PREFIX);
	}

	public double getBest() {
		return this.best;
	}

	public double getAverage() {
		return this.avg;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.best + ";" + this.avg);
		return sb.toString();
	}

	public static BestAvgDatum readFrom(final String line) {
		final String[] lineSplit = line.split("=");
		final String[] valueSplit = lineSplit[1].split(";");
		return new BestAvgDatum(lineSplit[0], Double.valueOf(valueSplit[0]), Double.valueOf(valueSplit[1]));
	}

}
