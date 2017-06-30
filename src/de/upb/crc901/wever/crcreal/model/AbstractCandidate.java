package de.upb.crc901.wever.crcreal.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;

public abstract class AbstractCandidate {

	private final ArrayList<Double> realObjectiveValues;
	private final ArrayList<Double> heuristicObjectiveValues;

	protected final List<? extends AbstractObjective> objectives;
	protected final List<String> objectiveIDs = new LinkedList<>();

	protected AbstractCandidate(final List<? extends AbstractObjective> pObjectives) {
		this.objectives = pObjectives;
		this.heuristicObjectiveValues = new ArrayList<>(pObjectives.size());
		this.realObjectiveValues = new ArrayList<>(pObjectives.size());
		IntStream.range(0, this.objectives.size()).forEach(x -> {
			this.realObjectiveValues.add(x, 0.0);
			this.heuristicObjectiveValues.add(x, 0.0);
			this.objectiveIDs.add(this.objectives.get(x).getID());
		});
	}

	public Double getHeuristicObjectiveValue(final int pIndex) {
		return this.heuristicObjectiveValues.get(pIndex);
	}

	public Double getHeuristicObjectiveValue(final String pIdentifier) {
		return this.getHeuristicObjectiveValue(this.objectiveIDs.indexOf(pIdentifier));
	}

	public void setHeuristicObjectiveValue(final int pIndex, final Double pObjectiveValue) {
		this.heuristicObjectiveValues.set(pIndex, pObjectiveValue);
	}

	public void setHeuristicObjectiveValue(final String pIdentifier, final Double pObjectiveValue) {
		this.setHeuristicObjectiveValue(this.objectiveIDs.indexOf(pIdentifier), pObjectiveValue);
	}

	public List<Double> getHeuristicObjectiveValues() {
		return this.heuristicObjectiveValues;
	}

	public List<Double> getRealObjectiveValues() {
		return this.realObjectiveValues;
	}

	public Double getRealObjectiveValue(final int pIndex) {
		return this.realObjectiveValues.get(pIndex);
	}

	public Double getRealObjectiveValue(final String pIdentifier) {
		return this.getRealObjectiveValue(this.objectiveIDs.indexOf(pIdentifier));
	}

	public void setRealObjectiveValue(final int pIndex, final Double pObjectiveValue) {
		this.realObjectiveValues.set(pIndex, pObjectiveValue);
	}

	public void setRealObjectiveValue(final String pIdentifier, final Double pObjectiveValue) {
		this.setRealObjectiveValue(this.objectiveIDs.indexOf(pIdentifier), pObjectiveValue);
	}

	public List<? extends AbstractObjective> getObjectives() {
		return this.objectives;
	}

	public void setHeuristicObjectiveValues(final List<Double> list) {
		for (int i = 0; i < list.size(); i++) {
			this.setHeuristicObjectiveValue(i, list.get(i));
		}
	}
}
