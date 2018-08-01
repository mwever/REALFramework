package de.upb.crc901.wever.crcreal.core.supplier;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class ASupplier {

	private final String identifier;
	private final IRandomGenerator rand;
	private EventBus eventBus;

	protected ASupplier(final String pIdentifier, final IRandomGenerator pRand) {
		this.identifier = pIdentifier;
		this.rand = pRand;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	protected IRandomGenerator getRandomGenerator() {
		return this.rand;
	}

	protected EventBus getEventBus() {
		return this.eventBus;
	}

	public void setEventBus(final EventBus pEventBus) {
		this.eventBus = pEventBus;
	}

}
