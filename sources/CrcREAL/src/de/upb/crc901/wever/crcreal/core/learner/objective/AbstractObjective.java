package de.upb.crc901.wever.crcreal.core.learner.objective;

public abstract class AbstractObjective {

	private boolean active;

	protected AbstractObjective() {
		this.active = true;
	}

	public abstract String getID();

	public abstract String getLabel();

	public abstract AbstractObjective newInstance();

	public abstract boolean hasRealData();

	public void setActive() {
		this.active = true;
	}

	public void setPassive() {
		this.active = false;
	}

	public boolean isActive() {
		return this.active;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(this.getID() + ":");
		if (this.isActive()) {
			sb.append("active");
		} else {
			sb.append("passive");
		}

		return sb.toString();
	}
}
