package de.upb.crc901.wever.crcreal.model.events;

public class SeedInitializationEvent {

	private final long algorithmSeed;
	private final long challengerSeed;
	private final long validatorSeed;
	private final long supplierSeed;

	public SeedInitializationEvent(final long pAlgorithmSeed, final long pChallengerSeed, final long pValidatorSeed, final long supplierSeed) {
		this.algorithmSeed = pAlgorithmSeed;
		this.challengerSeed = pChallengerSeed;
		this.validatorSeed = pValidatorSeed;
		this.supplierSeed = supplierSeed;
	}

	public long getAlgorithmSeed() {
		return this.algorithmSeed;
	}

	public long getChallengerSeed() {
		return this.challengerSeed;
	}

	public long getValidatorSeed() {
		return this.validatorSeed;
	}

	public long getSupplierSeed() {
		return this.supplierSeed;
	}

}
