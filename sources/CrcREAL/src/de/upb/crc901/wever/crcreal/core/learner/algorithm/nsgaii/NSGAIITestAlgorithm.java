package de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii;

import java.util.LinkedList;
import java.util.List;

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

import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractTestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.objective.TestObjectiveProcessor;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.util.SolutionUtil;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class NSGAIITestAlgorithm extends AbstractTestAlgorithm {

	public static final String ID = "crc.real.algorithm.test.NSGAIITestAlgorithm";

	private NSGAII algorithm = null;
	private final TestObjectiveProcessor processor;

	public NSGAIITestAlgorithm() {
		super();
		this.processor = null;
	}

	public NSGAIITestAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
		this.processor = new TestObjectiveProcessor(this.getAlphabet(), this.getTrainingData(), this.getObjectives());
	}

	@Override
	public void setCommittee(final List<CandidateModel> pCommittee) {
		super.setCommittee(pCommittee);
		this.processor.setCommittee(pCommittee);
	}

	@Override
	public void initPopulation() {
		final TestProblem testProblem = new TestProblem(this.getMaxTestLength(), this.getAlphabet().size(), this.processor);

		// Setup selection and variation operators
		final TournamentSelection selectionOperator = new TournamentSelection(2, new ChainedComparator(new ParetoDominanceComparator(), new CrowdingComparator()));
		final Variation variationOperator = new GAVariation(new SBX(1.0, 25.0), new PM(1.0 / testProblem.getNumberOfVariables(), 30.0));

		final Initialization init;
		if (this.getSeededPopulation() && this.algorithm != null) {
			final List<Solution> injectedSolutions = new LinkedList<>();
			for (final Solution sol : this.algorithm.getResult()) {
				final CandidateTest convertedTest = SolutionUtil.convertSolutionToCandidateTest(sol, this.getAlphabet(), this.getObjectives());
				if (!this.getTrainingData().containsWord(convertedTest.getTest())) {
					injectedSolutions.add(sol);
				}
			}
			init = new InjectedInitialization(testProblem, this.getSizeOfPopulation(), injectedSolutions);
		} else {
			init = new RandomInitialization(testProblem, this.getSizeOfPopulation());
		}

		// init MOEA algorithm NSGA-II with no archive
		this.algorithm = new NSGAII(testProblem, new NondominatedSortingPopulation(), null, selectionOperator, variationOperator, init);
		this.algorithm.step();
	}

	public NSGAII getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void step() {
		this.algorithm.step();
	}

	@Override
	public List<CandidateTest> getPopulation() {
		final List<CandidateTest> populationList = new LinkedList<>();
		for (final Solution solution : this.algorithm.getPopulation()) {
			populationList.add(SolutionUtil.convertSolutionToCandidateTest(solution, this.getAlphabet(), this.getObjectives()));
		}
		return populationList;
	}

	@Override
	public List<CandidateTest> getResult() {
		final List<CandidateTest> populationList = new LinkedList<>();
		for (final Solution solution : this.algorithm.getResult()) {
			final CandidateTest convertedTest = SolutionUtil.convertSolutionToCandidateTest(solution, this.getAlphabet(), this.getObjectives());
			if (!this.getTrainingData().containsWord(convertedTest.getTest())) {
				populationList.add(convertedTest);
			}
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
		return new NSGAIITestAlgorithm(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
	}

}
