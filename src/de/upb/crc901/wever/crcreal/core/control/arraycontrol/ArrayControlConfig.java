package de.upb.crc901.wever.crcreal.core.control.arraycontrol;

import java.util.List;

import org.aeonbits.owner.Config;

public interface ArrayControlConfig extends Config {

	public final static String KEY_PREFIX = "crc.real.";

	// evaluation cycle
	public static final String EVAL_CYCLE = KEY_PREFIX + "stats.evalCycle";
	public static final String EVAL_INDIVUALS = KEY_PREFIX + "stats.evalIndividuals";
	public static final String EVAL_BOUND = KEY_PREFIX + "stats.evalBound";

	// run configuration
	public final static String RANDOMNESS_SEED = KEY_PREFIX + "runConf.randomnessSeed";
	public static final String SIZE_OF_ALPHABET = KEY_PREFIX + "runconf.sizeOfAlphabet";
	public static final String SIZE_OF_TRAININGSET = KEY_PREFIX + "runConf.sizeOfTrainingSet";
	public static final String SIZE_OF_POPULATION = KEY_PREFIX + "runConf.sizeOfPopulation";
	public static final String NUMBER_OF_STATES = KEY_PREFIX + "runConf.numberOfStates";
	public static final String NUMBER_OF_ROUNDS = KEY_PREFIX + "runConf.numberOfRounds";
	public static final String NUMBER_OF_GENERATIONS = KEY_PREFIX + "runConf.numberOfGenerations";
	public final static String NUMBER_OF_SAMPLES = KEY_PREFIX + "runConf.numberOfSamples";
	public final static String LIST_OF_ALGORITHMS = KEY_PREFIX + "runConf.algorithms";

	public static final String MAX_TEST_LENGTH = KEY_PREFIX + "maxTestLength";

	@Key(MAX_TEST_LENGTH)
	@DefaultValue("20")
	public int maxTestLength();

	/* START Evaluation Cycle */
	@Key(EVAL_CYCLE)
	@DefaultValue("round")
	public String evalcycle();

	@Key(EVAL_INDIVUALS)
	@DefaultValue("all")
	public String evalIndividuals();

	@Key(EVAL_BOUND)
	@DefaultValue("30000")
	public int evalBound();
	/* END Evaluation Cycle */

	/* START Run Configuration */
	@Key(RANDOMNESS_SEED)
	@DefaultValue("12356789")
	public int randomnessSeed();

	@Key(NUMBER_OF_SAMPLES)
	@DefaultValue("1")
	public int numberOfSamples();

	@Key(NUMBER_OF_STATES)
	@DefaultValue("8")
	public List<Integer> numberOfStates();

	@Key(SIZE_OF_ALPHABET)
	@DefaultValue("8")
	public List<Integer> sizeOfAlphabet();

	@Key(SIZE_OF_TRAININGSET)
	@DefaultValue("10")
	public List<Integer> sizeOfTrainingSet();

	@Key(NUMBER_OF_ROUNDS)
	@DefaultValue("1000")
	public List<Integer> numberOfRounds();

	@Key(SIZE_OF_POPULATION)
	@DefaultValue("100")
	public List<Integer> sizeOfPopulation();

	@Key(NUMBER_OF_GENERATIONS)
	@DefaultValue("5")
	public List<Integer> numberOfGenerations();

	@Key(LIST_OF_ALGORITHMS)
	@DefaultValue("crc.real.learner.eemoo")
	public List<String> algorithms();

	/* END Run Configuration */

}
