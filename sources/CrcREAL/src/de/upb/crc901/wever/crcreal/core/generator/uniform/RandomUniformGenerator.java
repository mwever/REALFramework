package de.upb.crc901.wever.crcreal.core.generator.uniform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.generator.AbstractGenerator;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.events.AnnounceTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.GenerateTargetModelEvent;
import de.upb.crc901.wever.crcreal.util.CandidateModelUtil;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class RandomUniformGenerator extends AbstractGenerator {

	private final static Logger LOGGER = LoggerFactory.getLogger(RandomUniformGenerator.class);

	public final static String ID = "crc.real.generator.randomUniformGenerator";

	public RandomUniformGenerator(final IRandomGenerator pPRG) {
		super(ID, pPRG);
	}

	@Override
	public void rcvGenerateChallengeEvent(final GenerateTargetModelEvent e) {
		super.rcvGenerateChallengeEvent(e);
		LOGGER.debug("Received generate challenge event");
		final FiniteAutomaton targetModel = CandidateModelUtil.generateRandomAutomatonWithNumberOfStates(e.getNumberOfStates(), e.getAlphabet(), this.getPRG());
		this.getEventBus().post(new AnnounceTargetModelEvent(targetModel));
	}

}
