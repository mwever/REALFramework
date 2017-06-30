package de.upb.crc901.wever.crcreal.core.learner.objective.testset;

import java.util.LinkedList;
import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestSetObjective;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.CandidateTestSet;

public class TestSetTransitionMatrixCoverage extends AbstractTestSetObjective {

	public final static String ID = "crc.real.objective.testset.matrixcover";

	@Override
	public double evaluate(final TrainingSet pTrainingData, final List<CandidateModel> candidateModelList, final List<CandidateTest> candidateTestList,
			final CandidateTestSet setToEvaluate) {

		final List<Double> coverageValues = new LinkedList<>();
		candidateModelList.stream().map(x -> this.evaluateSingleCandidate(x, setToEvaluate)).forEach(coverageValues::add);

		return 0.0;
	}

	private double evaluateSingleCandidate(final CandidateModel pCandidate, final CandidateTestSet setToEvaluate) {

		return 0.0;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Matrix Coverage";
	}

	@Override
	public AbstractObjective newInstance() {
		return new TestSetAvgDisagreement();
	}

}
