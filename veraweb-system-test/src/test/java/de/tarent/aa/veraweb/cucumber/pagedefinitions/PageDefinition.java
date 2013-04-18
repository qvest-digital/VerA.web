package de.tarent.aa.veraweb.cucumber.pagedefinitions;

import java.util.HashMap;
import java.util.Map;

import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

/**
 * {@link Enum} for all pages.
 * 
 * @author Michael Kutz, tarent Solutions GmbH
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 */
public enum PageDefinition {

	/** 
	 * Anmeldungsseite 
	 */
	ANMELDUNGSSEITE("ShowLogin",
	        new ElementDefinition("Header-Titel", "header.title"),
			new ElementDefinition("Benutzername-Feld", "input.username", HtmlType.INPUT),
			new ElementDefinition("Passwort-Feld", "input.password", HtmlType.INPUT),
			new ElementDefinition("In Vertretung Anmelden-Checkbox", "input.loginasproxy", HtmlType.CHECKBOX),
			new ElementDefinition("Anmelden", "button.login"),
			new ElementDefinition("FelderLeeren-Button", "button.clearFields"),
			new ElementDefinition("Infobox-Titel", "text.infoTitle"),
			new ElementDefinition("Infobox-Text", "text.infoText"),
	        new ElementDefinition("Fehler-Text", "text.error", false)),
			
	/** 
     * Anmeldungsseite 
     */
    ABMELDUNGSSEITE("logout",
            new ElementDefinition("Header-Titel", "header.title"),
            new ElementDefinition("Benutzername-Feld", "input.username", HtmlType.INPUT),
            new ElementDefinition("Passwort-Feld", "input.password", HtmlType.INPUT),
            new ElementDefinition("In Vertretung Anmelden-Checkbox", "input.loginasproxy", HtmlType.CHECKBOX),
            new ElementDefinition("Anmelden", "button.login"),
            new ElementDefinition("Felder Leeren", "button.clearFields"),
            new ElementDefinition("Infobox-Titel", "text.infoTitle"),
            new ElementDefinition("Infobox-Text", "text.infoText"),
            new ElementDefinition("Fehler-Text", "text.error", false)),
            
    /** 
     * Anmeldungsseite 
     */
    STARTSEITE_ANGEMELDET("default",
            new ElementDefinition("Abmelden", "menu.logout"),
            new ElementDefinition("Person bearbeiten", "menu.searchPerson"),
            new ElementDefinition("Person Neu", "menu.newPerson"),
            new ElementDefinition("Veranstaltung bearbeiten", "menu.searchEvent"),
	        new ElementDefinition("Veranstaltung Neu", "menu.newEvent")),
            
    /** 
     * Veranstaltung suchen
     */
    VERANSTALTUNG_SUCHEN(
            new ElementDefinition("Kurzbeschreibung-Feld", "input.shortname", HtmlType.INPUT),
            new ElementDefinition("Suche starten", "button.startSearch")),
            
    /** 
     * Veranstaltung Detailansicht
     */
    VERANSTALTUNG_DETAILANSICHT(
            new ElementDefinition("Aufgaben", "tab.tasks")),

	/** 
	 * Aufgabenübersichtsseite 
	 */
	AUFGABENUEBERSICHTSEITE(
	        new ElementDefinition("Löschen-Button", "button.delete"),
            new ElementDefinition("Zurück-Button", "button.back")),
	        
    /** 
     * Aufgabendetailseite
     */
    AUFGABEDETAILSEITE(
            new ElementDefinition("Kurzbeschreibung-Feld", "input.title"),
            new ElementDefinition("Beschreibung-Feld", "input.description", HtmlType.INPUT),
            new ElementDefinition("Startdatum-Feld", "input.startdate", HtmlType.INPUT),
            new ElementDefinition("Enddatum-Feld", "input.enddate", HtmlType.INPUT),
            new ElementDefinition("Fortschrittsgrad-Feld", "input.degreeOfCompletion", HtmlType.SELECT),
            new ElementDefinition("Priorität-Feld", "input.priority", HtmlType.SELECT),
            new ElementDefinition("Veranwortliche Person-Feld", "input.responsiblePerson", HtmlType.INPUT),
            new ElementDefinition("Speichern-Button", "button.save"),
            new ElementDefinition("Zurück-Button", "button.back"));
	

	/** 
	 * URL of the page relative to the {@link GlobalConfig#getDnsaBaseUrl()}. 
	 */
	public final String url;

	/** 
	 * All {@link PageElementDefinition}s mapped to their name. 
	 */
	private final Map<String, ElementDefinition> elementMap = new HashMap<String, ElementDefinition>();

	/** 
	 * All {@link PageElementDefinition}s. 
	 */
	public final ElementDefinition[] elements;

	private PageDefinition(String url, ElementDefinition... elements) {
		this.url = url;
		this.elements = elements;
		for (ElementDefinition element : this.elements) {
			ElementDefinition previous = this.elementMap.put(element.name, element);
			if (previous != null) {
				throw new IllegalArgumentException(String.format(
						"The element %1$s was added before. Element names must be unique.", element.name));
			}
		}
	}

	private PageDefinition(ElementDefinition... elements) {
		this((String) null, elements);
	}

	public static PageDefinition pageForName(String pageName) {
		return valueOf(NameUtil.nameToEnumName(pageName));
	}

	public ElementDefinition elementForName(String elementName) {
		if (!this.elementMap.containsKey(elementName)) {
			throw new IllegalArgumentException(
					String.format("Page %1$s has no element %2$s.", this.name(), elementName));
		}
		return this.elementMap.get(elementName);
	}
}
