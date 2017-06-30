package de.upb.crc901.wever.crcreal.util.boxplot;

public class BoxPlotDatum {

	private final Double minimum;
	private final Double maximum;
	private final Double average;
	private final Double median;
	private final Double lowerQuartile;
	private final Double upperQuartile;

	public BoxPlotDatum(final Double pMinimum, final Double pMaximum, final Double pAverage, final Double pMedian, final Double pLowerQuartile, final Double pUpperQuartile) {
		this.minimum = pMinimum;
		this.maximum = pMaximum;
		this.average = pAverage;
		this.median = pMedian;
		this.lowerQuartile = pLowerQuartile;
		this.upperQuartile = pUpperQuartile;
	}

	public Double getMinimum() {
		return this.minimum;
	}

	public Double getMaximum() {
		return this.maximum;
	}

	public Double getAverage() {
		return this.average;
	}

	public Double getMedian() {
		return this.median;
	}

	public Double getLowerQuartile() {
		return this.lowerQuartile;
	}

	public Double getUpperQuartile() {
		return this.upperQuartile;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("(");

		sb.append("Min:" + this.getMinimum() + ";");
		sb.append("Max:" + this.getMaximum() + ";");
		sb.append("Med:" + this.getMedian() + ";");
		sb.append("Q1:" + this.getLowerQuartile() + ";");
		sb.append("Q3:" + this.getUpperQuartile() + ";");
		sb.append("Avg:" + this.getAverage() + "");

		sb.append(")");

		return sb.toString();
	}
}
