/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
 * 
 * Created on 31.05.2005
 */
package de.tarent.aa.veraweb.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

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
                if (logger.isInfoEnabled())
                    logger.info("Leeres Mapping " + mapping);
                continue;
            }
            info[0] = mapping.getKey().toString();
            List target = (List) targets.get(targetField);
            if (target == null)
                targets.put(targetField, target = new ArrayList());
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
                            while (index >= orderedSources.size())
                                orderedSources.add(null);
                            orderedSources.set(index, sourceField);
                            continue;
                        }
                    } catch(NumberFormatException nfe) {
                    }
                    logger.warn("Ungültiger Index " + info[1] + " im Mapping von " + sourceField + "; Indexangabe wird ignoriert");
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
                if (source == null || source.length() == 0)
                    continue;
                if (format.length() > 0)
                    format.append(' ');
                if (source.charAt(0) == ':')
                    format.append(source.substring(1));
                else {
                    int index = sourceFields.indexOf(source);
                    if (index < 0) {
                        index = sourceFields.size();
                        sourceFields.add(source);
                    }
                    format.append('{').append(index).append('}');
                    requiredFields.add(source);
                    if (primeSource == null)
                        primeSource = source;
                }
            }
            targetRequiredFields.put(targetField, requiredFields);
            targetFormats.put(targetField, new MessageFormat(format.toString()));
            if (primeSource != null)
                sourcePrimeTargets.put(primeSource, targetField);
        }
        // Zielfeldliste erzeugen
        targetFields.addAll(targetFormats.keySet());
    }
    
    //
    // öffentliche Methoden
    //
    /**
     * Diese Methode setzt die Namen der Spalten der einkommenden Zeilen.
     * 
     * @param incomingSourceFields Spaltennamen der einkommenden Zeilen in
     *  der einkommenden Reihenfolge.
     */
    public void setIncomingSourceFields(List incomingSourceFields) {
        if (incomingSourceFields == null)
            incomingSourceFields = Collections.EMPTY_LIST;
        formatToIncomingIndex = new int[sourceFields.size()];
        for (int i = 0; i < formatToIncomingIndex.length; i++) {
            formatToIncomingIndex[i] = incomingSourceFields.indexOf(sourceFields.get(i));
        }
    }
    
    /**
     * Diese Methode setzt die aktuelle Zeile.
     * 
     * @param rowFields Felder der aktuellen Zeile in der mit {@link #setIncomingSourceFields(List)}
     *  festgelegten Reihenfolge.
     */
    public void setRow(List rowFields) {
        if (formatToIncomingIndex == null)
            setIncomingSourceFields(null);
        if (rowFields == null)
            rowFields = Collections.EMPTY_LIST;
        row = new Object[formatToIncomingIndex.length];
        for (int i = 0; i < row.length; i++) {
            int incomingIndex = formatToIncomingIndex[i];
            if (incomingIndex < 0 || incomingIndex > rowFields.size())
                row[i] = null;
            else
                row[i] = rowFields.get(incomingIndex);
            if (row[i] == null)
            	row[i] = "";
        }
    }
    
    /**
     * Diese Methode liefert den Wert zu einem Zielfeld aus der aktuellen Zeile. 
     * 
     * @param targetField Spaltenname des Zielfelds, das aus der aktuellen
     *  Zeile ausgelesen werden soll
     * @return Wert des Zielfelds aus der aktuellen Zeile berechnet
     */
    public String getValue(String targetField) {
        if (targetField == null || !targetFields.contains(targetField) || row == null)
            return null;
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
     *  benötigten Quellfelder
     */
    public Set getRequiredSources(Collection targets) {
        if (targets == null || targets.isEmpty())
            return Collections.EMPTY_SET;
        Set result = new HashSet();
        Iterator itTargets = targets.iterator();
        while (itTargets.hasNext()) {
            Object target = itTargets.next();
            if (targetRequiredFields.containsKey(target))
                result.addAll((Collection) targetRequiredFields.get(target));
        }
        return result;
    }
    
    /**
     * Diese Methode liefert zu einem Quellfeld das Zielfeld, in dem es die
     * Hauptquelle ist, d.h. das erste Feld im Format.
     * 
     * @param sourceField Spaltenname des Quellfelds, zu dem das Primärzielfeld
     *  gesucht wird
     * @return Spaltenname des Zielfelds, in dem das angegebene Quellfeld primär
     *  ist, oder <code>null</code>.
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
    /** aktuelle Zeile in der Sortierung von {@link #sourceFields} */
    private Object[] row = null;
    /** Abbildung Formatindex auf einkommender Index */
    private int[] formatToIncomingIndex = null;
    /** Liste der verwendeten Quellfelder in der Reihenfolge, die in den Formaten gefordert ist */
    private List sourceFields = new ArrayList();
    /** Liste der verfügbaren Zielfelder */
    private List targetFields = new ArrayList();
    /** Sets benötigter Quellfelder zu den verfügbaren Zielfeldern */
    private Map targetRequiredFields = new HashMap();
    /** MessageFormat-Instanzen zu den verfügbaren Zielfeldern */
    private Map targetFormats = new HashMap();
    /** Zielfeld zu den verfügbaren Quellfeldern, in dem diese führend sind */
    private Map sourcePrimeTargets = new HashMap();
    /** Logger für diese Klasse */
    private final static Logger logger = Logger.getLogger(FieldMapping.class);
}
