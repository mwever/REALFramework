package de.upb.crc901.wever.crcreal.model.config;

import java.util.List;

public class LearnerConfig {

	private final String learnerID;
	private final boolean active;

	private final List<AlgorithmConfig> modelAlgorithmConfigList;
	private final List<AlgorithmConfig> testAlgorithmConfigList;

	public LearnerConfig(final String pLearnerID, final boolean pActive, final List<AlgorithmConfig> pModelAlgorithmConfigList,
			final List<AlgorithmConfig> pTestAlgorithmConfigList) {
		this.learnerID = pLearnerID;
		this.active = pActive;
		this.modelAlgorithmConfigList = pModelAlgorithmConfigList;
		this.testAlgorithmConfigList = pTestAlgorithmConfigList;
	}

	public LearnerConfig(final String pLearnerID, final List<AlgorithmConfig> pModelAlgorithmConfigList) {
		this.learnerID = pLearnerID;
		this.modelAlgorithmConfigList = pModelAlgorithmConfigList;
		this.testAlgorithmConfigList = null;
		this.active = false;
	}

	public String getLearnerID() {
		return this.learnerID;
	}

	public boolean isActive() {
		return this.active;
	}

	public List<AlgorithmConfig> getModelAlgorithmConfigs() {
		return this.modelAlgorithmConfigList;
	}

	public List<AlgorithmConfig> getTestAlgorithmConfigs() {
		return this.testAlgorithmConfigList;
	}
}
