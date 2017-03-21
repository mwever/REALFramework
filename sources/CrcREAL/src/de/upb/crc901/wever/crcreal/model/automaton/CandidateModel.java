package de.upb.crc901.wever.crcreal.model.automaton;

import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;

public class CandidateModel extends AbstractCandidate {

	private final FiniteAutomaton model;

	public CandidateModel(final List<? extends AbstractObjective> pObjectives, final FiniteAutomaton pModel) {
		super(pObjectives);
		this.model = pModel;
	}

	public FiniteAutomaton getModel() {
		return this.model;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("#" + this.model.getID());
		sb.append("<");

		boolean first = true;
		for (final AbstractObjective objective : this.objectives) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(objective.getLabel() + ":");
			sb.append(this.getHeuristicObjectiveValue(objective.getID()));
			sb.append("|");
			sb.append(this.getRealObjectiveValue(objective.getID()));
		}

		sb.append(">");

		return sb.toString();
	}

}
