package de.upb.crc901.wever.crcreal.util.chunk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class RealChunk extends LinkedList<REALTask> {

	/**
	 *
	 */
	private static final long serialVersionUID = 5153903840141657334L;

	private int chunkID;

	public RealChunk() {
		super();
	}

	public RealChunk(final Collection<REALTask> tasksToAdd) {
		super(tasksToAdd);
	}

	public static RealChunk readFrom(final String pFileName) {
		final RealChunk readInChunk = new RealChunk();
		try (final BufferedReader br = new BufferedReader(new FileReader(pFileName))) {
			String line;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					first = false;
					readInChunk.setChunkID(Integer.valueOf(line));
				} else {
					readInChunk.add(REALTask.readFrom(line));
				}
			}

			readInChunk.stream().forEach(x -> x.setChunkID(readInChunk.chunkID));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return readInChunk;
	}

	public static RealChunk readFromString(final String pString) {
		final RealChunk readInChunk = new RealChunk();
		boolean first = true;

		for (final String line : pString.split("\n")) {
			if (first) {
				first = false;
				readInChunk.setChunkID(Integer.valueOf(line));
			} else {
				readInChunk.add(REALTask.readFrom(line));
			}
		}
		return readInChunk;
	}

	private void setChunkID(final Integer pChunkID) {
		this.chunkID = pChunkID;
	}

	public int getChunkID() {
		return this.chunkID;
	}

	public String getTextualRepresentation() {
		final StringBuilder sb = new StringBuilder();

		sb.append(this.getChunkID() + "\n");

		this.stream().map(x -> x.getStringRepresentation() + "\n").forEach(sb::append);

		return sb.toString();
	}

}
