package de.upb.crc901.wever.crcreal.core.validator.sample;

import java.util.List;

import de.upb.crc901.wever.crcreal.core.validator.AbstractValidator;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.events.AnnounceTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class SampleValidator extends AbstractValidator {

	private static final String ID = "crc.real.validator.sample";

	private TrainingSet testData;

	public SampleValidator(final IRandomGenerator pPRG) {
		super(ID, pPRG);
	}

	public double validate(final FiniteAutomaton targetDFA, final FiniteAutomaton candidateDFA) {
		return 0.0;
	}

	@Override
	public void validate(final List<CandidateModel> modelList) {

	}

	@Override
	public void rcvAnnounceTargetModelEvent(final AnnounceTargetModelEvent e) {
		super.rcvAnnounceTargetModelEvent(e);
		this.testData = null;
		this.createTestData();
	}

	public void createTestData() {

	}

}
