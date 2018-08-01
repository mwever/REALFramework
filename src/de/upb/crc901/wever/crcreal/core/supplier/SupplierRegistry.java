package de.upb.crc901.wever.crcreal.core.supplier;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.core.AbstractRegistry;
import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;

public class SupplierRegistry extends AbstractRegistry<ASupplier> {

	public SupplierRegistry(final EventBus pEventBus) {
		super(pEventBus);
	}

	@Override
	public void rcvInitialSetupEvent(final InitialSetupEvent e) {
		this.activateListener(e.getSupplierID());
	}

	public void register(final ASupplier supplier) {
		supplier.setEventBus(this.getEventBus());
		this.register(supplier.getIdentifier(), supplier);
	}

}
