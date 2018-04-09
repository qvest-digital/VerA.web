/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */
package de.tarent.commons.spreadsheet.export;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Repr�sentiert eine Tabellenkalkulation-Dokument, stellt entsprechende
 * Methoden zur Erstellung von Tabellen, Zeilen und Zellen zur Verf�gung.
 * API ist wie folgende Baumstruktur zu verwenden:
 * 
 * <pre>
 * {@link SpreadSheetFactory}.getSpreadSheet(SpreadSheetHelper.TYPE_*)
 * init()
 * 
 * openTable("Tabelle 1", 3)
 *   openRow()
 *     addCell("A1")
 *     addCell("B1")
 *     addCell("C1")
 *   closeRow()
 *   openRow()
 *     addCell("A2")
 *     addCell("")
 *     addCell(null)
 *   closeRow()
 * closeTable()
 * 
 * save(OutputStream)
 * </pre>
 * 
 * @author Christoph Jerolimov
 */
public interface SpreadSheet {
	/**
	 * Setzt eine Einstellung f�r dieses Dokument. Sollte vor dem
	 * initalisieren geschehen.
	 * 
	 * @param key Schl��el
	 * @param value Wert
	 */
	public void setProperty(String key, String value) throws IOException;

	/**
	 * Gibt eine Einstellung f�r dieses Dokument zur�ck.
	 * 
	 * @param key Schl��el
	 * @return Wert
	 */
	public String getProperty(String key) throws IOException;

	/**
	 * Initalisiert das SpreadSheetDocument.
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException;

	/**
	 * Speichert das Dokument in dem �bergebenem OutputStream.
	 * <strong>Schlie�t diesen NICHT.</strong>
	 * 
	 * @param outputStream
	 */
	public void save(OutputStream outputStream) throws IOException;

	/**
	 * Gibt den MimeType dieses Dokumentes zur�ck.
	 * 
	 * @return MimeType
	 */
	public String getContentType();

	/**
	 * Gibt die Standard Dateierweiterung dieses Dokumentes zur�ck.
	 * 
	 * @return Dateierweiterung
	 */
	public String getFileExtension();

	/**
	 * �ffnet eine neue Tabelle mit dem �bergebenem Namen.
	 * 
	 * @param name Tabellen-Name
	 * @param colCount Gibt an wieviele Spalten die Tabelle hat.
	 */
	public void openTable(String name, int colCount);

	/**
	 * Schlie�t die Tabelle wieder.
	 */
	public void closeTable();

	/**
	 * �ffnet eine neue Zeile.
	 */
	public void openRow();

	/**
	 * Schlie�t eine neue Zeile.
	 */
	public void closeRow();

	/**
	 * F�gt eine neue Zelle mit dem �bergebenem Inhalt ein.
	 * 
	 * @param content Zellen-Inhalt
	 */
	public void addCell(Object content);
}
