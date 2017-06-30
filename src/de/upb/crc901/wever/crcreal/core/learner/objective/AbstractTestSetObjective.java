package de.upb.crc901.wever.crcreal.core.learner.objective;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.CandidateTestSet;

public abstract class AbstractTestSetObjective extends AbstractObjective {

	public abstract double evaluate(TrainingSet pTrainingData, List<CandidateModel> candidateModels, List<CandidateTest> candidateTests, CandidateTestSet setToEvaluate);

	@Override
	public boolean hasRealData() {
		return false;
	}
}
