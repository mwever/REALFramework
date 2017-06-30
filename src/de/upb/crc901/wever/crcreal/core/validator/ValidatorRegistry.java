package de.upb.crc901.wever.crcreal.core.validator;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.core.AbstractRegistry;
import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;

public class ValidatorRegistry extends AbstractRegistry<AbstractValidator> {

	public ValidatorRegistry(final EventBus pEventBus) {
		super(pEventBus);
	}

	@Override
	public void rcvInitialSetupEvent(final InitialSetupEvent e) {
		this.activateListener(e.getValidatorID());
	}

	public void register(final AbstractValidator pValidator) {
		pValidator.setEventBus(this.getEventBus());
		super.register(pValidator.getIdentifier(), pValidator);
	}

}
