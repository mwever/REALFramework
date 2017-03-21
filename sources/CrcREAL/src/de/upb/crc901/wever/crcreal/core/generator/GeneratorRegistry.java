package de.upb.crc901.wever.crcreal.core.generator;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.core.AbstractRegistry;
import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;

public class GeneratorRegistry extends AbstractRegistry<AbstractGenerator> {

	public GeneratorRegistry(final EventBus pEventBus) {
		super(pEventBus);
	}

	@Override
	public void rcvInitialSetupEvent(final InitialSetupEvent e) {
		this.activateListener(e.getChallengerID());
	}

	public void register(final AbstractGenerator aChallenger) {
		aChallenger.setEventBus(this.getEventBus());
		this.register(aChallenger.getIdentifier(), aChallenger);
	}

}
