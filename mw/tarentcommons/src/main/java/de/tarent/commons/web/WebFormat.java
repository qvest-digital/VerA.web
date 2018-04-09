/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: WebFormat.java,v 1.4 2007/06/14 13:34:49 dgoema Exp $
 * 
 * Created on 13.09.2004
 * 
 * Original aus der tarent-groupware-Library.
 */
package de.tarent.commons.web;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.MissingResourceException;

import de.tarent.commons.messages.MessageHelper;
import de.tarent.commons.utils.VariableDateFormat;

/**
 * Diese Klasse holt (potentiell lokalisiert) ausgelagerte Mitteilungen; diese
 * können so roh oder mittels {@link MessageFormat} mit Parametern gefüllt
 * abgefragt werden.  
 * 
 * @author Christoph Jerolimov, tarent GmbH;
 * 			Tim Steffens, tarent GmbH
 */
public class WebFormat {
	/** Wohlformatiertes Datum. */
	public static String DATE;
	/** Wohlformatiertes Datum inkl. Uhrzeit. */
	public static String DATE_TIME;
	/** Wohlformatierte Uhrzeit */
	public static String TIME;
	/** Tag im Monat, 1 bis n. */
	public static String DAY;
	/** Monat im Jahr, 1 bis 12. */
	public static String MONTH;
	/** Jahr */
	public static String YEAR;
	
	/**
	 * Encodet einen Text zur direkten Darstellung im HTML-Inhalt.
	 * 
	 * @param input Original String
	 * @return Encodeter String
	 */
	public Object plain(Object input) {
		if (input != null && input instanceof String)
			return formatTextToHtmlString((String)input).replaceAll("\n", "<br>");
		return input;
	}

	/**
	 * Encodet einen Text zur direkten Darstellung im HTML-Input-Feldern.
	 * 
	 * @param input Original String
	 * @return Encodeter String
	 */
	public Object input(Object input) {
		if (input != null && input instanceof String)
			return formatTextToHtmlString((String)input);
		return input;
	}

	/**
	 * Encodet einen Text zur direkten Darstellung in HTML-Textarea-Feldern.
	 * 
	 * @param input Original String
	 * @return Encodeter String
	 */
	public Object textarea(Object input) {
		if (input != null && input instanceof String)
			return formatTextToHtmlString((String)input);
		return input;
	}

	/**
	 * Converts a date of the form yyyy-MM-dd hh:mm:ss.S to the form dd.MM.yyy and returns it.
	 */
	public String brokerDateToHumanDate(String brokerDate) throws ParseException {
		VariableDateFormat vdf = new VariableDateFormat();
		return date(vdf.analyzeString(brokerDate));
	}
	
    /**
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param input Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public String date(Object input) {
		return formatCalendar(DATE, getCalendar(input));
	}

    /**
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param input Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public Object dateTime(Object input) {
		return formatCalendar(DATE_TIME, getCalendar(input));
	}

    /**
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param input Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public Object time(Object input) {
		return formatCalendar(TIME, getCalendar(input));
	}

    /**
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param input Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public Object day(Object input) {
		return formatCalendar(DAY, getCalendar(input));
	}

    /**
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param input Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public Object month(Object input) {
		return formatCalendar(MONTH, getCalendar(input));
	}

    /**
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param input Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public Object year(Object input) {
		return formatCalendar(YEAR, getCalendar(input));
	}

	protected Calendar getCalendar(Object input) {
		try {
			Calendar calendar = Calendar.getInstance();
			if (input instanceof Date) {
				calendar.setTime((Date)input);
			} else if (input instanceof Long) {
				calendar.setTimeInMillis(((Long)input).longValue());
			} else {
				calendar.setTimeInMillis(new Long(input.toString()).longValue());
			}
			return calendar;
		} catch (MissingResourceException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	protected String formatCalendar(String format, Calendar calendar) {
		if (calendar == null)
			return null;
		return new SimpleDateFormat(format).format(calendar.getTime());
	}

	/**
	 * Encodet einen String in einen HTML-String.
	 * 
	 * @param string Original String
	 * @return Encodeted HTML
	 */
	public static String formatTextToHtmlString(String string) {
		if (string == null) return null;
		return string
			.replaceAll("\u0026", "&amp;")
			.replaceAll("\u005c\u0022", "&quot;")
//			.replaceAll("\u0027", "&apos;")
			.replaceAll("\u003c", "&lt;")
			.replaceAll("\u003e", "&gt;");
//			.replaceAll("\u00e4", "&auml;")
//			.replaceAll("\u00c4", "&Auml;")
//			.replaceAll("\u00fc", "&uuml;")
//			.replaceAll("\u00dc", "&Uuml;")
//			.replaceAll("\u00f6", "&ouml;")
//			.replaceAll("\u00d6", "&Ouml;")
//			.replaceAll("\u00df", "&szlig;");
	}

	/**
	 * Decodet einen String in einen Plaintext-String.
	 * 
	 * @param string HTML String
	 * @return Plaintext
	 */
	public static String formatHtmlToTextString(String string) {
		if (string == null) return null;
		return string
			.replaceAll("&quot;", "\u005c\u0022")
//			.replaceAll("&apos;", "\u0027")
			.replaceAll("&lt;", "\u003c")
			.replaceAll("&gt;", "\u003e")
//			.replaceAll("&auml;", "\u00e4")
//			.replaceAll("&Auml;", "\u00c4")
//			.replaceAll("&uuml;", "\u00fc")
//			.replaceAll("&Uuml;", "\u00dc")
//			.replaceAll("&ouml;", "\u00f6")
//			.replaceAll("&Ouml;", "\u00d6")
//			.replaceAll("&szlig;", "\u00df")
            .replaceAll("&amp;", "\u0026")
			.replaceAll("<br>", "\n");
	}

	static {
		MessageHelper.init(WebFormat.class.getName(), "de.tarent.commons.messages.WebFormat");
	}
}
