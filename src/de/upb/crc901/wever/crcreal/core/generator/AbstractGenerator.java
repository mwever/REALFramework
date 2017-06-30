package de.upb.crc901.wever.crcreal.core.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.AbstractREALEntity;
import de.upb.crc901.wever.crcreal.model.events.GenerateTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.SeedInitializationEvent;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractGenerator extends AbstractREALEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenerator.class);

	protected AbstractGenerator(final String pIdentifier, final IRandomGenerator pPRG) {
		super(pIdentifier, pPRG);
	}

	@Subscribe
	public void rcvSeedInitializationEvent(final SeedInitializationEvent e) {
		this.getPRG().setSeed(e.getChallengerSeed());
	}

	@Subscribe
	public void rcvGenerateChallengeEvent(final GenerateTargetModelEvent e) {
		LOGGER.trace("Received GenerateChallengeEvent");
	}

}
