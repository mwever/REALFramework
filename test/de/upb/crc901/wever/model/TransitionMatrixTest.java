package de.upb.crc901.wever.model;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upb.crc901.wever.crcreal.model.automaton.TransitionFunction;

public class TransitionMatrixTest {

	private int[][] transitionArray = { { 0, 1, 2, 3 }, { 1, 2, 3, 0 }, { 2, 3, 0, 1 }, { 3, 0, 1, 2 } };

	@Test
	public void testReadingFromIntArray() {
		TransitionFunction matrix = TransitionFunction.readFromArray(transitionArray);

		for (int startingState = 0; startingState < transitionArray.length; startingState++) {
			for (int inputValue = 0; inputValue < transitionArray[startingState].length; inputValue++) {
				assertEquals(transitionArray[startingState][inputValue],
						matrix.getNextState(startingState, inputValue));
			}
		}
	}
	
	@Test
	public void testExportingIntArray() {
		TransitionFunction matrix = TransitionFunction.readFromArray(transitionArray);
		assertArrayEquals(transitionArray, TransitionFunction.toMultIntArray(matrix));
	}

}
