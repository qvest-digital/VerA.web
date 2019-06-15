package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
