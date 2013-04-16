package de.tarent.aa.veraweb.cucumber.data;

import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public enum AufgabeData {
	
	AUFGABE_DETAILANSICHT("Aufgabe 1", "Text_Beschreibung", "12.04.2013", "13.04.2013", "10%", "1");
	
	public final String kurzbezeichnung;
	
	public final String beschreibung;
	
	public final String beginn;
	
	public final String ende;
	
	public final String fertigstellung;
	
	public final String prioritaet;
	
	private AufgabeData(String kurzbezeichnung, String beschreibung, String beginn, String ende, String fertigstellung, String prioritaet) {
		this.kurzbezeichnung = kurzbezeichnung;
		this.beschreibung = beschreibung;
		this.beginn = beginn;
		this.ende = ende;
		this.fertigstellung = fertigstellung;
		this.prioritaet = prioritaet;
	}
	
	public static AufgabeData forName(String name) {
		return valueOf(NameUtil.nameToEnumName(name));
	}

}
