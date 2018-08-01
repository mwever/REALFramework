package de.upb.crc901.wever.crcreal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class CandidateModelUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(CandidateModelUtil.class);

	/**
	 * This method generates a DFA target model that shall be inferred by the
	 * learning algorithm.
	 *
	 * @return Returns a DeterministicFiniteAutomaton representing the target model.
	 */
	public static FiniteAutomaton generateRandomAutomatonWithNumberOfStates(final int pNumberOfStates,
			final Alphabet alphabet, final IRandomGenerator prg) {
		FiniteAutomaton generatedAutomaton = null;
		try {
			int numberOfIterationsNeeded = 0;
			do {
				numberOfIterationsNeeded++;

				final int[][] transitionMatrixArray = new int[pNumberOfStates][alphabet.size()];
				for (int fromState = 0; fromState < pNumberOfStates; fromState++) {
					for (int symbol = 0; symbol < alphabet.size(); symbol++) {
						transitionMatrixArray[fromState][symbol] = prg.nextStateForTransitionUniform(pNumberOfStates);
					}
				}

				// create target model candidate and check whether if every state is reachable
				// then, check whether the automaton can be minimized. if so, we need to search
				// for a new one
				generatedAutomaton = new FiniteAutomaton(pNumberOfStates, transitionMatrixArray, alphabet);
				if (!generatedAutomaton.allStatesReachable()) {
					generatedAutomaton = null;
				} else {
					final Map<Integer, EWordLabel> labelMap = new HashMap<>();
					IntStream.range(0, pNumberOfStates).forEach(x -> {
						final EWordLabel label = prg.nextStateLabel();
						labelMap.put(x, label);
					});
					generatedAutomaton.setLabeling(labelMap);
				}
			} while (generatedAutomaton == null);

			LOGGER.trace("Needed " + numberOfIterationsNeeded + " many iterations to produce a complete DFA with "
					+ pNumberOfStates + " states.");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return generatedAutomaton;
	}

	public static FiniteAutomaton generateRandomAutomaton(final int pNumberOfStates, final Alphabet alphabet,
			final IRandomGenerator prg) {
		FiniteAutomaton generatedAutomaton = null;
		final int[][] transitionMatrixArray = new int[pNumberOfStates][alphabet.size()];
		for (int fromState = 0; fromState < pNumberOfStates; fromState++) {
			for (int symbol = 0; symbol < alphabet.size(); symbol++) {
				transitionMatrixArray[fromState][symbol] = prg.nextStateForTransitionUniform(pNumberOfStates);
			}
		}
		generatedAutomaton = new FiniteAutomaton(pNumberOfStates, transitionMatrixArray, alphabet);
		return generatedAutomaton;
	}

}
