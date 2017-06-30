package de.upb.crc901.wever.crcreal.core.learner.objective.test;

import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class MinTestLengthObjective extends AbstractTestObjective {

	public final static String ID = "crc.real.objective.test.testlength";

	@Override
	public double evaluate(final TrainingSet pTrainingData, final List<FiniteAutomaton> candidateModels, final Word candidateTest) {
		return candidateTest.size();
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Test Length";
	}

	@Override
	public AbstractObjective newInstance() {
		return new MinTestLengthObjective();
	}

}
