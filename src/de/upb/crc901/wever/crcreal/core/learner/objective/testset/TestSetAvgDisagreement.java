package de.upb.crc901.wever.crcreal.core.learner.objective.testset;

import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestSetObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.DisagreementObjective;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.CandidateTestSet;

public class TestSetAvgDisagreement extends AbstractTestSetObjective {

	public final static String ID = "crc.real.objective.testset.avgdisagreement";

	@Override
	public double evaluate(final TrainingSet pTrainingData, final List<CandidateModel> candidateModelList, final List<CandidateTest> candidateTestList,
			final CandidateTestSet setToEvaluate) {
		setToEvaluate.stream().filter(x -> (x.getHeuristicObjectiveValue(DisagreementObjective.ID) == 0))
				.forEach(x -> new DisagreementObjective().evaluate(pTrainingData, candidateModelList, x));

		final double averageDisagreementValue = setToEvaluate.stream().map(x -> x.getHeuristicObjectiveValue(DisagreementObjective.ID)).reduce(0.0, (a, b) -> a + b)
				/ setToEvaluate.getTestSet().size();

		setToEvaluate.setHeuristicObjectiveValue(ID, averageDisagreementValue);

		return averageDisagreementValue;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Avg Disagreement";
	}

	@Override
	public AbstractObjective newInstance() {
		return new TestSetAvgDisagreement();
	}

}
