package de.upb.wever.util.latex;

public class Paragraph implements ITeXComponent {

	private final String text;

	public Paragraph(final String pText) {
		this.text = pText;
	}

	@Override
	public String toString() {
		return "\\FloatBarrier\n" + this.text + "\\\\[\\baselineskip]";
	}

}
