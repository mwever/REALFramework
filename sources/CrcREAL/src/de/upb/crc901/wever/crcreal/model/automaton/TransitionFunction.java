package de.upb.crc901.wever.crcreal.model.automaton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a transition function of a DFA.
 *
 * @author wever
 */
public class TransitionFunction {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransitionFunction.class);

	private final Map<Integer, Map<Integer, Integer>> transitionMap;
	private final int[][] transitionArray;

	/**
	 * Creates a transition function according to the transition map. The transition map is used as a look-up table.
	 *
	 * @param pTransitionMap
	 *            Look-up table for state transitions.
	 */
	public TransitionFunction(final Map<Integer, Map<Integer, Integer>> pTransitionMap, final int[][] pTransitionArray) {
		this.transitionMap = pTransitionMap;
		this.transitionArray = pTransitionArray;
	}

	/**
	 * Creates a transition function according to the transition map. The transition map is used as a look-up table.
	 *
	 * @param pTransitionMap
	 *            Look-up table for state transitions.
	 */
	public TransitionFunction(final Map<Integer, Map<Integer, Integer>> pTransitionMap) {
		this.transitionMap = pTransitionMap;
		this.transitionArray = TransitionFunction.toMultIntArray(this);
	}

	public TransitionFunction(final TransitionFunction transitionMatrix) {
		this.transitionArray = TransitionFunction.toMultIntArray(transitionMatrix);
		this.transitionMap = new HashMap<>();
		putToTransitionMap(this.transitionMap, this.transitionArray);
	}

	/**
	 * Returns the number of the next state according to the starting state and the input value.
	 *
	 * @param pStartingState
	 *            The current state to read the input from.
	 * @param pInputValue
	 *            Input read to change into the next state.
	 * @return Returns the new state computed from the starting state on reading the input value.
	 */
	public int getNextState(final int pStartingState, final int pInputValue) {
		return this.transitionMap.get(pStartingState).get(pInputValue);
	}

	/**
	 * Converts an int array into a transition function.
	 *
	 * @param pTransitionArray
	 *            Multi-dimensional int array representing the transition matrix.
	 * @return Returns a new object of a transition matrix with the behavior specified in the given array.
	 */
	public static TransitionFunction readFromArray(final int[][] pTransitionArray) {
		final Map<Integer, Map<Integer, Integer>> transitionMap = new HashMap<>();
		putToTransitionMap(transitionMap, pTransitionArray);
		return new TransitionFunction(transitionMap, pTransitionArray);
	}

	private static void putToTransitionMap(final Map<Integer, Map<Integer, Integer>> pTransitionMap, final int[][] pTransitionArray) {
		for (int startingState = 0; startingState < pTransitionArray.length; startingState++) {
			final Map<Integer, Integer> xTransitionMap = new HashMap<>();
			pTransitionMap.put(startingState, xTransitionMap);

			for (int input = 0; input < pTransitionArray[startingState].length; input++) {
				xTransitionMap.put(input, pTransitionArray[startingState][input]);
			}
		}
	}

	/**
	 * Converts a transition function object into a multi-dimensional int array.
	 *
	 * @param pTransitionMatrix
	 *            The transition matrix to be converted to int array.
	 * @return Multi-dimensional int array representing a transition matrix.
	 */
	public static int[][] toMultIntArray(final TransitionFunction pTransitionMatrix) {
		final int maxInput = pTransitionMatrix.transitionMap.values().stream().map(x -> x.size()).reduce(Integer::max).get();

		final int[][] transitionArray = new int[pTransitionMatrix.transitionMap.size()][maxInput];

		for (final Integer startingState : pTransitionMatrix.transitionMap.keySet()) {
			for (final Integer inputValue : pTransitionMatrix.transitionMap.get(startingState).keySet()) {
				transitionArray[startingState][inputValue] = pTransitionMatrix.transitionMap.get(startingState).get(inputValue);
			}
		}

		return transitionArray;
	}

	public void updateTransition(final int stateIndex, final int inputSymbol, final int updateValue) {
		this.transitionMap.get(stateIndex).put(inputSymbol, updateValue);
	}

	public Set<Integer> getInputsForStateSwitch(final Integer pStartState, final Integer pTargetState) {
		return this.transitionMap.get(pStartState).keySet().stream().filter(x -> this.transitionMap.get(pStartState).get(x) == pTargetState).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final int[][] transitionArray = toMultIntArray(this);

		int state = 0;

		sb.append("\t");
		for (int i = 0; i < transitionArray[0].length; i++) {
			sb.append("| " + i + "\t");
		}
		sb.append("\n");

		for (int i = 0; i <= transitionArray[0].length; i++) {
			sb.append("========");
		}
		sb.append("\n");

		for (final int[] y : transitionArray) {
			sb.append(state + "\t");
			for (final int value : y) {
				sb.append("| " + value + "\t");
			}
			sb.append("\n");
			state++;
		}

		return sb.toString();
	}

	public String getTextualRepresentation() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final int[] line : this.transitionArray) {
			for (final int entry : line) {
				if (first) {
					first = false;
				} else {
					// sb.append(", ");
					sb.append("");
				}
				sb.append(entry);
			}
		}
		return sb.toString();
	}
}
