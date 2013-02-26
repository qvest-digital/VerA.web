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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonCategorie;
import de.tarent.aa.veraweb.beans.ImportPersonDoctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zum Import von Personendaten zur
 * Verf�gung. 
 * 
 * @author hendrik
 * @author mikel
 */
public class ImportPersonsWorker {
    //
    // �ffentliche Konstanten
    //
    /***/
    public final static String FIELD_IMPORTED_COUNT = "imported";
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabe-Parameter f�r {@link #importStoredRecord(OctopusContext, Integer)} */
    public static final String[] INPUT_importStoredRecord = { "REQUEST:importId" };
    /** Octopus-Eingabe-Parameter-Pflicht f�r {@link #importStoredRecord(OctopusContext, Integer)} */
    public static final boolean[] MANDATORY_importStoredRecord = { true };
    /** Octopus-Ausgabe-Parameter f�r {@link #importStoredRecord(OctopusContext, Integer)} */
    public static final String OUTPUT_importStoredRecord = "importStatus";
    /**
     * Diese Octopus-Aktion liefert zum Import mit der �bergebenen ID die
     * Import-Statistik-Daten 
     * 
     * @param cntx Octopus-Kontext
     * @param importId Import-ID
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datens�tze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datens�tze unter "saveCount" und der Import-ID unter "id".
     * @throws BeanException 
     * @throws IOException 
     */
	public Map importStoredRecord(OctopusContext cntx, Integer importId) throws BeanException, IOException {
		//Initialisiere Datenbank
		Database database = new DatabaseVeraWeb(cntx);
        ImportPerson sample = (ImportPerson) database.createBean("ImportPerson");
		//Erstelle SELECT-Anfrage, die die Anzahl der Datens�tze liest.
		Select select = database.getCount(sample);
		WhereList where = new WhereList();
			//Bed: Datensatz wurde noch nicht festgeschrieben
		where.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
			//Bed: Nur Datens�tze von dem aktuellen Importvorgang
		where.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
		select.where(where);
		Integer dsCount = database.getCount(select);
		
		//Erstelle SELECT-Anfrage, die die Anzahl der Datens�tze mit Duplikaten liest.
		select = database.getCount(sample);
		where = new WhereList();
			//Bed: Es existieren Duplikate zu dem Datensatz 
		where.addAnd(Expr.isNotNull(database.getProperty(sample, "duplicates")));
			//Bed: Datensatz wurde noch nicht festgeschrieben
		where.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
			//Bed: Nur Datens�tze von dem aktuellen Importvorgang
		where.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
		select.where(where);
		Integer dupCount = database.getCount(select);
		
		//Erstelle SELECT-Anfrage, die die Anzahl der zum Speichern freigegebenen Datens�tze liest.
		select = database.getCount(sample);
		where = new WhereList();
		where.addAnd(
				Where.or(
						//Bed: Es existieren keine Duplikate zu dem Datensatz 
						Expr.isNull(database.getProperty(sample, "duplicates")),
						//Bed: Datensatz explizit zum speichern gekennzeichnet
						Expr.equal(database.getProperty(sample, "dupcheckstatus"), ImportPerson.TRUE)
		));
			//Bed: Datensatz wurde noch nicht festgeschrieben
		where.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
			//Bed: Nur Datens�tze von dem aktuellen Importvorgang
		where.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
		select.where(where);
		Integer saveCount = database.getCount(select);
		
        return DataExchangeWorker.createImportStats(dsCount.intValue(), dupCount.intValue(), saveCount.intValue(), importId);
	}
	
    /** Octopus-Eingabe-Parameter f�r {@link #finalise(OctopusContext, Integer, List, Map)} */
    public static final String[] INPUT_finalise = { "REQUEST:importId", "CONFIG:ignorePersonFields", "CONFIG:importTextfieldMapping" };
    /** Octopus-Eingabe-Parameter-Pflicht f�r {@link #finalise(OctopusContext, Integer, List, Map)} */
    public static final boolean[] MANDATORY_finalise = { true, true, true };
    /** Octopus-Ausgabe-Parameter f�r {@link #finalise(OctopusContext, Integer, List, Map)} */
    public static final String OUTPUT_finalise = "importStatus";
    /**
     * Diese Octopus-Aktion finalisiert einen Import. Hierbei wird als Nebeneffekt in
     * den Content unter dem Schl�ssel {@link #FIELD_IMPORTED_COUNT "imported"} die Anzahl
     * der tats�chlich importierten Datens�tze eingetragen.
     * 
     * @param cntx Octopus-Kontext
     * @param importId ID eines fr�heren Imports
     * @param ignorePersonFields
     * @param importTextfieldMapping Map f�r das Mapping der Adressfreitextfelder 
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datens�tze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datens�tze unter "saveCount" und der Import-ID unter "id".
     * @throws BeanException
     * @throws IOException 
     */
	public Map finalise(OctopusContext cntx, Integer importId, List ignorePersonFields, Map importTextfieldMapping) throws BeanException, IOException {
		//Initialisiere Datenbank und lese die ID f�r den Importvorgang
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();
		
		List cleanupOrgunits = new ArrayList();
		
		try {
	    	int dsCount=0;
	    	
            ImportPerson sampleImportPerson = new ImportPerson();
            // importTextfieldMapping analysieren, Doctypes holen...
            List personDoctypeCreators = parseTextfieldMapping(database, context, importTextfieldMapping);
			//Erstelle SELECT-Anfrage, die die einzuf�genden Datens�tze liest.
			Select select = database.getSelect(sampleImportPerson);
			WhereList where = new WhereList();
            //Bed: Nur Datens�tze von dem aktuellen Importvorgang
			where.addAnd(Expr.equal(database.getProperty(sampleImportPerson, "fk_import"), importId));
			where.addAnd(
					Where.or(
							//Bed: Es existieren keine Duplikate zu dem Datensatz (NULL wird gefordert!!!)
							Expr.isNull(database.getProperty(sampleImportPerson, "duplicates")),
							//Bed: Datensatz explizit zum speichern gekennzeichnet
							Expr.equal(database.getProperty(sampleImportPerson, "dupcheckstatus"), ImportPerson.TRUE)
			));
			//Bed: Datensatz wurde noch nicht festgeschrieben
			where.addAnd(Expr.equal(database.getProperty(sampleImportPerson, "dupcheckaction"), ImportPerson.FALSE));
			select.where(where);
			
			//Hole die festzuschreibenden Datens�tze und schreiben diese iterativ in die Personen-Tabellen
			List result = database.getList(select, database);
			for (Iterator it=result.iterator(); it.hasNext(); ) {
				Map importPerson = (Map) it.next();
				Integer ipID = (Integer)importPerson.get("id");
				
				// Import-Bean auf Personen-Bean mappen
				Person person = new Person();
				for (Iterator fieldIt = person.getFields().iterator(); fieldIt.hasNext(); ) {
					String key = (String) fieldIt.next();
					if (!ignorePersonFields.contains(key))
						person.setField(key, importPerson.get(key));
				}
				AddressHelper.copyAddressData(cntx, person, null);
				
				/* assign default workarea = 0 in case that person.workarea is null
				 * 
				 * modified as per change request for version 1.2.0
				 * cklein 2008-03-27
				 */
				if ( person.workarea == null )
				{
					person.workarea = new Integer( 0 );
				}

				// Neue Person speichern
				person.verify();
				if (person.isCorrect()) {
					database.saveBean(person, context, true);
					
					if (!cleanupOrgunits.contains(person.orgunit))
						cleanupOrgunits.add(person.orgunit);
					
	                // Importierte Kategorien zu Personen erzeugen
	                createPersonCategories(database, context, (Integer)importPerson.get("id"), person);
	                // TODO: auslagern in MAdLANImporter
	                if (importPerson.get("category") != null && ((String)importPerson.get("category")).length() != 0)
	                    createPersonCategories(database, context, ((String)importPerson.get("category")).split("\n"), person, new Integer(Categorie.FLAG_DEFAULT));
	               
	                if (importPerson.get("occasion") != null && ((String)importPerson.get("occasion")).length() != 0)
	                    createPersonCategories(database, context, ((String)importPerson.get("occasion")).split("\n"), person, new Integer(Categorie.FLAG_EVENT));
	                
	                // Importierte Dokumenttypen zu Personen erzeugen
	                createPersonDoctypes(database, context, ipID, person.id);
	                // TODO: auslagern in MAdLANImporter
	                Iterator itPersonDoctypeCreators = personDoctypeCreators.iterator();
	                while (itPersonDoctypeCreators.hasNext())
                        ((PersonDoctypeImporter) itPersonDoctypeCreators.next()).createFor(importPerson, person.id);
	                
	                // Restlichen Personen Dokumenttypen erzeugen
	                PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person);
	                
					// Datensatz als festgeschrieben markieren
					context.execute(database.getUpdate("ImportPerson").update("dupcheckaction", ImportPerson.TRUE).
                            where(Expr.equal(database.getProperty(sampleImportPerson, "id"), ipID)));
					
					// Datensatz erfolgreich bearbeitet
					dsCount++;
				}
			}
            context.commit();
            
            cntx.setContent(FIELD_IMPORTED_COUNT, new Integer(dsCount));
            cntx.setContent("cleanupOrgunits", cleanupOrgunits);
            
    		return importStoredRecord(cntx, importId);
		} 
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die Personendaten konnten nicht importiert werden.", e );
		}
	}
	
    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt zu einer Person und einer Menge Kategoriennamen
     * passende {@link PersonCategorie}-Instanzen. Gegebenenfalls werden hierbei
     * die Kategorien erst noch erzeugt.
     * 
     * @param database die Datenbank, in der agiert werden soll
     * @param categoryNames ein Array aus Kategoriennamen
     * @param person Person
     * @param flags Art der gesuchten und gegebenenfalls erzeugten Kategorie
     */
    private static void createPersonCategories(Database database, ExecutionContext context, String[] categoryNames, Person person, Integer flags) throws BeanException, IOException {
        for (int i = 0; i < categoryNames.length; i++) {
            String categoryName = categoryNames[i].trim();
            if (categoryName.length() != 0) {
                Categorie category = (Categorie) database.getBean("Categorie",
                        database.getSelect("Categorie").where(Where.and(
                                Expr.equal("catname", categoryName), Where.and(
                                Expr.equal("flags", flags),
                                Expr.equal("fk_orgunit", person.orgunit)))), context);
                if (category == null) {
                    category = (Categorie) database.createBean("Categorie");
                    category.flags = flags;
                    category.name = categoryName;
                    category.orgunit = person.orgunit;
                    database.saveBean(category, context, true);
                }
                PersonCategorie personCategory = new PersonCategorie();
                personCategory.categorie = category.id;
                personCategory.person = person.id;
                database.saveBean(personCategory, context, false);
            }
        }
    }

    /**
     * Diese Methode erzeugt zu einer Person und einer ImportPerson
     * passende {@link PersonCategorie}-Instanzen. Gegebenenfalls werden hierbei
     * die Kategorien erst noch erzeugt.
     * 
     * @param database die Datenbank, in der agiert werden soll
     * @param importPersonId ID einer ImportPerson
     * @param person Person, als die die ImportPerson importiert wird
     */
    private static void createPersonCategories(Database database, ExecutionContext context, Integer importPersonId, Person person) throws BeanException, IOException {
        ImportPersonCategorie sample = new ImportPersonCategorie();
        Select select = database.getSelect(sample);
        select.where(Expr.equal(database.getProperty(sample, "importperson"), importPersonId));
        
        List importPersonCategories = database.getBeanList("ImportPersonCategorie", select, context);
        for (Iterator itImportPersonCategories = importPersonCategories.iterator(); itImportPersonCategories.hasNext(); ) {
            ImportPersonCategorie importPersonCategorie = (ImportPersonCategorie) itImportPersonCategories.next();
            if (importPersonCategorie.name != null) {
                Categorie category = (Categorie) database.getBean("Categorie",
                        database.getSelect("Categorie").where(Where.and(
                            Expr.equal("catname", importPersonCategorie.name),
                            Expr.equal("fk_orgunit", person.orgunit))), context);
                if (category == null) {
                    category = (Categorie) database.createBean("Categorie");
                    category.flags = importPersonCategorie.flags;
                    category.name = importPersonCategorie.name;
                    category.rank = importPersonCategorie.rank;
                    category.orgunit = person.orgunit;
                    database.saveBean(category, context, true);
                }
                PersonCategorie personCategory = new PersonCategorie();
                personCategory.categorie = category.id;
                personCategory.person = person.id;
                personCategory.rank = importPersonCategorie.rank;
                database.saveBean(personCategory, context, false);
            }
        }
    }

    /**
     * Diese Methode erzeugt zu einer Person und einer ImportPerson
     * passende {@link PersonDoctype}-Instanzen. Gegebenenfalls werden hierbei
     * die Dokumenttypen erst noch erzeugt.
     * 
     * @param database die Datenbank, in der agiert werden soll
     * @param importPersonId ID einer ImportPerson
     * @param personId ID der Person, als die die ImportPerson importiert wird
     */
    private static void createPersonDoctypes(Database database, ExecutionContext context, Integer importPersonId, Integer personId) throws BeanException, IOException {
        ImportPersonDoctype sample = (ImportPersonDoctype) database.createBean("ImportPersonDoctype");
        Select select = database.getSelect(sample);
        select.where(Expr.equal(database.getProperty(sample, "importperson"), importPersonId));
        
        List importPersonDoctypes = database.getList(select, context);
        for (Iterator itImportPersonDoctypes = importPersonDoctypes.iterator(); itImportPersonDoctypes.hasNext(); ) {
            Map importPersonDoctype = (Map) itImportPersonDoctypes.next();
            String name = (String)importPersonDoctype.get("name");
            if (name != null) {
                Doctype doctype = (Doctype) database.getBean("Doctype",
                        database.getSelect("Doctype").where(Expr.equal("docname", name)), context);
                if (doctype == null) {
                	logger.warn( "Der Dokumenttyp '" + name + "' existiert nicht mehr und wird nicht importiert." );
                    continue;
                }
                PersonDoctype personDoctype = (PersonDoctype) database.createBean("PersonDoctype");
                personDoctype.doctype = doctype.id;
                personDoctype.person = personId;
                personDoctype.textfield = (String) importPersonDoctype.get("textfield");
                personDoctype.textfieldJoin = (String) importPersonDoctype.get("textfieldJoin");
                personDoctype.textfieldPartner = (String) importPersonDoctype.get("textfieldPartner");
                personDoctype.verify();
                database.saveBean(personDoctype, context, false);
            }
        }
    }

    /**
     * Diese Methode parst das importTextfieldMapping und liefert eine Liste von
     * {@link ImportPersonsWorker.PersonDoctypeImporter}-Instanzen.
     * 
     * @param database Datenbank
     * @param importTextfieldMapping konfiguriertes importTextfieldMapping
     * @return Liste erkannter PersonDoctype-Erstellungsvorschriften aus den 
     *  Adressfreitextfeldern
     * @throws IOException 
     * @throws BeanException 
     */
    private static List parseTextfieldMapping(Database database, ExecutionContext context, Map importTextfieldMapping) throws BeanException, IOException {
        assert database != null;
        if (importTextfieldMapping == null)
            return Collections.EMPTY_LIST;
        
        List result = new ArrayList();
        for (int i = 0; i < importTextfieldMapping.size(); i++) {
            String indexString = String.valueOf(i);
            String keyDoctype = indexString + ":Doctype";
            if (importTextfieldMapping.containsKey(keyDoctype)) {
                String keyPerson = indexString + ":Person";
                String keyPartner = indexString + ":Partner";
                String keyJoin = indexString + ":Join";
                result.add(new PersonDoctypeImporter(database, context,
                        (String) importTextfieldMapping.get(keyDoctype),
                        (String) importTextfieldMapping.get(keyPerson),
                        (String) importTextfieldMapping.get(keyPartner),
                        (String) importTextfieldMapping.get(keyJoin)));
            }
        }
        return result;
    }
    
	//
    // innere Klassen
    //
    /**
     * Diese Klasse dient dem Erzeugen von {@link PersonDoctype}-Instanzen zu 
     * {@link ImportPerson}s.
     */
    private static class PersonDoctypeImporter {
        //
        // Konstruktoren
        //
        private PersonDoctypeImporter(Database database, ExecutionContext context, String doctypeName, String personField, String partnerField, String join) throws BeanException, IOException {
            this.database = database;
            this.context = context;
            this.personField = personField;
            this.partnerField = partnerField;
            this.join = join;
            doctype = (Doctype) database.getBean("Doctype", database.getSelect("Doctype").where(Expr.equal("docname", doctypeName)), context);
            if (doctype == null)
                logger.warn("Für den Import konfigurierten Dokumenttyp '" + doctypeName + "' nicht gefunden.");
        }
        //
        // Methoden
        //
        private void createFor(Map importPerson, Integer personId) throws BeanException, IOException {
            String personText = (String)importPerson.get(personField);
            String partnerText = (String)importPerson.get(partnerField);
            if (doctype != null && ((personText != null && personText.length() > 0) || (partnerText != null && partnerText.length() > 0))) {
                PersonDoctype personDoctype = new PersonDoctype();
                personDoctype.person = personId;
                personDoctype.doctype = doctype.id;
                personDoctype.locale = doctype.locale;
                personDoctype.addresstype = doctype.addresstype;
                personDoctype.doctypeLocale = doctype.locale;
                personDoctype.doctypeAddresstype = doctype.addresstype;
                personDoctype.name = doctype.name;
                personDoctype.textfield = personText;
                personDoctype.textfieldPartner = partnerText;
                personDoctype.textfieldJoin = join;
                database.saveBean(personDoctype, context, false);
            }
        }
        
        //
        // Member
        //
        final Database database;
        final ExecutionContext context;
        final String personField;
        final String partnerField;
        final String join;
        final Doctype doctype;
    }
    
    //
    // interne Member
    //
    /** Logger dieser Klasse */
    static Logger logger = Logger.getLogger(ImportPersonsWorker.class.getName());
}
