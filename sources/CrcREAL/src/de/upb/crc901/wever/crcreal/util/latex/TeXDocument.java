package de.upb.wever.util.latex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TeXDocument {

	private static final String DOCUMENT_CLASS = "article";
	private static final String[] PACKAGES = { "placeins", "pgfplots" };

	private final List<String> includeList = new LinkedList<>();
	private final List<ITeXComponent> componentList = new LinkedList<>();

	public void addToIncludeList(final String include) {
		this.componentList.add(new Input(include));
	}

	public void addITeXComponent(final ITeXComponent pComponent) {
		this.componentList.add(pComponent);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("\\documentclass{" + DOCUMENT_CLASS + "}\n\n");

		for (final String pkg : PACKAGES) {
			sb.append("\\usepackage{" + pkg + "}\n");
		}

		sb.append("\\pgfplotsset{compat=newest}\n");
		sb.append("\\pagestyle{empty}\n\n");

		sb.append("\\begin{document}\n");

		for (final ITeXComponent component : this.componentList) {
			sb.append(component + "\n");
		}

		sb.append("\n");
		sb.append("\\end{document}");
		return sb.toString();
	}

	public void writeToFile(final String fileName) {
		final File outputFile = new File(fileName);
		outputFile.getParentFile().mkdirs();
		try (final FileWriter writer = new FileWriter(outputFile)) {
			writer.write(this + "\n");
			writer.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
