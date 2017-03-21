package de.upb.crc901.wever.crcreal.util.rand;

import de.upb.crc901.wever.crcreal.model.word.EWordLabel;

public interface IRandomGenerator {

	public int nextStateForTransitionUniform(final int numberOfStates);

	public int nextInputSymbol(int alphabetSize);

	public int nextInputSequenceLength(int maxSequenceLength, int alphabetSize);

	public EWordLabel nextStateLabel();

	public int nextInteger(int upperBound);

	public long nextSeed();

	public void reset();

	public void setSeed(long challengerSeed);

	public boolean nextHonestyDecision(double honesty);

}
