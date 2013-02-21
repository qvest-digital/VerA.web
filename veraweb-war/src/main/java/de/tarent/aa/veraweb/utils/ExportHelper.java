/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
 * 
 * Created on 07.02.2006
 */
package de.tarent.aa.veraweb.utils;

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
}
