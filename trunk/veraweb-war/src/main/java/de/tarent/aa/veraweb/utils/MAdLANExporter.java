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
 * Created on 14.06.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.DatabaseUtilizer;

/**
 * Diese Klasse dient dem Erzeugen eines MAdLAN-CSV-Exports über den
 * {@link ExchangeFormat}-Mechanismus.
 * 
 * @author mikel
 */
public class MAdLANExporter implements Exporter, Exchanger, DatabaseUtilizer, MadlanConstants {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor ist {@link ExchangeFormat}-kompatibel.
     */
    public MAdLANExporter() {
        
    }
    
    //
    // Konstanten
    //
    /** Dieses Zeichen stellt einen Zeilenumbruch in einem Feld dar. */
    public final static char CSV_LINE_BREAK_ESCAPE = 0xb;
    
    //
    // Schnittstelle DatabaseUtilizer
    //
    /**
     * Die zu nutzende Datenbank.
     * 
     * @see de.tarent.octopus.beans.DatabaseUtilizer#setDatabase(de.tarent.octopus.beans.Database)
     */
    public void setDatabase(Database database) {
        this.db = database;
    }
    /**
     * Die zu nutzende Datenbank.
     * 
     * @see de.tarent.octopus.beans.DatabaseUtilizer#getDatabase()
     */
    public Database getDatabase() {
        return db;
    }

    //
    // Schnittstelle Exchanger
    //
    /** Das zu verwendende Austauschformat */
    public ExchangeFormat getExchangeFormat() {
        return format;
    }
    /** Das zu verwendende Austauschformat */
    public void setExchangeFormat(ExchangeFormat format) {
        this.format = format;
    }

    /**
     * Der zu verwendende Eingabedatenstrom --- wird intern nicht genutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#getInputStream()
     */
    public InputStream getInputStream() {
        return inputStream;
    }
    /**
     * Der zu verwendende Eingabedatenstrom --- wird intern nicht genutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#setInputStream(java.io.InputStream)
     */
    public void setInputStream(InputStream stream) {
        this.inputStream = stream;
    }
    
    /** Der zu verwendende Ausgabedatenstrom */
    public OutputStream getOutputStream() {
        return os;
    }
    /** Der zu verwendende Ausgabedatenstrom */
    public void setOutputStream(OutputStream stream) {
        this.os = stream;
    }
    
    //
    // Schnittstelle Exporter
    //
    /**
     * Diese Methode wird zu Beginn eines Exports aufgerufen.
     */
    public void startExport() throws IOException {
        assert format != null;
        assert os != null;
        
        mapping = null;
        exportFields = null;
        encodingA = new char[0];
        encodingB = encodingA;
        kyrillicFields = Collections.EMPTY_LIST;
        if (format != null && format.getProperties() != null) {
            Object property = format.getProperties().get("fieldMapping");
            if (property instanceof Map) {
                mapping = new FieldMapping((Map) property);
                exportFields = new ArrayList(mapping.getPrimeTargetSources());
            }
            property = format.getProperties().get(KEY_MAPPING_A);
            if (property instanceof String)
                encodingA = getCharsInverted(property.toString(), '?');
            property = format.getProperties().get(KEY_MAPPING_B);
            if (property instanceof String)
                encodingB = getCharsInverted(property.toString(), '?');
            property = format.getProperties().get(KEY_KYRILLIC_FIELDS);
            if (property instanceof List)
                kyrillicFields = (List) property;
            property = format.getProperties().get("textfieldMapping");
            if (property instanceof Map) try {
                parseTextfieldMappings((Map) property);
            } catch(BeanException be) {
                IOException ioe = new IOException("Fehler beim Parsen der Freitext-Mappings");
                ioe.initCause(be);
                throw ioe;
            }
        }
        pw = new PrintWriter(new OutputStreamWriter(os, "ISO-8859-1"));
        writeHeader();
    }

    /**
     * Diese Methode fügt dem Export eine Beschreibung der übergebenen VerA.web-Person
     * hinzu.
     * 
     * @param person {@link Person}-Bean
     * @see Exporter#exportPerson(Person)
     */
    public void exportPerson(Person person) throws BeanException, IOException {
        if (person == null)
            return;
        writePerson(person);
    }

    /**
     * Diese Methode schreibt das bisher gesammelte Dokument fest. 
     * 
     * @throws IOException
     * @see de.tarent.aa.veraweb.utils.Exporter#endExport()
     */
    public void endExport() throws IOException {
        pw.flush();
    }

    //
    // geschützte Methoden
    //
    void writeHeader() {
        boolean first = true;
        for (Iterator itFields = exportFields.iterator(); itFields.hasNext(); ) {
            if (first)
                first = false;
            else
                pw.print(';');
            pw.print(escape(itFields.next()));
        }
        pw.println();
    }
    
    void writePerson(Person person) throws BeanException, IOException {
        boolean first = true;
        for (Iterator itFields = exportFields.iterator(); itFields.hasNext(); ) {
            if (first)
                first = false;
            else
                pw.print(';');
            pw.print(escape(get(person, itFields.next().toString())));
        }
        pw.println();
    }
    
    /**
     * Diese Methode bereitet einen Wert oder einen Spaltennamen auf das Einfügen in
     * die CSV-Datei vor. Dies bedeutet:
     * <ul>
     * <li><code>null</code> wird zu einem Leerstring
     * <li>'\r' und '\n' werden zu {@link #CSV_LINE_BREAK_ESCAPE}
     * <li>';' wird zu ','
     * </ul> 
     * 
     * @param value umzuformender Wert
     * @return CSV-fester Wert
     */
    final static String escape(Object value) {
        return value != null ? value.toString().replaceAll("[\r\n]", String.valueOf(CSV_LINE_BREAK_ESCAPE)).replace(';', ',') : "";
    }
    
    final Object get(Person person, String key) throws BeanException, IOException {
        assert person != null;
        boolean kyrillic = kyrillicFields.contains(key);
        key = mapping.getPrimeTarget(key);
        
        Object result = null;
        if (person.containsKey(key))
            result = person.getField(key);
        else if ("occasion".equals(key))
            result = getCategory(person, new Integer(Categorie.FLAG_EVENT));
        else if ("category".equals(key))
            result = getCategory(person, new Integer(Categorie.FLAG_DEFAULT));
        else if (key.startsWith("textfield"))
            result = getTextfield(person, key);
        else
            throw new BeanException("Not a person field: " + key);

        return result != null ? remap(result.toString(), kyrillic ? encodingB : encodingA, '?') : null;
    }
    
    final static String remap(String string, char[] mapping, char unmapped) {
        if (string == null)
            return null;
        char[] result = new char[string.length()];
        for (int i = 0; i < result.length; i++) {
            char c = string.charAt(i);
            result[i] = (c < mapping.length) ? mapping[c] : unmapped;
        }
        return new String(result);
    }
    
    final String getTextfield(Person person, String key) throws BeanException {
        MessageFormat format = (MessageFormat) textfieldSelects.get(key);
        if (format != null)
        try {
            Iterator it = new ResultList(DB.result(db, format.format(new Object[] {person.id})).resultSet()).iterator();
            if (it.hasNext())
                return String.valueOf(((Map)it.next()).get("field")); 
        } catch (SQLException e) {
            throw new BeanException("Problem beim Ermitteln eines Freitextfeldes", e);
        }
        return null;
    }
    
    final String getCategory(Person person, Integer flags) throws BeanException, IOException {
        Select personCategoriesSelect = new Select(false).from("veraweb.tperson_categorie")
            .select("fk_categorie").where(Expr.equal("fk_person", person.id));
        List categories = db.getBeanList("Categorie",
                db.getSelect("Categorie").where(new WhereList().addAnd(
                        Expr.equal("flags", flags)).addAnd(
                        new RawClause("pk in (" + personCategoriesSelect + ")"))));
        StringBuffer categoryBuffer = new StringBuffer();
        boolean first = true;
        for (Iterator itCategories = categories.iterator(); itCategories.hasNext(); ) {
            Categorie category = (Categorie) itCategories.next();
            if (category != null) {
                if (first)
                    first = false;
                else
                    categoryBuffer.append('\n');
                categoryBuffer.append(category.name);
            }
        }
        return categoryBuffer.toString();
    }
    
    void parseTextfieldMappings(Map rawTextfieldMappings) throws BeanException, IOException {
        assert db != null;
        if (rawTextfieldMappings == null) {
            textfieldSelects = Collections.EMPTY_MAP;
        } else {
            Map result = new HashMap();
            for (int i = 0; i < rawTextfieldMappings.size(); i++) {
                String indexString = String.valueOf(i);
                String keyDoctype = indexString + ":Doctype";
                if (rawTextfieldMappings.containsKey(keyDoctype)) {
                    String doctypeName = String.valueOf(rawTextfieldMappings.get(keyDoctype));
                    String personTextfield = String.valueOf(rawTextfieldMappings.get(indexString + ":Person"));
                    String partnerTextfield = String.valueOf(rawTextfieldMappings.get(indexString + ":Partner"));
                    addSelectFormat(result, doctypeName, personTextfield, false);
                    addSelectFormat(result, doctypeName, partnerTextfield, true);
                }
            }
            textfieldSelects = result;
        }
    }
    
    void addSelectFormat(Map selectFormats, String doctypeName, String textfield, boolean partner) throws BeanException, IOException {
        Doctype doctype = (Doctype) db.getBean("Doctype", db.getSelect("Doctype").where(Expr.equal("docname", doctypeName)));
        if (doctype == null)
            logger.warning("Für den Export konfigurierten Dokumenttyp '" + doctypeName + "' nicht gefunden.");
        else {
            Select select = new Select(true).from("veraweb.tperson_doctype")
                .selectAs(partner ? "textfield_p" : "textfield", "field")
                .where(new WhereList().addAnd(
                        Expr.equal("fk_doctype", doctype.id)).addAnd(
                        new RawClause("fk_person = {0}")));
            selectFormats.put(textfield, new MessageFormat(select.toString()));
        }
    }
    
    //
    // Encoding-Hilfsmethoden
    //
    /**
     * Diese Methode liefert zu einem Schlüssel ein Zeichenmapping.
     * Die erlaubten Schlüssel sind {@link #CHARS_BALT}, {@link #CHARS_BALTIC},
     * {@link #CHARS_CYR_EXT}, {@link #CHARS_CYRILLIC}, {@link #CHARS_EAST},
     * {@link #CHARS_GREEK}, {@link #CHARS_LATIN}, {@link #CHARS_TUR_ASB},
     * {@link #CHARS_TURKISH} und {@link #CHARS_WEST}. Als Default wird
     * {@link #CHARS_LATIN} genommen. Dieses Zeichenmapping ist invers zu dem,
     * das {@link MadlanReader#getChars(String)} liefert.
     * 
     * @param key Zeichenmappingschlüssel
     * @param unmapped Wert für nicht zugeordnete Zeichen
     * @return ein <code>char[]</code>.
     */
    public final static char[] getCharsInverted(String key, char unmapped) {
        char[] orig = MadlanReader.getChars(key);
        if (orig == null)
            return new char[0];
        
        int max = -1;
        for (int i = 0; i < orig.length; i++)
            if (orig[i] > max) max = orig[i];

        // zu großes max abfangen??
        char[] invers = new char[max + 1];
        Arrays.fill(invers, unmapped);
        for (int i = orig.length - 1; i >= 0; i--)
            invers[orig[i]] = (char) i;
        return invers;
    }

    //
    // geschützte Member-Variablen
    //
    OutputStream os = null;

    PrintWriter pw = null;
    
    /** Die zu nutzende Datenbank */
    Database db = null;
    
    FieldMapping mapping = null;
    
    List exportFields = null;
    
    List kyrillicFields = null;
    
    Map textfieldSelects = null;
    
    /** Der zu verwendende Eingabedatenstrom --- wird intern nicht genutzt */
    InputStream inputStream = null;

    /** Das zu verwendende Austauschformat */
    ExchangeFormat format = null;
    
    char[] encodingA;
    
    char[] encodingB;
    
    /** Logger dieser Klasse */
    static Logger logger = Logger.getLogger(MAdLANExporter.class.getName());

		/* (non-Javadoc)
		 * @see de.tarent.aa.veraweb.utils.Exporter#setOrgUnitId(java.lang.Integer)
		 */
		public void setOrgUnitId(Integer orgUnitId)
		{
			// obsolete
			
		}
		/* (non-Javadoc)
		 * @see de.tarent.aa.veraweb.utils.Exporter#setCategoryId(java.lang.Integer)
		 */
		public void setCategoryId(Integer categoryId)
		{
			// obsolete
			
		}
}
