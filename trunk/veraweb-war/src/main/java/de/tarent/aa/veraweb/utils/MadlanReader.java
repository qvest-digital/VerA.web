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
 * Created on 17.03.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.tarent.data.exchange.ExchangeFormat;

/**
 * Klasse zum {@link java.io.Reader}-basierten Lesen einer Madlan-Datei (spezielle Csv-Datei).
 * 
 * @author hendrik
 */
public class MadlanReader implements MadlanConstants {

	private StreamTokenizer tokenizer;
	private List header;
    private final List kyrillicFields;
    private boolean[] isKyrillic;
	private int readRows=0; //Anzahl schon gelesener Zeilen
	private boolean closed = false; //Stream schon geschlossen ?
	private boolean fillShortenedRows = true;
	private boolean trimExtendedRows = true;
	private static Logger logger = Logger.getLogger(MadlanReader.class.getName());
	private String emptyField = "";
	private char separatorChar = ';';
	private char lineBreakChar = 0xb;
    private final char[] mappingA;
    private final char[] mappingB;
    
    /**
     * Dieser Konstruktor merkt sich die übergebenen kyrillisch zu interpretierenden Felder
     * und initialisiert einen {@link StreamTokenizer} 
     * 
     * @param reader Eingangsdaten
     * @param format das zugrunde liegende Austauschformat
     */
	public MadlanReader(Reader reader, ExchangeFormat format) {
        Map properties = format.getProperties();
        kyrillicFields = (List) properties.get(KEY_KYRILLIC_FIELDS);
        if (logger.isEnabledFor(Level.DEBUG))
        	logger.log(Level.DEBUG, "Ermittle Haupt-Zeichen-Mapping");
        mappingA = getChars((String)properties.get(KEY_MAPPING_A));
        if (logger.isEnabledFor(Level.DEBUG))
        	logger.log(Level.DEBUG, "Ermittle kyrillisches Zeichen-Mapping");
        mappingB = getChars((String)properties.get(KEY_MAPPING_B));
		tokenizer = new StreamTokenizer(reader);
		//Tokenizer konfigurieren
		tokenizer.resetSyntax();
		tokenizer.eolIsSignificant(true);
		tokenizer.lowerCaseMode(false);
		tokenizer.slashSlashComments(false);
		tokenizer.slashStarComments(false);
		tokenizer.wordChars(' ',':');
		tokenizer.wordChars('<','~');
	}
	
	/**
	 * @return Die Feldnamen der Tabelle als {@link List}e von {@link String}s.
	 *         Anzahl der Felder entspricht der Rückgabe von {@link #getReadRowsCount()}.
	 * @throws IOException
	 */
	public List getHeader() throws IOException {
		if (header == null)
			header = readRow(true); //Header lesen
        isKyrillic = new boolean[header.size()];
        if (kyrillicFields != null) {
        	if (logger.isEnabledFor(Level.DEBUG))
            	logger.log(Level.DEBUG, "Anzahl bekannter kyrillischer Felder: " + kyrillicFields.size());
            Iterator itHeaders = header.iterator();
            int fieldsFound = 0; 
            for(int i = 0; i < isKyrillic.length; i++) {
                isKyrillic[i] = (kyrillicFields.contains(itHeaders.next()));
                if (isKyrillic[i])
                    fieldsFound++;
            }
            if (logger.isEnabledFor(Level.DEBUG))
            	logger.log(Level.DEBUG, "Anzahl gefundener kyrillischer Felder: " + fieldsFound);
        }   
        
		return header; 
	}
	
	/**
	 * Liest eine Zeile der Tabelle ein. 
	 * 
	 * @return Die Feldwerte einer Zeile der Tabelle als {@link List}e von {@link String}s.
	 *         Anzahl der Felder entspricht der Rückgabe von {@link #getReadRowsCount()}.
	 *         Kann keine Zeile mehr gelesen werden, wird null zurückgegeben.
	 * @throws IOException
	 */
	public List readRow() throws IOException {
		if (header == null)
			throw new RuntimeException("Bevor eine Zeile gelesen werden kann, muss der Header gelesen werden.");
		return readRow(false);
	}
	
	
	/**
	 * Liest den Header oder eine Zeile der Tabelle ein.
	 * Es wird nach dem letzten Feld der Zeile kein Feldtrennzeichen mehr erwartet.
	 * 
	 * @param isHeader Wird der Header oder eine normale Zeile gelesen?
	 * @return List
	 * @throws IOException
	 */
	private List readRow(boolean isHeader) throws IOException {
		if (closed)
			return null;
		
		List row;
		if (isHeader)
			row = new LinkedList();
		else
			row = new ArrayList(getColumnCount());
		
		int type;               //Typ des erkannten Tokens
		int i = 0;              //Zeiger auf das aktuell betrachtete Feld 
		boolean filled = false; //wurde in das aktuelle Feld schon ein Wert eingefügt ?
		while ((type = tokenizer.nextToken()) != StreamTokenizer.TT_EOL && 
				type != StreamTokenizer.TT_EOF) { //Token lesen bis Zeile oder Datei endet
			
			// Behandlung von zu langen Zeilen.
			if (!isHeader && i >= getColumnCount()) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("Zeile ").append(readRows).append(" hat zu viele Felder. ");
				buffer.append("Feld '").append(type).append(trimExtendedRows ? "' wird ignoriert. " : "' verursacht Fehler. ");
				buffer.append("Bis jetzt eingelesene Felder waren: [");
				for (int count = 0; count < row.size(); count++) {
					if (getHeader().size() >= count)
						buffer.append(getHeader().get(count));
					buffer.append("=").append(row.get(count));
					if (count < row.size() -1)
						buffer.append("; ");
				}
				buffer.append("]");
				prepareForLog(buffer);
				if (trimExtendedRows) {
					logger.warn(buffer.toString());
				} else {
					throw new RuntimeException(buffer.toString());
				}
			} else {
				//Zeile noch nicht vollständig
				switch (type) {
					case StreamTokenizer.TT_WORD:
						if (filled)
							row.set(i, (String) row.get(i) + tokenizer.sval);
						else {
							row.add(tokenizer.sval);
							filled = true;
						}
						break;
					case StreamTokenizer.TT_NUMBER:
						assert false; //durch die Wahl der Wortzeichen (s.o.)
									  // eigentlich nicht möglich
					default:
						char c = (char) tokenizer.ttype;
						if (c == separatorChar) { //Feld fertig gelesen
							if (!filled)
								row.add(emptyField);
							else
								filled = false;
							i++;
						} else if (c != '\r') { //unbekanntes Feldzeichen gelesen.
							if (c == lineBreakChar) //Umbruch erzeugen
								c = '\n';
							if (filled)
								row.set(i, (String) row.get(i) + c);
							else {
								row.add(Character.toString(c));
								filled = true;
							}
						}
				}
			}
		}
		if (type == StreamTokenizer.TT_EOF) //Dateiende erreicht
			closed = true;
		if (!isHeader)
			readRows++;
		if (!filled) //Feld fertig gelesen
			row.add(emptyField);
		
		// Behandlung von zu kurzen Zeilen
		if (!isHeader && ++i < getColumnCount()) {
			if (i > 1) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("Zeile ").append(readRows).append(" hat zu wenig Felder. ");
				buffer.append("(").append(i).append(" < ").append(getColumnCount()).append(") ");
				buffer.append("Bis jetzt eingelesene Felder waren: [");
				for (int count = 0; count < row.size(); count++) {
					if (getHeader().size() >= count)
						buffer.append(getHeader().get(count));
					buffer.append("=").append(row.get(count));
					if (count < row.size() -1)
						buffer.append("; ");
				}
				buffer.append("]");
				prepareForLog(buffer);
				if (fillShortenedRows) {
					logger.warn(buffer.toString());
					while (i < getColumnCount()) {
						row.add(emptyField);
						i++;
					}
				} else {
					throw new RuntimeException(buffer.toString());
				}
			} else
				return null; //leere Zeile
		}
        if (!isHeader)
            for (i = 0; i < isKyrillic.length; i++) {
                char[] mapping = isKyrillic[i] ? mappingB : mappingA;
                char[] chars = row.get(i).toString().toCharArray();
                for(int j = 0; j < chars.length; j++) {
                    char c = chars[j];
                    if (c >= 0 && c <= 255)
                        chars[j] = mapping[c];
                }
                row.set(i, new String(chars).trim());
            }
		return row;
	}
    
	private void prepareForLog(StringBuffer buffer) {
		int pos;
		while ((pos = buffer.indexOf("\r")) != -1) {
			buffer.replace(pos, pos + 1, " ");
		}
		while ((pos = buffer.indexOf("\n")) != -1) {
			buffer.replace(pos, pos + 1, " ");
		}
		while ((pos = buffer.indexOf("  ")) != -1) {
			buffer.replace(pos, pos + 2, " ");
		}
	}

	/**
	 * @return die Anzahl der bisher gelesenen Zeilen (ohne Header).
	 */
	public int getReadRowsCount() {
		return readRows;
	}
	
	/**
	 * @return die Breite der Tabelle.
	 * @throws IOException
	 */
	public int getColumnCount() throws IOException {
		return getHeader().size();
	}

	
    /**
     * Hier wird festgelegt wie zu kurze Zeilen behandelt werden sollen.
     * Wird der Parameter auf true gesetzt, dann werden Zeilen mit weniger Feldern als im Header
     * mit dem Wert {@link #emptyField} aufgefüllt. Ist der Wert false, wird ein Fehler erzeugt.
     * Standardwert ist true. 
     */
	public boolean getFillShortenedRows() {
		return fillShortenedRows;
	}
	
	/**
	 * Hier wird festgelegt wie zu kurze Zeilen behandelt werden sollen.
	 * Wird der Parameter auf true gesetzt, dann werden Zeilen mit weniger Feldern als im Header
	 * mit dem Wert {@link #emptyField} aufgefüllt. Ist der Wert false, wird ein Fehler erzeugt.
	 * Standardwert ist true. 
	 * 
	 * @param fillShortenedRows Flag: Sollen zu kurze Zeilen aufgefüllt akzeptiert werden.
	 */
	public void setFillShortenedRows(boolean fillShortenedRows) {
		this.fillShortenedRows = fillShortenedRows;
	}

    /**
     * Hier wird festgelegt wie zu lange Zeilen behandelt werden sollen.
     * Wird der Parameter auf true gesetzt, dann werden bei Zeilen mit mehr Feldern als im Header
     * nur die zuerst stehenden Felder übernommen. Ist der Wert false, wird ein Fehler erzeugt.
     * Standardwert ist true.
     */
	public boolean getTrimExtendedRows() {
		return fillShortenedRows;
	}
	
	/**
	 * Hier wird festgelegt wie zu lange Zeilen behandelt werden sollen.
	 * Wird der Parameter auf true gesetzt, dann werden bei Zeilen mit mehr Feldern als im Header
	 * nur die zuerst stehenden Felder übernommen. Ist der Wert false, wird ein Fehler erzeugt.
	 * Standardwert ist true.
	 * 
	 * @param trimExtendedRows Flag: sollen überlange Zeilen gekürzt akzeptiert werden.
	 */
	public void setTrimExtendedRows(boolean trimExtendedRows) {
		this.trimExtendedRows = trimExtendedRows;
	}

    /**
     * Liefert den {@link String} zum Auffüllen der Felder von zu kurzen Zeilen.
     * @see #fillShortenedRows
     */
	public String getEmptyField() {
		return emptyField;
	}
    
	/**
	 * Legt den {@link String} zum Auffüllen der Felder von zu kurzen Zeilen fest.
	 * @see #fillShortenedRows
	 * @param emptyField {@link String} zum Auffüllen der Felder von zu kurzen Zeilen
	 */
	public void setEmptyField(String emptyField) {
		this.emptyField = emptyField;
	}

    /**
     * Liefert das Trennzeichen zwischen den Feldern der Tabelle.
     * @see #fillShortenedRows
     */
	public char getSeparatorChar() {
		return separatorChar;
	}
	
	/**
	 * Legt das Trennzeichen zwischen den Feldern der Tabelle fest.
	 * @see #fillShortenedRows
	 * @param separatorChar das Trennzeichen zwischen den Feldern der Tabelle
	 */
	public void setSeparatorChar(char separatorChar) {
		this.separatorChar = separatorChar;
	}
	
    /**
     * Liefert das Zeichen, das als Umbruch innerhalb von Feldern interpretiert werden soll.
     */
	public char getLineBreakChar() {
		return lineBreakChar;
	}
	
	/**
	 * Legt das Zeichen fest, das als Umbruch innerhalb von Feldern interpretiert werden soll.
     * 
	 * @param lineBreakChar Zeichen, das als Umbruch innerhalb von Feldern interpretiert werden soll
	 */
	public void setLineBreakChar(char lineBreakChar) {
		this.lineBreakChar = lineBreakChar;
	}

    /**
     * Diese Methode liefert zu einem Schlüssel ein Zeichenmapping.
     * Die erlaubten Schlüssel sind {@link #CHARS_BALT}, {@link #CHARS_BALTIC},
     * {@link #CHARS_CYR_EXT}, {@link #CHARS_CYRILLIC}, {@link #CHARS_EAST},
     * {@link #CHARS_GREEK}, {@link #CHARS_LATIN}, {@link #CHARS_TUR_ASB},
     * {@link #CHARS_TURKISH} und {@link #CHARS_WEST}. Als Default wird
     * {@link #CHARS_LATIN} genommen.  
     * 
     * @param key Zeichenmappingschlüssel
     * @return ein <code>char[256]</code>.
     */
    public final static char[] getChars(String key) {
        char[] result = null;
        if (key != null)
            result = (char[]) charsets.get(key.toLowerCase());
        if (result == null) {
            result = charsLatin;
            if (logger.isEnabledFor(Level.DEBUG))
            	logger.log(Level.DEBUG, "Mapping '" + key + "' nicht erkannt; benutze Latin.");
        } else
        	if (logger.isEnabledFor(Level.DEBUG))
            	logger.log(Level.DEBUG, "Mapping '" + key + "' wird benutzt.");
        return result;
    }
    
    final static Map charsets = new TreeMap();
    static {
        charsets.put(CHARS_BALT, charsBalt);
        charsets.put(CHARS_BALTIC, charsBaltic);
        charsets.put(CHARS_CYR_EXT, charsCyrExt);
        charsets.put(CHARS_CYRILLIC, charsCyrillic);
        charsets.put(CHARS_EAST, charsEast);
        charsets.put(CHARS_GREEK, charsGreek);
        charsets.put(CHARS_LATIN, charsLatin);
        charsets.put(CHARS_TUR_ASB, charsTurAsb);
        charsets.put(CHARS_TURKISH, charsTurkish);
        charsets.put(CHARS_WEST, charsWest);
    }
}