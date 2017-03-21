package de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.InjectedInitialization;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractModelAlgorithm;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.SolutionUtil;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class NSGAIIModelAlgorithm extends AbstractModelAlgorithm {
	private static final Logger LOGGER = LoggerFactory.getLogger(NSGAIIModelAlgorithm.class);

	public static final String ID = "crc.real.algorithm.model.NSGAIIModelAlgorithm";
	protected NSGAII algorithm = null;

	public NSGAIIModelAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
	}

	public NSGAIIModelAlgorithm() {
		super();
	}

	@Override
	public void initPopulation() {
		final ModelProblem modelProblem = new ModelProblem(this.getNumberOfStates(), this.getAlphabet(),
				this.getObjectives().stream().filter(x -> x.isActive()).collect(Collectors.toList()), this.getTrainingData());

		// Setup selection and variation operators
		final TournamentSelection selectionOperator = new TournamentSelection(2, new ChainedComparator(new ParetoDominanceComparator(), new CrowdingComparator()));
		final Variation variationOperator = new GAVariation(new SBX(1.0, 25.0), new PM(1.0 / modelProblem.getNumberOfVariables(), 30.0));

		final Initialization init;
		if (this.getSeededPopulation() && this.algorithm != null) {
			final List<Solution> injectedSolutions = new LinkedList<>();
			for (final Solution sol : this.algorithm.getResult()) {
				injectedSolutions.add(sol);
			}
			init = new InjectedInitialization(modelProblem, this.getSizeOfPopulation(), injectedSolutions);
		} else {
			init = new RandomInitialization(modelProblem, this.getSizeOfPopulation());
		}

		// init MOEA algorithm NSGA-II with no archive
		this.algorithm = new NSGAII(modelProblem, new NondominatedSortingPopulation(), null, selectionOperator, variationOperator, init);
		this.algorithm.step();
	}

	@Override
	public void step() {
		this.algorithm.step();
	}

	@Override
	public List<CandidateModel> getPopulation() {
		final List<CandidateModel> populationList = new LinkedList<>();
		for (final Solution solution : this.algorithm.getPopulation()) {
			populationList.add(SolutionUtil.convertSolutionToCandidateModel(solution, this.getObjectives(), this.getNumberOfStates(), this.getAlphabet(), this.getTrainingData()));
		}
		return populationList;
	}

	@Override
	public List<CandidateModel> getResult() {
		final List<CandidateModel> populationList = new LinkedList<>();
		for (final Solution solution : this.algorithm.getResult()) {
			populationList.add(SolutionUtil.convertSolutionToCandidateModel(solution, this.getObjectives(), this.getNumberOfStates(), this.getAlphabet(), this.getTrainingData()));
		}
		return populationList;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public AbstractAlgorithm newInstance(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		return new NSGAIIModelAlgorithm(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
	}

}
