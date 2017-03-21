package de.upb.crc901.wever.crcreal.model.config;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.upb.crc901.wever.crcreal.core.REALManager;
import de.upb.crc901.wever.crcreal.core.learner.GeneralActiveLearner;
import de.upb.crc901.wever.crcreal.core.learner.GeneralPassiveLearner;
import de.upb.crc901.wever.crcreal.core.learner.LearnerRegistry;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii.NSGAIIModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii.NSGAIIPreSatModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii.NSGAIITestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.simple.SimpleModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.simple.SimpleTestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.AllTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.CountSinkStatesObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.ModelSizeObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.NegativeTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.PositiveTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.RelevantPartObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.DisagreementObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.MinTestLengthObjective;
import de.upb.crc901.wever.crcreal.util.ListUtil;

public class LearnerConfigLoader {

	private static final String DEF_CONFIG_FILE = "config/algorithmConfig.json";

	private final List<LearnerConfig> algorithmConfigList;

	public LearnerConfigLoader(final String pConfigFile) {
		this.algorithmConfigList = new LinkedList<>();
		this.loadEEA();
		this.loadPEEA();
		this.loadPMOO();
		this.loadEEPMOO();
		this.loadEEMOO();
	}

	public LearnerConfigLoader() {
		this(DEF_CONFIG_FILE);
	}

	private void loadPEEA() {
		final List<String> modelObjectives = ListUtil.commaStringToList(AllTestExampleObjective.ID + "," + RelevantPartObjective.ID);
		final List<String> passiveModelObjectives = new LinkedList<>();
		final List<AlgorithmConfig> modelAlgorithmConfigs = IntStream.range(0, 2)
				.mapToObj(x -> new AlgorithmConfig(SimpleModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 50)).collect(Collectors.toList());

		final LearnerConfig config = new LearnerConfig("crc.real.learner.peea", modelAlgorithmConfigs);
		this.algorithmConfigList.add(config);
	}

	private void loadEEA() {

		final List<String> modelObjectives = ListUtil.commaStringToList(AllTestExampleObjective.ID + "," + RelevantPartObjective.ID);
		final List<String> passiveModelObjectives = new LinkedList<>();
		final List<AlgorithmConfig> modelAlgorithmConfigs = IntStream.range(0, 2)
				.mapToObj(x -> new AlgorithmConfig(SimpleModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 50)).collect(Collectors.toList());

		final List<String> testObjectives = ListUtil.commaStringToList(DisagreementObjective.ID);
		final List<String> passiveTestObjectives = new LinkedList<>();

		final List<AlgorithmConfig> testAlgorithmConfigs = IntStream.range(0, 1)
				.mapToObj(x -> new AlgorithmConfig(SimpleTestAlgorithm.ID, testObjectives, passiveTestObjectives, false, 100)).collect(Collectors.toList());

		final LearnerConfig config = new LearnerConfig("crc.real.learner.eea", true, modelAlgorithmConfigs, testAlgorithmConfigs);
		this.algorithmConfigList.add(config);
	}

	private void loadPMOO() {
		final List<String> modelObjectives = ListUtil.commaStringToList(PositiveTestExampleObjective.ID + "," + NegativeTestExampleObjective.ID + "," + RelevantPartObjective.ID
				+ "," + CountSinkStatesObjective.ID + "," + AllTestExampleObjective.ID);
		final List<String> passiveModelObjectives = ListUtil.commaStringToList(AllTestExampleObjective.ID);
		final List<AlgorithmConfig> modelAlgorithmConfigs = IntStream.range(0, 1)
				.mapToObj(x -> new AlgorithmConfig(NSGAIIModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 100)).collect(Collectors.toList());

		final LearnerConfig config = new LearnerConfig("crc.real.learner.pmoo", modelAlgorithmConfigs);
		this.algorithmConfigList.add(config);
	}

	private void loadEEPMOO() {
		final List<String> modelObjectives = ListUtil.commaStringToList(PositiveTestExampleObjective.ID + "," + NegativeTestExampleObjective.ID + "," + RelevantPartObjective.ID
				+ "," + CountSinkStatesObjective.ID + "," + AllTestExampleObjective.ID);
		final List<String> passiveModelObjectives = ListUtil.commaStringToList(AllTestExampleObjective.ID);
		final List<AlgorithmConfig> modelAlgorithmConfigs = IntStream.range(0, 1)
				.mapToObj(x -> new AlgorithmConfig(NSGAIIModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 50)).collect(Collectors.toList());
		modelAlgorithmConfigs.add(new AlgorithmConfig(NSGAIIPreSatModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 50));

		final List<String> testObjectives = ListUtil.commaStringToList(DisagreementObjective.ID);
		final List<String> passiveTestObjectives = new LinkedList<>();
		final List<AlgorithmConfig> testAlgorithmConfigs = IntStream.range(0, 1)
				.mapToObj(x -> new AlgorithmConfig(NSGAIITestAlgorithm.ID, testObjectives, passiveTestObjectives, false, 100)).collect(Collectors.toList());

		final LearnerConfig config = new LearnerConfig("crc.real.learner.eepmoo", true, modelAlgorithmConfigs, testAlgorithmConfigs);
		this.algorithmConfigList.add(config);
	}

	private void loadEEMOO() {
		final List<String> modelObjectives = ListUtil.commaStringToList(AllTestExampleObjective.ID + "," + ModelSizeObjective.ID);
		final List<String> passiveModelObjectives = new LinkedList<>();
		final List<AlgorithmConfig> modelAlgorithmConfigs = IntStream.range(0, 1)
				.mapToObj(x -> new AlgorithmConfig(NSGAIIModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 50)).collect(Collectors.toList());
		modelAlgorithmConfigs.add(new AlgorithmConfig(NSGAIIPreSatModelAlgorithm.ID, modelObjectives, passiveModelObjectives, true, 50));

		final List<String> testObjectives = ListUtil.commaStringToList(DisagreementObjective.ID + "," + MinTestLengthObjective.ID);
		final List<String> passiveTestObjectives = new LinkedList<>();
		final List<AlgorithmConfig> testAlgorithmConfigs = IntStream.range(0, 1)
				.mapToObj(x -> new AlgorithmConfig(NSGAIITestAlgorithm.ID, testObjectives, passiveTestObjectives, false, 100)).collect(Collectors.toList());

		final LearnerConfig config = new LearnerConfig("crc.real.learner.eemoo", true, modelAlgorithmConfigs, testAlgorithmConfigs);
		this.algorithmConfigList.add(config);
	}

	public void registerLearners(final LearnerRegistry learnerRegistry, final REALManager realMan) {
		for (final LearnerConfig config : this.algorithmConfigList) {
			if (config.isActive()) {
				learnerRegistry.register(new GeneralActiveLearner(config, realMan.getPRG()));
			} else {
				learnerRegistry.register(new GeneralPassiveLearner(config, realMan.getPRG()));
			}
		}
	}

}
