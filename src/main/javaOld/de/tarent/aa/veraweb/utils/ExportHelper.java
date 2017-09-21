package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
/**
 * Diese Klasse enthält statische Hilfsmethoden für die Behandlung von
 * Export -Dateinamen und -Content-Typen. Diese werden hier zentral
 * "entstandardisiert" um sicherzustellen das diese vom Browser
 * als Download angeboten werden statt diese z.B. als Active-X-Controll
 * einzubinden.
 */
public class ExportHelper {
	/**
	 * Erweitert die Standard-Dateiendung um den Zusatz <code>.export</code>.
	 *
	 * @param extension Original Dateiendung
	 * @return angepasste Dateiendung
	 */
	static public String getExtension(String extension) {
		return extension;
//		return extension.endsWith(".export") ? extension : extension + ".export";
	}

	/**
	 * Erweitert den Standard-Dateinamen um den Zusatz <code>.export</code>.
	 *
	 * @param filename Original Dateiname
	 * @return angepassten Dateinamen
	 */
	static public String getFilename(String filename) {
		return filename;
//		return filename.endsWith(".export") ? filename : filename + ".export";
	}

	/**
	 * Ersetzt den Standard-Content-Type durch den allgemeinen Standard
	 * für beliebige Datenströme: <code>application/octet-stream</code>
	 *
	 * @param contentType Original Content-Type
	 * @return angepassten Content-Type
	 */
	static public String getContentType(String contentType) {
		return contentType;
//		return "application/octet-stream";
	}

	/**
	 * Anschrifttyp als P,G,S zurückgeben.
	 *
	 * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
	 * Muss auch in anderen Bereichen umgesetzt werden.
	 * Z.B. beim "Neu Laden" in Worker und Template.
	 */
	public static String getAddresstype(Integer addresstype) {
		if (addresstype == null) {
			return null;
		} else if (addresstype.intValue() == 2) {
			return "P";
		} else if (addresstype.intValue() == 3) {
			return "W";
		} else { // addresstype.intValue() == 1
			return "G";
		}
	}

	/**
	 * Zeichensatz als L,F1,F2 zurückgeben.
	 *
	 * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
	 * Muss auch in anderen Bereichen umgesetzt werden.
	 * Z.B. beim "Neu Laden" in Worker und Template.
	 */
	public static String getLocale(Integer locale) {
		if (locale == null) {
			return null;
		} else if (locale.intValue() == 2) {
			return "ZS1";
		} else if (locale.intValue() == 3) {
			return "ZS2";
		} else { // locale.intValue() == 1
			return "L";
		}
	}

	/** Diese Methode liefert eine String-Darstellung eines Gender-Werts */
	public static String getGender(String gender) {
		return gender;
	}
}
