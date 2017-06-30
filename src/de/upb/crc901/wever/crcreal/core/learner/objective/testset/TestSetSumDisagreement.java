package de.upb.crc901.wever.crcreal.core.learner.objective.testset;

import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestSetObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.DisagreementObjective;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.CandidateTestSet;

public class TestSetSumDisagreement extends AbstractTestSetObjective {

	public final static String ID = "crc.real.objective.testset.sumdisagreement";

	@Override
	public double evaluate(final TrainingSet pTrainingData, final List<CandidateModel> candidateModelList, final List<CandidateTest> candidateTestList,
			final CandidateTestSet setToEvaluate) {
		setToEvaluate.stream().filter(x -> (x.getHeuristicObjectiveValue(DisagreementObjective.ID) == 0))
				.forEach(x -> new DisagreementObjective().evaluate(pTrainingData, candidateModelList, x));

		final double sumDisagreementValue = setToEvaluate.stream().map(x -> x.getHeuristicObjectiveValue(DisagreementObjective.ID)).reduce(0.0, (a, b) -> a + b);

		setToEvaluate.setHeuristicObjectiveValue(ID, sumDisagreementValue);

		return sumDisagreementValue;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Sum Disagreement";
	}

	@Override
	public AbstractObjective newInstance() {
		return new TestSetSumDisagreement();
	}

}
