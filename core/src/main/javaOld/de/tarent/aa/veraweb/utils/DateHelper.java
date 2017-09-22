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
     * @param date {@link Date}-Objekt, dessen Zeit gesetzt werden soll.
     * @param input Zeitangabe als 'STUNDE.MINUTE', 'STUNDE:MINUTE',
     *  'STUNDE' oder ''.
     * @param errors Liste, in die gegebenenfalls eine Fehlermeldung eingetragen
     *  wird; falls <code>null</code>, so wird kein Fehlereintrag versucht.
     */
	static public void addTimeToDate(Timestamp date, String input, List errors) {
		if (date == null)
			return;

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
					if (Integer.parseInt(tokens[0]) != time.get(Calendar.HOUR_OF_DAY))
						throw new NumberFormatException();
					if (Integer.parseInt(tokens[1]) != time.get(Calendar.MINUTE))
						throw new NumberFormatException();
				}
			} else if (input.indexOf(".") != -1) {
				String tokens[] = input.split("\\.");
				if (tokens.length == 2) {
					time.setTime(TIME_FORMAT_EN.parse(input));
					if (Integer.parseInt(tokens[0]) != time.get(Calendar.HOUR_OF_DAY))
						throw new NumberFormatException();
					if (Integer.parseInt(tokens[1]) != time.get(Calendar.MINUTE))
						throw new NumberFormatException();
				}
			} else if (input.length() != 0) {
				int hour = Integer.parseInt(input);
				if (hour < 0 || hour > 23)
					throw new NumberFormatException();
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
		if ( input != null )
		{
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
     *  Sekundenanteil 0 hat, also im VerA.web-Kontext einen gültigen
     *  Zeiteintrag.
     */
	static public boolean isTimeInDate(Date date) {
		return date != null && ((date.getTime() / 1000) % 60) == 0;
	}

	/**
	 * 2009-05-17 cklein
	 * Introducing this temporary fix here. Normally this should have gone into
	 * octopus, however, I currently do not have access to the old/new cvsroot.
	 * Temporarily fixes issue #1529.
	 * @param errors FIXME
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

	/** Deutsches Datumsformat */
    private static final DateFormat TIME_FORMAT_DE = new SimpleDateFormat("H:m");

    /** Englisches Datumsformat */
    private static final DateFormat TIME_FORMAT_EN = new SimpleDateFormat("H.m");
}
