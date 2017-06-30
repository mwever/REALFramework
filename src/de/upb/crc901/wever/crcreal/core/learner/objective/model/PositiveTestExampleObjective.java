package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;

public class PositiveTestExampleObjective extends AbstractModelObjective {

	public final static String ID = "crc.real.objective.model.positivetestexample";

	@Override
	public double evaluate(final TrainingSet pTrainingData, final FiniteAutomaton candidateModel) {
		assert (pTrainingData != null) : "Training data is null";

		if (pTrainingData.allSamplesWithLabel(EWordLabel.REJECTING)) {
			return 1;
		}
		final int numberOfConsistentAcceptingSamples = (int) pTrainingData.stream()
				.filter(x -> x.getLabel() == EWordLabel.ACCEPTING && candidateModel.execute(x.getWord()).getLabel() == x.getLabel()).count();
		return (-1) * (double) numberOfConsistentAcceptingSamples / pTrainingData.size();
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Positive Fitness";
	}

	@Override
	public AbstractObjective newInstance() {
		return new PositiveTestExampleObjective();
	}

}
