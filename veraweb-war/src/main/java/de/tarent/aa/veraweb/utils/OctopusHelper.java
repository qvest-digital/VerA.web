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
 */
package de.tarent.aa.veraweb.utils;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse stellt statische Hilfsmethoden für Octopus-spezifische Aufgaben
 * zur Verfügung. 
 * 
 * @author mikel
 */
public class OctopusHelper {
	/** Log4J Logger */
	private static final Logger logger = Logger.getLogger(OctopusHelper.class);

	/**
	 * Gibt einen angeforderten Dateinamen zurück.
	 * 
	 * module/task/filename.txt?parameter
	 * filename.txt
	 * 
	 * @param cntx  OctopusContext
	 * @param ex    Erweiterung des gesuchten Dateinamens.-
	 * @param def   Default Wert.
	 * @return Dateiname
	 */
	public static String getFilename(OctopusContext cntx, String ex, String def) {
		String file = cntx.requestAsString("pathInfo");
		ex = '.' + ex;
		if (file != null && file.indexOf(ex) != -1) {
			int s = file.lastIndexOf('/', file.indexOf(ex));
			if (s == -1) s = 0;
			int e = file.indexOf('?', s);
			if (e == -1) e = file.length();
			return file.substring(s, e);
		}
		return def;
	}

	/**
	 * Transformiert einen String in einen anderen Zeichensatz,
	 * beachtet dabei die beiden Parameter "encoding.input" und
	 * "encoding.output" aus der Modulconfiguration.
	 * 
	 * <code>encodeString(cntx, in, "default", "default");</code>
	 * 
	 * @see #encodeString(OctopusContext, String, String, String)
	 * @param cntx
	 * @param in Original String.
	 * @return Encodeter String.
	 */
	public static String encodeString(OctopusContext cntx, String in) {
		return encodeString(cntx, in, "default", "default");
	}

	/**
	 * Transformiert einen String in einen anderen Zeichensatz.
	 * Wenn als Zeichensatz Parameter "default" übergeben wird, wird dieser
	 * aus der Modul-Konfiguration geladen. Wenn stattdessen null übergeben
	 * wird, wird der VM-Standard verwendet.
	 * 
	 * <code>new String(in.getBytes(encin), encout);</code>
	 * 
	 * @param cntx
	 * @param in Original String.
	 * @param encin
	 * @param encout
	 * @return Encodeter String.
	 */
	public static String encodeString(OctopusContext cntx, String in, String encin, String encout) {
		if (encin != null && encin.equals("default"))
			encin = cntx.moduleConfig().getParam("encoding.input");
		if (encout != null && encout.equals("default"))
			encout = cntx.moduleConfig().getParam("encoding.output");
		try {
			if (encin != null && encout != null)
				return new String(in.getBytes(encin), encout);
			else if (encin != null) // encout == null
				return new String(in.getBytes(encin));
			else if (encout != null) // encin == null
				return new String(in.getBytes(), encout);
			else
				return in;
		} catch (UnsupportedEncodingException e) {
			logger.warn("Zeichenkette konnte nicht von '" + encin +
					"' nach '" + encout + "' konvertiert werden.", e);
			return in;
		}
	}
}
