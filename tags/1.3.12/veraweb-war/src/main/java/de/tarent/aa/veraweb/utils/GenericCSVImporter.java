/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonCategorie;
import de.tarent.aa.veraweb.beans.ImportPersonDoctype;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.MappingException;
import de.tarent.data.exchange.FieldMapping.Entity;
import de.tarent.octopus.beans.BeanException;
import de.tarent.utils.CSVFileReader;

/**
 * Diese Klasse implementiert einen generischen CSV-Import von VerA.web-Personen.
 * 
 * @author mikel
 */
public class GenericCSVImporter extends GenericCSVBase implements Importer {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor ist leer; dieser wird von {@link ExchangeFormat#getImporterClass()}
     * genutzt.
     */
    public GenericCSVImporter() {
    }

    //
    // Schnittstelle Importer
    //
    /**
     * Diese Methode f�hrt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatens�tze und Zus�tze nacheinander dem �bergebenen 
     * {@link ImportDigester} �bergeben.
     * 
     * @param digester der {@link ImportDigester}, der die Datens�tze weiter
     *  verarbeitet.
     * @throws IOException 
     * @see de.tarent.aa.veraweb.utils.Importer#importAll(de.tarent.aa.veraweb.utils.ImportDigester)
     */
    public void importAll(ImportDigester digester) throws IOException {
        if (exchangeFormat == null)
            throw new IOException("F�r einen Import muss ein Format angegeben sein.");
        if (exchangeFormat.getProperties() == null)
            throw new IOException("F�r einen Import m�ssen in der Formatspezifikation die notwendigen Parameter angegeben sein.");
        if (inputStream == null)
            throw new IOException("F�r einen Import muss ein Eingabedatenstrom angegeben sein.");

        try {
            readProperties();
            parseFormat(false);
            initReader();
            readHeader();
            fieldMapping.extendCategoryImport(headers);
            importRows(digester);
            csvReader.close();
        } catch (MappingException e) {
            IOException ioe = new IOException("Fehler im Feldmapping");
            ioe.initCause(e);
            throw ioe;
        } catch (BeanException e) {
            IOException ioe = new IOException("Fehler beim Daten-Bean-Zugriff");
            ioe.initCause(e);
            throw ioe;
        }
    }

    //
    // gesch�tzte Hilfsmethoden
    //
    /**
     * Diese Methode initialisiert den internen {@link CSVFileReader}.
     */
    void initReader() throws UnsupportedEncodingException, IOException {
        assert exchangeFormat != null;
        assert inputStream != null;
        Reader reader = new InputStreamReader(inputStream, encoding);
        csvReader = new CSVFileReader(reader, fieldSeparator, textQualifier);
    }
    
    /**
     * Diese Methode liest die n�chste Zeile als Kopfzeile mit den Spaltennamen ein.
     * Diese werden lokal in {@link #headers} abgelegt. 
     * 
     * @throws IOException
     */
    void readHeader() throws IOException {
        assert headers == null;
        headers = csvReader.readFields();
    }
    
    /**
     * Diese Methode importiert alle Datenzeilen der CSV-Datei in den �bergebenen
     * {@link ImportDigester}. Hierbei wird vorausgesetzt, dass die f�hrende Zeile
     * mit den Spaltennamen schon mit {@link #readHeader()} eingelesen wurde.  
     * 
     * @param digester der {@link ImportDigester}, der die Datens�tze weiter
     *  verarbeitet.
     * @throws IOException
     * @throws BeanException
     */
    void importRows(ImportDigester digester) throws IOException, BeanException {
        assert headers != null;
        assert digester != null;
        RowEntity row = new RowEntity();
        List rawRow;
        digester.startImport();
        while ((rawRow = csvReader.readFields()) != null) {
            row.parseRow(rawRow);
            digestRow(digester, row);
        }
        digester.endImport();
    }
    
    /**
     * Diese Methode verarbeitet eine einzelne aufbereitete Zeile; das bedeutet, dass eine
     * entsprechende {@link ImportPerson} und gegebenenfalls {@link ImportPersonCategorie}-
     * und {@link ImportPersonDoctype}-Instanzen erstellt werden und dem �bergebenen
     * {@link ImportDigester} als neue Person weitergereicht werden.<br>
     * TODO: Timestamp-Format konfigurierbar machen
     * 
     * @param digester der {@link ImportDigester}, der die Datens�tze weiter
     *  verarbeitet.
     * @param row die aufbereitete CSV-Zeile
     * @throws BeanException
     * @throws IOException
     */
    void digestRow(ImportDigester digester, RowEntity row) throws BeanException, IOException {
        ImportPerson person = new ImportPerson();
        Map extras = new HashMap();
        for (Iterator itTargetFields = fieldMapping.getTargets().iterator(); itTargetFields.hasNext(); ) {
            String targetField = itTargetFields.next().toString();
            if (targetField != null && targetField.length() > 0) {
                String targetValue = fieldMapping.resolve(targetField, row);
                if (targetValue != null) {
                	targetValue = targetValue.trim();
                }
                if (targetField.charAt(0) == ':') {
                    Object targetObject = targetValue;
                    Class fieldClass = person.getFieldClass(targetField.substring(1));
                    if (Date.class.isAssignableFrom(fieldClass)) {
                        try {
                        	if (targetValue != null && targetValue.length() > 0) {
	                            Date dateValue = dateFormat.parse(targetValue.toString()); 
	                            Constructor constructor = fieldClass.getConstructor(ONE_LONG);
	                            targetObject = constructor.newInstance(new Object[] {new Long(dateValue.getTime())});
                        	} else {
                        		targetObject = null;
                        	}
                        } catch (NoSuchMethodException e) {
                            logger.log(Level.WARNING, "Datums-Klasse ohne (long)-Konstruktor", e);
                        } catch (ParseException e) {
                            logger.log(Level.WARNING, "Datum nicht parse-bar", e);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Datumsklasse nicht instanziierbar", e);
                        }
                    }
//                    if (person.getFieldClass(targetField.substring(1)).isAssignableFrom(Timestamp.class))
//                        try {
//                            targetObject = Timestamp.valueOf(targetValue);
//                        } catch(IllegalArgumentException iae) {}
                    person.put(targetField.substring(1), targetObject);
                }
                else if (targetField.startsWith("CAT:"))
                    addCategory(targetField.substring(4), targetValue, Categorie.FLAG_DEFAULT, extras);
                else if (targetField.startsWith("EVE:"))
                	addCategory(targetField.substring(4), targetValue, Categorie.FLAG_EVENT, extras);
                else if (targetField.startsWith("COR:"))
                	addCategory(targetField.substring(4), targetValue, Categorie.FLAG_DIPLO_CORPS, extras);
                else if (targetField.startsWith("DTM:"))
                    addDoctypeMain(targetField.substring(4), targetValue, extras);
                else if (targetField.startsWith("DTP:"))
                    addDoctypePartner(targetField.substring(4), targetValue, extras);
                else
                    logger.warning("Unbekanntes Zielfeld " + targetField);
            }
        }
        digester.importPerson(person, new ArrayList(extras.values()));
    }
    
    /**
     * Diese Methode f�gt der �bergebenen Sammlung von Import-Extras eine neue
     * {@link ImportPersonCategorie} hinzu oder --- falls es schon eine unter
     * dem Namen gibt --- aktualisiert diese. 
     * 
     * @param name Name der Kategorie
     * @param rank Rang in der Kategorie; <code>""</code> bedeutet keine Zuordnung
     *  zu der Kategorie
     * @param extras Map mit Import-Extras.
     */
    void addCategory(String name, String rank, int flags, Map extras) {
        assert name != null;
        assert extras != null;
        
        if (rank == null || rank.length() == 0)
        	return;
        
        Integer rankNumber;
        try {
       		rankNumber = new Integer(rank);
        } catch (NumberFormatException nfe) {
        	if (rank.toUpperCase().equals(DEFAULT_RANK.toUpperCase())) {
        		rankNumber = null;
        	} else {
//        		logger.warning("Ung�ltiger Kategorie-Rang beim Import einer CSV-Datei. Rang: " + rank);
        		return;
        	}
        }
        
        String catKey;
        if (flags == Categorie.FLAG_EVENT) {
        	catKey = "EVE:" + name;
        } else if (flags == Categorie.FLAG_DIPLO_CORPS) {
        	catKey = "COR:" + name;
        } else {
        	catKey = "CAT:" + name;
        }
        
        if (extras.containsKey(catKey)) {
            ImportPersonCategorie category = (ImportPersonCategorie) extras.get(catKey);
            if (rankNumber != null && category.rank != null && rankNumber.intValue() > category.rank.intValue())
                category.rank = rankNumber;
        } else {
            ImportPersonCategorie category = new ImportPersonCategorie();
            category.name = name;
            category.rank = rankNumber;
            category.flags = new Integer(flags);
            extras.put(catKey, category);
        }
    }
    
    /**
     * Diese Methode f�gt der �bergebenen Sammlung von Import-Extras einen neuen
     * {@link ImportPersonDoctype} hinzu oder --- falls es schon einen unter
     * dem Namen gibt --- aktualisiert diesen. 
     * 
     * @param name Name des Dokumenttyps
     * @param text Hauptpersonfreitext des Dokumenttyps
     * @param extras Map mit Import-Extras.
     */
    void addDoctypeMain(String name, String text, Map extras) {
        assert name != null;
        assert text != null;
        assert extras != null;
        String docKey = "D" + name;
        if (text == null || text.trim().length() == 0)
            return;
        
        if (extras.containsKey(docKey)) {
            ImportPersonDoctype doctype = (ImportPersonDoctype) extras.get(docKey);
            if (doctype.textfield == null || doctype.textfield.length() == 0)
                doctype.textfield = text;
        } else {
            ImportPersonDoctype doctype = new ImportPersonDoctype();
            doctype.name = name;
            doctype.textfield = text;
            extras.put(docKey, doctype);
        }
    }
    
    /**
     * Diese Methode f�gt der �bergebenen Sammlung von Import-Extras einen neuen
     * {@link ImportPersonDoctype} hinzu oder --- falls es schon einen unter
     * dem Namen gibt --- aktualisiert diesen. 
     * 
     * @param name Name des Dokumenttyps
     * @param text Partnerpersonfreitext des Dokumenttyps
     * @param extras Map mit Import-Extras.
     */
    void addDoctypePartner(String name, String text, Map extras) {
        assert name != null;
        assert text != null;
        assert extras != null;
        String docKey = "D" + name;
        if (text == null || text.trim().length() == 0)
            return;
        
        if (extras.containsKey(docKey)) {
            ImportPersonDoctype doctype = (ImportPersonDoctype) extras.get(docKey);
            if (doctype.textfieldPartner == null || doctype.textfieldPartner.length() == 0)
                doctype.textfieldPartner = text;
        } else {
            ImportPersonDoctype doctype = new ImportPersonDoctype();
            doctype.name = name;
            doctype.textfieldPartner = text;
            extras.put(docKey, doctype);
        }
    }
    
    //
    // gesch�tzte innere Klassen
    //
    /**
     * Diese Klasse stellt eine CSV-Zeile als {@link de.tarent.data.exchange.FieldMapping.Entity}
     * dar.
     */
    class RowEntity implements Entity {
        //
        // Schnittstelle Entity
        //
        /**
         * Diese Methode erlaubt das Abfragen von Daten zu einem bestimmten Schl�ssel.
         * Die Schl�ssel sind die CSV-Spaltennamen.
         * 
         * @param sourceKey Quellfeldschl�ssel
         * @return Quellfeldwert als {@link String}; Werte unbekannter Felder werden
         *  als Leerstring <code>""</code> geliefert.
         */
        public String get(String sourceKey) {
            Object result = rowMapping.get(sourceKey);
            return result != null ? result.toString() : "";
        }
        
        //
        // �ffentliche Methoden
        //
        /**
         * Diese Methode parse-t die �bergebene Liste --- die aktuelle Zeile ---
         * und legt die Werte unter dem zugeh�rigen Spaltennamen lokal ab.<br>
         * TODO: Warnung bei zu langen oder zu kurzen Zeilen
         * 
         * @param row Liste der Werte der aktuellen Zeile
         */
        public void parseRow(List row) {
            rowMapping.clear();
            if (row != null) {
                Iterator itHeaders = headers.iterator();
                Iterator itRowFields = row.iterator();
                while (itHeaders.hasNext() && itRowFields.hasNext())
                    rowMapping.put(itHeaders.next(), itRowFields.next());
            }
        }
        
        //
        // gesch�tzte Member
        //
        /** Hier werden die Zuordnungen der aktuellen Zeile gehalten */
        final Map rowMapping = new HashMap();
    }
    
    //
    // gesch�tzte Membervariablen
    //
    /** CSV-Eingabe-Objekt */
    CSVFileReader csvReader = null;
    /** Import-Targets */
    List targets = null;
    /** Header-Felder */
    List headers = null;
    final static Class[] ONE_LONG = new Class[] {Long.TYPE}; 
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger(GenericCSVImporter.class.getName());
}
