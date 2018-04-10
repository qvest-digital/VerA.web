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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
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

    /**
     * Anschrifttyp als P,G,S zurückgeben.
     *
     * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
     * Muss auch in anderen Bereichen umgesetzt werden.
     * Z.B. beim "Neu Laden" in Worker und Template.
     *
     * @param addresstype adresstype
     * @return addresstype
     */
    public static String getAddresstype(Integer addresstype) {
        if (addresstype == null) {
            return null;
        } else if (addresstype.intValue() == 2) {
            return "P";
        } else if (addresstype.intValue() == 3) {
            return "W";
        } else { // addresstype.intValue() == 1
            return "G";
        }
    }

    /**
     * Zeichensatz als L,F1,F2 zurückgeben.
     *
     * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
     * Muss auch in anderen Bereichen umgesetzt werden.
     * Z.B. beim "Neu Laden" in Worker und Template.
     *
     * @param locale locale
     * @return locale
     */
    public static String getLocale(Integer locale) {
        if (locale == null) {
            return null;
        } else if (locale.intValue() == 2) {
            return "ZS1";
        } else if (locale.intValue() == 3) {
            return "ZS2";
        } else { // locale.intValue() == 1
            return "L";
        }
    }

    /**
     * Diese Methode liefert eine String-Darstellung eines Gender-Werts
     *
     * @param gender gender
     * @return gender
     */
    public static String getGender(String gender) {
        return gender;
    }
}
