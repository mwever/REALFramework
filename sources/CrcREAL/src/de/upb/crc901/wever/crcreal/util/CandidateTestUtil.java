package de.upb.crc901.wever.crcreal.util;

import java.util.List;
import java.util.stream.IntStream;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.word.CandidateTest;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class CandidateTestUtil {

	public static CandidateTest generateRandomCandidateTest(final IRandomGenerator pPRG, final List<AbstractTestObjective> pObjectives, final Alphabet pAlphabet,
			final int pMaxLength) {
		final int length = pPRG.nextInputSequenceLength(pMaxLength, pAlphabet.size());
		final Word randomWord = new Word();
		IntStream.range(0, pMaxLength).forEach(x -> {
			randomWord.add(pAlphabet.get(pPRG.nextInteger(pAlphabet.size())));
		});
		return new CandidateTest(pObjectives, randomWord, length);
	}

	public static CandidateTest generateSemiUniformRandomCandidateTest(final IRandomGenerator pPRG, final List<AbstractTestObjective> pObjectives, final Alphabet pAlphabet,
			final int pMaxLength) {
		final int length = pPRG.nextInteger(pMaxLength + 1);
		final Word randomWord = new Word();
		IntStream.range(0, pMaxLength).forEach(x -> {
			randomWord.add(pAlphabet.get(pPRG.nextInteger(pAlphabet.size())));
		});
		return new CandidateTest(pObjectives, randomWord, length);
	}
}
