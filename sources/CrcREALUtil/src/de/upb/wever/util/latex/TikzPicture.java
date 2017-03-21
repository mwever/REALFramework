package de.upb.wever.util.latex;

import java.util.LinkedList;
import java.util.List;

public class TikzPicture {

	private final List<TikzPictureComponent> tikzPictureComponentList = new LinkedList<>();
	private String captionString = "Picture";

	public void addComponent(final TikzPictureComponent pComponent) {
		this.tikzPictureComponentList.add(pComponent);
	}

	public void setCaptionString(final String pCaptionString) {
		this.captionString = pCaptionString;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("\\begin{figure}[ht]\n");
		sb.append("\\begin{tikzpicture}\n");

		for (final TikzPictureComponent component : this.tikzPictureComponentList) {
			sb.append(component + "\n");
		}

		sb.append("\\end{tikzpicture}\n");

		sb.append("\\caption{" + this.captionString + "}");

		sb.append("\\end{figure}");

		return sb.toString();
	}

}
