package de.upb.crc901.wever.crcreal.model.events;

public class InitialSetupEvent {

	private final String learnerID;
	private final String challengerID;
	private final String oracleID;
	private final String validatorID;
	private final String supplierID;

	public InitialSetupEvent(final String pLearnerID, final String pChallengerID, final String pOracleID, final String pValidatorID, final String pSupplierID) {
		this.learnerID = pLearnerID;
		this.challengerID = pChallengerID;
		this.oracleID = pOracleID;
		this.validatorID = pValidatorID;
		this.supplierID = pSupplierID;
	}

	public String getLearnerID() {
		return this.learnerID;
	}

	public String getChallengerID() {
		return this.challengerID;
	}

	public String getOracleID() {
		return this.oracleID;
	}

	public String getValidatorID() {
		return this.validatorID;
	}

	public String getSupplierID() {
		return this.supplierID;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("Learner: " + this.learnerID + " ");
		sb.append("Challenger: " + this.challengerID + " ");
		sb.append("Oracle: " + this.oracleID + " ");
		sb.append("Validator: " + this.validatorID + " ");
		sb.append("Supplier: " + this.supplierID);

		return sb.toString();
	}

}
