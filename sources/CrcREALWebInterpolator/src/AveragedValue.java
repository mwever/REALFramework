
public class AveragedValue {
	private final int samples;
	private final double averageValue;

	public AveragedValue(final int pSamples, final double pAverageValue) {
		this.samples = pSamples;
		this.averageValue = pAverageValue;
	}

	public int getSamples() {
		return this.samples;
	}

	public double getAverageValue() {
		return this.averageValue;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<#" + this.samples + ", " + this.averageValue + ">");
		return sb.toString();
	}

}
