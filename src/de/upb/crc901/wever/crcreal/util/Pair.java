package de.upb.crc901.wever.crcreal.util;

public class Pair<X, Y> {

	private final X x;
	private final Y y;

	public Pair(final X pX, final Y pY) {
		this.x = pX;
		this.y = pY;
	}

	public X getX() {
		return this.x;
	}

	public Y getY() {
		return this.y;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("<");
		sb.append(this.x.toString());
		sb.append(", ");
		sb.append(this.y.toString());
		sb.append(">");

		return sb.toString();
	}
}
