package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Diese Klasse liefert ein Feld-Mapping, das allgemeiner ist als 1:1.
 *
 * @author mikel
 */
public class FieldMapping {
    //
    // Konstruktoren
    //

    /**
     * Dieser Konstruktor bekommt ein simples Mapping übergeben. Hierbei wird
     * ein Quellfeld auf ein Zielfeld erweitert um Reihenfolgen- und Optionsangaben
     * angebildet: <code>Zielfeldname[:0-basierter Index[:Optionen]]</code><br>
     * Als Schlüssel kann statt eines Quellfelds auch ein Ausdruck <code>:Wert</code>
     * benutzt werden. Hierbei darf allerdings in Wert kein Ausdruck vorkommen, der
     * in einer {@link MessageFormat}-Instanz Probleme bereiten könnte.
     *
     * @param mappingDescription FIXME
     */
    public FieldMapping(Map mappingDescription) {
        assert mappingDescription != null;
        // nach Zielfeld sortieren
        Map targets = new HashMap();
        Iterator itMapping = mappingDescription.entrySet().iterator();
        while (itMapping.hasNext()) {
            Map.Entry mapping = (Entry) itMapping.next();
            String[] info = (mapping.getValue() != null ? mapping.getValue().toString() : "").split(":");
            String targetField = info[0];
            if (targetField.length() == 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Leeres Mapping " + mapping);
                }
                continue;
            }
            info[0] = mapping.getKey().toString();
            List target = (List) targets.get(targetField);
            if (target == null) {
                targets.put(targetField, target = new ArrayList());
            }
            target.add(info);
        }
        // Quellfeldliste und Formate für Zielfelder erstellen
        Iterator itTargets = targets.entrySet().iterator();
        List orderedSources = new ArrayList();
        Set unorderedSources = new HashSet();
        while (itTargets.hasNext()) {
            Map.Entry targetEntry = (Entry) itTargets.next();
            String targetField = (String) targetEntry.getKey();
            List infos = (List) targetEntry.getValue();
            orderedSources.clear();
            unorderedSources.clear();
            Iterator itInfos = infos.iterator();
            // angegebene Quellen sortieren, nicht-indizierte hinter die indizierten
            while (itInfos.hasNext()) {
                String[] info = (String[]) itInfos.next();
                String sourceField = info[0];
                if (info.length > 1) {
                    try {
                        int index = Integer.parseInt(info[1]);
                        if (index >= 0) {
                            while (index >= orderedSources.size()) {
                                orderedSources.add(null);
                            }
                            orderedSources.set(index, sourceField);
                            continue;
                        }
                    } catch (NumberFormatException nfe) {
                    }
                    logger.warn("Ungültiger Index " + info[1] + " im Mapping von " +
                            sourceField + "; Indexangabe wird ignoriert");
                }
                unorderedSources.add(sourceField);
            }
            orderedSources.addAll(unorderedSources);
            // Format erzeugen, dabei Quellfeldliste erweitern
            StringBuffer format = new StringBuffer();
            Set requiredFields = new HashSet();
            Iterator itSources = orderedSources.iterator();
            String primeSource = null;
            while (itSources.hasNext()) {
                String source = (String) itSources.next();
                if (source == null || source.length() == 0) {
                    continue;
                }
                if (format.length() > 0) {
                    format.append(' ');
                }
                if (source.charAt(0) == ':') {
                    format.append(source.substring(1));
                } else {
                    int index = sourceFields.indexOf(source);
                    if (index < 0) {
                        index = sourceFields.size();
                        sourceFields.add(source);
                    }
                    format.append('{').append(index).append('}');
                    requiredFields.add(source);
                    if (primeSource == null) {
                        primeSource = source;
                    }
                }
            }
            targetRequiredFields.put(targetField, requiredFields);
            targetFormats.put(targetField, new MessageFormat(format.toString()));
            if (primeSource != null) {
                sourcePrimeTargets.put(primeSource, targetField);
            }
        }
        // Zielfeldliste erzeugen
        targetFields.addAll(targetFormats.keySet());
    }

    //
    // Öffentliche Methoden
    //

    /**
     * Diese Methode setzt die Namen der Spalten der einkommenden Zeilen.
     *
     * @param incomingSourceFields Spaltennamen der einkommenden Zeilen in
     *                             der einkommenden Reihenfolge.
     */
    public void setIncomingSourceFields(List incomingSourceFields) {
        if (incomingSourceFields == null) {
            incomingSourceFields = Collections.EMPTY_LIST;
        }
        formatToIncomingIndex = new int[sourceFields.size()];
        for (int i = 0; i < formatToIncomingIndex.length; i++) {
            formatToIncomingIndex[i] = incomingSourceFields.indexOf(sourceFields.get(i));
        }
    }

    /**
     * Diese Methode setzt die aktuelle Zeile.
     *
     * @param rowFields Felder der aktuellen Zeile in der mit {@link #setIncomingSourceFields(List)}
     *                  festgelegten Reihenfolge.
     */
    public void setRow(List rowFields) {
        if (formatToIncomingIndex == null) {
            setIncomingSourceFields(null);
        }
        if (rowFields == null) {
            rowFields = Collections.EMPTY_LIST;
        }
        row = new Object[formatToIncomingIndex.length];
        for (int i = 0; i < row.length; i++) {
            int incomingIndex = formatToIncomingIndex[i];
            if (incomingIndex < 0 || incomingIndex > rowFields.size()) {
                row[i] = null;
            } else {
                row[i] = rowFields.get(incomingIndex);
            }
            if (row[i] == null) {
                row[i] = "";
            }
        }
    }

    /**
     * Diese Methode liefert den Wert zu einem Zielfeld aus der aktuellen Zeile.
     *
     * @param targetField Spaltenname des Zielfelds, das aus der aktuellen
     *                    Zeile ausgelesen werden soll
     * @return Wert des Zielfelds aus der aktuellen Zeile berechnet
     */
    public String getValue(String targetField) {
        if (targetField == null || !targetFields.contains(targetField) || row == null) {
            return null;
        }
        MessageFormat format = (MessageFormat) targetFormats.get(targetField);
        return format.format(row).trim();
    }

    /**
     * Diese Methode liefert eine Liste der Quellfeldnamen.
     *
     * @return Liste der Spaltennamen der Quellfelder.
     */
    public List getSourceFields() {
        return Collections.unmodifiableList(sourceFields);
    }

    /**
     * Diese Methode liefert eine Liste der Zielfeldnamen.
     *
     * @return Liste der Spaltennamen der Zielfelder.
     */
    public List getTargetFields() {
        return Collections.unmodifiableList(targetFields);
    }

    /**
     * Diese Methode liefert zu einer Sammlung von Zielfeldern die Menge
     * der für sie benötigten Quellfelder.
     *
     * @param targets Sammlung von Spaltennamen der betrachteten Zielfelder
     * @return Menge der Spaltennamen der für die betrachteten Zielfelder
     * benötigten Quellfelder
     */
    public Set getRequiredSources(Collection targets) {
        if (targets == null || targets.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set result = new HashSet();
        Iterator itTargets = targets.iterator();
        while (itTargets.hasNext()) {
            Object target = itTargets.next();
            if (targetRequiredFields.containsKey(target)) {
                result.addAll((Collection) targetRequiredFields.get(target));
            }
        }
        return result;
    }

    /**
     * Diese Methode liefert zu einem Quellfeld das Zielfeld, in dem es die
     * Hauptquelle ist, d.h. das erste Feld im Format.
     *
     * @param sourceField Spaltenname des Quellfelds, zu dem das Primärzielfeld
     *                    gesucht wird
     * @return Spaltenname des Zielfelds, in dem das angegebene Quellfeld primär
     * ist, oder <code>null</code>.
     */
    public String getPrimeTarget(String sourceField) {
        return (String) sourcePrimeTargets.get(sourceField);
    }

    /**
     * Diese Methode liefert die Menge der Quellfelder, die in einem Zielfeld
     * primär sind.
     *
     * @return Menge der Namen Hauptquellfelder
     */
    public Set getPrimeTargetSources() {
        return sourcePrimeTargets.keySet();
    }

    //
    // geschützte Member
    //
    /**
     * aktuelle Zeile in der Sortierung von {@link #sourceFields}
     */
    private Object[] row = null;
    /**
     * Abbildung Formatindex auf einkommender Index
     */
    private int[] formatToIncomingIndex = null;
    /**
     * Liste der verwendeten Quellfelder in der Reihenfolge, die in den Formaten gefordert ist
     */
    private List sourceFields = new ArrayList();
    /**
     * Liste der verfügbaren Zielfelder
     */
    private List targetFields = new ArrayList();
    /**
     * Sets benötigter Quellfelder zu den verfügbaren Zielfeldern
     */
    private Map targetRequiredFields = new HashMap();
    /**
     * MessageFormat-Instanzen zu den verfügbaren Zielfeldern
     */
    private Map targetFormats = new HashMap();
    /**
     * Zielfeld zu den verfügbaren Quellfeldern, in dem diese führend sind
     */
    private Map sourcePrimeTargets = new HashMap();
    /**
     * Logger für diese Klasse
     */
    private final static Logger logger = LogManager.getLogger(FieldMapping.class);
}
