package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class ModelSizeObjective extends AbstractModelObjective {

	public final static String ID = "crc.real.objective.model.modelsize";

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public double evaluate(final TrainingSet trainingData, final FiniteAutomaton automaton) {
		return automaton.size();
	}

	@Override
	public String getLabel() {
		return "Model Size";
	}

	@Override
	public AbstractObjective newInstance() {
		return new ModelSizeObjective();
	}

}
