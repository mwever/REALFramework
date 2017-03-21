package de.upb.wever.util.boxplot;

import java.util.Collections;
import java.util.List;

public class BoxPlotUtil {

	public static BoxPlotDatum computeBoxPlotFromList(final List<Double> values) {
		Collections.sort(values);

		final Double avg = values.stream().reduce(0.0, (a, b) -> a + b) / values.size();

		final Double median;
		if (values.size() % 2 == 0) {
			median = (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2;
		} else {
			median = values.get(values.size() / 2);
		}

		final Double lowerQuartile = values.get((int) (values.size() * 0.25));
		final Double upperQuartile = values.get((int) (values.size() * 0.75));

		return new BoxPlotDatum(values.get(0), values.get(values.size() - 1), avg, median, lowerQuartile, upperQuartile);
	}

}
