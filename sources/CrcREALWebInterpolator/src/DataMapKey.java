import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DataMapKey {

	private final String setupID;
	private final String instanceSetupID;
	private final String objectiveID;
	private final String round;
	private final String generation;

	public DataMapKey(final String pSetupID, final String pInstanceSetupID, final String pRound, final String pGeneration, final String pObjectiveID) {
		this.setupID = pSetupID;
		this.instanceSetupID = pInstanceSetupID;
		this.objectiveID = pObjectiveID;
		this.round = pRound;
		this.generation = pGeneration;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.setupID).append(this.instanceSetupID).append(this.objectiveID).append(this.round).append(this.generation).hashCode();
	}

	@Override
	public boolean equals(final Object arg) {
		if (!(arg instanceof DataMapKey)) {
			return false;
		}
		final DataMapKey other = (DataMapKey) arg;
		return new EqualsBuilder().append(this.setupID, other.setupID).append(this.instanceSetupID, other.instanceSetupID).append(this.objectiveID, other.objectiveID)
				.append(this.round, other.round).append(this.generation, other.generation).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(this.setupID);
		sb.append(";" + this.instanceSetupID);
		sb.append(";" + this.round);
		sb.append(";" + this.generation);
		sb.append(";" + this.objectiveID);

		return sb.toString();
	}

	public static DataMapKey readFrom(final String line) {
		final String[] lineSplit = line.split(";");
		return new DataMapKey(lineSplit[0], lineSplit[1], lineSplit[2], lineSplit[3], lineSplit[4]);
	}

	public String getSetupID() {
		return this.setupID;
	}

	public String getInstanceSetupID() {
		return this.instanceSetupID;
	}

	public String getObjectiveID() {
		return this.objectiveID;
	}

	public String getRound() {
		return this.round;
	}

	public String getGeneration() {
		return this.generation;
	}

}
