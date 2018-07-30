package de.upb.crc901.wever.crcreal.core.learner.objective.model;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;

public class FMeasureObjective extends AbstractModelObjective {
	
	public final static String ID = "crc.real.objective.model.fmeasure";

	@Override
	public double evaluate(TrainingSet pTrainingData, FiniteAutomaton pCandidateModel) {
		int tp = 0;
		int tn = 0;
		int fn = 0;
		int fp = 0;
		
		for(TrainingExample ex : pTrainingData) {
			EWordLabel predictedLabel = pCandidateModel.execute(ex.getWord()).getLabel();
			
			if(predictedLabel == ex.getLabel()) {
				if(predictedLabel == EWordLabel.ACCEPTING) {
					tp++;
				} else {
					tn++;
				}
			} else {
				if(predictedLabel == EWordLabel.ACCEPTING) {
					fp++;
				} else {
					fn++;
				}
			}
		}
		
		double precision = (double) tp / (tp + fp);
		double recall = (double) tp / (tp + fn);
		
		double beta = 1.0;
		double fbetaScore = (1 + Math.pow(beta, 2)) * ((precision * recall) / (Math.pow(beta, 2) * precision + recall));
		
		return 1-fbetaScore;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return "FMeasure";
	}

	@Override
	public AbstractObjective newInstance() {
		return new FMeasureObjective();
	}

}
