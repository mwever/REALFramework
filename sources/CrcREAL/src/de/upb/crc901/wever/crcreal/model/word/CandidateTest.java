package de.upb.crc901.wever.crcreal.model.word;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;

public class CandidateTest extends AbstractCandidate {

	private static final AtomicInteger GLOBAL_TESTID = new AtomicInteger(0);

	private final int id;
	private final Word test;
	private final int length;
	private final Word wordAccordingToLength;

	public CandidateTest(final List<AbstractTestObjective> pObjectives, final Word pTest, final int pLength) {
		super((List<? extends AbstractObjective>) pObjectives);
		this.id = GLOBAL_TESTID.getAndIncrement();
		this.test = pTest;
		this.length = pLength;
		this.wordAccordingToLength = new Word();
		IntStream.range(0, this.length).forEach(x -> this.wordAccordingToLength.add(this.test.get(x)));
	}

	public Word getWholeWord() {
		return this.test;
	}

	public Word getTest() {
		return this.wordAccordingToLength;
	}

	public int getLength() {
		return this.length;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.getTest() + " (length: " + this.getLength() + ")" + this.getHeuristicObjectiveValues());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append("CandidateTest").append(this.id).toHashCode();
	}

}
