
public class ExperimentTbl {
	private final String id;
	private final String setupID;
	private final String instanceSetupID;

	public ExperimentTbl(final String pID, final String pSetupID, final String pInstanceSetupID) {
		this.id = pID;
		this.setupID = pSetupID;
		this.instanceSetupID = pInstanceSetupID;
	}

	public String getID() {
		return this.id;
	}

	public String getSetupID() {
		return this.setupID;
	}

	public String getInstanceSetupID() {
		return this.instanceSetupID;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<#" + this.id);
		sb.append(";" + this.setupID);
		sb.append(";" + this.instanceSetupID);
		sb.append(">");

		return sb.toString();
	}

}
