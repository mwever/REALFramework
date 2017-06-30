package de.upb.crc901.wever.crcreal.model.word;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestSetObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;

public class CandidateTestSet extends AbstractCandidate implements Iterable<CandidateTest> {

	private final Set<CandidateTest> testSet;

	public CandidateTestSet(final List<AbstractTestSetObjective> pObjectives) {
		super(pObjectives);
		this.testSet = new HashSet<>();
	}

	public CandidateTestSet(final List<AbstractTestSetObjective> pObjectives, final Set<CandidateTest> pTestSet) {
		this(pObjectives);
		this.testSet.addAll(pTestSet);
	}

	public boolean add(final CandidateTest test) {
		return this.testSet.add(test);
	}

	public boolean addAll(final Collection<CandidateTest> testSet) {
		return this.testSet.addAll(testSet);
	}

	public boolean remove(final CandidateTest test) {
		return this.testSet.remove(test);
	}

	public boolean removeAll(final Collection<CandidateTest> testSet) {
		return this.testSet.removeAll(testSet);
	}

	public Set<CandidateTest> getTestSet() {
		return this.testSet;
	}

	public Stream<CandidateTest> stream() {
		return this.testSet.stream();
	}

	public int size() {
		return this.testSet.size();
	}

	@Override
	public Iterator<CandidateTest> iterator() {
		return this.testSet.iterator();
	}

}
