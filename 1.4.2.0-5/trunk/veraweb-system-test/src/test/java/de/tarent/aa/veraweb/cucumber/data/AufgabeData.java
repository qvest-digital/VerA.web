package de.tarent.aa.veraweb.cucumber.data;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateMidnight;

import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.ElementDefinition;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public enum AufgabeData {

    AUFGABE_DETAILANSICHT("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer",	"10", "1"),
//  AUFGABE_MIT_LEEREN_FELDERN("", "", , , "", "", ""),
    AUFGABE_DETAILANSICHT_GEAENDERT("Aufgabe 2", "Beschreibung 2", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Fred Feuerstein", "20%", "3"),
	TITEL_MIT_LEEREM_PFLICHTFELD("", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	TITEL_MIT_EINHUNDERTNEUN_ZEICHEN_TEXT("Dies ist ein Text mit 109 Zeichen. Dies ist ein Text mit 109 Zeichen. Dies ist ein Text mit 109 Zeichen Ende.","Text_Beschreibung", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	TITEL_MIT_HUNDERT_ZEICHEN_TEXT("Dies ist ein Text mit 100 Zeichen. Dies ist ein Text mit 100 Zeichen. Dies ist ein Text mit 100Ende.", "Text_Beschreibung", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	TITEL_MIT_NEUNUNDNEUNZIG_ZEICHEN_TEXT("Dies ist ein Text mit 99 Zeichen. Dies ist ein Text mit 99 Zeichen. Dies ist ein Text mit 99 ZEnde.","Text_Beschreibung", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1" ),
	TITEL_MIT_SONDERZEICHEN("Dies ist ein Text mit 100 Zeichen und Sonderzeichen !“§$%&/()=?<*+->. Dies ist ein Text mit 100 Zei.","Text_Beschreibung", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	TITEL_MIT_JAVASCRIPT("<script type=\"text/javascript\">alert(\"huhu\")</script>","Text_Beschreibung", DateMidnight.now().minus(1), DateMidnight.now().plus(1),  "Barny Geröllheimer", "10", "1"),
	BESCHREIBUNG_MIT_LEEREM_FELD("Aufgabe 1", "", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	BESCHREIBUNG_MIT_NEUNHUNDERTNEUNUNDNEUNZIG_ZEICHEN_TEXT("Aufgabe 1", 
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. " +
			"Dies ist ein Text mit 999 Zeichen. Dies ist ein Text mit 999 Zeichen. Dies ist ein Text.",	DateMidnight.now().minus(1), DateMidnight.now().plus(1),  "Barny Geröllheimer", "10", "1"),
	BESCHREIBUNG_MIT_EINTAUSEND_ZEICHEN_TEXT("Aufgabe 1",
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000 Zeichen. " +
			"Dies ist ein Text mit 1000 Zeichen. Dies ist ein Text mit 1000Z.", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1" ),
	BESCHREIBUNG_MIT_EINTAUSENDUNDEINUNDZWANZIG_ZEICHEN_TEXT("Aufgabe 1", 
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. " +
			"Dies ist ein Text mit 1021 Zeichen. Dies ist ein Text mit 1021 Zeichen. Dies ist ein.", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	BESCHREIBUNG_MIT_SONDERZEICHEN("Aufgabe1", "Dies ist ein Text mit 100 Zeichen und Sonderzeichen !“§$%&/()=?<*+->. Dies ist ein Text mit 100 Zei.", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	BESCHREIBUNG_MIT_JAVASCRIPT("<script type=\"text/javascript\">alert(\"huhu\")</script>","Text_Beschreibung", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
//	STARTDATUM_MIT_LEEREM_FELD("Aufgabe 1", "Text_Beschreibung", "", "13.04.2013", "Barny Geröllheimer", "10", "1"),
	STARTDATUM_VALIDE("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
//	STARTDATUM_MIT_SONDERZEICHEN("Aufgabe 1", "Beschreibung 1","§$%/()=","13.04.2013", "Barny Geröllheimer", "10", "1"),
//	STARTDATUM_MIT_JAVASCRIPT("Aufgabe 1", "Beschreibung 1","<script type=\"text/javascript\">alert(\"huhu\")</script>","13.04.2013", "Barny Geröllheimer", "10", "1"),
//	STARTDATUM_MIT_MONATSNAME("Aufgabe 1", "Beschreibung 1","12. April 2013","13.04.2013", "Barny Geröllheimer", "10", "1"),
//	ENDDATUM_MIT_LEEREM_FELD("Aufgabe 1", "Text_Beschreibung", "12.04.2013", "", "Barny Geröllheimer", "10", "1"),
	ENDDATUM_VALIDE("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
//	ENDDATUM_MIT_SONDERZEICHEN("Aufgabe 1", "Beschreibung 1", "12.04.2013", "§$%/()=", "Barny Geröllheimer", "10", "1"),
//	ENDDATUM_MIT_JAVASCRIPT("Aufgabe 1", "Beschreibung 1", "12.04.2013","<script type=\"text/javascript\">alert(\"huhu\")</script>", "Barny Geröllheimer", "10", "1"),
//	ENDDATUM_MIT_MONATSNAME("Aufgabe 1", "Beschreibung 1", "12.04.2013", "13. April 2013", "Barny Geröllheimer", "10", "1"),
	ENDDATUM_VOR_STARTDATUM("Aufgabe 1", "Beschreibung 1", DateMidnight.now().plus(2), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	ENDDATUM_IN_VERGANGENHEIT("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().minus(2), "Barny Geröllheimer", "10", "1"),
	FERTIGSTELLUNG_GEANDERT_10("Aufgabe 1", "Beschreibung 1",DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "10", "1"),
	FERTIGSTELLUNG_GEANDERT_50("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "50", "1"),
	FERTIGSTELLUNG_GEANDERT_90("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "Barny Geröllheimer", "90", "1"),
	PRIORITAET_GEANDERT_2("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "10", "Barny Geröllheimer", "2"),
	PRIORITAET_GEANDERT_4("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "10", "Barny Geröllheimer", "4"),
	PRIORITAET_GEANDERT_6("Aufgabe 1", "Beschreibung 1", DateMidnight.now().minus(1), DateMidnight.now().plus(1), "10", "Barny Geröllheimer", "6");
	
    public final String kurzbezeichnung;

    public final String beschreibung;

    public final DateMidnight beginn;

    public final String beginnZeit;

    public final DateMidnight ende;
    
    public final String endeZeit;
    
    public final String verantwortlicher;

    public final String fertigstellung;

    public final String prioritaet;

    public final Map<String, ElementDefinition> valuesForPageFields;

    private AufgabeData(String kurzbezeichnung, String beschreibung, DateMidnight beginn,String beginnZeit, DateMidnight ende, String endeZeit, String verantwortlicher,
            String fertigstellung, String prioritaet) {
        this.kurzbezeichnung = kurzbezeichnung;
        this.beschreibung = beschreibung;
        this.beginn = beginn;
        this.beginnZeit = beginnZeit;
        this.ende = ende;
        this.endeZeit = endeZeit;
        this.verantwortlicher = verantwortlicher;
        this.fertigstellung = fertigstellung;
        this.prioritaet = prioritaet;

        valuesForPageFields = new HashMap<String, ElementDefinition>();
        valuesForPageFields.put(kurzbezeichnung, PageDefinition.AUFGABEDETAILSEITE.elementForName("Kurzbeschreibung-Feld"));
        valuesForPageFields.put(beschreibung, PageDefinition.AUFGABEDETAILSEITE.elementForName("Beschreibung-Feld"));
        valuesForPageFields.put(beginn.toString(Utils.DEFAULT_DATETIME_FORMATTER),
                PageDefinition.AUFGABEDETAILSEITE.elementForName("Startdatum-Feld"));
        valuesForPageFields.put(beginnZeit,
                PageDefinition.AUFGABEDETAILSEITE.elementForName("Startzeit-Feld"));
        valuesForPageFields.put(ende.toString(Utils.DEFAULT_DATETIME_FORMATTER),
                PageDefinition.AUFGABEDETAILSEITE.elementForName("Enddatum-Feld"));
        valuesForPageFields.put(endeZeit,
                PageDefinition.AUFGABEDETAILSEITE.elementForName("Endzeit-Feld"));
        valuesForPageFields.put(verantwortlicher, PageDefinition.AUFGABEDETAILSEITE.elementForName("Veranwortliche Person-Feld"));
        valuesForPageFields.put(fertigstellung, PageDefinition.AUFGABEDETAILSEITE.elementForName("Fortschrittsgrad-Feld"));
        valuesForPageFields.put(prioritaet, PageDefinition.AUFGABEDETAILSEITE.elementForName("Priorität-Feld"));
    }
    

    private AufgabeData(String kurzbezeichnung, String beschreibung, DateMidnight beginn, DateMidnight ende,  String verantwortlicher,
            String fertigstellung, String prioritaet) {
    	this(kurzbezeichnung, beschreibung, beginn,"", ende, "", verantwortlicher,
                fertigstellung,  prioritaet);
    }

    public static AufgabeData forName(String name) {
        return valueOf(NameUtil.nameToEnumName(name));
    }

}
