/* $Id: CheckInput.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * Created on 22.09.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.tarent.octopus.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Heiko Ferger
 */
public class CheckInput {
    /**
     * InputRegexp beinhaltet die Definitionen f�r Eingabefelder
     * verschiedener Eingabetypen als:
     * Key:
     * type  String : z.B. EMail, PLZ usw  
     * Value: eine HashMap mit 
     *             Key:
     *             Vorkompilierter Regul�rer Ausdruck vom typ Pattern und
     *             Value:
     *                 Ein String mit der Beschreibung der Eingabe f�r den Benutzer
     *                  z.B. 
     *                 Eine g�ltige Eingabe f�r das Datums-format ist folgende:
     *                TT.MM.JJJJ
     * 
     * Bem: diese HashMap dient dem Worker CheckInput als Basis
     *  zur �berpr�fung von Eingaben aus einem Velocity (HTML)
     * kontext.
     * 
     * Die Werte f�r diese Map werden in der Funktion InitInputRegexp
     * einmal initialisiert. Sie sollten dann statisch f�r alle instanzen dieser 
     * Klasse vorliegen.
     * 
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
        new String("Eine g�ltige EMail-Adresse hat folgendes Format: adressad@Domain.TopDomain");
    private static Pattern IP_EMail = Pattern.compile("[A-Za-z]*@[A-Za-z]*.[A-Za-z]{2,3}");
    /* Vorname */
    public static String IT_Vorname = new String("Vorname");
    private static String ID_Vorname =
        new String("Eine g�ltiger Vorname besteht aus Buchstaben mit der maximalen \n l�nge von 30 Zeichen. \n Der erste Buchstabe ist ein Gro�buchstabe alle folgenden werden klein geschrieben.");
    private static Pattern IP_Vorname = Pattern.compile("[A-Z][a-z]{1,29}");
    /* Nachname */
    public static String IT_Nachname = new String("Nachname");
    private static String ID_Nachname =
        new String("Eine g�ltiger Nachname besteht aus Buchstaben mit der maximalen \n l�nge von 30 Zeichen. \n Der erste Buchstabe ist ein Gro�buchstabe alle folgenden werden klein geschrieben.");
    private static Pattern IP_Nachname = Pattern.compile("[A-Z][a-z]{1,29}");
    /* PLZ */
    public static String IT_PLZ = new String("PLZ");
    private static String ID_PLZ = new String("Eine g�ltige PLZ besteht aus f�nf Ziffern.");
    private static Pattern IP_PLZ = Pattern.compile("[0-9]{5}");
    /* Datum */
    public static String IT_Datum = new String("Datum");
    private static String ID_Datum = new String("Eine g�ltiges Datum hat folgendes Format:\n\n TT.MM.JJJJ");
    private static Pattern IP_Datum = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}");

    /**
     * Dient zur Registrierung der Elemente (IT,ID,IP)
     * in den HashMaps.
     * 
     * Wird von der Funktion InitStatic aufgerufen.
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
    * @author Heiko Ferger
     */
    private void InitStatic() {
        // Variablen mit hilfe der Funktion r (registrieren).
        // Typ, Beschreibung, Regul�ren Ausdruck (Vorkompiliert)
        reg(IT_EMail, ID_EMail, IP_EMail);
        reg(IT_Vorname, ID_Vorname, IP_Vorname);
        reg(IT_Nachname, ID_Nachname, IP_Nachname);
        reg(IT_PLZ, ID_PLZ, IP_PLZ);
    }

    /**
     * Gibt eine Liste von verf�gbaren Eingabetypen 
     * zur�ck.
     * 
     * @return ein Stringarray von verf�gbaren EigabeTypen
     */
    public String getInputTypes() {
        return InputDesc.keySet().toString();
    }

    /**
     * Gibt die Beschreibung zum format eines Eingabetypen (InputType) als String wieder.
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
     * Gibt das Pattern (RegExp) zu einem EingabeTyp als String zur�ck
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
     * Gibt true zur�ck, falls der InputType existiert, sonnsten false.
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
     * @param InputType String  : Type des Eingabeformates
     * @param InputString String : Zu �berpr�fender String
     * @return true falls eine �bereinstimmung des EingabeTypen mit dem InputString erfolgreich war.
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
