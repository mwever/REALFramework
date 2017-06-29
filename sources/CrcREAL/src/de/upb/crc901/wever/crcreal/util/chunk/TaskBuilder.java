package de.upb.crc901.wever.crcreal.util.chunk;

public class TaskBuilder {

	private int chunkID;
	private int taskID;
	private long rootSeed;

	private String algorithmID;
	private String generatorID;
	private String oracleID;
	private String validatorID;
	private String supplierID;

	private int numberOfRounds;
	private int numberOfGenerations;
	private int sizeOfPopulation;
	private int sizeOfTrainingSet;
	private int maxTestLength;

	private int numberOfStates;
	private int sizeOfAlphabet;

	private EEvaluationType evaluationType;
	private EEvaluationCycle evaluationCycle;
	private int evaluationBound;

	public static TaskBuilder getInstance() {
		return new TaskBuilder();
	}

	public TaskBuilder setChunkID(final int chunkID) {
		this.chunkID = chunkID;
		return this;
	}

	public TaskBuilder setTaskID(final int taskID) {
		this.taskID = taskID;
		return this;
	}

	public TaskBuilder setRootSeed(final long rootSeed) {
		this.rootSeed = rootSeed;
		return this;
	}

	public TaskBuilder setAlgorithmID(final String algorithmID) {
		this.algorithmID = algorithmID;
		return this;
	}

	public TaskBuilder setGeneratorID(final String pGeneratorID) {
		this.generatorID = pGeneratorID;
		return this;
	}

	public TaskBuilder setOracleID(final String oracleID) {
		this.oracleID = oracleID;
		return this;
	}

	public TaskBuilder setValidatorID(final String validatorID) {
		this.validatorID = validatorID;
		return this;
	}

	public TaskBuilder setSupplierID(final String supplierID) {
		this.supplierID = supplierID;
		return this;
	}

	public TaskBuilder setNumberOfRounds(final int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
		return this;
	}

	public TaskBuilder setNumberOfGenerations(final int numberOfGenerations) {
		this.numberOfGenerations = numberOfGenerations;
		return this;
	}

	public TaskBuilder setSizeOfPopulation(final int sizeOfPopulation) {
		this.sizeOfPopulation = sizeOfPopulation;
		return this;
	}

	public TaskBuilder setSizeOfTrainingSet(final int sizeOfTrainingSet) {
		this.sizeOfTrainingSet = sizeOfTrainingSet;
		return this;
	}

	public TaskBuilder setMaxTestLength(final int maxTestLength) {
		this.maxTestLength = maxTestLength;
		return this;
	}

	public TaskBuilder setNumberOfStates(final int numberOfStates) {
		this.numberOfStates = numberOfStates;
		return this;
	}

	public TaskBuilder setSizeOfAlphabet(final int sizeOfAlphabet) {
		this.sizeOfAlphabet = sizeOfAlphabet;
		return this;
	}

	public TaskBuilder setEvaluationType(final EEvaluationType evaluationType) {
		this.evaluationType = evaluationType;
		return this;
	}

	public TaskBuilder setEvaluationCycle(final EEvaluationCycle evaluationCycle) {
		this.evaluationCycle = evaluationCycle;
		return this;
	}

	public TaskBuilder setEvaluationBound(final int evaluationBound) {
		this.evaluationBound = evaluationBound;
		return this;
	}

	public Task toTask() {
		final Task taskToReturn = new Task(this.taskID, this.rootSeed, this.algorithmID, this.generatorID, this.oracleID, this.validatorID, this.supplierID, this.numberOfStates,
				this.sizeOfAlphabet, this.numberOfRounds, this.sizeOfTrainingSet, this.numberOfGenerations, this.sizeOfPopulation, this.maxTestLength, this.evaluationType,
				this.evaluationCycle, this.evaluationBound);
		taskToReturn.setChunkID(this.chunkID);
		return taskToReturn;
	}

}
