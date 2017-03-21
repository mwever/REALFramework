package de.upb.crc901.wever.crcreal.model.events;

import java.util.List;

import de.upb.crc901.wever.crcreal.model.word.Word;

public class OracleRequestEvent {

	private final List<Word> requestedWords;

	public OracleRequestEvent(final List<Word> pRequestedWords) {
		this.requestedWords = pRequestedWords;
	}

	public List<Word> getRequestedWord() {
		return this.requestedWords;
	}

}
