package de.upb.crc901.wever.crcreal.model.alphabet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class represents a unique input symbol. Instances can be created via the InputFactory.
 *
 * @author wever
 *
 */
public class InputSymbol implements Comparable<InputSymbol> {

	private final String name;
	private final int id;

	InputSymbol(final String pName, final int pID) {
		this.name = pName;
		this.id = pID;
	}

	/**
	 * @return Returns the identifier string of the input symbol.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * @return returns the factory-wide unique id of this input symbol.
	 */
	public int id() {
		return this.id;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.name());
		return sb.toString();
	}

	@Override
	public int compareTo(final InputSymbol arg0) {
		return Integer.valueOf(this.id()).compareTo(arg0.id());
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof InputSymbol)) {
			return false;
		}
		final InputSymbol otherInputSymbol = (InputSymbol) other;
		return new EqualsBuilder().append(this.name, otherInputSymbol.name).append(this.id, otherInputSymbol.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.name).append(this.id).toHashCode();
	}
}
