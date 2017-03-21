package de.upb.crc901.wever.crcreal.core.learner;

import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AlgorithmRegistry;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.config.LearnerConfig;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class GeneralPassiveLearner extends AbstractPassiveLearner {

	private final LearnerConfig config;

	public GeneralPassiveLearner(final LearnerConfig pConfig, final IRandomGenerator pPRG) {
		super(pConfig.getLearnerID(), pPRG);
		this.config = pConfig;
	}

	@Override
	public void beforeFirstStep() {
		this.getModelAlgorithms().clear();
		for (final AlgorithmConfig modelAlgoConfig : this.config.getModelAlgorithmConfigs()) {
			this.getModelAlgorithms().add((AbstractModelAlgorithm) AlgorithmRegistry.getInstance().getAlgorithm(modelAlgoConfig, this.getNumberOfStates(), this.getAlphabet(),
					this.getTrainingData(), this.getTask().getMaxTestLength(), this.getPRG()));
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(this.getIdentifier());
		sb.append("<passive>");
		sb.append("(");
		sb.append(this.getModelAlgorithms());
		sb.append(")");

		sb.append("(");
		sb.append(this.getTestAlgorithms());
		sb.append(")");

		return sb.toString();
	}
}
