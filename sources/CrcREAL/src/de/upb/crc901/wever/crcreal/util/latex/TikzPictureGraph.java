package de.upb.wever.util.latex;

import java.util.LinkedList;
import java.util.List;

public class TikzPictureGraph implements TikzPictureComponent {

	private final List<TikzPictureGraphPlot> plotList;
	private String xLabel = "Generations";
	private String yLabel = "Fitness";

	public void setXLabel(final String pLabel) {
		this.xLabel = pLabel;
	}

	public void setYLabel(final String pLabel) {
		this.yLabel = pLabel;
	}

	public TikzPictureGraph() {
		this.plotList = new LinkedList<>();
	}

	public void addPlot(final TikzPictureGraphPlot pPlotToAdd) {
		this.plotList.add(pPlotToAdd);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\\begin{axis}[height=9cm, ymin=0, ymax=1, width=9cm, grid=major, xlabel=" + this.xLabel + ", ylabel=" + this.yLabel + "]");

		for (final TikzPictureGraphPlot plot : this.plotList) {
			sb.append(plot + "\n");
		}

		sb.append("\\end{axis}");
		return sb.toString();
	}

}
