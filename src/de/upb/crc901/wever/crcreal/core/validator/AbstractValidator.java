package de.upb.crc901.wever.crcreal.core.validator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.AbstractREALEntity;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.events.AnnounceTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.LearnerRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.ModelPopulationEvaluationEvent;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractValidator extends AbstractREALEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractValidator.class);

	private FiniteAutomaton targetModel;
	private TrainingSet trainingData;

	protected AbstractValidator(final String pIdentifier, final IRandomGenerator pPRG) {
		super(pIdentifier, pPRG);
	}

	@Subscribe
	public void rcvAnnounceTargetModelEvent(final AnnounceTargetModelEvent e) {
		this.targetModel = e.getTargetModel();
	}

	@Subscribe
	public void rcvCurrentModelPopulationEvaluationEvent(final ModelPopulationEvaluationEvent e) {
	}

	@Subscribe
	public void rcvLearnerRequestEvent(final LearnerRequestEvent e) {
		LOGGER.debug("Received initial learning event");
		this.trainingData = new TrainingSet(e.getTrainingData());
	}

	protected FiniteAutomaton getTargetModel() {
		return this.targetModel;
	}

	protected TrainingSet getTrainingData() {
		return this.trainingData;
	}

	public abstract void validate(List<CandidateModel> modelList);
}
