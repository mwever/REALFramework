package de.upb.crc901.wever.crcreal.model.word;

/**
 * This enum collects all possible labels of sequences. Initially, there are only accepting and rejecting for binary classifying automata. UNKNOWN is used to explicitly state that
 * there is no label yet.
 *
 * @author wever
 */
public enum EWordLabel {
	ACCEPTING, REJECTING, NONE;
}
