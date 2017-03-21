package de.upb.wever.util.latex;

public class TikzPictureGraphCoordinate {

	private final double x;
	private final double y;

	public TikzPictureGraphCoordinate(final double pX, final double pY) {
		this.x = pX;
		this.y = pY;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(" + this.x + "," + this.y + ")");
		return sb.toString();
	}
}
