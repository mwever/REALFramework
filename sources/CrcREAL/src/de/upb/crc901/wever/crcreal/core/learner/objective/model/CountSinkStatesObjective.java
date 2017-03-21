package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.alphabet.InputSymbol;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class CountSinkStatesObjective extends AbstractModelObjective {

	public static final String ID = "crc.real.objective.model.countsinkstates";

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "Sink States";
	}

	@Override
	public AbstractObjective newInstance() {
		return new CountSinkStatesObjective();
	}

	@Override
	public double evaluate(final TrainingSet pTrainingData, final FiniteAutomaton pCandidateModel) {
		int numberOfSinkStates = 0;

		for (int i = 0; i < pCandidateModel.getNumberOfStates(); i++) {
			boolean allTransitionSelfLoops = true;
			for (final InputSymbol input : pCandidateModel.getAlphabet()) {
				if (pCandidateModel.getTransitionFunction().getNextState(i, input.id()) != i) {
					allTransitionSelfLoops = false;
					break;
				}
			}

			if (allTransitionSelfLoops) {
				numberOfSinkStates++;
			}
		}

		return -1.0 * numberOfSinkStates / pCandidateModel.getNumberOfStates();
	}

}
