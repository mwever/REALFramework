package de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii;

import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import de.upb.crc901.wever.crcreal.core.learner.objective.TestObjectiveProcessor;

public class TestProblem extends AbstractProblem {

	private final int maxTestLength;
	private final int alphabetSize;
	private final TestObjectiveProcessor testObjectiveProcessor;

	public TestProblem(final int pMaxTestLength, final int pAlphabetSize, final TestObjectiveProcessor testObjectiveProcessor) {
		super(pMaxTestLength + 1, testObjectiveProcessor.getNumberOfObjectives());
		this.maxTestLength = pMaxTestLength;
		this.alphabetSize = pAlphabetSize;
		this.testObjectiveProcessor = testObjectiveProcessor;
	}

	@Override
	public void evaluate(final Solution solution) {
		final double[] objectiveValues = this.testObjectiveProcessor.process(solution);
		for (int i = 0; i < objectiveValues.length; i++) {
			solution.setObjective(i, objectiveValues[i]);
		}
	}

	@Override
	public Solution newSolution() {
		final Solution newSolution = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());
		newSolution.setVariable(0, EncodingUtils.newInt(0, this.maxTestLength));
		IntStream.range(1, this.maxTestLength + 1).forEach(x -> newSolution.setVariable(x, EncodingUtils.newInt(0, this.alphabetSize - 1)));
		return newSolution;
	}

}
