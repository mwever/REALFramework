package de.upb.crc901.wever.crcreal.model.word;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.upb.crc901.wever.crcreal.model.alphabet.InputSymbol;

/**
 * Represents an exemplary sequence of edge labels and its respective label.
 *
 * @author wever
 */
public class Word extends LinkedList<InputSymbol> {
	/**
	 *
	 */
	private static final long serialVersionUID = 7229575586883396115L;

	public Word(final List<InputSymbol> listOfInputSymbols) {
		super(listOfInputSymbols);
	}

	public Word() {
		super();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		for (final InputSymbol symbol : this) {
			sb.append("(" + symbol + ")");
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hb = new HashCodeBuilder();

		for (final InputSymbol i : this) {
			hb.append(i);
		}

		return hb.toHashCode();
	}

}
