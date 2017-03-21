package de.upb.crc901.wever.crcreal.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;

public abstract class AbstractRegistry<T> {

	private final EventBus eventBus;
	private final Map<String, T> registeredEntities;
	private T activeRegisteredEntity;

	protected AbstractRegistry(final EventBus pEventBus) {
		this.eventBus = pEventBus;
		this.eventBus.register(this);
		this.registeredEntities = new HashMap<>();
	}

	protected boolean register(final String entityID, final T entity) {
		if (this.registeredEntities.containsKey(entityID)) {
			return false;
		}
		this.registeredEntities.put(entityID, entity);
		return true;
	}

	public T deregister(final String entityID) {
		return this.registeredEntities.remove(entityID);
	}

	public T get(final String entityID) {
		return this.registeredEntities.get(entityID);
	}

	protected EventBus getEventBus() {
		return this.eventBus;
	}

	protected Map<String, T> getRegisteredEntities() {
		return this.registeredEntities;
	}

	public Collection<T> registeredEntities() {
		return this.registeredEntities.values();
	}

	protected void activateListener(final String pListenerID) {
		if (this.activeRegisteredEntity != null) {
			this.getEventBus().unregister(this.activeRegisteredEntity);
		}

		for (final String entityID : this.getRegisteredEntities().keySet()) {
			if (entityID.equals(pListenerID)) {
				this.activeRegisteredEntity = this.getRegisteredEntities().get(entityID);
				this.getEventBus().register(this.activeRegisteredEntity);
			}
		}
	}

	@Subscribe
	public abstract void rcvInitialSetupEvent(InitialSetupEvent e);

}
