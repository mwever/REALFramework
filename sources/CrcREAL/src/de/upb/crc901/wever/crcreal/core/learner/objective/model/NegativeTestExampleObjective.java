package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;

public class NegativeTestExampleObjective extends AbstractModelObjective {

	public final static String ID = "crc.real.objective.model.negativetestexample";

	@Override
	public double evaluate(final TrainingSet pTrainingData, final FiniteAutomaton candidateModel) {
		final int numberOfConsistentAcceptingSamples = (int) pTrainingData.stream()
				.filter(x -> x.getLabel() == EWordLabel.REJECTING && candidateModel.execute(x.getWord()).getLabel() == x.getLabel()).count();
		return (-1) * (double) numberOfConsistentAcceptingSamples / pTrainingData.size();
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Negative Fitness";
	}

	@Override
	public AbstractObjective newInstance() {
		return new NegativeTestExampleObjective();
	}

}
