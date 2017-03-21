package de.upb.crc901.wever.crcreal.core.oracle.phonest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.oracle.AbstractOracle;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.events.AnnounceTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.FinishedTaskProcessingEvent;
import de.upb.crc901.wever.crcreal.model.events.GenerateTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.LearnerRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.LearnerResultEvent;
import de.upb.crc901.wever.crcreal.model.events.OracleRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.OracleResultEvent;
import de.upb.crc901.wever.crcreal.model.events.StartTaskProcessingEvent;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.AlphabetUtil;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

// TODO documentation
public class PHonestOracle extends AbstractOracle {

	private static final Logger LOGGER = LoggerFactory.getLogger(PHonestOracle.class);

	public static final String ID = "crc.real.oracle.phonest";

	private final double honesty;
	private FiniteAutomaton targetModel;

	/**
	 * C'tor for instantiating an oracle with a certain honesty proportion.
	 *
	 * @param pPRG
	 *            Random generator, might be some seeded pseudo random generator for reproducability
	 * @param pHonesty
	 *            Takes values between 0 and 1 and denotes the proportion of honest answers on average.
	 */
	public PHonestOracle(final String pName, final IRandomGenerator pPRG, final double pHonesty) {
		super(pName, pPRG);
		if (pHonesty < 0 || pHonesty > 1) {
			throw new IllegalArgumentException("Honesty parameter must be between 0 and 1");
		}
		this.honesty = pHonesty;
	}

	@Override
	public void rcvStartTaskProcessingEvent(final StartTaskProcessingEvent e) {
		LOGGER.trace("Received StartTaskProcessingEvent, Post GenerateTargetModelEvent");
		this.getEventBus().post(new GenerateTargetModelEvent(this.getTask().getNumberOfStates(), AlphabetUtil.getAlphabetOfSize(this.getTask().getSizeOfAlphabet()),
				this.getTask().getSizeOfTrainingSet()));
	}

	@Override
	public void rcvAnnounceTargetModelEvent(final AnnounceTargetModelEvent e) {
		LOGGER.trace("Received AnnounceTargetModelEvent");
		this.targetModel = e.getTargetModel();

		final TrainingSet trainingData = this.drawSampleSequences(this.targetModel, this.getTask().getSizeOfTrainingSet(), this.getTask().getSizeOfAlphabet());
		LOGGER.debug("Produced training data. Send initial learning event.");
		this.getEventBus().post(new LearnerRequestEvent(this.getTask().getNumberOfStates(), trainingData));
	}

	@Override
	public void rcvOracleRequestEvent(final OracleRequestEvent e) {
		super.rcvOracleRequestEvent(e);
		LOGGER.debug("Received oracle request event {}", e);
		final Map<Word, EWordLabel> labeledWords = new HashMap<>();
		for (final Word word : e.getRequestedWord()) {
			labeledWords.put(word, this.getHonestyLabelForWord(this.targetModel.execute(word).getLabel()));
		}
		this.getEventBus().post(new OracleResultEvent(e, labeledWords));
	}

	@Override
	public void rcvLearnerResultEvent(final LearnerResultEvent e) {
		this.getEventBus().post(new FinishedTaskProcessingEvent());
	}

	private EWordLabel getHonestyLabelForWord(final EWordLabel label) {
		final boolean tellTruth = this.getPRG().nextHonestyDecision(this.honesty);
		if (!tellTruth) {
			if (label == EWordLabel.ACCEPTING) {
				return EWordLabel.REJECTING;
			} else {
				return EWordLabel.ACCEPTING;
			}
		}
		return label;
	}

	/**
	 * This method draws a list of sample sequences to learn from.
	 *
	 * @param targetModel
	 *            The target model for which the input sequences shall be drawn for.
	 * @param alphabet
	 *            The alphabet that is used for inputs of the target model.
	 * @param numberOfSequences
	 *            The amount of sequences that shall be drawn.
	 * @return Returns a list of labeled InputSequences.
	 */
	private TrainingSet drawSampleSequences(final FiniteAutomaton targetModel, final int numberOfSequences, final int alphabetSize) {
		final Set<List<Integer>> sampleSet = new HashSet<>();

		IntStream.range(0, numberOfSequences).forEach(x -> {
			final List<Integer> drawnInputSequence = new LinkedList<>();
			do {
				final int lengthOfInputSequence = this.getPRG().nextInputSequenceLength(this.getTask().getMaxTestLength(), alphabetSize);
				drawnInputSequence.clear();
				IntStream.range(0, lengthOfInputSequence).forEach(y -> {
					drawnInputSequence.add(this.getPRG().nextInputSymbol(targetModel.getAlphabet().size()));
				});

			} while (!sampleSet.add(drawnInputSequence));
		});

		return new TrainingSet(sampleSet.stream().map(x -> {
			final Word inputList = new Word(x.stream().map(y -> targetModel.getAlphabet().get(y)).collect(Collectors.toList()));
			return new TrainingExample(inputList, targetModel.execute(inputList).getLabel());
		}).collect(Collectors.toList()));
	}

	public double getHonesty() {
		return this.honesty;
	}

}
