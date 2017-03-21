package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class AllTestExampleObjective extends AbstractModelObjective {

	public final static String ID = "crc.real.objective.model.alltestexamples";

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public double evaluate(final TrainingSet pTrainingData, final FiniteAutomaton pCandidateModel) {
		final int numberOfConsistentAcceptingSamples = (int) pTrainingData.stream().filter(x -> pCandidateModel.execute(x.getWord()).getLabel() == x.getLabel()).count();
		return (-1) * (double) numberOfConsistentAcceptingSamples / pTrainingData.size();
	}

	@Override
	public String getLabel() {
		return "Total Fitness";
	}

	@Override
	public AbstractObjective newInstance() {
		return new AllTestExampleObjective();
	}

}
