package de.upb.wever.util.chunk;

public class Task {

	private int chunkID;
	private final int taskID;
	private final int sampleID;
	private final long rootSeed;

	private final String algorithmID;
	private final String challengerID;
	private final String validatorID;
	private final String supplierID;

	private final int numberOfStates;
	private final int sizeOfAlphabet;
	private final int numberOfRounds;
	private final int sizeOfTrainingSet;

	private final int numberOfGenerations;
	private final int sizeOfPopulation;

	private final EEvaluationType evaluationType;
	private final EEvaluationCycle evaluationCycle;
	private final int evaluationBound;

	public void setChunkID(final int pChunkID) {
		this.chunkID = pChunkID;
	}

	public int getChunkID() {
		return this.chunkID;
	}

	public String getTextualRepresentation() {
		final StringBuilder sb = new StringBuilder();

		sb.append("taskID=" + this.taskID + ";");
		sb.append("sampleID=" + this.sampleID + ";");
		sb.append("rootSeed=" + this.rootSeed + ";");

		sb.append("algorithmID=" + this.algorithmID + ";");
		sb.append("challengerID=" + this.challengerID + ";");
		sb.append("validatorID=" + this.validatorID + ";");
		sb.append("supplierID=" + this.supplierID + ";");

		sb.append("numberOfStates=" + this.numberOfStates + ";");
		sb.append("sizeOfAlphabet=" + this.sizeOfAlphabet + ";");
		sb.append("numberOfRounds=" + this.numberOfRounds + ";");
		sb.append("sizeOfTrainingSet=" + this.sizeOfTrainingSet + ";");
		sb.append("numberOfGenerations=" + this.numberOfGenerations + ";");
		sb.append("sizeOfPopulation=" + this.sizeOfPopulation + ";");

		sb.append("evaluationType=" + this.evaluationType + ";");
		sb.append("evaluationCycle=" + this.evaluationCycle + ";");
		sb.append("evaluationBound=" + this.evaluationBound);

		return sb.toString();
	}

	public Task(final int pTaskID, final int pSampleID, final long pRootSeed, final String pAlgorithmID, final String pChallengerID, final String pValidatorID,
			final String pSupplierID, final int pNumberOfStates, final int pSizeOfAlphabet, final int pNumberOfRounds, final int pSizeOfTrainingSet, final int pNumberOfGenerations,
			final int pSizeOfPopulation, final EEvaluationType pEvaluationType, final EEvaluationCycle pEvaluationCycle, final int pEvaluationBound) {
		this.taskID = pTaskID;
		this.sampleID = pSampleID;
		this.rootSeed = pRootSeed;

		this.algorithmID = pAlgorithmID;
		this.challengerID = pChallengerID;
		this.validatorID = pValidatorID;
		this.supplierID = pSupplierID;

		this.numberOfStates = pNumberOfStates;
		this.sizeOfAlphabet = pSizeOfAlphabet;
		this.numberOfRounds = pNumberOfRounds;
		this.sizeOfTrainingSet = pSizeOfTrainingSet;

		this.numberOfGenerations = pNumberOfGenerations;
		this.sizeOfPopulation = pSizeOfPopulation;

		this.evaluationType = pEvaluationType;
		this.evaluationCycle = pEvaluationCycle;
		this.evaluationBound = pEvaluationBound;
	}

	public int getSizeOfTrainingSet() {
		return this.sizeOfTrainingSet;
	}

	public int getTaskID() {
		return this.taskID;
	}

	public String getAlgorithmID() {
		return this.algorithmID;
	}

	public String getChallengerID() {
		return this.challengerID;
	}

	public String getValidatorID() {
		return this.validatorID;
	}

	public String getSupplierID() {
		return this.supplierID;
	}

	public int getNumberOfStates() {
		return this.numberOfStates;
	}

	public int getSizeOfAlphabet() {
		return this.sizeOfAlphabet;
	}

	public int getNumberOfRounds() {
		return this.numberOfRounds;
	}

	public EEvaluationType getEvaluationType() {
		return this.evaluationType;
	}

	public EEvaluationCycle getEvaluationCycle() {
		return this.evaluationCycle;
	}

	public int getEvaluationBound() {
		return this.evaluationBound;
	}

	public int getNumberOfGenerations() {
		return this.numberOfGenerations;
	}

	public int getSizeOfPopulation() {
		return this.sizeOfPopulation;
	}

	public int getSampleID() {
		return this.sampleID;
	}

	public long getRootSeed() {
		return this.rootSeed;
	}

	public static Task readFrom(final String line) {
		final String[] lineSplit = line.split(";");
		int taskID = -1;
		int sampleID = 1;
		long rootSeed = 0;

		String algorithmID = "";
		String challengerID = "";
		String validatorID = "";
		String supplierID = "";

		int numberOfStates = -1;
		int sizeOfAlphabet = -1;
		int numberOfRounds = -1;
		int sizeOfTrainingSet = -1;

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
			case "sampleID":
				sampleID = Integer.valueOf(keyValueSplit[1]);
				break;
			case "rootSeed":
				rootSeed = Long.valueOf(keyValueSplit[1]);
				break;
			case "algorithmID":
				algorithmID = keyValueSplit[1];
				break;
			case "challengerID":
				challengerID = keyValueSplit[1];
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

		return new Task(taskID, sampleID, rootSeed, algorithmID, challengerID, validatorID, supplierID, numberOfStates, sizeOfAlphabet, numberOfRounds, sizeOfTrainingSet,
				numberOfGenerations, sizeOfPopulation, evaluationType, evaluationCycle, evaluationBound);
	}
}
