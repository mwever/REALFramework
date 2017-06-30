package de.upb.crc901.wever.crcreal.model.trainingdata;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class TrainingExample {

	private final Word word;
	private final EWordLabel label;

	public TrainingExample(final Word pWord, final EWordLabel pLabel) {
		this.word = pWord;
		this.label = pLabel;
	}

	public Word getWord() {
		return this.word;
	}

	public EWordLabel getLabel() {
		return this.label;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("[");

		sb.append(this.word);
		sb.append(";");
		sb.append(this.label);

		sb.append("]");

		return sb.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.word).append(this.label).toHashCode();
	}
}
