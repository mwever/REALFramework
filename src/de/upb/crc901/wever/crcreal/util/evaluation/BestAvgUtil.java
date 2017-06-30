package de.upb.crc901.wever.crcreal.util.evaluation;

import java.util.Collections;
import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.util.bestavg.BestAvgDatum;

public class BestAvgUtil {

	public static BestAvgDatum computeBestAvgFromList(final AbstractObjective objective, final List<Double> realEvaluationValueList) {
		Collections.sort(realEvaluationValueList);
		final double avg = realEvaluationValueList.stream().mapToDouble(x -> x).sum() / realEvaluationValueList.size();
		return new BestAvgDatum(objective.getID(), realEvaluationValueList.get(0), avg);
	}

}
