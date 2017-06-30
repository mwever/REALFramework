package de.upb.crc901.wever.crcreal.core.oracle;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.AbstractREALEntity;
import de.upb.crc901.wever.crcreal.model.events.AnnounceTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.LearnerResultEvent;
import de.upb.crc901.wever.crcreal.model.events.OracleRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.StartTaskProcessingEvent;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractOracle extends AbstractREALEntity {

	protected AbstractOracle(final String pIdentifier, final IRandomGenerator pPRG) {
		super(pIdentifier, pPRG);
	}

	@Subscribe
	public void rcvStartTaskProcessingEvent(final StartTaskProcessingEvent e) {

	}

	@Subscribe
	public void rcvAnnounceTargetModelEvent(final AnnounceTargetModelEvent e) {

	}

	@Subscribe
	public void rcvOracleRequestEvent(final OracleRequestEvent e) {

	}

	@Subscribe
	public void rcvLearnerResultEvent(final LearnerResultEvent e) {

	}

}
