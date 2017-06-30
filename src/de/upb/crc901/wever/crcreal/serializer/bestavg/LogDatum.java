package de.upb.crc901.wever.crcreal.serializer.bestavg;

public class LogDatum {

	private final String file;
	private final String log;

	public LogDatum(final String pFile, final String pLog) {
		this.file = pFile;
		this.log = pLog;
	}

	public String getFile() {
		return this.file;
	}

	public String getLog() {
		return this.log;
	}

}
