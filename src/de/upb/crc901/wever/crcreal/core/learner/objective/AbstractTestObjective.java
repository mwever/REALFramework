package de.upb.crc901.wever.crcreal.core.learner.objective;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.Word;

public abstract class AbstractTestObjective extends AbstractObjective {

	public abstract double evaluate(final TrainingSet pTrainingData, List<FiniteAutomaton> candidateModels, Word candidateTest);

	@Override
	public boolean hasRealData() {
		return false;
	}

}
