package de.upb.crc901.wever.crcreal.core.learner.objective;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public abstract class AbstractModelObjective extends AbstractObjective {

	public abstract double evaluate(TrainingSet pTrainingData, FiniteAutomaton pCandidateModel);

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getID()).toHashCode();
	}

	@Override
	public boolean hasRealData() {
		return true;
	}

}
