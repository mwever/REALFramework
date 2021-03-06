package de.upb.crc901.wever.crcreal.model.events;

import java.util.Map;

import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class SupplierResultEvent {

	private final SupplierRequestEvent originalRequest;
	private final Map<Word, EWordLabel> labelForRequestedWord;

	public SupplierResultEvent(final SupplierRequestEvent pOriginalRequest,
			final Map<Word, EWordLabel> pLabelForRequestedWord) {
		this.originalRequest = pOriginalRequest;
		this.labelForRequestedWord = pLabelForRequestedWord;
	}

	public SupplierRequestEvent getOriginalRequest() {
		return this.originalRequest;
	}

	public Map<Word, EWordLabel> getLabelForRequestedWord() {
		return this.labelForRequestedWord;
	}

}
