package de.upb.crc901.wever.crcreal.model.alphabet;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;

public class Alphabet extends LinkedList<InputSymbol> {

	private static final long serialVersionUID = 7159057030961300703L;

	public Alphabet() {
	}

	public Alphabet(final Set<InputSymbol> setOfAllInputs) {
		super(setOfAllInputs);
	}

	public void sort() {
		Collections.sort(this);
	}

	public static Alphabet readFromTrainingSet(final TrainingSet pTrainingData) {
		final Set<InputSymbol> usedInputSymbols = new HashSet<>();
		pTrainingData.stream().forEach(x -> x.getWord().stream().forEach(usedInputSymbols::add));
		return new Alphabet(usedInputSymbols);
	}
}
