package de.tarent.aa.veraweb.cucumber.utils;

/**
 * Utility class for name conversions.
 * 
 * @author Michael Kutz, tarent Solutions GmbH, 06.02.2013
 */
public final class NameUtil {

	/** 
	 * Private default constructor since this is a utility class.
	 */
	private NameUtil() {
	}

	/**
	 * Converts the given {@code name} to a Enum constant name by replacing special characters with simple ASCII and
	 * white space as well as minus with underscores.
	 * 
	 * @param name
	 *            the name that should be converted.
	 * @return the Enum name for the given {@code name}.
	 */
	public static String nameToEnumName(String name) {
		return name.toUpperCase().replaceAll("[\\s\\-]", "_").replaceAll("ß", "SS").replaceAll("Ä", "AE")
				.replaceAll("Ü", "UE").replaceAll("Ö", "OE");
	}
}
