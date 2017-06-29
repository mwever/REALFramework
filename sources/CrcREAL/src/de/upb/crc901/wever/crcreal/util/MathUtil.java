package de.upb.crc901.wever.crcreal.util;

public class MathUtil {

	public static double round(final double numberToRound, final int decimalPlaces) {
		final int roundedNumber = (int) Math.round(numberToRound * Math.pow(10, decimalPlaces));
		return roundedNumber / Math.pow(10, decimalPlaces);
	}

}
