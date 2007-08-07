/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.aa.veraweb.beans.facade.PersonDoctypeFacade;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.ExecutionContext;
import de.tarent.octopus.custom.beans.Request;
import de.tarent.octopus.custom.beans.TransactionContext;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * <p>
 * Diese Octopus-Worker-Klasse stellt Operationen für die Verbindung
 * von Personen zu Dokumenttypen zur Verfügung.
 * Details bitte dem BeanListWorker entnehmen.
 * </p>
 * 
 * <p>
 * Die Aktion {@link #showList(OctopusContext) showList} gibt eine
 * Liste zurück die zwigend alle Dokumenttypen enthält.
 * Sollte zu der entsprechenden Person noch kein Dokumenttyp vorhanden
 * sein wird ein unvollständiges Bean zurückgegeben.
 * </p>
 * 
 * <p>
 * Der Worker stellt zusätzlich zur anzeige der Liste Aktionen
 * zum direkten Anzeigen ({@link #showDetail(OctopusContext, Integer, Integer) showDetail})
 * und Speichern ({@link #saveDetail(OctopusContext) saveDetail}) von
 * {@link de.tarent.aa.veraweb.beans.PersonDoctype}-Beans zur Verfügung.
 * </p>
 * 
 * @author Christoph Jerolimov
 */
public class PersonDoctypeWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public PersonDoctypeWorker() {
		super("PersonDoctype");
	}

    //
    // Oberklasse BeanListWorker
    //
	/**
	 * Überladen weil die standard Erstellung nicht richtig zählt.
	 */
	protected Integer getCount(OctopusContext cntx, Database database) throws BeanException, IOException {
		Select select = SQL.Select();
		select.from("veraweb.tperson_doctype");
		select.select("COUNT(*)");
		extendWhere(cntx, select);
		return database.getCount(select);
	}

	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Person person = (Person)cntx.contentAsObject("person");
		select.join(new Join(Join.RIGHT_OUTER, "veraweb.tdoctype", new RawClause(
				"tperson_doctype.fk_doctype = tdoctype.pk AND tperson_doctype.fk_person = " + person.id)));
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.selectAs("tdoctype.pk", "doctypeId");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
		select.selectAs("tdoctype.locale", "doctypeLocale");
		select.selectAs("tdoctype.addresstype", "doctypeAddresstype");
		select.orderBy(Order.asc("tdoctype.sortorder").andAsc("tdoctype.docname"));
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		PersonDoctype personDoctype = (PersonDoctype)bean;
		if (personDoctype.id != null) {
			Database database = getDatabase(cntx);
			Update update = SQL.Update().
					table("veraweb.tperson_doctype").
					update("addresstype", personDoctype.addresstype).
					update("locale", personDoctype.locale).
					where(Expr.equal("pk", personDoctype.id));
			database.execute(update);
		} else {
			super.saveBean(cntx, bean);
		}
		
		WorkerFactory.getPersonDetailWorker(cntx).updatePerson(cntx, null, personDoctype.person);
	}

	//
    // Octopus-Aktionen
    //
    /** Octopus-Eingabeparameter für die Aktion {@link #showDetail(OctopusContext, Integer, Integer)} */
	public static final String INPUT_showDetail[] = { "persondoctype-id", "persondoctype-doctype" };
    /** Octopus-Eingabeparameterzwang für die Aktion {@link #showDetail(OctopusContext, Integer, Integer)} */
	public static final boolean MANDATORY_showDetail[] = { false, false };
	/**
	 * <p>
	 * Lädt eine Person-Dokumententyp-Verknüpfung und stellt diese
	 * als <code>persondoctype</code> in den Content.
	 * </p>
	 * 
	 * <p>
	 * Wenn eine Person-Doctype-ID übergeben wird, wird ein entsprechender
	 * Eintrag aus der Datenbank geladen, falls dies nicht der Fall ist,
	 * wird anhand der Doctype-ID dann ein neuer Person-Doctype-Eingtrag
	 * erzeugt (nicht in der Datenbank) und zurückgegeben.
	 * </p>
	 * 
	 * @param cntx OctopusContext
	 * @param id Person-Doctype-ID
	 * @param doctype Doctype-ID
	 * @throws BeanException
	 * @throws IOException
	 */
	public void showDetail(OctopusContext cntx, Integer id, Integer doctype) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		Person person = (Person)cntx.contentAsObject("person");
		
		Select select = database.getSelect("PersonDoctype");
		select.selectAs("tdoctype.pk", "doctype");
		select.selectAs("tdoctype.pk", "doctypeId");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.addresstype", "doctypeAddresstype");
		select.selectAs("tdoctype.locale", "doctypeLocale");
		
		if (id != null) {
			select.join("veraweb.tdoctype", "fk_doctype", "tdoctype.pk");
			select.where(Expr.equal("tperson_doctype.pk", id));
		} else {
			select.join(new Join(Join.RIGHT_OUTER, "veraweb.tdoctype", new RawClause(
					"tperson_doctype.fk_doctype = tdoctype.pk AND tperson_doctype.fk_person = " + person.id)));
			select.where(Expr.equal("tdoctype.pk", doctype));
		}
		
		PersonDoctype personDoctype = (PersonDoctype)database.getBean("PersonDoctype", select);
		if (personDoctype == null) {
			personDoctype = new PersonDoctype();
		}
		if (personDoctype.addresstype == null)
			personDoctype.addresstype = personDoctype.doctypeAddresstype;
		if (personDoctype.locale == null)
			personDoctype.locale = personDoctype.doctypeLocale;
		
		if (personDoctype.id == null) {
			PersonDoctypeFacade helper = new PersonDoctypeFacade(cntx, person);
			personDoctype.person = person.id;
			personDoctype.textfield = helper.getFreitext(
					personDoctype.doctype, personDoctype.addresstype, personDoctype.locale, true);
			personDoctype.textfieldPartner = helper.getFreitext(
					personDoctype.doctype, personDoctype.addresstype, personDoctype.locale, false);
			personDoctype.textfieldJoin = helper.getFreitextVerbinder(
					personDoctype.doctype, personDoctype.addresstype, personDoctype.locale);
		}
		
		cntx.setContent("persondoctype", personDoctype);
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)} */
	public static final String INPUT_saveDetail[] = {};
	/**
	 * Speichert eine Person-Doctype-Verknüpfung, die als <code>persondoctype</code>
	 * Bean im Request vorhanden und gültig sein muss.
	 * 
	 * @param cntx OctopusContext
	 * @throws BeanException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void saveDetail(OctopusContext cntx) throws BeanException, IOException {
		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
		
		PersonDoctype personDoctype = (PersonDoctype)request.getBean("PersonDoctype", "persondoctype");
        database.saveBean(personDoctype);
		cntx.setContent("persondoctype-id", personDoctype.id);
		
		WorkerFactory.getPersonDetailWorker(cntx).updatePerson(cntx, null, personDoctype.person);
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #createAll(OctopusContext)} */
    public static final String INPUT_createAll[] = {};
    /**
     * Diese Octopus-Aktion erzeugt alle fehlenden Personen-Dokumenttypen
     * zu der Person unter dem Schlüssel "person" im Octopus-Content.
     * 
     * @param cntx Octopus-Kontext
     */
    public void createAll(OctopusContext cntx) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        
        Person person = (Person)cntx.contentAsObject("person");
        try {
            createPersonDoctype(cntx, database, context, person);
            context.commit();
        } finally {
            context.rollBack();
        }
    }

	//
    // öffentliche Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt alle fehlenden Personen-Dokumenttypen
     * zu der übergebenen Person.
     */
	public static void createPersonDoctype(OctopusContext cntx, Database database, ExecutionContext context, Person person) throws BeanException, IOException {
		if (person == null || person.id == null) return;
		
		PersonDoctypeFacade helper = new PersonDoctypeFacade(cntx, person);
		PersonDoctype personDoctype = new PersonDoctype();
		
		Select select = database.getSelect("PersonDoctype").
				selectAs("tdoctype.pk", "doctypePk").
				selectAs("tdoctype.addresstype", "doctypeAddresstype").
				selectAs("tdoctype.locale", "doctypeLocale").
				selectAs("tdoctype.flags", "doctypeFlags").
				join(new Join(Join.RIGHT_OUTER, "veraweb.tdoctype", new RawClause(
						"tperson_doctype.fk_doctype = tdoctype.pk AND tperson_doctype.fk_person = " + person.id))).
				where(Expr.isNull("tperson_doctype.pk"));
		
		for (Iterator it = database.getList(select, context).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			personDoctype.id = (Integer)data.get("id");
			personDoctype.person = person.id;
			personDoctype.doctypeId = personDoctype.id;
			personDoctype.doctype = (Integer)data.get("doctypePk");
			personDoctype.locale = (Integer)data.get("locale");
			personDoctype.addresstype = (Integer)data.get("addresstype");
			if (personDoctype.locale == null)
				personDoctype.locale = (Integer)data.get("doctypeLocale");
			if (personDoctype.addresstype == null)
				personDoctype.addresstype = (Integer)data.get("doctypeAddresstype");
			
			Integer flags = (Integer)data.get("doctypeFlags");
			if (flags == null || flags.intValue() != Doctype.FLAG_NO_FREITEXT) {
				personDoctype.textfield = helper.getFreitext(
						personDoctype.doctype, personDoctype.addresstype, personDoctype.locale, true);
				personDoctype.textfieldPartner = helper.getFreitext(
						personDoctype.doctype, personDoctype.addresstype, personDoctype.locale, false);
				personDoctype.textfieldJoin = helper.getFreitextVerbinder(
						personDoctype.doctype, personDoctype.addresstype, personDoctype.locale);
			} else {
				personDoctype.textfield = "";
				personDoctype.textfieldPartner = "";
				personDoctype.textfieldJoin = "";
			}
			
			database.saveBean(personDoctype, context, false);
		}
	}
}
