package de.tarent.aa.veraweb.utils;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.io.UnsupportedEncodingException;

/**
 * Diese Klasse stellt statische Hilfsmethoden für Octopus-spezifische Aufgaben
 * zur Verfügung.
 *
 * @author mikel
 */
@Log4j2
public class OctopusHelper {
    /**
     * Gibt einen angeforderten Dateinamen zurück.
     *
     * module/task/filename.txt?parameter
     * filename.txt
     *
     * @param octopusContext OctopusContext
     * @param ex             Erweiterung des gesuchten Dateinamens.-
     * @param def            Default Wert.
     * @return Dateiname
     */
    public static String getFilename(OctopusContext octopusContext, String ex, String def) {
        String file = octopusContext.requestAsString("pathInfo");
        ex = '.' + ex;
        if (file != null && file.indexOf(ex) != -1) {
            int s = file.lastIndexOf('/', file.indexOf(ex));
            if (s == -1) {
                s = 0;
            }
            int e = file.indexOf('?', s);
            if (e == -1) {
                e = file.length();
            }
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
     * @param octopusContext FIXME
     * @param in             Original String.
     * @return Encodeter String.
     * @see #encodeString(OctopusContext, String, String, String)
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
     * @param in             Original String.
     * @param encin          FIXME
     * @param encout         FIXME
     * @return Encodeter String.
     */
    public static String encodeString(OctopusContext octopusContext, String in, String encin, String encout) {
        if (encin != null && encin.equals("default")) {
            encin = octopusContext.moduleConfig().getParam("encoding.input");
        }
        if (encout != null && encout.equals("default")) {
            encout = octopusContext.moduleConfig().getParam("encoding.output");
        }
        try {
            if (encin != null && encout != null) {
                return new String(in.getBytes(encin), encout);
            } else if (encin != null) // encout == null
            {
                return new String(in.getBytes(encin));
            } else if (encout != null) // encin == null
            {
                return new String(in.getBytes(), encout);
            } else {
                return in;
            }
        } catch (UnsupportedEncodingException e) {
            logger.warn("Zeichenkette konnte nicht von '" + encin +
              "' nach '" + encout + "' konvertiert werden.", e);
            return in;
        }
    }
}
