package de.upb.crc901.wever.crcreal.core.learner.objective;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.core.Solution;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.util.SolutionUtil;

public class TestObjectiveProcessor {

	private final Alphabet alphabet;
	private final List<AbstractTestObjective> testObjectives;
	private final TrainingSet trainingData;
	private final List<FiniteAutomaton> candidateModels;

	public TestObjectiveProcessor(final Alphabet pAlphabet, final TrainingSet pTrainingData, final List<AbstractTestObjective> pTestObjectives) {
		this.alphabet = pAlphabet;
		this.testObjectives = pTestObjectives;
		this.candidateModels = new LinkedList<>();
		this.trainingData = pTrainingData;
	}

	public double[] process(final Solution solution) {
		final CandidateTest solutionAsWord = SolutionUtil.convertSolutionToCandidateTest(solution, this.alphabet, this.testObjectives);

		final double[] objectiveValues = new double[this.getNumberOfObjectives()];
		for (int i = 0; i < this.testObjectives.size(); i++) {
			objectiveValues[i] = this.testObjectives.get(i).evaluate(this.trainingData, this.candidateModels, solutionAsWord.getTest());
		}

		return objectiveValues;
	}

	public int getNumberOfObjectives() {
		return this.testObjectives.size();
	}

	public void setCommittee(final List<CandidateModel> pCommittee) {
		this.candidateModels.clear();
		this.candidateModels.addAll(pCommittee.stream().map(x -> x.getModel()).collect(Collectors.toList()));
	}

}
