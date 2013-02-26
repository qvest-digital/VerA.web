/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

/**
 * Diese Klasse enth�lt statische Hilfsmethoden f�r die Behandlung von
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
	 * f�r beliebige Datenstr�me: <code>application/octet-stream</code>
	 * 
	 * @param contentType Original Content-Type
	 * @return angepassten Content-Type
	 */
	static public String getContentType(String contentType) {
		return contentType;
//		return "application/octet-stream";
	}
}
