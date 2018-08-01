package de.upb.crc901.wever.crcreal.core.learner.objective;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveRegistry {

	private static ObjectiveRegistry INSTANCE;

	public static ObjectiveRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ObjectiveRegistry();
		}
		return INSTANCE;
	}

	private final Map<String, AbstractObjective> objectiveCache;

	private ObjectiveRegistry() {
		this.objectiveCache = new HashMap<>();
	}

	public void register(final AbstractObjective pObjectiveToRegister) {
		this.objectiveCache.put(pObjectiveToRegister.getID(), pObjectiveToRegister);
	}

	public AbstractObjective getObjective(final String pIdentifier) {
		if (!this.objectiveCache.containsKey(pIdentifier)) {
			throw new IllegalArgumentException("Unknown identifier for getting objective function: " + pIdentifier);
		}
		return this.objectiveCache.get(pIdentifier);
	}

}
