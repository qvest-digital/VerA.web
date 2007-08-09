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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Beispiel-Anwendung
 * 
 * @author Christoph Jerolimov
 */
public class Sample {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			testXML(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_SXC_CONTENT));
			testXML(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_ODS_CONTENT));
			testFile(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_SXC_DOCUMENT));
			testFile(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_ODS_DOCUMENT));
			testFile(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_XLS_DOCUMENT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testXML(SpreadSheet spreadSheet) throws IOException {
		spreadSheet.init();
		test(spreadSheet);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		spreadSheet.save(os);
		os.close();
//		System.out.println(os.toString());
	}

	public static void testFile(SpreadSheet spreadSheet) throws IOException {
		spreadSheet.init();
		test(spreadSheet);
		FileOutputStream os = new FileOutputStream(getFilename(spreadSheet.getFileExtension()));
		spreadSheet.save(os);
		os.close();
	}

	public static void test(SpreadSheet content) throws IOException {
		content.openTable("Tabelle 1", 3);
		content.openRow();
		content.addCell("A1");
		content.addCell("B1");
		content.addCell("C1");
		content.addCell("D1");
		content.closeRow();
		content.openRow();
		content.addCell(null);
		content.addCell("A2");
		content.addCell("");
		content.closeRow();
		content.openRow();
		content.addCell(new Integer(new Random().nextInt()));
		content.addCell(new Long(new Random().nextLong()));
		content.addCell(new Double(new Random().nextDouble()));
		content.closeRow();
		content.openRow();
		content.addCell(new BigInteger(new Long(new Random().nextLong()).toString()));
		content.addCell(new BigDecimal(new Long(new Random().nextLong()).toString()));
		content.addCell(new Date());
		content.closeRow();
		content.openRow();
		content.addCell("A3");
		content.addCell("<test>");
		content.addCell("C3\n\ntest");
		content.closeRow();
		content.closeTable();
	}

	/**
	 * @return Datei auf dem Desktop bestehend aus Datum und Zeit.
	 * @param extension Dateierweiterung
	 */
	protected static File getFilename(String extension) {
		// Sollte unter Windows + KDE + Gnome funktionieren.
		return new File(
				System.getProperty("user.home") + "/Desktop/" +
				new SimpleDateFormat("MM-dd_HH-mm").format(new Date()) +
				'.' + extension);
	}
}
