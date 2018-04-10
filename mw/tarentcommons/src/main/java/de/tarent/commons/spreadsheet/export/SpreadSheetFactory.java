package de.tarent.commons.spreadsheet.export;

import java.util.ResourceBundle;

import de.tarent.commons.spreadsheet.export.csv.CSVDocument;
import de.tarent.commons.spreadsheet.export.ods.ODSContent;
import de.tarent.commons.spreadsheet.export.ods.ODSDocument;
import de.tarent.commons.spreadsheet.export.sxc.SXCContent;
import de.tarent.commons.spreadsheet.export.sxc.SXCDocument;
import de.tarent.commons.spreadsheet.export.xls.XLSSpreadSheet;

/**
 * Hilfsklasse für SpreadSheets.
 *
 * @author Christoph Jerolimov
 */
public class SpreadSheetFactory {
	/** ResourceBundle */
	public static final ResourceBundle bundle = ResourceBundle.getBundle("de.tarent.commons.spreadsheet.export.messages");

	/** Gibt eine SpreadSheet-Instanz zurück, welches eine CSV-Datei erzeugen kann. */
	public static final String TYPE_CSV_DOCUMENT = "csv";
	/** Gibt eine SpreadSheet-Instanz zurück, welches ein ODS ZIP-Archiv erzeugen kann. */
	public static final String TYPE_SXC_DOCUMENT = "sxc-document";
	/** Gibt eine SpreadSheet-Instanz zurück, welches ein ODS XML-Dokument erzeugen kann. */
	public static final String TYPE_SXC_CONTENT = "sxc-content";
	/** Gibt eine SpreadSheet-Instanz zurück, welches ein ODS ZIP-Archiv erzeugen kann. */
	public static final String TYPE_ODS_DOCUMENT = "ods-document";
	/** Gibt eine SpreadSheet-Instanz zurück, welches ein ODS XML-Dokument erzeugen kann. */
	public static final String TYPE_ODS_CONTENT = "ods-content";
	/** Gibt eine SpreadSheet-Instanz zurück, welches ein Excel XLS-Dokument erzeugen kann. */
	public static final String TYPE_XLS_DOCUMENT = "xls";
	/** Gibt die standard SpreadSheet-Instanz, aktuell: {@link #TYPE_ODS_DOCUMENT} */
	public static final String TYPE_DEFAULT = TYPE_ODS_DOCUMENT;

	/**
	 * Gibt eine SpreadSheet-Instanz entsprechend des übergebenen Typens zurück.
	 * Als Default gibt es ein {@link #TYPE_ODS_DOCUMENT} zurück.
	 *
	 * @param type siehe TYPE_*
	 * @return SpreadSheetContent
	 */
	public static SpreadSheet getSpreadSheet(String type) {
		if (TYPE_CSV_DOCUMENT.equals(type)) {
			return new CSVDocument();
		} else if (TYPE_SXC_CONTENT.equals(type)) {
			return new SXCContent();
		} else if (TYPE_SXC_DOCUMENT.equals(type)) {
			return new SXCDocument();
		} else if (TYPE_ODS_CONTENT.equals(type)) {
			return new ODSContent();
		} else if (TYPE_ODS_DOCUMENT.equals(type)) {
			return new ODSDocument();
		} else if (TYPE_XLS_DOCUMENT.equals(type)) {
			return new XLSSpreadSheet();
		} else {
			return getSpreadSheet(TYPE_DEFAULT);
		}
	}
}
