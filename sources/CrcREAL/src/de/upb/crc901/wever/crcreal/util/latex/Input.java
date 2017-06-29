package de.upb.wever.util.latex;

public class Input implements ITeXComponent {

	private final String inputString;

	public Input(final String pInputString) {
		this.inputString = pInputString;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\\input{" + this.inputString + "}");
		return sb.toString();
	}

}
