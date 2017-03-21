package de.upb.crc901.wever.crcreal.core.learner;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractPassiveLearner extends AbstractActiveLearner {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPassiveLearner.class);

	protected AbstractPassiveLearner(final String pName, final IRandomGenerator pPRG) {
		super(pName, pPRG);
	}

	/** Do not evolve tests since the oracle request is randomly generated. */
	@Override
	protected void evolveTests() {
		LOGGER.debug("Evolve tests for passive learner");
		return;
	}

	@Override
	public void generateCandidateTestsStep(final TrainingSet trainingData, final List<CandidateModel> candidateModels) {
		return;
	}

	@Override
	public List<CandidateTest> getTestResults() {
		final List<CandidateTest> resultList = new LinkedList<>();

		CandidateTest test;
		do {
			final Word randomWord = new Word();
			IntStream.range(0, this.getTask().getMaxTestLength() + 1).forEach(x -> {
				randomWord.add(this.getAlphabet().get(this.getPRG().nextInputSymbol(this.getAlphabet().size())));
			});
			final int length = this.getPRG().nextInteger(this.getTask().getMaxTestLength());
			test = new CandidateTest(new LinkedList<>(), randomWord, length);

		} while (this.getTrainingData().contains(test.getTest()));
		resultList.add(test);
		return resultList;
	}

	@Override
	public List<CandidateTest> getTestPopulation() {
		return new LinkedList<>();
	}
}
