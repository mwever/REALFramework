package de.upb.crc901.wever.crcreal.util.chunk;

import jaicore.basic.chunks.Task;

public class REALTask extends Task {

	/**
	 *
	 */
	private static final long serialVersionUID = 8411780813540854314L;

	public static final String TASK_ID = "taskID";
	public static final String CHUNK_ID = "chunkID";

	public static final String LEARNER_ID = "learnerID";
	public static final String GENERATOR_ID = "generatorID";
	public static final String ORACLE_ID = "oracleID";
	public static final String VALIDATOR_ID = "validatorID";
	public static final String SUPPLIER_ID = "supplierID";

	public static final String ROOT_SEED = "rootSeed";
	public static final String SIZE_OF_TRAINING_SET = "sizeOfTrainingSet";
	public static final String NUMBER_OF_STATES = "numberOfStates";
	public static final String SIZE_OF_ALPHABET = "sizeOfAlphabet";
	public static final String MAX_TEST_LENGTH = "maxTestLength";
	public static final String NUMBER_OF_ROUNDS = "numberOfRounds";
	public static final String NUMBER_OF_GENERATIONS = "numberOfGenerations";
	public static final String SIZE_OF_POPULATION = "sizeOfPopulation";

	public static final String EVALUATION_BOUND = "evaluationBound";
	public static final String EVALUATION_TYPE = "evaluationType";
	public static final String EVALUATION_CYCLE = "evaluationCycle";

	public void setChunkID(final int pChunkID) {
		this.store(CHUNK_ID, pChunkID);
	}

	public int getChunkID() {
		return this.getValueAsInt(CHUNK_ID);
	}

	public int getSizeOfTrainingSet() {
		return this.getValueAsInt(SIZE_OF_TRAINING_SET);
	}

	public String getAlgorithmID() {
		return this.getValueAsString(LEARNER_ID);
	}

	public String getGeneratorID() {
		return this.getValueAsString(GENERATOR_ID);
	}

	public String getOracleID() {
		return this.getValueAsString(ORACLE_ID);
	}

	public String getValidatorID() {
		return this.getValueAsString(VALIDATOR_ID);
	}

	public String getSupplierID() {
		return this.getValueAsString(SUPPLIER_ID);
	}

	public int getNumberOfStates() {
		return this.getValueAsInt(NUMBER_OF_STATES);
	}

	public int getSizeOfAlphabet() {
		return this.getValueAsInt(SIZE_OF_ALPHABET);
	}

	public int getMaxTestLength() {
		return this.getValueAsInt(MAX_TEST_LENGTH);
	}

	public int getNumberOfRounds() {
		return this.getValueAsInt(NUMBER_OF_ROUNDS);
	}

	public EEvaluationType getEvaluationType() {
		return EEvaluationType.valueOf(this.getValueAsString(EVALUATION_TYPE));
	}

	public EEvaluationCycle getEvaluationCycle() {
		return EEvaluationCycle.valueOf(this.getValueAsString(EVALUATION_CYCLE));
	}

	public int getEvaluationBound() {
		return this.getValueAsInt(EVALUATION_BOUND);
	}

	public int getNumberOfGenerations() {
		return this.getValueAsInt(NUMBER_OF_GENERATIONS);
	}

	public int getSizeOfPopulation() {
		return this.getValueAsInt(SIZE_OF_POPULATION);
	}

	public long getRootSeed() {
		return this.getValueAsLong(ROOT_SEED);
	}

	public REALTask(final int pTaskID, final long pRootSeed, final String pAlgorithmID, final String pGeneratorID,
			final String pOracleID, final String pValidatorID, final String pSupplierID, final int pNumberOfStates,
			final int pSizeOfAlphabet, final int pNumberOfRounds, final int pSizeOfTrainingSet,
			final int pNumberOfGenerations, final int pSizeOfPopulation, final int pMaxTestLength,
			final EEvaluationType pEvaluationType, final EEvaluationCycle pEvaluationCycle,
			final int pEvaluationBound) {
		this.store(TASK_ID, pTaskID);

		this.store(LEARNER_ID, pAlgorithmID);
		this.store(GENERATOR_ID, pGeneratorID);
		this.store(ORACLE_ID, pOracleID);
		this.store(VALIDATOR_ID, pValidatorID);
		this.store(SUPPLIER_ID, pSupplierID);

		this.store(ROOT_SEED, pRootSeed);
		this.store(NUMBER_OF_STATES, pNumberOfStates);
		this.store(SIZE_OF_ALPHABET, pSizeOfAlphabet);
		this.store(NUMBER_OF_ROUNDS, pNumberOfRounds);
		this.store(SIZE_OF_TRAINING_SET, pSizeOfTrainingSet);
		this.store(MAX_TEST_LENGTH, pMaxTestLength);
		this.store(NUMBER_OF_GENERATIONS, pNumberOfGenerations);
		this.store(SIZE_OF_POPULATION, pSizeOfPopulation);

		this.store(EVALUATION_BOUND, pEvaluationBound);
		this.store(EVALUATION_TYPE, pEvaluationType.name());
		this.store(EVALUATION_CYCLE, pEvaluationCycle.name());
	}

	public static REALTask readFrom(final String line) {
		final String[] lineSplit = line.split(";");
		int taskID = -1;
		long rootSeed = 0;

		String algorithmID = "";
		String generatorID = "";
		String oracleID = "";
		String validatorID = "";
		String supplierID = "";

		int numberOfStates = -1;
		int sizeOfAlphabet = -1;
		int numberOfRounds = -1;
		int sizeOfTrainingSet = -1;
		int maxTestLength = -1;

		int numberOfGenerations = -1;
		int sizeOfPopulation = -1;

		EEvaluationType evaluationType = EEvaluationType.ALL;
		EEvaluationCycle evaluationCycle = EEvaluationCycle.ROUND;
		int evaluationBound = -1;

		for (final String keyValue : lineSplit) {
			final String[] keyValueSplit = keyValue.split("=");

			if (keyValueSplit.length != 2) {
				continue;
			}

			switch (keyValueSplit[0]) {
			case "taskID":
				taskID = Integer.valueOf(keyValueSplit[1]);
				break;
			case "rootSeed":
				rootSeed = Long.valueOf(keyValueSplit[1]);
				break;
			case "algorithmID":
				algorithmID = keyValueSplit[1];
				break;
			case "generatorID":
				generatorID = keyValueSplit[1];
				break;
			case "oracleID":
				oracleID = keyValueSplit[1];
				break;
			case "validatorID":
				validatorID = keyValueSplit[1];
				break;
			case "supplierID":
				supplierID = keyValueSplit[1];
				break;
			case "numberOfStates":
				numberOfStates = Integer.valueOf(keyValueSplit[1]);
				break;
			case "sizeOfAlphabet":
				sizeOfAlphabet = Integer.valueOf(keyValueSplit[1]);
				break;
			case "numberOfRounds":
				numberOfRounds = Integer.valueOf(keyValueSplit[1]);
				break;
			case "sizeOfTrainingSet":
				sizeOfTrainingSet = Integer.valueOf(keyValueSplit[1]);
				break;
			case "numberOfGenerations":
				numberOfGenerations = Integer.valueOf(keyValueSplit[1]);
				break;
			case "sizeOfPopulation":
				sizeOfPopulation = Integer.valueOf(keyValueSplit[1]);
				break;
			case "maxTestLength":
				maxTestLength = Integer.valueOf(keyValueSplit[1]);
				break;
			case "evaluationBound":
				evaluationBound = Integer.valueOf(keyValueSplit[1]);
				break;
			case "evaluationType":
				switch (keyValueSplit[1]) {
				case "elite":
					evaluationType = EEvaluationType.ELITE;
					break;
				case "all":
					evaluationType = EEvaluationType.ALL;
					break;
				default:
					evaluationType = EEvaluationType.ALL;
					break;
				}
				break;
			case "evaluationCycle":
				switch (keyValueSplit[1]) {
				case "generation":
					evaluationCycle = EEvaluationCycle.GENERATION;
					break;
				case "round":
					evaluationCycle = EEvaluationCycle.ROUND;
					break;
				case "last":
					evaluationCycle = EEvaluationCycle.LAST;
					break;
				default:
					evaluationCycle = EEvaluationCycle.ROUND;
					break;
				}
				break;
			}
		}

		return new REALTask(taskID, rootSeed, algorithmID, generatorID, oracleID, validatorID, supplierID,
				numberOfStates, sizeOfAlphabet, numberOfRounds, sizeOfTrainingSet, numberOfGenerations,
				sizeOfPopulation, maxTestLength, evaluationType, evaluationCycle, evaluationBound);
	}
}
