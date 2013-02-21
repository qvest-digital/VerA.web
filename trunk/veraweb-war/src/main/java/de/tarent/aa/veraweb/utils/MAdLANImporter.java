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
 * Created on 01.09.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.octopus.beans.BeanException;

/**
 * Diese Klasse dient dem Import eines MAdLAN-CSV-Exports über den
 * {@link ExchangeFormat}-Mechanismus.
 * 
 * @author mikel
 */
public class MAdLANImporter implements Importer, Exchanger {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor ist {@link ExchangeFormat}-kompatibel parameterlos.
     */
    public MAdLANImporter() {
    }

    //
    // Schnittstelle Exchanger
    //
    /**
     * Das zu verwendende Austauschformat.
     * 
     * @see de.tarent.data.exchange.Exchanger#getExchangeFormat()
     */
    public ExchangeFormat getExchangeFormat() {
        return format;
    }
    /**
     * Das zu verwendende Austauschformat.
     * 
     * @see de.tarent.data.exchange.Exchanger#setExchangeFormat(de.tarent.data.exchange.ExchangeFormat)
     */
    public void setExchangeFormat(ExchangeFormat format) {
        this.format = format;
    }

    /**
     * Der zu verwendende Eingabedatenstrom
     * 
     * @see de.tarent.data.exchange.Exchanger#getInputStream()
     */
    public InputStream getInputStream() {
        return inputStream;
    }
    /**
     * Der zu verwendende Eingabedatenstrom
     * 
     * @see de.tarent.data.exchange.Exchanger#setInputStream(java.io.InputStream)
     */
    public void setInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    /**
     * Der zu verwendende Ausgabedatenstrom --- wird hier nicht genutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#getOutputStream()
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }
    /**
     * Der zu verwendende Ausgabedatenstrom --- wird hier nicht genutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#setOutputStream(java.io.OutputStream)
     */
    public void setOutputStream(OutputStream stream) {
        this.outputStream = stream;
    }

    //
    // Schnittstelle Importer
    //
    /**
     * Diese Methode führt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatensätze und Zusätze nacheinander dem übergebenen 
     * {@link ImportDigester} übergeben.
     * 
     * @param digester der {@link ImportDigester}, der die Datensätze weiter
     *  verarbeitet.
     * @see de.tarent.aa.veraweb.utils.Importer#importAll(de.tarent.aa.veraweb.utils.ImportDigester)
     */
    public void importAll(ImportDigester digester) throws IOException {
        if (format == null)
            throw new IOException("Für einen Import muss ein Format angegeben sein.");
        if (format.getProperties() == null)
            throw new IOException("Für einen Import müssen in der Formatspezifikation die MAdLAN-Parameter angegeben sein.");
        if (inputStream == null)
            throw new IOException("Für einen Import muss ein Eingabedatenstrom angegeben sein.");
        
        String madlanFileEncoding = (String) format.getProperties().get("madlanFileEncoding");
        if (madlanFileEncoding == null || madlanFileEncoding.length() == 0)
            madlanFileEncoding = "ISO-8859-1"; // Einbettung Latin-1 in Unicode
        Reader reader = new InputStreamReader(inputStream, madlanFileEncoding);
        
        //Teste die Gültigkeit der Konfiguration
        List requiredFields = (List) format.getProperties().get("importRequiredFields");
        List fitDateFields = (List)format.getProperties().get("fitDateFields");
        List setNullFields = (List) format.getProperties().get("setNullFields");
        Map rawMadlanFieldMapping = (Map) format.getProperties().get("fieldMapping");
        if (rawMadlanFieldMapping == null)
            throw new IOException("Es wurde keine gültige Feldzuordnung angegeben.");
        FieldMapping mapping = new FieldMapping(rawMadlanFieldMapping);
        Object test = isSubset(mapping.getTargetFields(), requiredFields);
        if (test != null)
            throw new IOException("Datenbankfeld \""+test+"\" wird benötigt, fehlt aber in der konfigurierten Mapping-Definition.");
        test = isSubset((new ImportPerson()).getFields(), mapping.getTargetFields());
        if (test != null)
            throw new IOException("Datenbankfeld \""+test+"\" wird in der Mapping-Definition beschrieben, fehlt aber in den "+ImportPerson.class.getName()+"-Bean-Parametern.");
        
        //Beginne mit dem Parsen (Auswertung des Headers)
        MadlanReader mr = new MadlanReader(reader, format);
        List header = mr.getHeader();
        logger.info("Madlan-Header: " + header);
        mapping.setIncomingSourceFields(header);
        
        //Teste die Gültigkeit der MAdLAN-Datei
        test = isSubset(header, mapping.getRequiredSources(requiredFields));
        if (test != null)
            throw new IOException("Importfeld \"" + test + "\" wird benötigt, fehlt aber in der Madlan-Datei.");
        
        try {
            digester.startImport();
            //Weiter mit dem Parsen (Iteration über die Datensätze in der Matlan-Datei).
            List row;
            while ((row = mr.readRow()) != null) {
                //erzeuge neues Datensatz-Bean und setze Import-Identifikation
                ImportPerson importPerson = new ImportPerson();
                
                //setze Feldwerte und führe Abbildung der Feldbezeichner durch
                mapping.setRow(row);
                Iterator it = mapping.getTargetFields().iterator();
                while (it.hasNext()) {
                    String key = (String)it.next();
                    importPerson.setField(key, mapping.getValue(key));
                }
                
                //Struktur eines Datumfelds wenn gewünscht anpassen
                if (fitDateFields != null) {
                    it = fitDateFields.iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        Timestamp value = (Timestamp) importPerson.getField(key);
                        importPerson.setField(key, fitDateField(value));
                    }
                }
                
                //leere Felder wenn gewünscht auf NULL setzen
                if (setNullFields != null) {
                    it = setNullFields.iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if ("".equals(importPerson.getField(key)))
                            importPerson.setField(key, null);
                    }
                }
                
                // Die Geschaeftsanschrift/Latin beinhaltet evtl. mehrzeilige Daten.
                // Hierbei handelt es sich um weitere Firmenanschriften, die hier
                // in die Private bzw. Weitere Anschrift/Latin einsortiert werden.
                
            	String company = importPerson.getBusinessLatin().getCompany();
                if (company != null) {
                	company = company.trim();
               		String ca[] = company.split("[\r\n]");
               		List cl = new ArrayList();
               		for (int i = 0; i < ca.length; i++)
               			if (ca[i] != null && ca[i].trim().length() != 0)
               				cl.add(ca[i].trim());
                	
                	if (cl.size() == 0) {
                   		importPerson.company_a_e1 = "";
                   		importPerson.company_b_e1 = "";
                   		importPerson.company_c_e1 = "";
                	} else if (cl.size() == 1) {
                   		importPerson.company_a_e1 = (String)cl.get(0);
                   		importPerson.company_b_e1 = "";
                   		importPerson.company_c_e1 = "";
                	} else if (cl.size() == 2) {
                   		importPerson.company_a_e1 = (String)cl.get(0);
                   		importPerson.company_b_e1 = (String)cl.get(1);
                   		importPerson.company_c_e1 = "";
                	} else if (cl.size() >= 3) {
                   		importPerson.company_a_e1 = (String)cl.get(0);
                   		importPerson.company_b_e1 = (String)cl.get(1);
                   		StringBuffer sb = new StringBuffer();
                   		for (Iterator clit = cl.iterator(); clit.hasNext(); ) {
                   			sb.append((String)clit.next());
                   			if (clit.hasNext())
                   				sb.append(", ");
                   		}
                   		importPerson.company_c_e1 = sb.toString();
                	}
                }
                
                digester.importPerson(importPerson, null);
            }
            digester.endImport();
        } catch (BeanException e) {
            throw (IOException) new IOException("Fehler beim Ablegen importierter Daten").initCause(e);
        }
    }

    //
    // geschützte Hilfsmethoden
    //
    /**
     * Diese Methode testet, ob eine Menge Teilmenge einer anderen Menge ist.
     * 
     * @param mainSet Menge, in der gesucht wird.
     * @param searchSet Suchmenge.
     * @return Das als erstes gefundene Element von searchSet, welches in mainSet
     *  nicht enthalten ist, oder null, wenn searchSet Teilmenge von mainSet ist.
     */
    private static Object isSubset(Collection mainSet, Collection searchSet) { //naive Implementierung reicht.
        Iterator its = searchSet.iterator();
        while (its.hasNext()) {
            Object obj = its.next();
            if (!mainSet.contains(obj))
                return obj;
        }
        return null;
    }
    
    /**
     * Datumsfelder mit zweistelliger Jahreszahl werden dem Jahrhundert 19 zugeordnet.
     * 
     * @param value
     * @return Timestamp
     */
    private static Timestamp fitDateField(Timestamp value) {
        /*if (field.length() == 8 && field.charAt(field.length()-3) == '.') {//Jahresangabe ist nur zweistellig
            StringBuffer buffer = new StringBuffer(field);
            buffer.insert(6, "19");
            return buffer.toString();
        } else
            return field;*/
        logger.finer("Timestamp: " + value);
        return value;
    }
    
    //
    // geschützte Member
    //
    /** Das zu verwendende Austauschformat */
    ExchangeFormat format = null;

    /** Der zu verwendende Eingabedatenstrom */
    InputStream inputStream = null;
    
    /** Der zu verwendende Ausgabedatenstrom */
    OutputStream outputStream = null;

    /** Logger dieser Klasse */
    static Logger logger = Logger.getLogger(MAdLANImporter.class.getName());
}
