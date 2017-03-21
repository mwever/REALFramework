package de.upb.crc901.wever.crcreal.model.config;

import java.util.LinkedList;
import java.util.List;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.ObjectiveRegistry;

public class AlgorithmConfig {

	private final String algorithmID;
	private final List<String> objectives;
	private final List<String> passiveObjectives;
	private final boolean seededPopulation;
	private final int sizeOfPopulation;

	public AlgorithmConfig(final String pAlgorithmID, final List<String> pObjectives, final List<String> pPassiveObjectives, final boolean pSeededPopulation,
			final int pSizeOfPopulation) {
		this.algorithmID = pAlgorithmID;
		this.objectives = pObjectives;
		this.passiveObjectives = pPassiveObjectives;
		this.seededPopulation = pSeededPopulation;
		this.sizeOfPopulation = pSizeOfPopulation;
	}

	public String getAlgorithmID() {
		return this.algorithmID;
	}

	public boolean getSeededPopulation() {
		return this.seededPopulation;
	}

	public List<AbstractObjective> getObjectivesObjects() {
		final List<AbstractObjective> objectiveList = new LinkedList<>();

		for (final String objectiveIdentifier : this.objectives) {
			final AbstractObjective obj = ObjectiveRegistry.getInstance().getObjective(objectiveIdentifier);
			if (this.passiveObjectives.contains(objectiveIdentifier)) {
				obj.setPassive();
			}
			objectiveList.add(obj);
		}

		return objectiveList;
	}

	public List<String> getObjectives() {
		return this.objectives;
	}

	public List<String> getPassiveObjectives() {
		return this.passiveObjectives;
	}

	public int getSizeOfPopulation() {
		return this.sizeOfPopulation;
	}

}
