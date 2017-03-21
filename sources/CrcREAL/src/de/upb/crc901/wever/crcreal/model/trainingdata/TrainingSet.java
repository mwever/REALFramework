package de.upb.crc901.wever.crcreal.model.trainingdata;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class TrainingSet extends LinkedList<TrainingExample> {

	/**
	 * Auto-generated serial version UID for extending LinkedList
	 */
	private static final long serialVersionUID = 120543584215523128L;

	public TrainingSet(final List<TrainingExample> collect) {
		super(collect);
	}

	public TrainingSet() {
		super();
	}

	public int numberOfTrainingExamplesWithLabel(final EWordLabel label) {
		return (int) this.stream().filter(x -> x.getLabel() == label).count();
	}

	public void acceptEvaluationVisitor(final ITrainingSetEvaluatorVisitor pVisitor) {
		this.stream().forEach(pVisitor::visit);
	}

	public boolean containsWord(final Word element) {
		return this.stream().map(x -> x.getWord()).anyMatch(x -> x.equals(element));
	}

	public boolean allSamplesWithLabel(final EWordLabel label) {
		boolean equallyLabeled = false;

		for (final TrainingExample sample : this) {
			if (sample.getLabel() != label) {
				equallyLabeled = false;
				break;
			}
		}

		return equallyLabeled;
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hcb = new HashCodeBuilder();
		for (final TrainingExample e : this) {
			hcb.append(e);
		}
		return hcb.toHashCode();
	}

}
