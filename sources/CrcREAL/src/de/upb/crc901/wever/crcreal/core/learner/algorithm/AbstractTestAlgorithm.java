package de.upb.crc901.wever.crcreal.core.learner.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractTestAlgorithm extends AbstractAlgorithm {
	private final List<AbstractTestObjective> objectives;
	private List<CandidateModel> committee;

	protected AbstractTestAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
		this.objectives = pAlgorithmConfig.getObjectivesObjects().stream().map(x -> (AbstractTestObjective) x).collect(Collectors.toList());
	}

	public AbstractTestAlgorithm() {
		super();
		this.objectives = null;
	}

	public void setCommittee(final List<CandidateModel> pCommittee) {
		this.committee = pCommittee;
	}

	public List<CandidateModel> getCommittee() {
		return this.committee;
	}

	public List<AbstractTestObjective> getObjectives() {
		return this.objectives;
	}

	public abstract void initPopulation();

	public abstract void step();

	public abstract List<CandidateTest> getResult();

	public abstract List<CandidateTest> getPopulation();
}
