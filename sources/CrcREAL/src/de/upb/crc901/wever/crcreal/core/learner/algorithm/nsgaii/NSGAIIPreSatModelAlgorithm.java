package de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii;

import java.util.LinkedList;
import java.util.List;

import org.moeaframework.core.Solution;

import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractAlgorithm;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.SolutionUtil;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class NSGAIIPreSatModelAlgorithm extends NSGAIIModelAlgorithm {

	public static final String ID = "crc.real.algorithm.model.NSGAIIPreSatModelAlgorithm";
	private final List<Solution> result;

	public NSGAIIPreSatModelAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
		this.result = new LinkedList<>();
	}

	public NSGAIIPreSatModelAlgorithm() {
		super();
		this.result = null;
	}

	@Override
	public void step() {
		this.algorithm.step();
		if (this.algorithm.getResult().size() > 1) {
			this.result.clear();
			for (final Solution sol : this.algorithm.getResult()) {
				this.result.add(sol);
			}
		}
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
