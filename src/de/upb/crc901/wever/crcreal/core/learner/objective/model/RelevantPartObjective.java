package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import java.util.HashSet;
import java.util.Set;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class RelevantPartObjective extends AbstractModelObjective {

	public static final String ID = "crc.real.objective.model.relevantpart";

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Relevant DFA Part";
	}

	@Override
	public AbstractObjective newInstance() {
		return new RelevantPartObjective();
	}

	@Override
	public double evaluate(final TrainingSet pTrainingData, final FiniteAutomaton pCandidateModel) {
		final Set<Integer> usedStates = new HashSet<>();

		for (final TrainingExample example : pTrainingData) {
			usedStates.addAll(pCandidateModel.execute(example.getWord()).getStateTrace());
		}

		final double relevantPartRatio = (double) usedStates.size() / pCandidateModel.getNumberOfStates();
		return relevantPartRatio;
	}

}
