package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.octopus.server.OctopusContext;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Diese Klasse enthält statische Hilfsmethoden für die Behandlung
 * von Datums- und Zeitwerten.
 */
public class DateHelper {
    /**
     * Diese Methode setzt die Zeit (Stunde, Minute, Sekunde) im übergebenen
     * {@link Date}-Objekt gemäß dem ebenfalls übergebenen String. Akzeptiert
     * werden darin Zeitangaben im Format 'STUNDE.MINUTE', 'STUNDE:MINUTE',
     * 'STUNDE' oder ''. Im Fall einer ungültigen oder leeren Angabe wird die
     * Zeit auf 00:00:30 gesetzt (eine "Nicht-Zeit", {@link #isTimeInDate(Date)})
     * und gegebenenfalls eine Fehlermeldung eingetragen.
     *
     * @param date   {@link Date}-Objekt, dessen Zeit gesetzt werden soll.
     * @param input  Zeitangabe als 'STUNDE.MINUTE', 'STUNDE:MINUTE',
     *               'STUNDE' oder ''.
     * @param errors Liste, in die gegebenenfalls eine Fehlermeldung eingetragen
     *               wird; falls <code>null</code>, so wird kein Fehlereintrag versucht.
     */
    static public void addTimeToDate(Timestamp date, String input, List errors) {
        if (date == null) {
            return;
        }

        Calendar time = Calendar.getInstance();
        try {
            if (input == null) {
                /* fixed bug #1020
                 * throwing NPE here lead to the fact that the person record can no longer be copied
                 */
				/*
				time.set(Calendar.HOUR_OF_DAY, 0);
				time.set(Calendar.MINUTE, 0);
				time.set(Calendar.SECOND, 0);
				*/
            } else if (input.indexOf(":") != -1) {
                String tokens[] = input.split("\\:");
                if (tokens.length == 2) {
                    time.setTime(TIME_FORMAT_DE.parse(input));
                    if (Integer.parseInt(tokens[0]) != time.get(Calendar.HOUR_OF_DAY)) {
                        throw new NumberFormatException();
                    }
                    if (Integer.parseInt(tokens[1]) != time.get(Calendar.MINUTE)) {
                        throw new NumberFormatException();
                    }
                }
            } else if (input.indexOf(".") != -1) {
                String tokens[] = input.split("\\.");
                if (tokens.length == 2) {
                    time.setTime(TIME_FORMAT_EN.parse(input));
                    if (Integer.parseInt(tokens[0]) != time.get(Calendar.HOUR_OF_DAY)) {
                        throw new NumberFormatException();
                    }
                    if (Integer.parseInt(tokens[1]) != time.get(Calendar.MINUTE)) {
                        throw new NumberFormatException();
                    }
                }
            } else if (input.length() != 0) {
                int hour = Integer.parseInt(input);
                if (hour < 0 || hour > 23) {
                    throw new NumberFormatException();
                }
                time.set(Calendar.HOUR_OF_DAY, hour);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.SECOND, 0);
            } else {
                time.set(Calendar.HOUR_OF_DAY, 0);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.SECOND, 30);
            }
        } catch (Exception e) {
            if (errors != null) {
                input = StringEscapeUtils.escapeHtml(input);
                errors.add("'" + input + "' ist keine g\u00fcltige Uhrzeit. Bitte verwenden Sie das Format SS:MM.");
            }
            time.set(Calendar.HOUR_OF_DAY, 0);
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 30);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (input != null) {
            calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, time.get(Calendar.SECOND));
            calendar.set(Calendar.MILLISECOND, 0);
        }
        date.setTime(calendar.getTimeInMillis());
    }

    /**
     * Diese Methode testet, ob der Sekundenanteil der Zeit des übergebenen
     * {@link Date}-Objekts 0 ist.<br>
     * In VerA.web wird als Zeit 00:00:30 eingetragen, um darzustellen,
     * dass <strong>keine</strong> Zeitangabe im Datum vorliegt, während im Falle
     * vorhandener Zeitangaben nur Stunden- und Minutenangaben eingetragen
     * werden, vergleiche {@link #addTimeToDate(Timestamp, String, List)}.
     *
     * @param date zu testendes {@link Date}-Objekts
     * @return <code>true</code> genau dann, wenn das Datum den
     * Sekundenanteil 0 hat, also im VerA.web-Kontext einen gültigen
     * Zeiteintrag.
     */
    static public boolean isTimeInDate(Date date) {
        return date != null && ((date.getTime() / 1000) % 60) == 0;
    }

    /**
     * 2009-05-17 cklein
     * Introducing this temporary fix here. Normally this should have gone into
     * octopus, however, I currently do not have access to the old/new cvsroot.
     * Temporarily fixes issue #1529.
     *
     * @param errors         FIXME
     * @param octopusContext FIXME
     */
    static public void temporary_fix_translateErrormessageEN2DE(List<String> errors,
            final OctopusContext octopusContext) {
        List<String> found = new ArrayList<String>();
        for (String err : errors) {
            if (err != null && err.contains("is not a valid date")) {
                found.add(err);
            }
        }

        for (String err : found) {
            String input = StringEscapeUtils.escapeHtml(err.substring(0, err.indexOf(' ')));

            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);

            errors.remove(err);
            errors.add("" + input + languageProvider.getProperty("DATE_HELPER_INCORRECT_DATE"));
        }
    }

    /**
     * Deutsches Datumsformat
     */
    private static final DateFormat TIME_FORMAT_DE = new SimpleDateFormat("H:m");

    /**
     * Englisches Datumsformat
     */
    private static final DateFormat TIME_FORMAT_EN = new SimpleDateFormat("H.m");
}
