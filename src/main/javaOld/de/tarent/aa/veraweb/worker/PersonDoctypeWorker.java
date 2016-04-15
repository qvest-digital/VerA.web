/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.aa.veraweb.beans.facade.PersonDoctypeFacade;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
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
     * überladen weil die standard Erstellung nicht richtig zählt.
     */
    @Override
    protected Integer getCount(OctopusContext octopusContext, Database database) throws BeanException, IOException {
        Select select = SQL.Select(database);
        select.from("veraweb.tperson_doctype");
        select.select("COUNT(*)");
        extendWhere(octopusContext, select);
        return database.getCount(select);
    }

    @Override
    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        Person person = (Person) octopusContext.contentAsObject("person");
        select.join(new Join(Join.RIGHT_OUTER, "veraweb.tdoctype", new RawClause(
                "tperson_doctype.fk_doctype = tdoctype.pk AND tperson_doctype.fk_person = " + person.id)));
    }

    @Override
    protected void extendColumns(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        select.selectAs("tdoctype.pk", "doctypeId");
        select.selectAs("tdoctype.docname", "name");
        select.selectAs("tdoctype.sortorder", "sortorder");
        select.selectAs("tdoctype.locale", "doctypeLocale");
        select.selectAs("tdoctype.addresstype", "doctypeAddresstype");
        select.orderBy(Order.asc("tdoctype.sortorder").andAsc("tdoctype.docname"));
    }

    @Override
    protected void saveBean(final OctopusContext octopusContext, Bean bean, TransactionContext transactionContext) throws BeanException, IOException {
        PersonDoctype personDoctype = (PersonDoctype) bean;

        ((PersonDoctype) bean).verify(octopusContext);
        if (personDoctype.id != null) {
            Database database = transactionContext.getDatabase();
            Update update = SQL.Update(database).
                    table("veraweb.tperson_doctype").
                    update("addresstype", personDoctype.addresstype).
                    update("locale", personDoctype.locale).
                    where(Expr.equal("pk", personDoctype.id));
            transactionContext.execute(update);
            transactionContext.commit();
        } else {
            super.saveBean(octopusContext, bean, transactionContext);
        }

        WorkerFactory.getPersonDetailWorker(octopusContext).updatePerson(octopusContext, null, personDoctype.person);
    }

    //
    // Octopus-Aktionen
    //
    // 2009-05-07 removed second parameter as it was redundant as part of fixing issue #1528, made parameter mandatory
    /** Octopus-Eingabeparameter für die Aktion {@link #showDetail(OctopusContext, Integer)} */
    public static final String INPUT_showDetail[] = {"persondoctype-id"};
    /** Octopus-Eingabeparameterzwang für die Aktion {@link #showDetail(OctopusContext, Integer)} */
    public static final boolean MANDATORY_showDetail[] = {true};

    /**
     * <p>
     * Lädt eine Person-Dokumenttyp-Verknüpfung und stellt diese
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
     * @param octopusContext OctopusContext
     * @param id Person-Doctype-ID
     * @throws BeanException
     * @throws IOException
     */
    // 2009-05-07 removed second parameter as it was redundant as part of fixing issue #1528
    public void showDetail(OctopusContext octopusContext, Integer id) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);
        Person person = (Person) octopusContext.contentAsObject("person");

        Select select = database.getSelect("PersonDoctype");
        select.selectAs("tdoctype.pk", "doctype");
        select.selectAs("tdoctype.pk", "doctypeId");
        select.selectAs("tdoctype.docname", "name");
        select.selectAs("tdoctype.addresstype", "doctypeAddresstype");
        select.selectAs("tdoctype.locale", "doctypeLocale");

        select.join("veraweb.tdoctype", "fk_doctype", "tdoctype.pk");
        select.where(Expr.equal("tperson_doctype.pk", id));

        PersonDoctype personDoctype = (PersonDoctype) database.getBean("PersonDoctype", select);
        if (personDoctype == null) {
            personDoctype = new PersonDoctype();
        }
        if (personDoctype.addresstype == null)
            personDoctype.addresstype = personDoctype.doctypeAddresstype;
        if (personDoctype.locale == null)
            personDoctype.locale = personDoctype.doctypeLocale;

        if (personDoctype.id == null) {
            PersonDoctypeFacade helper = new PersonDoctypeFacade(octopusContext, person);
            personDoctype.person = person.id;
            personDoctype.textfield = helper.getFreitext(
                    personDoctype.doctype, personDoctype.addresstype, personDoctype.locale, true);
            personDoctype.textfieldPartner = helper.getFreitext(
                    personDoctype.doctype, personDoctype.addresstype, personDoctype.locale, false);
            personDoctype.textfieldJoin = helper.getFreitextVerbinder(
                    personDoctype.doctype, personDoctype.addresstype, personDoctype.locale);
        }

        octopusContext.setContent("persondoctype", personDoctype);
    }

    @Override
    public List showList(OctopusContext octopusContext) throws IOException, BeanException {

        Integer countRemove = (Integer) octopusContext.getContextField("countRemove");
        Integer countUpdate = (Integer) octopusContext.getContextField("countUpdate");
        Integer countInsert = (Integer) octopusContext.getContextField("countInsert");

        if (countRemove != null || countUpdate != null || countInsert != null) {
            VerawebMessages verawebMessages = new VerawebMessages(octopusContext);
            octopusContext.setContent("noChangesMessage", verawebMessages.getMessageNoChanges());
        }

        return super.showList(octopusContext);
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)} */
    public static final String INPUT_saveDetail[] = {};

    /**
     * Speichert eine Person-Doctype-Verknüpfung, die als <code>persondoctype</code>
     * Bean im Request vorhanden und gültig sein muss.
     *
     * @param octopusContext OctopusContext
     * @throws BeanException
     * @throws IOException
     * @throws SQLException
     */
    public void saveDetail(OctopusContext octopusContext) throws BeanException, IOException {
        Request request = new RequestVeraWeb(octopusContext);
        Database database = new DatabaseVeraWeb(octopusContext);

        PersonDoctype personDoctype = (PersonDoctype) request.getBean("PersonDoctype", "persondoctype");

        if (personDoctype.isCorrect()) {
            database.saveBean(personDoctype);
            octopusContext.setContent("persondoctype-id", personDoctype.id);
            WorkerFactory.getPersonDetailWorker(octopusContext).updatePerson(octopusContext, null, personDoctype.person);
        }
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #createAll(OctopusContext)} */
    public static final String INPUT_createAll[] = {};

    /**
     * Diese Octopus-Aktion erzeugt alle fehlenden Personen-Dokumenttypen
     * zu der Person unter dem Schlüssel "person" im Octopus-Content.
     *
     * @param octopusContext Octopus-Kontext
     */
    public void createAll(OctopusContext octopusContext) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);
        TransactionContext context = database.getTransactionContext();

        Person person = (Person) octopusContext.contentAsObject("person");
        try {
            createPersonDoctype(octopusContext, database, context, person);
            context.commit();
        } catch (BeanException e) {
            context.rollBack();
        }
    }

    // 2009-05-07 introduced as part of fixing issue #1528, made parameter mandatory
    public static final String INPUT_createOne[] = {"doctype-id"};
    public static final boolean MANDATORY_createOne[] = {true};

    public void createOne(OctopusContext octopusContext, Integer id) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);
        Person person = (Person) octopusContext.contentAsObject("person");

        Select select = database.getSelect("PersonDoctype");
        select.selectAs("tdoctype.pk", "doctype");
        select.selectAs("tdoctype.pk", "doctypeId");
        select.selectAs("tdoctype.docname", "name");
        select.selectAs("tdoctype.addresstype", "doctypeAddresstype");
        select.selectAs("tdoctype.locale", "doctypeLocale");
        select.join("veraweb.tdoctype", "fk_doctype", "tdoctype.pk");
        select.where(Expr.equal("tdoctype.pk", id));
        select.whereAnd(Expr.equal("tperson_doctype.fk_person", person.id));

        PersonDoctype personDoctype = (PersonDoctype) database.getBean("PersonDoctype", select);
        if (personDoctype == null) {
            Select select2 = database.getSelect("Doctype");
            select2.selectAs("tdoctype.pk", "doctype");
            select2.selectAs("tdoctype.pk", "doctypeId");
            select2.selectAs("tdoctype.docname", "name");
            select2.selectAs("tdoctype.addresstype", "doctypeAddresstype");
            select2.selectAs("tdoctype.locale", "doctypeLocale");
            select2.where(Expr.equal("tdoctype.pk", id));

            Doctype doctype = (Doctype) database.getBean("Doctype", select2);

            if (doctype != null) {
                personDoctype = new PersonDoctype();
                // found a non existing person doctype, now create it
                personDoctype.addresstype = doctype.addresstype;
                personDoctype.locale = doctype.locale;
                personDoctype.doctype = doctype.id;
                personDoctype.doctypeAddresstype = doctype.addresstype;
                personDoctype.doctypeLocale = doctype.locale;
                personDoctype.name = doctype.name;

                PersonDoctypeFacade helper = new PersonDoctypeFacade(octopusContext, person);
                Integer flags = doctype.flags;
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
                personDoctype.person = person.id;

                database.saveBean(personDoctype);

                octopusContext.setContent("persondoctype-id", personDoctype.id);
            }
        }
    }

    //
    // Öffentliche Hilfsmethoden
    //

    /**
     * Diese Methode erzeugt alle fehlenden Personen-Dokumenttypen
     * zu der übergebenen Person.
     */
    public static void createPersonDoctype(OctopusContext octopusContext, Database database, ExecutionContext context, Person person) throws BeanException, IOException {
        if (person == null || person.id == null) return;

        PersonDoctypeFacade helper = new PersonDoctypeFacade(octopusContext, person);
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
            Map data = (Map) it.next();
            personDoctype.id = (Integer) data.get("id");
            personDoctype.person = person.id;
            personDoctype.doctypeId = personDoctype.id;
            personDoctype.doctype = (Integer) data.get("doctypePk");
            personDoctype.locale = (Integer) data.get("locale");
            personDoctype.addresstype = (Integer) data.get("addresstype");
            if (personDoctype.locale == null)
                personDoctype.locale = (Integer) data.get("doctypeLocale");
            if (personDoctype.addresstype == null)
                personDoctype.addresstype = (Integer) data.get("doctypeAddresstype");

            Integer flags = (Integer) data.get("doctypeFlags");
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
