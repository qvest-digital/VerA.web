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
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Diese Klasse holt (potentiell lokalisiert) ausgelagerte Mitteilungen; diese
 * können so roh oder mittels {@link MessageFormat} mit Parametern gefüllt
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
     * Dieser Konstruktor bekommt die zu benutzende {@link Locale} übergeben.
	 *
     * @param locale FIXME
     */
	public LocaleMessage(Locale locale) {
		this.locale = locale;
		this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung roh.
     *
     * @param key Schlüssel der Bundle-Mitteilung
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
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format für
     * {@link MessageFormat} interpretiert mit einem Parameter ausgewertet.
     *
     * @param key Schlüssel der Bundle-Mitteilung
     * @param arg0 {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0) {
		try {
			return MessageFormat.format(bundle.getString(key), arg0);
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format für
     * {@link MessageFormat} interpretiert mit zwei Parametern ausgewertet.
     *
     * @param key Schlüssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1) {
		try {
			return MessageFormat.format(bundle.getString(key), arg0, arg1);
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format für
     * {@link MessageFormat} interpretiert mit drei Parametern ausgewertet.
     *
     * @param key Schlüssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @param arg2 dritter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1, Object arg2) {
		try {
			return MessageFormat.format(bundle.getString(key), arg0, arg1, arg2);
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format für
     * {@link MessageFormat} interpretiert mit vier Parametern ausgewertet.
     *
     * @param key Schlüssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @param arg2 dritter {@link MessageFormat}-Parameter
     * @param arg3 vierter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1, Object arg2, Object arg3) {
		try {
			return MessageFormat.format(bundle.getString(key), arg0, arg1, arg2, arg3);
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Diese Methode liefert eine Bundle-Mitteilung als ein Format für
     * {@link MessageFormat} interpretiert mit fünf Parametern ausgewertet.
     *
     * @param key Schlüssel der Bundle-Mitteilung
     * @param arg0 erster {@link MessageFormat}-Parameter
     * @param arg1 zweiter {@link MessageFormat}-Parameter
     * @param arg2 dritter {@link MessageFormat}-Parameter
     * @param arg3 vierter {@link MessageFormat}-Parameter
     * @param arg4 fünfter {@link MessageFormat}-Parameter
     * @return passend interpretierte Bundle-Mitteilung oder <code>null</code>.
     */
	public Object get(String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		try {
			return MessageFormat.format(bundle.getString(key), arg0, arg1, arg2, arg3, arg4);
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
     * @param key Bundle-Schlüssel des zu benutzenden {@link SimpleDateFormat}-Formats.
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
            .replaceAll("&amp;", "&")
			.replaceAll("<br>", "\n");
	}
}
