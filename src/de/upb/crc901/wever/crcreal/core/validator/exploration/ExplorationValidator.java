package de.upb.crc901.wever.crcreal.core.validator.exploration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.validator.AbstractValidator;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.alphabet.InputSymbol;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.ExecutionTrace;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.events.LearnerRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.ModelPopulationEvaluationEvent;
import de.upb.crc901.wever.crcreal.model.events.OracleResultEvent;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class ExplorationValidator extends AbstractValidator {
	private final static Logger LOGGER = LoggerFactory.getLogger(ExplorationValidator.class);

	public final static String ID = "crc.real.validator.explorationvalidator";

	private final Lock testDataLock = new ReentrantLock(true);
	private final Lock trainingDataLock = new ReentrantLock(true);

	private TrainingSet testData;

	public ExplorationValidator(final IRandomGenerator pPRG) {
		super(ID, pPRG);
	}

	@Override
	public void rcvLearnerRequestEvent(final LearnerRequestEvent e) {
		super.rcvLearnerRequestEvent(e);
		this.testData = null;
		this.createTestData();
	}

	private void createTestData() {
		this.testDataLock.lock();
		try {
			this.testData = this.getTestData(this.getTargetModel());
		} finally {
			this.testDataLock.unlock();
		}
	}

	private void validate(final CandidateModel candidateDFA) {
		final TrainingSet testData = this.testData;

		for (final AbstractObjective objective : candidateDFA.getObjectives()) {
			final double evaluationResult = ((AbstractModelObjective) objective).evaluate(testData,
					candidateDFA.getModel());
			candidateDFA.setRealObjectiveValue(objective.getID(), evaluationResult);
		}

	}

	@Override
	public void validate(final List<CandidateModel> candidateModels) {
		this.startTimeMeasure();
		candidateModels.parallelStream().forEach(x -> {
			this.validate(x);
		});
		LOGGER.debug("Finished validation process for candidateModelList with testset size {} in {}ms",
				this.testData.size(), this.getTimeMeasure());
	}

	private TrainingSet getTestData(final FiniteAutomaton targetDFA) {
		final Alphabet alphabet = targetDFA.getAlphabet();
		final Integer iterationBound = Math.min(this.getTask().getEvaluationBound(),
				(int) Math.pow(alphabet.size(), targetDFA.size() + alphabet.size() - 1));

		LOGGER.trace("Iteration Bound: " + iterationBound);
		final LinkedList<Word> openQueue = new LinkedList<>();
		openQueue.add(new Word());

		final TrainingSet testData = new TrainingSet();
		int counterForConsideredTraces = 0;

		int maxLengthChecked = 0;
		while (!openQueue.isEmpty() && counterForConsideredTraces < iterationBound) {
			final Word element = openQueue.poll();

			if (!this.getTrainingData().containsWord(element)) {
				final ExecutionTrace targetTrace = targetDFA.execute(element);
				testData.add(new TrainingExample(element, targetTrace.getLabel()));
			}
			for (final InputSymbol symbol : alphabet) {
				final Word newElement = new Word(element);
				newElement.add(symbol);
				openQueue.add(newElement);
				maxLengthChecked = Math.max(newElement.size(), maxLengthChecked);
			}

			counterForConsideredTraces++;
		}
		LOGGER.debug("Test data created and ready to use. Max length for words: {}", maxLengthChecked);
		return testData;
	}

	@Subscribe
	public void rcvOracleResultEvent(final OracleResultEvent e) {
		LOGGER.debug("Received oracle result event");
		final List<TrainingExample> newTrainingData = e.getLabelForRequestedWord().keySet().stream()
				.map(x -> new TrainingExample(x, e.getLabelForRequestedWord().get(x))).collect(Collectors.toList());
		this.testDataLock.lock();
		try {
			this.testData.removeAll(newTrainingData);
		} finally {
			this.testDataLock.unlock();
		}

		this.trainingDataLock.lock();
		try {
			this.getTrainingData().addAll(newTrainingData);
		} finally {
			this.trainingDataLock.unlock();
		}
	}

	@Override
	@Subscribe
	public void rcvCurrentModelPopulationEvaluationEvent(final ModelPopulationEvaluationEvent e) {
		this.validate(e.getModelList().getPopulation());
		this.getEventBus().post(e.getModelList());
	}
}
