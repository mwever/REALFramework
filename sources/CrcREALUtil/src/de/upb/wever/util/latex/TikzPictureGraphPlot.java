package de.upb.wever.util.latex;

import java.util.LinkedList;
import java.util.List;

public class TikzPictureGraphPlot {

	private final String legendEntry;
	private String color = "blue";
	private boolean dashed = false;
	private final String mark = "x";

	List<TikzPictureGraphCoordinate> coordinateList;

	public TikzPictureGraphPlot(final String legendEntry) {
		this.legendEntry = legendEntry;
		this.coordinateList = new LinkedList<>();
	}

	public void setColor(final String newColor) {
		this.color = newColor;
	}

	public String getColor() {
		return this.color;
	}

	public void setDashed(final boolean pDashed) {
		this.dashed = pDashed;
	}

	public void addCoordinate(final TikzPictureGraphCoordinate coord) {
		this.coordinateList.add(coord);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("\\addplot");
		sb.append("[mark=" + this.mark + "," + this.color);
		if (this.dashed) {
			sb.append(",dashed");
		}
		sb.append("]");
		sb.append(" coordinates {\n");

		this.coordinateList.stream().map(x -> x + "\n").forEach(sb::append);

		sb.append("};\n");
		// sb.append("\\addlegendentry{" + this.legendEntry + "}\n");

		return sb.toString();
	}

}
