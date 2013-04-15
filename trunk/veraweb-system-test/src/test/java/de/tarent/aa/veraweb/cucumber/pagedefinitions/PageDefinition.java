package de.tarent.aa.veraweb.cucumber.pagedefinitions;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;

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
			new ElementDefinition("Benutzername-Feld", "input.username"),
			new ElementDefinition("Passwort-Feld", "input.password"),
			new ElementDefinition("In Vertretung Anmelden-Checkbox", "input.loginasproxy", false),
			new ElementDefinition("Anmelden-Button", "button.login"),
			new ElementDefinition("FelderLeeren-Button", "button.clearFields"),
			new ElementDefinition("Infobox-Titel", "text.infoTitle"),
			new ElementDefinition("Infobox-Text", "text.infoText")),
			
	/** 
     * Anmeldungsseite 
     */
    ABMELDUNGSSEITE("logout",
            new ElementDefinition("Header-Titel", "header.title"),
            new ElementDefinition("Benutzername-Feld", "input.username"),
            new ElementDefinition("Passwort-Feld", "input.password"),
            new ElementDefinition("In Vertretung Anmelden-Checkbox", "input.loginasproxy", false),
            new ElementDefinition("Anmelden-Button", "button.login"),
            new ElementDefinition("FelderLeeren-Button", "button.clearFields"),
            new ElementDefinition("Infobox-Titel", "text.infoTitle"),
            new ElementDefinition("Infobox-Text", "text.infoText")),

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
            new ElementDefinition("Titel-Feld", "input.title"),
            new ElementDefinition("Beschreibung-Feld", "input.description", false),
            new ElementDefinition("Startdatum-Feld", "input.startdate", false),
            new ElementDefinition("Enddatum-Feld", "input.enddate", false),
            new ElementDefinition("Fortschrittsgrad-Button", "input.degreeOfCompletion", false),
            new ElementDefinition("Priorität-Button", "input.priority", false),
            new ElementDefinition("Veranwortliche Person-Button", "input.responsiblePerson", false),
            new ElementDefinition("Speichern-Button", "button.save"),
            new ElementDefinition("Zurück-Button", "button.back"));

	/** 
	 * URL of the page relative to the {@link GlobalConfig#getDnsaBaseUrl()}. 
	 */
	public final String url;

	/** 
	 * All {@link PageElementDefinition}s mapped to their name. 
	 */
	private final Map<String, ElementDefinition> elementMap = new HashMap<String, PageDefinition.ElementDefinition>();

	/** 
	 * All {@link PageElementDefinition}s. 
	 */
	public final ElementDefinition[] elements;

	private PageDefinition(String url, ElementDefinition... elements) {
		this.url = url;
		this.elements = elements;
		for (ElementDefinition element : elements) {
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

	/**
	 * Class to define a page element.
	 * 
	 * @author Michael Kutz, tarent Solutions GmbH
	 */
	public static class ElementDefinition {

		/**
		 * A name for the element that may be used in cucumber feature definitions. Will be used to resolve the element
		 * by {@link PageDefinition#elementForName(String)}.
		 */
		public final String name;

		/** 
		 * The {@link By} criteria to find the element. 
		 */
		public final By by;

		/** 
		 * True if the element is required to be present on its page. 
		 */
		public final boolean required;

		/**
		 * Standard constructor.
		 * 
		 * @param name
		 *            the {@link #name} {@link String}.
		 * @param idOrName
		 *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object
		 *            to find the actual element on an actual page.
		 * @param required
		 *            the {@link #required} {@link Boolean}.
		 */
		private ElementDefinition(final String name, final String idOrName, final boolean required) {
			this.name = name;
			this.by = new ByIdOrName(idOrName);
			this.required = required;
		}

		/**
		 * Convenience constructor for required elements (sets {@link #required} to {@code true}).
		 * 
		 * @param name
		 *            the {@link #name} {@link String}.
		 * @param idOrName
		 *            the elemet's ID or name attribute value. Will be used to create the element's {@link #by} object
		 *            to find the actual element on an actual page.
		 */
		private ElementDefinition(final String name, final String idOrName) {
			this(name, idOrName, true);
		}

		@Override
		public String toString() {
			return "ElementDefinition [name=" + name + ", by=" + by + ", required=" + required + "]";
		}
	}
}
