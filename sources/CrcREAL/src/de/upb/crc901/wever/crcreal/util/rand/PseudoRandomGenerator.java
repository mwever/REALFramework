package de.upb.crc901.wever.crcreal.util.rand;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import de.upb.crc901.wever.crcreal.model.word.EWordLabel;

public class PseudoRandomGenerator implements IRandomGenerator {

	private long seed;
	private RandomGenerator rand;

	public PseudoRandomGenerator(final long pSeed) {
		this.seed = pSeed;
		this.rand = new JDKRandomGenerator();
		this.rand.setSeed(pSeed);
	}

	@Override
	public int nextStateForTransitionUniform(final int numberOfStates) {
		return this.rand.nextInt(numberOfStates);
	}

	@Override
	public int nextInputSymbol(final int alphabetSize) {
		return this.rand.nextInt(alphabetSize);
	}

	/**
	 * @return Returns the seed this PRG has been initialized with.
	 */
	public long getSeed() {
		return this.seed;
	}

	@Override
	public EWordLabel nextStateLabel() {
		if (this.rand.nextBoolean()) {
			return EWordLabel.ACCEPTING;
		} else {
			return EWordLabel.REJECTING;
		}
	}

	@Override
	public int nextInputSequenceLength(final int maxSequenceLength, final int alphabetSize) {
		final List<Long> borders = new LinkedList<>();
		long sum = 0;
		for (int i = 0; i <= maxSequenceLength; i++) {
			sum += Math.pow(alphabetSize, i);
			borders.add(Long.valueOf(sum));
		}

		long elementIndex = this.rand.nextLong() % (sum + 1);
		if (elementIndex < 0) {
			elementIndex *= (-1);
		}

		long borderSum = 0;
		for (final Long borderNumber : borders) {
			borderSum += borderNumber;

			if (elementIndex <= borderSum) {
				return borders.indexOf(borderNumber);
			}
		}
		return -1;
	}

	@Override
	public int nextInteger(final int upperBound) {
		return this.rand.nextInt(upperBound);
	}

	@Override
	public long nextSeed() {
		final long nextSeed = this.rand.nextLong();
		return nextSeed;
	}

	@Override
	public void setSeed(final long pSeed) {
		this.seed = pSeed;
		this.reset();
	}

	@Override
	public void reset() {
		this.rand = new JDKRandomGenerator();
		this.rand.setSeed(this.seed);
	}

	public boolean nextHonestyDecision(final double honesty) {
		final double randomDouble = this.rand.nextDouble();
		if (randomDouble <= honesty) {
			return true;
		} else {
			return false;
		}
	}
}
