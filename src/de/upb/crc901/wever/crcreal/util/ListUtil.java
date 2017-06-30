package de.upb.crc901.wever.crcreal.util;

import java.util.LinkedList;
import java.util.List;

public class ListUtil {

	/**
	 * On input a string of the form a,b,c returns a list splitting the string by the commas.
	 *
	 * @param pInputString
	 *            String which may contain commas as a delimiter.
	 *
	 * @return List of the elements after splitting the string using ',' as a delimiter
	 */
	public static List<String> commaStringToList(final String pInputString) {
		if (pInputString == null || pInputString.equals("")) {
			return new LinkedList<>();
		}
		final String[] stringSplit = pInputString.split(",");

		final List<String> stringList = new LinkedList<>();
		for (final String stringSplitElement : stringSplit) {
			stringList.add(stringSplitElement);
		}
		return stringList;
	}

}
