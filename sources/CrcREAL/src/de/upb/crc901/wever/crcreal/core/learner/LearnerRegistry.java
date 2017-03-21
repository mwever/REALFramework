package de.upb.crc901.wever.crcreal.core.learner;

import com.google.common.eventbus.EventBus;

import de.upb.crc901.wever.crcreal.core.AbstractRegistry;
import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;

public class LearnerRegistry extends AbstractRegistry<AbstractActiveLearner> {

	public LearnerRegistry(final EventBus pEventBus) {
		super(pEventBus);
	}

	@Override
	public void rcvInitialSetupEvent(final InitialSetupEvent e) {
		this.activateListener(e.getLearnerID());
	}

	public void register(final AbstractActiveLearner aLearner) {
		aLearner.setEventBus(this.getEventBus());
		this.register(aLearner.getIdentifier(), aLearner);
	}

}
