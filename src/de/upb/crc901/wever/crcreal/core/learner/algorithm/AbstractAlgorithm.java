package de.upb.crc901.wever.crcreal.core.learner.algorithm;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractAlgorithm {

	private final AlgorithmConfig config;
	private final int numberOfStates;
	private final Alphabet alphabet;
	private final TrainingSet trainingData;
	private final int maxTestLength;
	private final IRandomGenerator prg;

	protected AbstractAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		this.config = pAlgorithmConfig;
		this.numberOfStates = pNumberOfStates;
		this.alphabet = pAlphabet;
		this.trainingData = pTrainingData;
		this.maxTestLength = pMaxTestLength;
		this.prg = pPRG;
	}

	public AbstractAlgorithm() {
		this.config = null;
		this.numberOfStates = -1;
		this.alphabet = null;
		this.trainingData = null;
		this.maxTestLength = -1;
		this.prg = null;
	}

	protected int getSizeOfPopulation() {
		return this.config.getSizeOfPopulation();
	}

	protected boolean getSeededPopulation() {
		return this.config.getSeededPopulation();
	}

	protected int getNumberOfStates() {
		return this.numberOfStates;
	}

	protected Alphabet getAlphabet() {
		return this.alphabet;
	}

	protected TrainingSet getTrainingData() {
		return this.trainingData;
	}

	protected int getMaxTestLength() {
		return this.maxTestLength;
	}

	protected IRandomGenerator getPRG() {
		return this.prg;
	}

	public abstract String getID();

	public abstract AbstractAlgorithm newInstance(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG);
}
