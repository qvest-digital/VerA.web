/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
