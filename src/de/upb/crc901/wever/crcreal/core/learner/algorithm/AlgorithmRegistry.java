package de.upb.crc901.wever.crcreal.core.learner.algorithm;

import java.util.HashMap;
import java.util.Map;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class AlgorithmRegistry {

	private static AlgorithmRegistry INSTANCE;

	public static AlgorithmRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AlgorithmRegistry();
		}
		return INSTANCE;
	}

	private final Map<String, AbstractAlgorithm> algorithmCache;

	private AlgorithmRegistry() {
		this.algorithmCache = new HashMap<>();
	}

	public void register(final AbstractAlgorithm pAlgorithmToRegister) {
		this.algorithmCache.put(pAlgorithmToRegister.getID(), pAlgorithmToRegister);
	}

	public AbstractAlgorithm getAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		if (!this.algorithmCache.containsKey(pAlgorithmConfig.getAlgorithmID())) {
			throw new IllegalArgumentException("Unknown identifier for getting algorithm: " + pAlgorithmConfig.getAlgorithmID());
		}
		return this.algorithmCache.get(pAlgorithmConfig.getAlgorithmID()).newInstance(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
	}
}
