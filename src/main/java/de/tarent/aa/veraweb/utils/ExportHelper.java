/*
 * $Id: ExportHelper.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
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
