package de.upb.crc901.wever.crcreal.util;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.alphabet.InputSymbolFactory;

public class AlphabetUtil {

	public static Alphabet getAlphabetOfSize(final int sizeOfAlphabet) {
		// create a new input factory for distinct set of inputs => alphabet
		// get the alphabet implied by the training data
		final InputSymbolFactory inputFactory = new InputSymbolFactory();
		for (int i = 0; i < sizeOfAlphabet; i++) {
			inputFactory.getInput(i + "");
		}
		return new Alphabet(inputFactory.getSetOfAllInputs());
	}
}
