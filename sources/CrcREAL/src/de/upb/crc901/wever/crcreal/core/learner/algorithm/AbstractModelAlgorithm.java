package de.upb.crc901.wever.crcreal.core.learner.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractModelAlgorithm extends AbstractAlgorithm {

	private final List<AbstractModelObjective> objectives;

	protected AbstractModelAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
		this.objectives = pAlgorithmConfig.getObjectivesObjects().stream().map(x -> (AbstractModelObjective) x).collect(Collectors.toList());

	}

	protected AbstractModelAlgorithm() {
		super();
		this.objectives = null;
	}

	public List<AbstractModelObjective> getObjectives() {
		return this.objectives;
	}

	public abstract void initPopulation();

	public abstract void step();

	public abstract List<CandidateModel> getResult();

	public abstract List<CandidateModel> getPopulation();

}
