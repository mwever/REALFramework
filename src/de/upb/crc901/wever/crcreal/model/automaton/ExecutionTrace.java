package de.upb.crc901.wever.crcreal.model.automaton;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class ExecutionTrace {
	
	private final Word word;
	private final List<Integer> stateTrace;
	private final EWordLabel label;
	
	public ExecutionTrace(final Word pWord, final List<Integer> pStateTrace, final EWordLabel pLabel) {
		this.word = pWord;
		this.stateTrace = pStateTrace;
		this.label = pLabel;
	}

	public Word getWord() {
		return word;
	}

	public List<Integer> getStateTrace() {
		return stateTrace;
	}

	public EWordLabel getLabel() {
		return label;
	}
	
}
