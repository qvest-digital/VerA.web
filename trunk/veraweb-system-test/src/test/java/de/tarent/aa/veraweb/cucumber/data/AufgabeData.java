package de.tarent.aa.veraweb.cucumber.data;

import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public enum AufgabeData {
	
	AUFGABE_DETAILANSICHT("Text_Kurzbezeichnung", "Text_Beschreibung", "01.01.2014", "10.01.2014", "10%", "3");
	
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
