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
 * Created on 22.08.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.MappingException;
import de.tarent.data.exchange.FieldMapping.Entity;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.utils.CSVFileWriter;

/**
 * Diese Klasse implementiert einen generischen CSV-Export von VerA.web-Personen.
 * 
 * @author mikel
 */
public class GenericCSVExporter extends GenericCSVBase implements Exporter
{
	//
	// Konstruktor
	//
	/**
	 * Dieser Konstruktor ist leer; dieser wird von {@link ExchangeFormat#getExporterClass()} genutzt.
	 */
	public GenericCSVExporter()
	{
	}

	//
	// Schnittstelle Exporter
	//
	/**
	 * Diese Methode wird zu jeder zu exportierenden Person aufgerufen, �bergeben wird die Person als Zusammenstellung von
	 * {@link Person}. Sie f�gt dem Export eine Beschreibung der �bergebenen VerA.web-Person hinzu.
	 * 
	 * @param person
	 *          {@link Person}-Bean
	 * @see de.tarent.aa.veraweb.utils.Exporter#exportPerson(de.tarent.aa.veraweb.beans.Person)
	 */
	public void exportPerson(Person person) throws BeanException, IOException
	{
		assert csvFieldNames != null;
		assert fieldMapping != null;
		assert csvWriter != null;
		if (person == null)
			return;
		Entity entity = new PersonEntity(person);
		List line = new ArrayList(csvFieldNames.size());
		for (Iterator itCsvFieldNames = csvFieldNames.iterator(); itCsvFieldNames.hasNext();)
			line.add(fieldMapping.resolve(itCsvFieldNames.next().toString(), entity));
		csvWriter.writeFields(line);
	}

	/**
	 * Diese Methode wird zu Beginn eines Exports aufgerufen. In ihr kann etwa das Dokument mit einem Kopf zu schreiben
	 * begonnen werden.
	 * 
	 * @throws IOException
	 * @see de.tarent.aa.veraweb.utils.Exporter#startExport()
	 */
	public void startExport() throws IOException
	{
		if (exchangeFormat == null)
			throw new IOException("Der Export ben�tigt ein ExchangeFormat.");
		if (database == null)
			throw new IOException("Der Export ben�tigt eine Database.");
		if (outputStream == null)
			throw new IOException("Der Export ben�tigt einen OutputStream.");
		try
		{
			readProperties();
			parseFormat(true);
			initWriter();
			writeHeaderLine();
		} catch (MappingException e)
		{
			IOException ioe = new IOException("Fehler im Feldmapping");
			ioe.initCause(e);
			throw ioe;
		} catch (BeanException e)
		{
			IOException ioe = new IOException("Fehler beim Daten-Bean-Zugriff");
			ioe.initCause(e);
			throw ioe;
		}
	}

	/**
	 * Diese Methode wird zum Ende eines Exports aufgerufen. In ihr kann etwa das bisher gesammelte Dokument
	 * festgeschrieben werden.
	 * 
	 * @throws IOException
	 * @see de.tarent.aa.veraweb.utils.Exporter#endExport()
	 */
	public void endExport() throws IOException
	{
		assert csvWriter != null;
		csvWriter.close();
	}

	//
	// gesch�tzte Hilfsmethoden
	//
	/**
	 * Diese Methode initialisiert den internen {@link CSVFileWriter}.
	 */
	void initWriter() throws UnsupportedEncodingException, IOException
	{
		assert exchangeFormat != null;
		assert outputStream != null;
		Writer writer = new OutputStreamWriter(outputStream, encoding);
		// if (ENCODING_UTF_8.equals(encoding))
		// outputStream.write(new byte[] {(byte)0xef,(byte)0xbb,(byte)0xBF});
		csvWriter = new CSVFileWriter(writer, fieldSeparator, textQualifier);
	}

	/**
	 * Diese Methode schreibt die Kopfzeile mit den Spaltennamen.
	 */
	void writeHeaderLine()
	{
		assert csvWriter != null;
		assert fieldMapping != null;
		csvWriter.writeFields(csvFieldNames);
	}

	//
	// gesch�tzte innere Klassen
	//
	/**
	 * Diese Klasse setzt die {@link Entity}-Facade f�r {@link Person}-Instanzen um.
	 */
	class PersonEntity implements Entity
	{
		//
		// Schnittstelle Entity
		//
		/**
		 * Diese Methode erlaubt das Abfragen von Daten zu einem bestimmten Schl�ssel. Die Schl�ssel werden in
		 * {@link GenericCSVExporter#getAvailableFields()} erstellt.
		 * 
		 * @param sourceKey
		 *          Quellfeldschl�ssel
		 * @return Quellfeldwert als {@link String}; <code>null</code>-Felder werden als Leerstring <code>""</code>
		 *         geliefert; <code>Date</code>-Felder werden mit {@link GenericCSVBase#dateFormat} formatiert.
		 */
		public String get(String sourceKey)
		{
			Object result = null;
			// Personenstammdatenfelder
			try
			{
				if (sourceKey.charAt(0) == ':')
					result = person.get(sourceKey.substring(1));
				else if (sourceKey.startsWith("CAT:"))
					result = getRank(sourceKey.substring(4));
				else if (sourceKey.startsWith("EVE:"))
					result = getRank(sourceKey.substring(4));
				else if (sourceKey.startsWith("COR:"))
					result = getRank(sourceKey.substring(4));
				else if (sourceKey.startsWith("DTM:"))
					result = getTextField(sourceKey.substring(4), false);
				else if (sourceKey.startsWith("DTP:"))
					result = getTextField(sourceKey.substring(4), true);
				else
					logger.warning("Unbekanntes Quellfeld");
			} catch (Exception e)
			{
				logger.log(Level.WARNING, "Fehler beim Beziehen von Personendaten", e);
			}
			if (result instanceof Date)
				return dateFormat.format(result);
			return result == null ? "" : result.toString();
		}

		//
		// Konstruktor
		//
		/** Der Konstruktor vermerkt die Person */
		PersonEntity(Person person)
		{
			this.person = person;
		}

		//
		// Hilfsmethoden
		//
		/**
		 * Diese Methode liefert den Rang einer Person in einer Kategorie.
		 * 
		 * @param categoryName
		 *          Name der Kategorie
		 * @return Rang der Person in der Kategorie; <code>null</code>, wenn nicht in der Kategorie,
		 *         {@link GenericCSVExporter#DEFAULT_RANK}, wenn weder ein individueller noch ein allgemeiner Rang zu der
		 *         Kategorie vorliegt, jedoch sehr wohl Kategorienzugeh�rigkeit besteht.
		 */
		Object getRank(String categoryName) throws BeanException, IOException
		{
			if (ranks == null)
			{
				ranks = new HashMap();
				Bean sampleCategory = database.createBean("Categorie");
				Bean samplePersonCategory = database.createBean("PersonCategorie");
				Select select = new Select(false).from(database.getProperty(samplePersonCategory, "table")).join(
					database.getProperty(sampleCategory, "table"), database.getProperty(sampleCategory, "id"),
					database.getProperty(samplePersonCategory, "categorie")).selectAs(database.getProperty(sampleCategory, "name"), "name").selectAs(
					database.getProperty(sampleCategory, "rank"), "rankDefault").selectAs(database.getProperty(samplePersonCategory, "rank"), "rank")
					.where(Expr.equal(database.getProperty(samplePersonCategory, "person"), person.id));
				List entries = database.getList(select, database);
				for (Iterator itEntries = entries.iterator(); itEntries.hasNext();)
				{
					Map entry = (Map) itEntries.next();
					Object name = entry.get("name");
					if (name == null)
						continue;
					Object rank = entry.get("rank");
					if (rank == null)
					{
						rank = entry.get("rankDefault");
						if (rank == null)
							rank = DEFAULT_RANK;
					}
					ranks.put(name, rank);
				}
			}
			return ranks.get(categoryName);
		}

		/**
		 * Diese Methode liefert den Freitext einer Person oder ihres Partners zu einem Dokumenttyp.
		 * 
		 * @param docTypeName
		 *          Name des Dokumenttyps
		 * @param partner
		 *          Flag, ob Freitext von Partner oder Hauptperson
		 * @return Freitext der Person oder des Partners zum Dokumenttyp; <code>null</code>, wenn kein Freitext vorliegt.
		 */
		Object getTextField(String docTypeName, boolean partner) throws IOException, BeanException
		{
			if (textFields == null)
			{
				textFields = new HashMap();
				textFieldsPartner = new HashMap();
				Bean sampleDocType = database.createBean("Doctype");
				Bean samplePersonDocType = database.createBean("PersonDoctype");
				Select select = new Select(false).from(database.getProperty(samplePersonDocType, "table")).join(
					database.getProperty(sampleDocType, "table"), database.getProperty(sampleDocType, "id"),
					database.getProperty(samplePersonDocType, "doctype")).selectAs(database.getProperty(sampleDocType, "name"), "name").selectAs(
					database.getProperty(samplePersonDocType, "textfield"), "textField").selectAs(
					database.getProperty(samplePersonDocType, "textfieldPartner"), "textFieldPartner").where(
					Expr.equal(database.getProperty(samplePersonDocType, "person"), person.id));
				List entries = database.getList(select, database);
				for (Iterator itEntries = entries.iterator(); itEntries.hasNext();)
				{
					Map entry = (Map) itEntries.next();
					Object name = entry.get("name");
					if (name == null)
						continue;
					Object field = entry.get("textField");
					if (field != null)
						textFields.put(name, field);
					field = entry.get("textFieldPartner");
					if (field != null)
						textFieldsPartner.put(name, field);
				}
			}
			return (partner ? textFieldsPartner : textFields).get(docTypeName);
		}

		//
		// Membervariablen
		//
		/** Die {@link Person}, f�r die dies eine Facade ist. */
		final Person	person;

		/** Die R�nge der Person in den Kategorien */
		Map						ranks							= null;

		/** Die Dokumenttypfreitexte der Hauptperson */
		Map						textFields				= null;

		/** Die Dokumenttypfreitexte des Partners */
		Map						textFieldsPartner	= null;
	}

	/** Einschr�nkung auf Mandant */
	protected Integer			orgUnitId			= null;

	/** Einschr�nkung auf Kategorie <code>Null</code> = alle Kategorien, 0 = keine Kategorie */
	protected Integer			categoryId		= null;

	//
	// gesch�tzte Membervariablen
	//
	/** CSV-Ausgabe-Objekt */
	CSVFileWriter					csvWriter			= null;

	/** Logger dieser Klasse */
	final static Logger		logger				= Logger.getLogger(GenericCSVExporter.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tarent.aa.veraweb.utils.Exporter#setOrgUnitId(java.lang.Integer)
	 */
	public void setOrgUnitId(Integer orgUnitId)
	{
		this.orgUnitId = orgUnitId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tarent.aa.veraweb.utils.Exporter#setCategoryId(java.lang.Integer)
	 */
	public void setCategoryId(Integer categoryId)
	{
		this.categoryId = categoryId;
	}

	/**
	 * Diese Methode holt alle notwendigen Kategorien aus der Datenbank.<br>
	 * Nur die vom Nutzer ausgew�hlte Kategorien. Nur die Kategorien des Mandanten
	 * 
	 * @throws IOException
	 * @throws BeanException
	 */
	@Override
    protected List getCategoriesFromDB() throws BeanException, IOException
	{
		// alle Kategorien.
		if (this.categoryId == null && this.orgUnitId == null)
			return super.getCategoriesFromDB();
		// keine Kategorien
		if (this.categoryId != null && this.categoryId.intValue() == 0)
			return null;

		Select sel = database.getSelect("Categorie");

		/*TODO sobald Select.whereAnd(Clause) zum DB-Layer.jar gehoert, kann der Code(Kot) unten ausgetauscht werden.
		if (this.categoryId != null)
		{
			sel.whereAnd(Expr.equal("pk", this.categoryId));
		}
		if (this.orgUnitId != null && this.orgUnitId.intValue() != -1)
		{
			sel.whereAnd(Expr.equal("fk_orgunit", this.orgUnitId));
		}
		*/

		Clause expr1 = null, expr2 = null;
		if (this.categoryId != null)
		{
			expr1 = Expr.equal("pk", this.categoryId);
		}
		if (this.orgUnitId != null && this.orgUnitId.intValue() != -1)
		{
			expr2 = Expr.equal("fk_orgunit", this.orgUnitId);
		}
		if (expr1 != null && expr2 != null)
			sel.where(Where.and(expr1, expr2));
		else if (expr1 != null && expr2 == null)
			sel.where(expr1);
		else if (expr1 == null && expr2 != null)
			sel.where(expr2);

		return database.getBeanList( "Categorie", sel );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tarent.aa.veraweb.utils.GenericCSVBase#getDocumentTypesFromDB()
	 */
	@Override
    protected List getDocumentTypesFromDB() throws BeanException, IOException
	{
		// TODO nur die Dokumenttypen des Mandanten, falls Doktypen auf Mandanten eingeschr�nkt werden.
		return super.getDocumentTypesFromDB();
	}

}
