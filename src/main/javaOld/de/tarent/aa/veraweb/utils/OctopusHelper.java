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
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;

/**
 * Diese Klasse stellt statische Hilfsmethoden für Octopus-spezifische Aufgaben
 * zur Verfügung.
 *
 * @author mikel
 */
public class OctopusHelper {
    /** Log4J Logger */
    private static final Logger logger = LogManager.getLogger(OctopusHelper.class);

    /**
     * Gibt einen angeforderten Dateinamen zurück.
     *
     * module/task/filename.txt?parameter
     * filename.txt
     *
     * @param octopusContext  OctopusContext
     * @param ex    Erweiterung des gesuchten Dateinamens.-
     * @param def   Default Wert.
     * @return Dateiname
     */
    public static String getFilename(OctopusContext octopusContext, String ex, String def) {
        String file = octopusContext.requestAsString("pathInfo");
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
     * <code>encodeString(octopusContext, in, "default", "default");</code>
     *
     * @see #encodeString(OctopusContext, String, String, String)
     * @param octopusContext FIXME
     * @param in Original String.
     * @return Encodeter String.
     */
    public static String encodeString(OctopusContext octopusContext, String in) {
        return encodeString(octopusContext, in, "default", "default");
    }

    /**
     * Transformiert einen String in einen anderen Zeichensatz.
     * Wenn als Zeichensatz Parameter "default" übergeben wird, wird dieser
     * aus der Modul-Konfiguration geladen. Wenn stattdessen null übergeben
     * wird, wird der VM-Standard verwendet.
     *
     * <code>new String(in.getBytes(encin), encout);</code>
     *
     * @param octopusContext FIXME
     * @param in Original String.
     * @param encin FIXME
     * @param encout FIXME
     * @return Encodeter String.
     */
    public static String encodeString(OctopusContext octopusContext, String in, String encin, String encout) {
        if (encin != null && encin.equals("default"))
            encin = octopusContext.moduleConfig().getParam("encoding.input");
        if (encout != null && encout.equals("default"))
            encout = octopusContext.moduleConfig().getParam("encoding.output");
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
