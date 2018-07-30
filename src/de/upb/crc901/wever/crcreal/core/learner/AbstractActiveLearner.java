package de.upb.crc901.wever.crcreal.core.learner;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.AbstractREALEntity;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractTestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.simple.ChainedCandidateComparator;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.events.LearnerRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.LearnerResultEvent;
import de.upb.crc901.wever.crcreal.model.events.ModelPopulationEvaluationEvent;
import de.upb.crc901.wever.crcreal.model.events.ModelPopulationEvent;
import de.upb.crc901.wever.crcreal.model.events.OracleRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.OracleResultEvent;
import de.upb.crc901.wever.crcreal.model.events.TestPopulationEvent;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.chunk.EEvaluationCycle;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractActiveLearner extends AbstractREALEntity {
	enum FAKTQQueryContent {
		SINGLEBEST, PARETO, ALL;
	}
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractActiveLearner.class);

	private final List<AbstractModelAlgorithm> modelAlgorithms = new LinkedList<>();
	private final List<AbstractTestAlgorithm> testAlgorithms = new LinkedList<>();

	protected int generationCounter;
	protected int roundCounter;

	private TrainingSet trainingData;
	private Alphabet alphabet;
	private int numberOfStates;
	
	private boolean faktQ = false;
	private FAKTQQueryContent faktQQueryContent = FAKTQQueryContent.SINGLEBEST;
	
	protected AbstractActiveLearner(final String pName, final IRandomGenerator pPRG) {
		super(pName, pPRG);
	}

	protected void run() {
		LOGGER.debug("Execute Run for {} in round {}/{}", this.getIdentifier(), this.roundCounter, this.getTask().getNumberOfRounds());
		this.beforeEachRound();

		this.startTimeMeasure();
		this.evolveModels();
		LOGGER.debug("{} Round {}/{} evolved models in {}ms.", this.getIdentifier(), this.roundCounter, this.getTask().getNumberOfRounds(), this.getTimeMeasure());

		if (this.roundCounter >= this.getTask().getNumberOfRounds()) {
			LOGGER.debug("Send Learner Result Event");
			this.sendLearnerResultEvent();
			return;
		}
		this.roundCounter++;

		this.beforeEvolvingTests();

		this.startTimeMeasure();
		this.evolveTests();
		LOGGER.debug("{} Round {} evolved tests in {}ms.", this.getIdentifier(), this.roundCounter, this.getTimeMeasure());

		if(faktQ) {
			switch (faktQQueryContent) {
			case SINGLEBEST:
				List<CandidateTest> testResults =this.getTestResults();
				Collections.sort(testResults, new ChainedCandidateComparator(testResults.get(0).getObjectives()));
				this.sendFaktQQuery(testResults);
				break;
			case PARETO:
				this.sendFaktQQuery(this.getTestResults());
				break;
			case ALL:
				this.sendFaktQQuery(this.getTestPopulation());
				break;
			}
		} else {
			LOGGER.debug("Send Oracle Request for generated tests");
			this.sendOracleQuery(this.getTestResults());
		}
	}

	protected void evolveModels() {
		LOGGER.trace("Evolve Models");
		this.modelAlgorithms.stream().forEach(x -> x.initPopulation());

		IntStream.range(0, this.getTask().getNumberOfGenerations()).forEach(generation -> {
			this.generationCounter = generation + 1;
			if (this.getTask().getEvaluationCycle() == EEvaluationCycle.GENERATION) {
				this.sendCurrentPopulationEvent("model", generation);
			}
			this.generateCandidateModelsStep();
		});

		if (this.getTask().getEvaluationCycle() == EEvaluationCycle.GENERATION || this.getTask().getEvaluationCycle() == EEvaluationCycle.ROUND) {
			this.sendCurrentPopulationEvent("model", this.getTask().getNumberOfGenerations());
		}
		LOGGER.trace("Finished model evolution");
	}

	protected void evolveTests() {
		LOGGER.trace("Evolve candidate tests");
		this.testAlgorithms.stream().forEach(x -> x.setCommittee(this.getModelResults()));
		this.testAlgorithms.stream().forEach(x -> x.initPopulation());

		IntStream.range(0, this.getTask().getNumberOfGenerations()).forEach(generation -> {
			this.generationCounter = generation + 1;
			if (this.getTask().getEvaluationCycle() == EEvaluationCycle.GENERATION) {
				this.sendCurrentPopulationEvent("test", generation);
			}
			this.generateCandidateTestsStep(this.trainingData, this.getModelResults());
		});

		if (this.getTask().getEvaluationCycle() == EEvaluationCycle.GENERATION || this.getTask().getEvaluationCycle() == EEvaluationCycle.ROUND) {
			this.sendCurrentPopulationEvent("test", this.getTask().getNumberOfGenerations());
		}
		LOGGER.trace("Finished test evolution");
	}

	public List<CandidateModel> getModelPopulation() {
		final List<CandidateModel> resultList = new LinkedList<>();
		this.modelAlgorithms.stream().map(x -> x.getPopulation()).forEach(resultList::addAll);
		return resultList;
	}

	public List<CandidateModel> getModelResults() {
		final List<CandidateModel> resultList = new LinkedList<>();
		this.modelAlgorithms.stream().map(x -> x.getResult()).forEach(resultList::addAll);
		return resultList;
	}

	public List<CandidateTest> getTestPopulation() {
		final List<CandidateTest> resultList = new LinkedList<>();
		this.testAlgorithms.stream().map(x -> x.getPopulation()).forEach(resultList::addAll);
		return resultList;
	}

	public void generateCandidateTestsStep(final TrainingSet trainingData, final List<CandidateModel> candidateModels) {
		LOGGER.debug("Round #{}: Exploration Generation {}", this.getRoundCounter(), this.getGenerationCounter());
		this.testAlgorithms.stream().forEach(x -> x.step());
	}

	public void generateCandidateModelsStep() {
		LOGGER.debug("Round #{}: Estimation Generation {}", this.getRoundCounter(), this.getGenerationCounter());
		this.modelAlgorithms.stream().forEach(x -> x.step());
	}

	public List<CandidateTest> getTestResults() {
		final List<CandidateTest> resultList = new LinkedList<>();
		this.testAlgorithms.stream().map(x -> x.getResult()).forEach(resultList::addAll);

		if (resultList.size() > 1) {
			LOGGER.warn("Result list of candidate tests is greater than 1.");
		}
		return resultList;
	}

	/* XXX BEGIN Receive Events */
	@Subscribe
	public void rcvInitialLearningEvent(final LearnerRequestEvent e) {
		LOGGER.debug("Received Initial Learning Event");
		this.trainingData = new TrainingSet(e.getTrainingData());
		this.alphabet = Alphabet.readFromTrainingSet(this.trainingData);
		this.numberOfStates = e.getNumberOfStates();
		this.roundCounter = 0;

		this.beforeFirstStep();
		this.run();
	}

	@Subscribe
	public final void rcvOracleResultEvent(final OracleResultEvent e) {
		LOGGER.debug("Received oracle result event.");
		for (final Word queriedWord : e.getLabelForRequestedWord().keySet()) {
			this.trainingData.add(new TrainingExample(queriedWord, e.getLabelForRequestedWord().get(queriedWord)));
		}
		this.run();
	}
	/* END Receive Events */

	/* XXX BEGIN Send Events */
	private void sendLearnerResultEvent() {
		LOGGER.debug("Send Learner Result Event");
		if (this.getTask().getEvaluationCycle() == EEvaluationCycle.LAST) {
			this.sendCurrentPopulationEvent("test", this.getTask().getNumberOfGenerations());
			this.sendCurrentPopulationEvent("model", this.getTask().getNumberOfGenerations());
		}
		this.getEventBus().post(new LearnerResultEvent(this.getModelResults()));
	}
	
	protected void sendFaktQQuery(final List<CandidateTest> testsForOracleRequest) {
		LOGGER.debug("Send Event for FaktQ Oracle Request");
		final List<CandidateTest> testsForOracleRequestCopy = new LinkedList<>(testsForOracleRequest);
		Collections.sort(testsForOracleRequestCopy, new ChainedCandidateComparator(testsForOracleRequestCopy.get(0).getObjectives()));

		final List<Word> wordsToSendToOracle = new LinkedList<>();
		if((this.faktQAllTest && !this.faktQParetoSet) || this.faktQParetoSet) {
			wordsToSendToOracle.addAll(testsForOracleRequestCopy.stream().map(x -> x.getTest()).collect(Collectors.toList()));
		}
		wordsToSendToOracle.add(testsForOracleRequestCopy.get(0).getTest());

		this.getEventBus().post(new OracleRequestEvent(wordsToSendToOracle));
	}

	protected void sendOracleQuery(final List<CandidateTest> testsForOracleRequest) {
		LOGGER.debug("Send Event for Oracle Request");
		final List<CandidateTest> testsForOracleRequestCopy = new LinkedList<>(testsForOracleRequest);
		Collections.sort(testsForOracleRequestCopy, new ChainedCandidateComparator(testsForOracleRequestCopy.get(0).getObjectives()));

		final List<Word> wordsToSendToOracle = new LinkedList<>();
		wordsToSendToOracle.add(testsForOracleRequestCopy.get(0).getTest());

		this.getEventBus().post(new OracleRequestEvent(wordsToSendToOracle));
	}

	private void sendCurrentPopulationEvent(final String pPopulationID, final int generation) {
		switch (this.getTask().getEvaluationType()) {
		case ALL:
			if (pPopulationID.equals("test")) {
				this.getEventBus().post(new TestPopulationEvent(this.roundCounter, generation, this.getTestPopulation()));
			} else {
				this.getEventBus().post(new ModelPopulationEvaluationEvent(new ModelPopulationEvent(this.roundCounter, generation, this.getModelPopulation())));
			}
			break;
		case ELITE:
			if (pPopulationID.equals("test")) {
				this.getEventBus().post(new TestPopulationEvent(this.roundCounter, generation, this.getTestResults()));
			} else {
				this.getEventBus().post(new ModelPopulationEvaluationEvent(new ModelPopulationEvent(this.roundCounter, generation, this.getModelResults())));
			}
			break;
		}
		LOGGER.trace("Posted test population event");
	}
	/* END Send Events */

	/* XXX BEGIN getters */
	protected TrainingSet getTrainingData() {
		return this.trainingData;
	}

	protected int getRoundCounter() {
		return this.roundCounter;
	}

	protected int getGenerationCounter() {
		return this.generationCounter;
	}

	protected Alphabet getAlphabet() {
		return this.alphabet;
	}

	protected int getNumberOfStates() {
		return this.numberOfStates;
	}

	protected List<AbstractModelAlgorithm> getModelAlgorithms() {
		return this.modelAlgorithms;
	}

	protected List<AbstractTestAlgorithm> getTestAlgorithms() {
		return this.testAlgorithms;
	}

	/* END getters */

	/* XXX BEGIN hooks for implementing classes */
	/**
	 * Hook for implementing actions that should be performed before making the first step in inference.
	 */
	protected void beforeFirstStep() {

	}

	public void beforeEachRound() {

	}

	public void beforeEvolvingTests() {

	}

	/* END hooks */

	/* XXX BEGIN abstract methods for implementing classes */

	/* END abstract methods */
}
