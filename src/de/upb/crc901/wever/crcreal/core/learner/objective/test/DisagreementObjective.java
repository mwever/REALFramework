package de.upb.crc901.wever.crcreal.core.learner.objective.test;

import java.util.List;
import java.util.stream.Collectors;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class DisagreementObjective extends AbstractTestObjective {

	public final static String ID = "crc.real.objective.test.disagreement";

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public double evaluate(final TrainingSet pTrainingData, final List<FiniteAutomaton> candidateModels, final Word pWord) {
		if (pTrainingData.containsWord(pWord)) {
			return 1.0;
		}

		double acceptingModels = 0;
		for (final FiniteAutomaton model : candidateModels) {
			if (model.execute(pWord).getLabel() == EWordLabel.ACCEPTING) {
				acceptingModels++;
			}
		}

		final double fraction = acceptingModels / candidateModels.size();
		final double absolute = Math.abs(0.5 - fraction);
		final double fitnessValue = 1 - 2 * absolute;
		return (-1) * fitnessValue;
	}

	public double evaluate(final TrainingSet pTrainingData, final List<CandidateModel> candidateModels, final CandidateTest pCandidateTest) {
		final List<FiniteAutomaton> candidateModelsAsAutomata = candidateModels.stream().map(x -> x.getModel()).collect(Collectors.toList());
		final double evaluationValue = this.evaluate(pTrainingData, candidateModelsAsAutomata, pCandidateTest.getTest());
		pCandidateTest.setHeuristicObjectiveValue(ID, evaluationValue);
		return evaluationValue;
	}

	@Override
	public String getLabel() {
		return "Disagreement";
	}

	@Override
	public AbstractObjective newInstance() {
		return new DisagreementObjective();
	}

}
