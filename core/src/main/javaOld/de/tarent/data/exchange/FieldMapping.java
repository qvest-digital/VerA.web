package de.tarent.data.exchange;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2015 benja <benja@benja.com>
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
 *  © 2015 Christian Luginbühl <dinkel@pimprecords.com>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2015 Dmytro Pishchukhin <demon@Demons-MBP.fritz.box>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 rysiekpl <rysiek@hackerspace.pl>
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
 *  © 2015 hong Xu <hong@topbug.net>
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt eine Abbildung von Feldinhalten aufeinander dar.
 *
 * @author mikel
 */
public class FieldMapping {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor bekommt die Menge der verfügbaren Quellfelder und
     * eine Abbildung Zielfeldname &rarr; Formatstring mit Quellfeldreferenzen
     * übergeben. Quellfeldreferenzen haben allgemein die Form <b>{</b>BEREICH<b>:</b>FELD<b>}</b>,
     * im VerA.web-Fall insbesondere:<br>
     * <ul>
     * <li><b>{:</b>FELD<b>}</b> für Stammdaten von Personen
     * <li><b>{CAT:</b>KATEGORIE<b>}</b> für Kategoriezugehörigkeiten von Personen
     * <li><b>{EVE:</b>EREIGNIS<b>}</b> für Ereigniszugehörigkeiten von Personen
     * <li><b>{COR:</b>DIPL. CORPS<b>}</b> für Dipl.Corps-Zugehörigkeiten von Personen
     * <li><b>{DTM:</b>DOKUMENTTYP<b>}</b> für Dokumenttypfreitexte von Hauptpersonen
     * <li><b>{DTP:</b>DOKUMENTTYP<b>}</b> für Dokumenttypfreitexte von Partnerpersonen
     * </ul>
     * Sonderfälle sind Benutzung von Jokern '*' im Bezeichner; hier ist nur eine
     * Quellfeldreferenz im Formatstring erlaubt, weiterhin muß im Zielfeld
     * ebenfalls ein Joker vorkommen. Solche Mappings werden interpretiert als
     * eine Zuordnung für alle passenden bisher nicht zugeordneten Bezeichner
     * zu einem Zielfald, in dem der Joker durch den Bezeichnernamen ersetzt
     * wird.<br>
     * Im Falle konkurrierender Zuordnungen erhält die speziellere den Vorrang.
     *
     * @param availableSources verfügbare Quellen
     * @param mappingDescription Abbildung Zielfeldnamen auf Formatstrings mit
     *  Quellfeldreferenzen
     * @throws MappingException bei Problemen beim Auflösen der Mapping-Beschreibung
     * @throws NullPointerException falls ein Parameter <code>null</code> ist.
     */
    public FieldMapping(Set availableSources, Map mappingDescription) throws MappingException {
        this.availableSources = new HashSet(availableSources);
        this.mappingDescription = mappingDescription;
        parseDescription(mappingDescription);
    }

    /**
     * Dieser private Konstruktor wird von der Methode {@link #invert()} benutzt.
     *
     * @param availableSources verfügbare Quellen
     * @param availableTargets verfügbare Ziele
     * @param resolvedMapping aufgelöstes (Joker-freies) Mapping
     */
    private FieldMapping(Set availableSources, Set availableTargets, Map resolvedMapping, Map mappingDescription) {
        if (availableTargets == null)
            availableTargets = resolvedMapping.keySet();
        else
            assert resolvedMapping.keySet().containsAll(availableTargets);
        this.availableSources = new HashSet(availableSources);
        this.resolvedMappings = new HashMap(resolvedMapping);
        this.availableTargets = new HashSet(availableTargets);
        this.mappingDescription = new HashMap(mappingDescription);
    }

    //
    // Getter
    //
    /** verfügbare Quellfelder */
    public Set getSources() {
        return availableSources;
    }

    /** verfügbare Zielfelder */
    public Set getTargets() {
        return availableTargets;
    }

    //
    // Öffentliche Interfaces
    //
    /**
     * Diese Schnittstelle stellt Quellentitäten dar, aus deren Inhalten
     * {@link FieldMapping#resolve(String, Entity)} Zielfeldwerte erzeugt.
     */
    public interface Entity {
        /**
         * Diese Methode holt den Wert eines Quellfeldes bezüglich eines
         * Quellfeldschlüssels BEREICH<b>:</b>FELD.
         *
         * @param sourceKey Quellfeldschlüssel BEREICH<b>:</b>FELD
         * @return Wert des beschriebenen Quellfelds
         * @see FieldMapping#FieldMapping(Set, Map)
         */
        String get(String sourceKey);
    }

    //
    // Öffentliche Methoden
    //
    /**
     * Diese Methode ermittelt den Wert eines Zielfelds basierend auf
     * den Werten einer Entität.
     *
     * @param targetKey Zielfeldbezeichner
     * @param entity Entität, der die Quellfeldwerte zu entnehmen sind
     * @return ermittelter Zielfeldwert
     */
    public String resolve(String targetKey, Entity entity) {
        if (targetKey == null || entity == null || !resolvedMappings.containsKey(targetKey))
            return null;
        String format = resolvedMappings.get(targetKey).toString();
        Matcher matcher = refPattern.matcher(format);
        StringBuffer buffer = new StringBuffer();
        int from = 0;
        while (matcher.find()) {
            buffer.append(format.substring(from, matcher.start()))
                  .append(entity.get(matcher.group(1)));
            from = matcher.end();
        }
        if (from < format.length())
            buffer.append(format.substring(from));
        return buffer.toString();
    }

    /**
     * Gibt ein entpackten Wert entsprechend des übergeben Mappings an,
     * z.B. wird aus <code>Kat *</code> und <code>Kat Sample</code>
     * nur noch <code>Sample</code>. Beide Werte dürfen nicht null sein.
     *
     * Gibt null zurück wenn der String <code>value</code> nicht auf
     * <code>mapping</code> matchet.
     *
     * @param mapping FIXME
     * @param value FIXME
     * @return FIXME
     */
    public String resolve(String mapping, String value) {
    	int starlet = mapping.indexOf("*");
    	if (starlet == -1 || mapping.indexOf("*", starlet + 1) != -1)
    		return null;

    	String pre = mapping.substring(0, starlet);
    	String suf = mapping.substring(starlet + 1);

    	if (!(value.startsWith(pre) && value.endsWith(suf)))
    		return null;

    	return value.substring(starlet, value.length() - suf.length());
    }

    /**
     * Diese Methode liefert ein Fieldmapping, das zu diesem invers ist.<br>
     * Hierbei sind gewisse Einschränkungen zu beachten, deren Verletzung zu
     * ungeeigneten Mappings führt. Einerseits wird nur das erste Quellfeld
     * eines Mappings beachtet; weitere sollten also in einem weiteren Mapping
     * führend vorkommen. Andererseits sollte jedes Quellfeld nur einmal führend
     * vorkommen, um eine Eindeutigkeit zu gewährleisten.
     *
     * @return ein Feldmapping, das zu diesem invers ist.
     */
    public FieldMapping invert() {
        assert resolvedMappings != null;
        Map invertedMapping = new HashMap();
        for (Iterator itMappings = resolvedMappings.entrySet().iterator(); itMappings.hasNext(); ) {
            Map.Entry mapping = (Entry) itMappings.next();
            Matcher matcher = refPattern.matcher(mapping.getValue().toString());
            if (matcher.find())
                invertedMapping.put(matcher.group(1), "{" + mapping.getKey() + '}');
        }
        return new FieldMapping(availableTargets, invertedMapping.keySet(), invertedMapping, mappingDescription);
    }

    /**
     * Diese Methode erweitert das FieldMapping um Kategorien-Felder die im
     * Header übergeben wurde, aber noch nicht in der Datenbank vorhanden sind
     * und deswegen nicht in den {@link #availableSources} verfügbar waren.
     */
	public void extendCategoryImport(List extensions) {
		assert mappingDescription != null && resolvedMappings != null;

        // Map der aufgelösten Mappings
        Map finalMappings = new HashMap();
        finalMappings.putAll(resolvedMappings);

		for (Iterator it = mappingDescription.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry)it.next();
			String mappingMarker = entry.getKey().toString();
			String mappingTarget = entry.getValue().toString();

			if (mappingTarget == null || mappingMarker == null)
				continue;
			if (mappingTarget.startsWith("{") || mappingTarget.startsWith("}"))
				mappingTarget = mappingTarget.substring(1, mappingTarget.length() - 1);
			if (!(
					mappingTarget.startsWith("CAT:") ||
					mappingTarget.startsWith("EVE:") ||
					mappingTarget.startsWith("COR:")))
				continue;

			for (Iterator it2 = extensions.iterator(); it2.hasNext(); ) {
				String extension = it2.next().toString();
				String cleaned = resolve(mappingMarker, extension);
				if (cleaned == null) continue;
				String resolved = mappingTarget.replaceAll("\\*", cleaned);
				if (resolved == null) continue;

				finalMappings.put(resolved, "{" + extension + "}");
			}
		}

		resolvedMappings = Collections.unmodifiableMap(finalMappings);
		availableTargets = finalMappings.keySet();
	}

	//
    // geschützte Hilfsmethoden
    //
    /**
	 * Diese Methode ermittelt mittels der verfügbaren Quellfelder und der
	 * allgemeinen Abbildungsbeschreibung (die im Konstruktor
	 * {@link #FieldMapping(Set, Map)} übergeben wurden) die aufgelösten simplen
	 * Abbildungen und die verfügbaren Zielfelder.
	 *
	 * @param mappingDescription
	 *            die zu parsende Abbildungsbeschreibung
	 * @throws MappingException
	 *             bei Problemen beim Auflösen der Mapping-Beschreibung
	 */
    void parseDescription(Map mappingDescription) throws MappingException {
        assert availableSources != null;
        assert mappingDescription != null;
        // Menge der (bisher) unbenutzten Quellen --- wird für Joker-Auflösung genutzt
        Set unusedSources = new HashSet(availableSources);
        // Map Præfixlänge vor Joker in Quellbezeichnern (Integer) -> Jokerzielfeldbezeichner --- wird für Joker-Auflösung genutzt
        Map targetsByPrefixLength = new HashMap();
        // Zielfeldbezeichner in targetsByPrefixLength einsortieren
        for (Iterator itMappingEntries = mappingDescription.entrySet().iterator(); itMappingEntries.hasNext(); ) {
            Map.Entry entry = (Entry) itMappingEntries.next();
            String targetFieldKey = entry.getKey().toString();
            int jokerIndex = removeReferences(unusedSources, entry.getValue().toString());
            Integer prefixLength = new Integer(jokerIndex);
            Collection theseTargets = (Collection) targetsByPrefixLength.get(prefixLength);
            if (theseTargets == null)
                targetsByPrefixLength.put(prefixLength, theseTargets = new HashSet());
            theseTargets.add(targetFieldKey);
        }
        // Map der aufgelösten Mappings
        Map finalMappings = new HashMap();
        // Einfache (Joker-freie) Mappings übertragen
        if (targetsByPrefixLength.containsKey(MINUS_ONE)) {
            for (Iterator itSimpleMappingTargets = ((Collection)targetsByPrefixLength.get(MINUS_ONE)).iterator(); itSimpleMappingTargets.hasNext(); ) {
                String targetKey = itSimpleMappingTargets.next().toString();
                finalMappings.put(targetKey, mappingDescription.get(targetKey));
            }
            targetsByPrefixLength.remove(MINUS_ONE);
        }
        // Komplexe (Joker-behaftete) Mappings auflösen und übertragen
        if (targetsByPrefixLength.size() > 0) {
            Integer[] lengths = (Integer[]) targetsByPrefixLength.keySet().toArray(new Integer[targetsByPrefixLength.size()]);
            Arrays.sort(lengths);
            for (int index = lengths.length - 1; index >= 0; index--) {
                for (Iterator itMappingTargets = ((Collection)targetsByPrefixLength.get(lengths[index])).iterator(); itMappingTargets.hasNext(); ) {
                    String targetKey = itMappingTargets.next().toString();
                    String format = mappingDescription.get(targetKey).toString();
                    String jokerReferencePrefix = getJokerReferencePrefix(format);
                    Set toRemove = new HashSet();
                    for (Iterator itUnusedSources = unusedSources.iterator(); itUnusedSources.hasNext(); ) {
                        String source = itUnusedSources.next().toString();
                        if (source.startsWith(jokerReferencePrefix)) {
                            addResolvedMapping(finalMappings, targetKey, format, source);
                            toRemove.add(source);
                        }
                    }
                    unusedSources.removeAll(toRemove);
                }
                if (unusedSources.isEmpty())
                    break;
            }
        }
        resolvedMappings = Collections.unmodifiableMap(finalMappings);
        availableTargets = finalMappings.keySet();
    }

    /**
     * Diese Methode sucht zu einem Formatstring eine Referenz mit Joker
     * und liefert hierzu das Anfangsstück zurück.
     *
     * @param format dieser Formatstring wird auf Referenzen mit Joker durchsucht.
     * @return Anfangsstück der Referenz bis exklusive dem Joker, sonst <code>null</code>.
     */
    private static String getJokerReferencePrefix(String format) {
        assert format != null;
        Matcher matcher = refPattern.matcher(format);
        while (matcher.find()) {
            String ref = matcher.group(1);
            int jokerIndex = ref.indexOf('*');
            if (jokerIndex >= 0)
                return ref.substring(0, jokerIndex);
        }
        return null;
    }

    /**
     * Diese Methode hat eine doppelte Funktion; einerseits entfernt sie alle einfachen
     * (also nicht Joker-behafteten) Referenzen im Formatstring aus der Sammlung nicht
     * benutzter Referenzen, und andererseits liefert sie die Joker-Position in einer
     * gegebenenfalls vorhandenen Joker-behafteten Referenz zurück.
     *
     * @param unusedReferences Sammlung bisher nicht benutzter verfügbarere Referenzen
     * @param format zu behandelnder Formatstring
     * @return Joker-Position in der vorhandenen Joker-behafteten Referenz, sonst <code>-1</code>.
     * @throws MappingException bei Problemen beim Auflösen der Mapping-Beschreibung
     */
    private static int removeReferences(Collection unusedReferences, String format) throws MappingException {
        int jokerPrefixLength = -1;
        Matcher matcher = refPattern.matcher(format);
        while (matcher.find()) {
            String ref = matcher.group(1);
            int jokerIndex = ref.indexOf('*');
            if (jokerIndex < 0)
                unusedReferences.remove(ref);
            else if (jokerIndex != ref.length() - 1)
                throw new MappingException("es werden nur endständige Joker im Formatstring unterstützt");
            else if (jokerPrefixLength < 0)
                jokerPrefixLength = jokerIndex;
            else
                throw new MappingException("nur eine Referenz in einem Formatstring darf Joker enthalten");
        }
        return jokerPrefixLength;
    }

    /**
     * Diese Methode fügt den einfachen Mappings eines hinzu, das dem übergebenen mit
     * Joker behafteten nach Ersetzung mittels der übergebenen Quelle entspricht.
     *
     * @param resolvedMappings {@link Map} der aufgelösten einfachen Mappings
     * @param targetPattern Zielreferenz mit Joker
     * @param formatPattern Formatstring mit Joker-behafteter Quellreferenz
     * @param source Quellfeld zur Auflösung der Joker.
     */
    private static void addResolvedMapping(Map resolvedMappings, String targetPattern, String formatPattern, String source) {
        assert formatPattern != null;
        Matcher matcher = refPattern.matcher(formatPattern);
        while (matcher.find()) {
            String ref = matcher.group(1);
            int jokerIndex = ref.indexOf('*');
            if (jokerIndex >= 0) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(formatPattern.substring(0, matcher.start(1)))
                      .append(source)
                      .append(formatPattern.substring(matcher.end(1)));
                int colonIndex = source.indexOf(':');
                if (colonIndex >= 0)
                    source = source.substring(colonIndex + 1);
                targetPattern = targetPattern.replaceAll("\\*", source);
                Object former = resolvedMappings.put(targetPattern, buffer.toString());
                if (former != null)
                    logger.warning("Ersetze altes Format '" + former + "' für '" + targetPattern + "' durch '" + buffer.toString() + "'.");
                return;
            }
        }
    }

    //
    // geschützte Member
    //
    final Set availableSources;
    final Map mappingDescription;
    Map resolvedMappings = null;
    Set availableTargets = null;
    final static Pattern refPattern = Pattern.compile("\\{([^{}]*)\\}");
    final static Integer MINUS_ONE = new Integer(-1);
    final static Logger logger = Logger.getLogger(FieldMapping.class.getName());
}
