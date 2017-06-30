package de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.ModelObjectiveEfficiencyWrapper;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.SolutionUtil;

public class ModelProblem extends AbstractProblem {

	private final int numberOfStates;
	private final Alphabet alphabet;
	private final List<AbstractModelObjective> objectives;
	private final ModelObjectiveEfficiencyWrapper modelObjectiveWrapper;

	private final TrainingSet trainingData;

	public ModelProblem(final int pNumberOfStates, final Alphabet pAlphabet, final List<AbstractModelObjective> pObjectives, final TrainingSet pTrainingData) {
		super(pNumberOfStates * pAlphabet.size(), pObjectives.size());
		this.numberOfStates = pNumberOfStates;
		this.alphabet = pAlphabet;
		this.objectives = pObjectives;
		this.trainingData = pTrainingData;
		this.modelObjectiveWrapper = new ModelObjectiveEfficiencyWrapper();
	}

	@Override
	public void evaluate(final Solution solution) {
		final CandidateModel model = SolutionUtil.convertSolutionToCandidateModel(solution, this.objectives, this.numberOfStates, this.alphabet, this.trainingData);
		model.getModel().executeSmartLabelingAlgorithm(this.trainingData);

		final Map<String, Double> wrappedObjectiveValues = this.modelObjectiveWrapper.evaluate(this.trainingData, model.getModel());
		this.objectives.stream().forEach(objective -> {
			if (wrappedObjectiveValues.get(objective.getID()) != null) {
				solution.setObjective(this.objectives.indexOf(objective), wrappedObjectiveValues.get(objective.getID()));
			} else {
				solution.setObjective(this.objectives.indexOf(objective), objective.evaluate(this.trainingData, model.getModel()));
			}
		});
	}

	@Override
	public Solution newSolution() {
		final Solution newSolution = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());
		IntStream.range(0, this.getNumberOfVariables()).forEach(x -> newSolution.setVariable(x, EncodingUtils.newInt(0, this.numberOfStates - 1)));
		return newSolution;
	}

}
