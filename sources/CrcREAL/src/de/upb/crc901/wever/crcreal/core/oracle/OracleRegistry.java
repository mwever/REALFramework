package de.upb.crc901.wever.crcreal.core.oracle;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.core.AbstractRegistry;
import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;

public class OracleRegistry extends AbstractRegistry<AbstractOracle> {

	public OracleRegistry(final EventBus pEventBus) {
		super(pEventBus);
	}

	@Override
	public void rcvInitialSetupEvent(final InitialSetupEvent e) {
		this.activateListener(e.getOracleID());
	}

	public void register(final AbstractOracle abstractOracle) {
		abstractOracle.setEventBus(this.getEventBus());
		this.register(abstractOracle.getIdentifier(), abstractOracle);
	}

}
