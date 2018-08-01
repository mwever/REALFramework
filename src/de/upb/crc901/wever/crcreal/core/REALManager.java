package de.upb.crc901.wever.crcreal.core;

import org.aeonbits.owner.ConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.core.control.AbstractControl;
import de.upb.crc901.wever.crcreal.core.generator.GeneratorRegistry;
import de.upb.crc901.wever.crcreal.core.generator.uniform.RandomUniformGenerator;
import de.upb.crc901.wever.crcreal.core.learner.LearnerRegistry;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AlgorithmRegistry;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii.NSGAIIModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii.NSGAIIPreSatModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.nsgaii.NSGAIITestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.simple.SimpleModelAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.simple.SimpleTestAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.objective.ObjectiveRegistry;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.AllTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.CountSinkStatesObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.FMeasureObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.ModelSizeObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.NegativeTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.PositiveTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.RelevantPartObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.DisagreementObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.MaxTestLengthObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.MinTestLengthObjective;
import de.upb.crc901.wever.crcreal.core.oracle.OracleRegistry;
import de.upb.crc901.wever.crcreal.core.oracle.phonest.PHonestOracle;
import de.upb.crc901.wever.crcreal.core.supplier.FAKTQSupplier;
import de.upb.crc901.wever.crcreal.core.supplier.SupplierRegistry;
import de.upb.crc901.wever.crcreal.core.validator.ValidatorRegistry;
import de.upb.crc901.wever.crcreal.core.validator.exploration.ExplorationValidator;
import de.upb.crc901.wever.crcreal.core.validator.sample.SampleValidator;
import de.upb.crc901.wever.crcreal.model.config.LearnerConfigLoader;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;
import de.upb.crc901.wever.crcreal.util.rand.PseudoRandomGenerator;

public class REALManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(REALManager.class);

	private final EventBus eventBus;
	private final IRandomGenerator rand;

	private final GeneratorRegistry generatorRegistry;
	private final OracleRegistry oracleRegistry;
	private final LearnerRegistry learnerRegistry;
	private final SupplierRegistry supplierRegistry;
	private final ValidatorRegistry validatorRegistry;

	private final ObjectiveRegistry objectiveRegistry;
	private final AlgorithmRegistry algorithmRegistry;
	private final REALConfig config;

	public REALManager() {
		// Read config file
		this.config = ConfigCache.getOrCreate(REALConfig.class);

		// Create asynchronous EventBus
		// final LinkedBlockingQueue<Runnable> blockingQueue = new
		// LinkedBlockingQueue<Runnable>();
		// this.eventBus = new AsyncEventBus(new
		// ThreadPoolExecutor(this.config.numberCoreThreads(),
		// this.config.numberAdditionalThreads(), 0L, TimeUnit.MILLISECONDS,
		// blockingQueue));
		this.eventBus = new EventBus();

		this.rand = new PseudoRandomGenerator(this.config.randomnessSeed());

		this.generatorRegistry = new GeneratorRegistry(this.eventBus);
		this.learnerRegistry = new LearnerRegistry(this.eventBus);
		this.supplierRegistry = new SupplierRegistry(this.eventBus);
		this.validatorRegistry = new ValidatorRegistry(this.eventBus);
		this.oracleRegistry = new OracleRegistry(this.eventBus);

		this.objectiveRegistry = ObjectiveRegistry.getInstance();
		this.algorithmRegistry = AlgorithmRegistry.getInstance();

		LOGGER.trace("Created eventbus, prg and instantiated all registries.");

		this.registerPreInstalledEntities();
	}

	private void registerPreInstalledEntities() {
		// register objectives for EA's

		// model objectives
		this.objectiveRegistry.register(new PositiveTestExampleObjective());
		this.objectiveRegistry.register(new NegativeTestExampleObjective());
		this.objectiveRegistry.register(new ModelSizeObjective());
		this.objectiveRegistry.register(new AllTestExampleObjective());
		this.objectiveRegistry.register(new RelevantPartObjective());
		this.objectiveRegistry.register(new CountSinkStatesObjective());
		this.objectiveRegistry.register(new FMeasureObjective());

		// test objectives
		this.objectiveRegistry.register(new DisagreementObjective());
		this.objectiveRegistry.register(new MinTestLengthObjective());
		this.objectiveRegistry.register(new MaxTestLengthObjective());

		// end of registration of objectives

		// register algorithms for performing requirements engineering by active
		// learning

		// model algorithms
		this.algorithmRegistry.register(new SimpleModelAlgorithm());
		this.algorithmRegistry.register(new NSGAIIModelAlgorithm());
		this.algorithmRegistry.register(new NSGAIIPreSatModelAlgorithm());

		// test algorithms
		this.algorithmRegistry.register(new NSGAIITestAlgorithm());
		this.algorithmRegistry.register(new SimpleTestAlgorithm());

		// register learners
		final LearnerConfigLoader algoConfigLoader = new LearnerConfigLoader();
		algoConfigLoader.registerLearners(this.learnerRegistry, this);

		// register challengers
		this.generatorRegistry.register(new RandomUniformGenerator(this.getPRG()));

		// register oracles
		this.oracleRegistry.register(new PHonestOracle("crc.real.oracle.10honestoracle", this.getPRG(), 1.0));
		this.oracleRegistry.register(new PHonestOracle("crc.real.oracle.09honestoracle", this.getPRG(), 0.9));
		this.oracleRegistry.register(new PHonestOracle("crc.real.oracle.08honestoracle", this.getPRG(), 0.8));
		this.oracleRegistry.register(new PHonestOracle("crc.real.oracle.07honestoracle", this.getPRG(), 0.7));
		this.oracleRegistry.register(new PHonestOracle("crc.real.oracle.06honestoracle", this.getPRG(), 0.6));
		this.oracleRegistry.register(new PHonestOracle("crc.real.oracle.05honestoracle", this.getPRG(), 0.5));

		// register suppliers
		this.supplierRegistry.register(new FAKTQSupplier("faktq-0", this.getPRG(), 0));
		this.supplierRegistry.register(new FAKTQSupplier("faktq-1", this.getPRG(), 1));
		this.supplierRegistry.register(new FAKTQSupplier("faktq-2", this.getPRG(), 2));
		this.supplierRegistry.register(new FAKTQSupplier("faktq-3", this.getPRG(), 3));

		// register validators
		this.validatorRegistry.register(new ExplorationValidator(this.getPRG()));
		this.validatorRegistry.register(new SampleValidator(this.getPRG()));

		LOGGER.trace("Registered all pre-contained entities of the framework to the respective registries.");
	}

	public GeneratorRegistry getGeneratorRegistry() {
		return this.generatorRegistry;
	}

	public OracleRegistry getOracleRegistry() {
		return this.oracleRegistry;
	}

	public LearnerRegistry getLearnerRegistry() {
		return this.learnerRegistry;
	}

	public SupplierRegistry getSupplierRegistry() {
		return this.supplierRegistry;
	}

	public ValidatorRegistry getValidatorRegistry() {
		return this.validatorRegistry;
	}

	public void registerListenerToEventBus(final Object listener) {
		this.eventBus.register(listener);
	}

	public void registerControl(final AbstractControl controller) {
		this.eventBus.register(controller);
		controller.setEventBus(this.eventBus);
	}

	public IRandomGenerator getPRG() {
		return new PseudoRandomGenerator(this.rand.nextSeed());
	}

}
