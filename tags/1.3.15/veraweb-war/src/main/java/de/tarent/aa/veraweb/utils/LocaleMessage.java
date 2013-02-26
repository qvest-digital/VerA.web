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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Diese Klasse holt (potentiell lokalisiert) ausgelagerte Mitteilungen; diese
 * k�nnen so roh oder mittels {@link MessageFormat} mit Parametern gef�llt
 * abgefragt werden.  
 * 
 * @author christoph
 */
public class LocaleMessage {
    /** Name des zu benutzenden Bundles */
	public static final String BUNDLE_NAME = "de.tarent.octopus.beans.messages";

	protected Locale locale;
	protected ResourceBundle bundle;

    /**
     * Dieser Konstruktor bekommt die zu benutzende {@link Locale} �bergeben.
     */
	public LocaleMessage(Locale locale) {
		this.locale = locale;
		this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung roh. 
     * 
     * @param key Schl�ssel der Bundle-Mitteilung
     * @return passende Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format f�r
     * {@link MessageFormat} interpretiert mit einem Parameter ausgewertet.
     * 
     * @param key Schl�ssel der Bundle-Mitteilung
     * @param arg0 {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0) {
		try {
			return MessageFormat.format(bundle.getString(key), new Object[] { arg0 });
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format f�r
     * {@link MessageFormat} interpretiert mit zwei Parametern ausgewertet.
     * 
     * @param key Schl�ssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1) {
		try {
			return MessageFormat.format(bundle.getString(key), new Object[] { arg0, arg1 });
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format f�r
     * {@link MessageFormat} interpretiert mit drei Parametern ausgewertet.
     * 
     * @param key Schl�ssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @param arg2 dritter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1, Object arg2) {
		try {
			return MessageFormat.format(bundle.getString(key), new Object[] { arg0, arg1, arg2 });
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format f�r
     * {@link MessageFormat} interpretiert mit vier Parametern ausgewertet.
     * 
     * @param key Schl�ssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @param arg2 dritter {@link MessageFormat}-Parameter
     * @param arg3 vierter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1, Object arg2, Object arg3) {
		try {
			return MessageFormat.format(bundle.getString(key), new Object[] { arg0, arg1, arg2, arg3});
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format f�r
     * {@link MessageFormat} interpretiert mit f�nf Parametern ausgewertet.
     * 
     * @param key Schl�ssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @param arg2 dritter {@link MessageFormat}-Parameter
     * @param arg3 vierter {@link MessageFormat}-Parameter
     * @param arg4 f�nfter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		try {
			return MessageFormat.format(bundle.getString(key), new Object[] { arg0, arg1, arg2, arg3, arg4});
		} catch (MissingResourceException e) {
			return null;
		}
	}

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
	public Object text(Object input) {
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
     * Encodet ein Datum zur direkten Darstellung in HTML-Textarea-Feldern.
     * 
     * @param key Bundle-Schl�ssel des zu benutzenden {@link SimpleDateFormat}-Formats.
     * @param arg0 Darzustellendes Datum als {@link Date}- oder {@link Long}-Instanz.
     *  Bei anderen Klassen wird die Stringdarstellung genommen und als {@link Long}
     *  interpretiert.
     * @return Encodetes Datum oder <code>null</code>
     */
	public Object date(String key, Object arg0) {
		return this.date(key, arg0, true);
	}

	public Object date(String key, Object arg0, boolean doInsertDefault)
	{
		try {
			Calendar calendar = Calendar.getInstance(locale);
			if (arg0 instanceof Date) {
				calendar.setTime((Date)arg0);
			} else if (arg0 instanceof Long) {
				calendar.setTimeInMillis(((Long)arg0).longValue());
			} else {
				// cf. issue #2168
				if ( doInsertDefault )
				{
					calendar.setTimeInMillis(new Long(arg0.toString()).longValue());
				}
				else
				{
					// we do not want a default return value
					return null;
				}
			}
			// FIXME throws missing resource exception most of the time
			return new SimpleDateFormat(bundle.getString(key), locale).format(calendar.getTime());
		} catch (MissingResourceException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
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
			.replaceAll("&", "&amp;")
			.replaceAll("\"", "&quot;")
//			.replaceAll("'", "&apos;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;");
//			.replaceAll("�", "&auml;")
//			.replaceAll("�", "&Auml;")
//			.replaceAll("�", "&uuml;")
//			.replaceAll("�", "&Uuml;")
//			.replaceAll("�", "&ouml;")
//			.replaceAll("�", "&Ouml;")
//			.replaceAll("�", "&szlig;");
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
			.replaceAll("&quot;", "\"")
//			.replaceAll("&apos;", "'")
			.replaceAll("&lt;", "<")
			.replaceAll("&gt;", ">")
//			.replaceAll("&auml;", "�")
//			.replaceAll("&Auml;", "�")
//			.replaceAll("&uuml;", "�")
//			.replaceAll("&Uuml;", "�")
//			.replaceAll("&ouml;", "�")
//			.replaceAll("&Ouml;", "�")
//			.replaceAll("&szlig;", "�")
            .replaceAll("&amp;", "&")
			.replaceAll("<br>", "\n");
	}
}
