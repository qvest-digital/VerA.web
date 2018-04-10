package de.tarent.commons.spreadsheet.export;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Repräsentiert eine Tabellenkalkulation-Dokument, stellt entsprechende
 * Methoden zur Erstellung von Tabellen, Zeilen und Zellen zur Verfügung.
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
	 * Setzt eine Einstellung für dieses Dokument. Sollte vor dem
	 * initalisieren geschehen.
	 *
	 * @param key Schlüßel
	 * @param value Wert
	 */
	public void setProperty(String key, String value) throws IOException;

	/**
	 * Gibt eine Einstellung für dieses Dokument zurück.
	 *
	 * @param key Schlüßel
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
	 * Speichert das Dokument in dem übergebenem OutputStream.
	 * <strong>Schließt diesen NICHT.</strong>
	 *
	 * @param outputStream
	 */
	public void save(OutputStream outputStream) throws IOException;

	/**
	 * Gibt den MimeType dieses Dokumentes zurück.
	 *
	 * @return MimeType
	 */
	public String getContentType();

	/**
	 * Gibt die Standard Dateierweiterung dieses Dokumentes zurück.
	 *
	 * @return Dateierweiterung
	 */
	public String getFileExtension();

	/**
	 * Öffnet eine neue Tabelle mit dem übergebenem Namen.
	 *
	 * @param name Tabellen-Name
	 * @param colCount Gibt an wieviele Spalten die Tabelle hat.
	 */
	public void openTable(String name, int colCount);

	/**
	 * Schließt die Tabelle wieder.
	 */
	public void closeTable();

	/**
	 * Öffnet eine neue Zeile.
	 */
	public void openRow();

	/**
	 * Schließt eine neue Zeile.
	 */
	public void closeRow();

	/**
	 * Fügt eine neue Zelle mit dem übergebenem Inhalt ein.
	 *
	 * @param content Zellen-Inhalt
	 */
	public void addCell(Object content);
}
