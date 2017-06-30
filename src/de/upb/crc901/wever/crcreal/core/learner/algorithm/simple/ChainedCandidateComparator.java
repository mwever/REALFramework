package de.upb.crc901.wever.crcreal.core.learner.algorithm.simple;

import java.util.Comparator;
import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;

public class ChainedCandidateComparator implements Comparator<AbstractCandidate> {

	private final List<? extends AbstractObjective> activeObjectivesToUseForComparison;

	public ChainedCandidateComparator(final List<? extends AbstractObjective> list) {
		this.activeObjectivesToUseForComparison = list;
	}

	@Override
	public int compare(final AbstractCandidate arg0, final AbstractCandidate arg1) {
		for (final AbstractObjective objective : this.activeObjectivesToUseForComparison) {
			final int compareValue = arg0.getHeuristicObjectiveValue(objective.getID()).compareTo(arg1.getHeuristicObjectiveValue(objective.getID()));
			if (compareValue != 0) {
				return compareValue;
			}
		}
		return 0;
	}

}
