package de.upb.crc901.wever.crcreal.core.learner.algorithm.simple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractTestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.alphabet.InputSymbol;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.CandidateTestUtil;
import de.upb.crc901.wever.crcreal.util.Pair;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class SimpleTestAlgorithm extends AbstractTestAlgorithm {

	public static final String ID = "crc.real.algorithm.test.simpleTestAlgorithm";
	private static final double SELECTION_FACTOR = 0.75;

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestAlgorithm.class);

	private final List<CandidateTest> population;

	public SimpleTestAlgorithm() {
		super();
		this.population = null;
	}

	public SimpleTestAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
		this.population = new LinkedList<>();
	}

	@Override
	public void setCommittee(final List<CandidateModel> pCommittee) {
		super.setCommittee(pCommittee);
		this.population.stream().forEach(this::computeFitnessForWord);
	}

	@Override
	public void initPopulation() {
		LOGGER.debug("Initialize test population");
		final List<CandidateTest> newPopulation = new LinkedList<>();
		IntStream.range(0, this.getSizeOfPopulation())
				.mapToObj(x -> CandidateTestUtil.generateSemiUniformRandomCandidateTest(this.getPRG(), this.getObjectives(), this.getAlphabet(), this.getMaxTestLength()))
				.forEach(newPopulation::add);
		this.population.clear();
		this.population.addAll(newPopulation);
		LOGGER.debug("Initialized test population which has now a size of {}", this.population.size());
	}

	@Override
	public void step() {
		LOGGER.trace("Process step in exploration phase algorithm with population of size {}", this.population.size());
		for (int i = 0; i < SELECTION_FACTOR * this.getSizeOfPopulation(); i++) {
			final Pair<AbstractCandidate, AbstractCandidate> tournamentResult = SimpleTournamentSelector.select(this.getPRG(), this.population,
					new ChainedCandidateComparator(this.getObjectives().stream().filter(x -> x.isActive()).collect(Collectors.toList())));
			final CandidateTest mutatedIndividual = this.mutateTest((CandidateTest) tournamentResult.getX());
			this.computeFitnessForWord(mutatedIndividual);

			this.population.remove(tournamentResult.getY());
			this.population.add(mutatedIndividual);
		}
	}

	@Override
	public List<CandidateTest> getResult() {
		LOGGER.trace("Get result of candidate tests of population with size: {}", this.population.size());
		Collections.sort(this.population, new ChainedCandidateComparator(this.getObjectives().stream().filter(x -> x.isActive()).collect(Collectors.toList())));
		final List<CandidateTest> resultList = new LinkedList<>();
		resultList.add(this.population.get(0));
		LOGGER.trace("Return result of candidate tests in exploration phase algorithm: {}", resultList);
		return resultList;
	}

	@Override
	public List<CandidateTest> getPopulation() {
		return new LinkedList<>(this.population);
	}

	private CandidateTest mutateTest(final CandidateTest betterIndividual) {
		int length = betterIndividual.getLength();
		final Word mutatedWord = new Word(betterIndividual.getWholeWord());

		LOGGER.trace("Mutate test with length {}, the candidate test is {}, the whole word is {}", betterIndividual.getLength(), betterIndividual.getTest(),
				betterIndividual.getWholeWord());

		// flip a coin to decide whether to mutate length value or to flip a input symbol
		if (this.getPRG().nextInteger(2) == 0) {
			// mutate length property
			length = this.getPRG().nextInteger(this.getMaxTestLength() + 1);
		} else {
			if (length > 0) {
				// mutate input symbol
				final int indexToMutate = this.getPRG().nextInteger(length);
				InputSymbol mutatedInputSymbol;
				do {
					mutatedInputSymbol = this.getAlphabet().get(this.getPRG().nextInputSymbol(this.getAlphabet().size()));
				} while (mutatedWord.get(indexToMutate) == mutatedInputSymbol);
				mutatedWord.remove(indexToMutate);
				mutatedWord.add(indexToMutate, mutatedInputSymbol);
			}
		}

		final CandidateTest mutatedTest = new CandidateTest(this.getObjectives(), mutatedWord, length);
		this.computeFitnessForWord(mutatedTest);

		return mutatedTest;
	}

	private void computeFitnessForWord(final CandidateTest pCandidateTest) {
		for (final AbstractTestObjective obj : this.getObjectives()) {
			pCandidateTest.setHeuristicObjectiveValue(obj.getID(),
					obj.evaluate(this.getTrainingData(), this.getCommittee().stream().map(x -> x.getModel()).collect(Collectors.toList()), pCandidateTest.getTest()));
		}
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public AbstractAlgorithm newInstance(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		return new SimpleTestAlgorithm(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
	}
}
