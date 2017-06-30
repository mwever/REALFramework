package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.upb.crc901.wever.crcreal.model.automaton.ExecutionTrace;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class ModelObjectiveEfficiencyWrapper {

	public Map<String, Double> evaluate(final TrainingSet pTrainingData, final FiniteAutomaton pCandidateModel) {
		int positiveTestExampleCounter = 0;
		int totalPositiveTestExamples = 0;

		int negativeTestExampleCounter = 0;
		int totalNegativeTestExamples = 0;

		final Set<Integer> relevantStates = new HashSet<>();

		for (final TrainingExample t : pTrainingData) {
			switch (t.getLabel()) {
			case ACCEPTING:
				totalPositiveTestExamples++;
			case REJECTING:
				totalNegativeTestExamples++;
			default:
				break;
			}

			final ExecutionTrace trace = pCandidateModel.execute(t.getWord());
			relevantStates.addAll(trace.getStateTrace());

			if (trace.getLabel() == t.getLabel()) {
				switch (trace.getLabel()) {
				case ACCEPTING:
					positiveTestExampleCounter++;
					break;
				case REJECTING:
					negativeTestExampleCounter++;
					break;
				default:
					break;
				}
			}
		}

		final Double allTestExample = ((double) (positiveTestExampleCounter + negativeTestExampleCounter) / pTrainingData.size()) * (-1);
		final Double posTestExample = ((double) positiveTestExampleCounter / totalPositiveTestExamples) * (-1);
		final Double negTestExample = ((double) negativeTestExampleCounter / totalNegativeTestExamples) * (-1);
		final Double relevantPart = (double) relevantStates.size() / pCandidateModel.size();

		final Map<String, Double> resultsMap = new HashMap<>();

		resultsMap.put(AllTestExampleObjective.ID, allTestExample);
		resultsMap.put(PositiveTestExampleObjective.ID, posTestExample);
		resultsMap.put(NegativeTestExampleObjective.ID, negTestExample);
		resultsMap.put(RelevantPartObjective.ID, relevantPart);

		return resultsMap;
	}

	public Set<String> getCoveredObjectives() {
		final Set<String> coveredObjectives = new HashSet<>();

		coveredObjectives.add(AllTestExampleObjective.ID);
		coveredObjectives.add(PositiveTestExampleObjective.ID);
		coveredObjectives.add(NegativeTestExampleObjective.ID);
		coveredObjectives.add(RelevantPartObjective.ID);

		return coveredObjectives;
	}

}
