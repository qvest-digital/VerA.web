package de.tarent.octopus.util;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Heiko Ferger
 */
public class CheckInput {
    /**
     * InputRegexp beinhaltet die Definitionen für Eingabefelder
     * verschiedener Eingabetypen als:
     * Key:
     * type  String : z.B. EMail, PLZ usw
     * Value: eine HashMap mit
     * Key:
     * Vorkompilierter Regulärer Ausdruck vom typ Pattern und
     * Value:
     * Ein String mit der Beschreibung der Eingabe für den Benutzer
     * z.B.
     * Eine gültige Eingabe für das Datums-format ist folgende:
     * TT.MM.JJJJ
     *
     * Bem: diese HashMap dient dem Worker CheckInput als Basis
     * zur überprüfung von Eingaben aus einem Velocity (HTML)
     * kontext.
     *
     * Die Werte für diese Map werden in der Funktion InitInputRegexp
     * einmal initialisiert. Sie sollten dann statisch für alle instanzen dieser
     * Klasse vorliegen.
     */
    private static HashMap InputRegexp = new HashMap();
    private static HashMap InputDesc = new HashMap();

    /* Definition von Eingabetypen als Konstanten */
    /*
     * IT = InputType
     * ID = InputDescription
     * IP = InputPattern
     *
     * */

    /* EMail */
    public static String IT_EMail = new String("EMail");
    private static String ID_EMail =
      new String("Eine gültige EMail-Adresse hat folgendes Format: adressad@Domain.TopDomain");
    private static Pattern IP_EMail = Pattern.compile("[A-Za-z]*@[A-Za-z]*.[A-Za-z]{2,3}");
    /* Vorname */
    public static String IT_Vorname = new String("Vorname");
    private static String ID_Vorname =
      new String(
        "Eine gültiger Vorname besteht aus Buchstaben mit der maximalen \n länge von 30 Zeichen. \n Der erste " +
          "Buchstabe ist ein Großbuchstabe alle folgenden werden klein geschrieben.");
    private static Pattern IP_Vorname = Pattern.compile("[A-Z][a-z]{1,29}");
    /* Nachname */
    public static String IT_Nachname = new String("Nachname");
    private static String ID_Nachname =
      new String(
        "Eine gültiger Nachname besteht aus Buchstaben mit der maximalen \n länge von 30 Zeichen. \n Der erste " +
          "Buchstabe ist ein Großbuchstabe alle folgenden werden klein geschrieben.");
    private static Pattern IP_Nachname = Pattern.compile("[A-Z][a-z]{1,29}");
    /* PLZ */
    public static String IT_PLZ = new String("PLZ");
    private static String ID_PLZ = new String("Eine gültige PLZ besteht aus fünf Ziffern.");
    private static Pattern IP_PLZ = Pattern.compile("[0-9]{5}");
    /* Datum */
    public static String IT_Datum = new String("Datum");

    /**
     * Dient zur Registrierung der Elemente (IT,ID,IP)
     * in den HashMaps.
     *
     * Wird von der Funktion InitStatic aufgerufen.
     *
     * @param IT
     * @param ID
     * @param IP
     */
    private void reg(String IT, String ID, Pattern IP) {
        InputRegexp.put(IT, IP);
        InputDesc.put(IT, ID);
    }

    /**
     * Dient zur Initialisierung der der Statischen Elemente
     * Diese Funktion wird im Konstruktor aufgerufen.
     *
     * @author Heiko Ferger
     */
    private void InitStatic() {
        // Variablen mit hilfe der Funktion r (registrieren).
        // Typ, Beschreibung, Regulären Ausdruck (Vorkompiliert)
        reg(IT_EMail, ID_EMail, IP_EMail);
        reg(IT_Vorname, ID_Vorname, IP_Vorname);
        reg(IT_Nachname, ID_Nachname, IP_Nachname);
        reg(IT_PLZ, ID_PLZ, IP_PLZ);
    }

    /**
     * Gibt eine Liste von verfügbaren Eingabetypen
     * zurück.
     *
     * @return ein Stringarray von verfügbaren EigabeTypen
     */
    public String getInputTypes() {
        return InputDesc.keySet().toString();
    }

    /**
     * Gibt die Beschreibung zum format eines Eingabetypen (InputType) als String wieder.
     *
     * @param InputType
     * @return Beschreibung zum Eingabetyp
     */
    public String getDescription(String InputType) {
        String result = "";
        String out = (String) InputDesc.get(InputType);

        if (out != null) {
            return out;
        } else {
            return result;
        }
    }

    /**
     * Gibt das Pattern (RegExp) zu einem EingabeTyp als String zurück
     *
     * @param InputType
     * @return Pattern zum Eingabetyp
     */
    public String getPattern(String InputType) {
        String result = "";

        if (isInputType(InputType) == true) {
            Pattern p = (Pattern) InputRegexp.get(InputType);
            return p.pattern();
        } else {
            return result;
        }
    }

    /**
     * Gibt true zurück, falls der InputType existiert, sonnsten false.
     *
     * @param InputType
     * @return boolean
     */
    public boolean isInputType(String InputType) {
        boolean result = false;

        if (InputType != null) {
            return InputDesc.containsKey(InputType);
        } else {
            return result;
        }
    }

    /**
     * @param InputType   String  : Type des Eingabeformates
     * @param InputString String : Zu überprüfender String
     * @return true falls eine Übereinstimmung des EingabeTypen mit dem InputString erfolgreich war.
     * Ansonsten false
     */
    public boolean check(String InputType, String InputString) {
        boolean result = false;

        if (isInputType(InputType)) {
            Pattern p = (Pattern) InputRegexp.get(InputType);
            Matcher m = p.matcher(InputString.subSequence(0, InputString.length()));
            result = m.matches();
        }

        return result;
    }

    public String getCheckResultToHTML(String elementname, boolean iselement, String InputType, String InputString) {
        String ok = new String("#7bffb2");
        String nok = new String("#ff6565");

        String outstr = new String("");

        outstr += "<table style=\"width:100%;border:1px solid #000000;\" >\n";
        outstr += "<tr >\n";
        outstr += "<td  style=\"width:8%;background-color:#67caff;\">Name</td>\n";
        if (iselement == true) {
            outstr += "<td  style=\"background-color:" + ok + ";\">" + elementname + "</td>\n";
        } else {
            outstr += "<td  style=\"background-color:" + nok + ";\">" + elementname + "</td>\n";
        }

        outstr += "</tr><tr>\n";

        outstr += "<td style=\"background-color:#67caff;\">Type</td>\n";
        if (isInputType(InputType)) {
            outstr += "<td style=\"background-color:" + ok + ";\">" + InputType + "</td>\n";
        } else {
            outstr += "<td style=\"background-color:" + nok + ";\">" + InputType + "</td>\n";
        }

        outstr += "</tr><tr>\n";
        outstr += "<td style=\"background-color:#67caff;\">Value</td>\n";
        if (InputString.length() > 0) {
            outstr += "<td style=\"background-color:" + ok + ";\">" + InputString + "</td>\n";
        } else {
            outstr += "<td style=\"background-color:" + nok + ";\">" + InputString + "</td>\n";
        }
        outstr += "</tr><tr>\n";
        outstr += "<td style=\"background-color:#67caff;\">Pattern</td>\n";
        if (getPattern(InputType).length() > 0) {
            outstr += "<td style=\"background-color:" + ok + ";\">" + getPattern(InputType) + "</td>\n";
        } else {
            outstr += "<td style=\"background-color:" + nok + ";\">" + getPattern(InputType) + "</td>\n";
        }
        outstr += "</tr><tr>\n";
        outstr += "<td style=\"background-color:#67caff;\">Result</td>\n";
        if (check(InputType, InputString)) {
            outstr += "<td style=\"background-color:" + ok + ";\">" + check(InputType, InputString) + "</td>\n";
        } else {
            outstr += "<td style=\"background-color:" + nok + ";\">" + check(InputType, InputString) + "</td>\n";
        }

        outstr += "</tr><tr>\n";
        outstr += "<td style=\"background-color:#67caff;\">Desc</td>\n";
        if (getDescription(InputType).length() > 0) {
            outstr += "<td style=\"background-color:" + ok + ";\">" + getDescription(InputType) + "</td>\n";
        } else {
            outstr += "<td style=\"background-color:" + nok + ";\">" + getDescription(InputType) + "</td>\n";
        }
        outstr += "</tr>\n";
        outstr += "</table>\n";

        return outstr;
    }

    public CheckInput() {
        super();
        // TODO: Nur eine Initialisierung der Statischen Elemente
        InitStatic();
    }
}
