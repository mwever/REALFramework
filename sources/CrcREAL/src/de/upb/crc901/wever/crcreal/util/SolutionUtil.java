package de.upb.crc901.wever.crcreal.util;

import java.util.List;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.automaton.TransitionFunction;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class SolutionUtil {

	public static FiniteAutomaton readDFAFromSolution(final Solution pSolution, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet trainingData) {
		final int[][] matrixArray = new int[pNumberOfStates][pSolution.getNumberOfVariables() / pNumberOfStates];

		IntStream.range(0, pSolution.getNumberOfVariables()).forEach(x -> {
			matrixArray[x / pAlphabet.size()][x % pAlphabet.size()] = EncodingUtils.getInt(pSolution.getVariable(x));
		});

		final TransitionFunction delta = TransitionFunction.readFromArray(matrixArray);
		final FiniteAutomaton automaton = new FiniteAutomaton(pNumberOfStates, delta, pAlphabet);
		automaton.executeSmartLabelingAlgorithm(trainingData);
		return automaton;
	}

	public static CandidateModel convertSolutionToCandidateModel(final Solution sol, final List<AbstractModelObjective> objectives, final int numberOfStates,
			final Alphabet alphabet, final TrainingSet trainingData) {
		final CandidateModel convertedCandidateModel = new CandidateModel(objectives, readDFAFromSolution(sol, numberOfStates, alphabet, trainingData));
		for (int i = 0; i < sol.getObjectives().length; i++) {
			convertedCandidateModel.setHeuristicObjectiveValue(objectives.get(i).getID(), sol.getObjective(i));
		}
		computePassiveHeuristicObjectives(convertedCandidateModel, trainingData, objectives);
		return convertedCandidateModel;
	}

	public static void computePassiveHeuristicObjectives(final CandidateModel solutionModel, final TrainingSet trainingData, final List<AbstractModelObjective> objectives) {
		objectives.stream().filter(x -> !x.isActive()).forEach(x -> {
			solutionModel.setHeuristicObjectiveValue(x.getID(), x.evaluate(trainingData, solutionModel.getModel()));
		});
	}

	public static void computePassiveHeuristicObjectives(final List<CandidateModel> candidateModelList, final TrainingSet trainingData,
			final List<AbstractModelObjective> objectives) {
		candidateModelList.stream().forEach(solutionModel -> {
			objectives.stream().filter(x -> !x.isActive()).forEach(x -> {
				solutionModel.setHeuristicObjectiveValue(x.getID(), x.evaluate(trainingData, solutionModel.getModel()));
			});
		});
	}

	public static CandidateTest convertSolutionToCandidateTest(final Solution sol, final Alphabet alphabet, final List<AbstractTestObjective> objectives) {
		final int length = EncodingUtils.getInt(sol.getVariable(0));
		final Word containedWord = new Word();
		IntStream.range(1, sol.getNumberOfVariables()).forEach(x -> {
			containedWord.add(alphabet.get(EncodingUtils.getInt(sol.getVariable(x))));
		});

		final CandidateTest test = new CandidateTest(objectives, containedWord, length);
		for (int i = 0; i < sol.getObjectives().length; i++) {
			test.setHeuristicObjectiveValue(i, sol.getObjective(i));
		}
		return test;
	}
}
