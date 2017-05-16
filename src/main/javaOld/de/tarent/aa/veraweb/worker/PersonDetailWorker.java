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

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.OnlineRegistrationHelper;
import de.tarent.aa.veraweb.utils.OsiamLoginCreator;
import de.tarent.aa.veraweb.utils.OsiamLoginRemover;
import de.tarent.aa.veraweb.utils.PersonNameTrimmer;
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evolvis.veraweb.onlinereg.entities.LinkType;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * Octopus-Worker der Aktionen zur Detailansicht von Personen bereitstellt, wie
 * das {@link #showDetail(OctopusContext, Integer, Person) Laden} oder das
 * {@link #saveDetail(OctopusContext, Person) Speichern} von Daten.
 *
 * @author Christoph
 * @author Stefan Weiz, tarent solutions GmbH
 */
public class PersonDetailWorker implements PersonConstants {
    /**
     * Logger dieser Klasse
     */
    private static final Logger LOGGER = LogManager.getLogger(PersonDetailWorker.class);

    /**
     * Example Property file: client.id=example-client client.secret=secret
     * client
     * .redirect_uri=http://osiam-test.lan.tarent.de:8080/addon-administration/
     * <p/>
     * osiam.server.resource=http://osiam-test.lan.tarent.de:8080/osiam-resource
     * -server/
     * osiam.server.auth=http://osiam-test.lan.tarent.de:8080/osiam-auth-server/
     */
    private static final String OSIAM_RESOURCE_SERVER_ENDPOINT = "osiam.server.resource";
    private static final String OSIAM_AUTH_SERVER_ENDPOINT = "osiam.server.auth";
    private static final String OSIAM_CLIENT_REDIRECT_URI = "osiam.client.redirect_uri";
    private static final String OSIAM_CLIENT_SECRET = "osiam.client.secret";
    private static final String OSIAM_CLIENT_ID = "osiam.client.id";

    //
    // Octopus-Aktionen
    //
    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #showDetail(OctopusContext, Integer, Person)}
     */
    public static final String INPUT_showDetail[] = { "id", "person" };
    /**
     * Ausgabe-Parameter der Octopus-Aktion
     * {@link #showDetail(OctopusContext, Integer, Person)}
     */
    public static final String OUTPUT_showDetail = "person";
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion
     * {@link #showDetail(OctopusContext, Integer, Person)}
     */
    public static final boolean MANDATORY_showDetail[] = { false, false };
    private static Database database;
    private TransactionContext transactionalContext;
    private static final Update updateEventStatement = SQL.Update(database).table("veraweb.tevent").update("fk_host", null).update("hostname", null);
    private static final Update deletePerson = SQL.Update(database).table("veraweb.tperson").update("deleted", PersonConstants.DELETED_TRUE);
    private static final Delete deleteGuest = SQL.Delete(database).from("veraweb.tguest");
    private static final Delete deletePersonTasks = SQL.Delete(database).from("veraweb.ttask");
    private static final Delete deletePersonMailinglist = SQL.Delete(database).from("veraweb.tperson_mailinglist");
    private static final Delete deletePersonCategory = SQL.Delete(database).from("veraweb.tperson_categorie");
    private static final Logger logger = LogManager.getLogger(PersonDetailWorker.class.getCanonicalName());

    /**
     * Diese Octopus Aktion nimmt die übergebene Person oder die Person zu der
     * übergebenen ID oder die Person zu der ID unter "person-id" in der Session
     * und gibt sie zurück. Als Seiteneffekt wird (wenn die Person nicht null
     * ist) im Octopus-Content unter "person-diplodatetime" ein Flag, ob das
     * Akkreditierungsdatum einen Zeitanteil enthält, und die übergebene ID
     * (falls die Person durch sie identifiziert wurde) in der Session unter
     * "person-id" abgelegt.
     *
     * @param octopusContext
     *            Octopus-Kontext
     * @param id
     *            ID der Person
     * @param person
     *            Person
     */
    public Person showDetail(OctopusContext octopusContext, Integer id, Person person) throws BeanException, IOException {
        logger.debug("Show person details");

        Database database = new DatabaseVeraWeb(octopusContext);

        if (person == null) {
            id = getPersonId(octopusContext, id, false);
            person = (Person) database.getBean("Person", id);
        }

        /** BUGFIX: 18738 */
        Person originalPerson = (Person) database.getBean("Person", id);
        if (originalPerson != null) {
            person.setField("birthday_a_e1", originalPerson.birthday_a_e1);
            person.setField("birthday_b_e1", originalPerson.birthday_b_e1);
            person.setField("diplodate_a_e1", originalPerson.diplodate_a_e1);
            person.setField("diplodate_b_e1", originalPerson.diplodate_b_e1);
            person.setField("internal_id", originalPerson.internal_id);
        }

        /** BUGFIX: 18738 */
        if (person != null) {
            octopusContext.setContent("person-diplodatetime", DateHelper.isTimeInDate(person.diplodate_a_e1));
        }

        Map map = (Map) octopusContext.sessionAsObject("statistikSettings");
        if (map == null) {
            map = new HashMap();
            octopusContext.setSession("statistikSettings", map);
        }

        /*
         * added for support of direct search result list navigation, see below
         * cklein 2008-03-12
         */
        this.restoreNavigation(octopusContext, person, database);

        setCurrentTimeForPersonInMap(person, map);

        octopusContext.setContent("personTab", octopusContext.requestAsString("personTab"));
        octopusContext.setContent("personMemberTab", octopusContext.requestAsString("personMemberTab"));
        octopusContext.setContent("personAddresstypeTab", octopusContext.requestAsString("personAddresstypeTab"));
        octopusContext.setContent("personLocaleTab", octopusContext.requestAsString("personLocaleTab"));

        return person;
    }

    private void setCurrentTimeForPersonInMap(Person person, Map map) {
        /*
         * modified to support a direct statistics access from the detail view
         * as per the change request for version 1.2.0 cklein 2008-02-21
         */
        map.put("statistik", "EventsGroupByGuest");
        Date d = new Date(System.currentTimeMillis());

        if (person != null && person.created != null) {
            d = new Date(person.created.getTime());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        map.put("begin", "01." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR));
    }

    protected void restoreNavigation(OctopusContext octopusContext, Person person, Database database) throws BeanException, IOException {
        final String action = octopusContext.requestAsString("action");
        final Integer personId = octopusContext.requestAsInteger("id");

        // now get the proper select from the workers based on
        // the optionally defined action parameter
        Person sample = new Person();
        Select select = database.getSelectIds(sample);
        if ("duplicateSearch".equals(action)) {
            select = getPersonIdAndName(octopusContext, database, action, personId);
        } else if (action == null || action.length() == 0 || "person".equals(action)) {
            setPersonsForNavigation(octopusContext, select);
        }

        try {
            fillNavigationWithPersonData(octopusContext, person, database, action, personId, select);
        } catch (SQLException e) {
            throw new BeanException("Unexpected exception occurred: ", e);
        }
    }

    private void fillNavigationWithPersonData(OctopusContext octopusContext, Person person, Database database, final String action,
            final Integer personId, Select select) throws SQLException {
        Map<String, Map<String, Object>> navigation = new HashMap<>();
        Map<String, Object> entry = new HashMap<>();

        // setup current
        entry.put("person", person);
        navigation.put("current", entry);

        ResultList result = new ResultList(select.executeSelect(database).resultSet());

        int size = result.size();
        int i;
        i = setPersonMapSize(personId, navigation, result, size);

        Map<String, Object> meta = new HashMap<>();
        meta.put("action", action);
        meta.put("offset", i + 1);
        meta.put("count", size);
        navigation.put("meta", meta);
        octopusContext.setContent("navigation", navigation);
    }

    private int setPersonMapSize(final Integer personId, Map<String, Map<String, Object>> navigation, ResultList result, int size) {
        int i;
        for (i = 0; i < size; i++) {
            Map cur = (Map) result.get(i);
            if (cur.get("id").equals(personId)) {
                break;
            }
        }
        if (i >= size) {
            i = size - 1;
        }
        Map first = null;
        Map previous = null;
        if (i > 0) {
            first = copyPersonMap((Map) result.get(0));
            previous = copyPersonMap((Map) result.get(i - 1));
        }

        Map fentry = null;
        if (first != null) {
            fentry = new HashMap<>();
            fentry.put("person", first);
        }
        navigation.put("first", fentry);

        Map pentry = null;
        if (previous != null) {
            pentry = new HashMap<>();
            pentry.put("person", previous);
        }
        navigation.put("previous", pentry);

        Map next = null;
        Map last = null;
        if (i < size - 1) {
            next = copyPersonMap((Map) result.get(i + 1));
            last = copyPersonMap((Map) result.get(size - 1));
        }

        Map nentry = null;
        if (next != null) {
            nentry = new HashMap<>();
            nentry.put("person", next);
        }
        navigation.put("next", nentry);

        Map lentry = null;
        if (last != null) {
            lentry = new HashMap<>();
            lentry.put("person", last);
        }
        navigation.put("last", lentry);
        return i;
    }

    private void setPersonsForNavigation(OctopusContext octopusContext, Select select) throws BeanException {
        // standard person list
        // must navigate through all persons matching current search query
        // filter
        PersonListWorker w = WorkerFactory.getPersonListWorker(octopusContext);
        PersonSearch s = w.getSearch(octopusContext);
        w.extendWhere(octopusContext, select);

        // part replication of the worker's behaviour
        select.setDistinct(s.categoriesSelection != null || s.categorie2 != null);
        select.select("tperson.lastname_a_e1");
        select.select("tperson.firstname_a_e1");
        select.orderBy(Order.asc("tperson.lastname_a_e1").andAsc("tperson.firstname_a_e1"));
    }

    private Select getPersonIdAndName(OctopusContext octopusContext, Database database, final String action, final Integer personId)
            throws BeanException, IOException {
        Select select;
        // add the action and personId once again to the context
        octopusContext.setContent("action", action);
        octopusContext.setContent("id", personId);

        // must navigate through all persons matching duplicate search query
        // filter
        PersonDuplicateSearchWorker w = WorkerFactory.getPersonDuplicateSearchWorker(octopusContext);
        // replaces the original select as it is very similar
        select = w.getSelect(database);
        w.extendWhere(octopusContext, select);
        select.clearColumnSelection();
        select.selectAs("tperson.pk", "id");
        select.select("tperson.lastname_a_e1");
        select.select("tperson.firstname_a_e1");
        select.orderBy(Order.asc("tperson.lastname_a_e1").andAsc("tperson.firstname_a_e1"));
        return select;
    }

    private Map<String, Object> copyPersonMap(Map<String, Object> personMap) {
        Map<String, Object> result = new HashMap<>();
        for (String key : personMap.keySet()) {
            result.put(key, personMap.get(key));
        }
        return result;
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #copyPerson(OctopusContext, Integer)}
     */
    public static final String INPUT_copyPerson[] = { "id" };
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion
     * {@link #copyPerson(OctopusContext, Integer)}
     */
    public static final boolean MANDATORY_copyPerson[] = { false };

    /**
     * Kopiert die Personendaten, die der übergebenen ID zugeordnet sind oder
     * sich im Octopus-Request unterhalb des Schlüssels "person" befinden, und
     * stellt diese unter dem Schlüssel "person" und ein Flag, ob das
     * Akkreditierungsdatum einen Zeitanteil enthält, unter
     * "person-diplodatetime" in den Octopus-Content.<br>
     * In der aktuellen Implementierung werden dabei neben den Teilpersonen- und
     * Adressangaben in den Zusatzzeichensätzen lediglich folgende Felder auf
     * <code>null</code> gesetzt: {@link Person#id}, {@link Person#expire},
     * {@link Person#created}, {@link Person#createdby}, {@link Person#changed},
     * {@link Person#changedby} und {@link Person#importsource}.
     *
     * @param octopusContext
     *            Octopus-Kontext
     * @param id
     *            Personen-ID
     */
    public void copyPerson(OctopusContext octopusContext, Integer id) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);

        Person person;
        if (id != null) {
            person = showDetail(octopusContext, id, null);
        } else {
            Request request = new RequestVeraWeb(octopusContext);
            person = (Person) request.getBean("Person", "person");
            DateHelper.addTimeToDate(person.diplodate_a_e1, octopusContext.requestAsString("person-diplotime_a_e1"), person.getErrors());
        }

        person = createNewPersonOrClearData(database, person);

        octopusContext.setContent("person", person);
        octopusContext.setContent("person-diplodatetime", DateHelper.isTimeInDate(person.diplodate_a_e1));
        octopusContext.setContent("originalPersonId", octopusContext.requestAsInteger("originalPersonId"));

        /*
         * added for support of direct search result list navigation, see below
         * cklein 2008-03-12
         */
        this.restoreNavigation(octopusContext, person, database);
    }

    private Person createNewPersonOrClearData(Database database, Person person) throws BeanException, IOException {
        if (person == null) {
            person = new Person();
        } else {
            person.id = null;
            person.expire = null;
            person.deleted = PersonConstants.DELETED_FALSE;
            person.created = null;
            person.createdby = null;
            person.changed = null;
            person.changedby = null;
            person.importsource = null;
            person.username = null;

            AddressHelper.clearAddressData(person.getMainExtra1());
            AddressHelper.clearAddressData(person.getMainExtra2());
            AddressHelper.clearAddressData(person.getPartnerExtra1());
            AddressHelper.clearAddressData(person.getPartnerExtra2());
            AddressHelper.clearAddressData(person.getBusinessExtra1());
            AddressHelper.clearAddressData(person.getBusinessExtra2());
            AddressHelper.clearAddressData(person.getPrivateExtra1());
            AddressHelper.clearAddressData(person.getPrivateExtra2());
            AddressHelper.clearAddressData(person.getOtherExtra1());
            AddressHelper.clearAddressData(person.getOtherExtra2());
            AddressHelper.checkPersonSalutation(person, database, database.getTransactionContext());
        }
        return person;
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #showTestPerson(OctopusContext)}
     */
    public static final String INPUT_showTestPerson[] = {};

    /**
     * Erstellt eine Test-Person und stellt diese unter dem Schlüssel "person"
     * und ein Flag, ob das Akkreditierungsdatum einen Zeitanteil enthält, unter
     * "person-diplodatetime" in den Octopus-Content.<br>
     * Anhand des Werts des Octopus-Request-Parameters zum Schlüssel "partner"
     * werden nur Daten zur Partnerperson ("only"), nur Daten zur Hauptperson
     * ("without") oder zu beiden (sonst) erzeugt.
     *
     * @param octopusContext
     *            Octopus-Kontext
     */
    public void showTestPerson(OctopusContext octopusContext) throws BeanException {
        String partner = octopusContext.requestAsString("partner");
        Person person = getTestPerson(partner);
        octopusContext.setContent("person", person);
        octopusContext.setContent("person-diplodatetime", DateHelper.isTimeInDate(person.diplodate_a_e1));
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #prepareSaveDetail(OctopusContext, Boolean)}
     */
    public static final String INPUT_prepareSaveDetail[] = { "saveperson" };
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion
     * {@link #prepareSaveDetail(OctopusContext, Boolean)}
     */
    public static final boolean MANDATORY_prepareSaveDetail[] = { false };

    /**
     * Diese Octopus-Aktion testet das übergebene Flag; falls es gesetzt ist,
     * wird der Status "saveperson" gesetzt.
     *
     * @param octopusContext
     *            Octopus-Kontext
     * @throws BeanException
     */
    public void prepareSaveDetail(OctopusContext octopusContext, Boolean saveperson) {
        if (saveperson != null && saveperson) {
            octopusContext.setStatus("saveperson");
        }
    }

    public static final String INPUT_verify[] = { "person-nodupcheck" };
    public static final boolean MANDATORY_verify[] = { false };

    public void verify(final OctopusContext octopusContext, Boolean nodupcheck) throws BeanException {
        if (nodupcheck == null || !nodupcheck) {

            Request request = new RequestVeraWeb(octopusContext);
            Person person = (Person) request.getBean("Person", "person");

            if (person != null) {
                person.verify(octopusContext);
                if (!person.isCorrect()) {
                    /* VERA-188 */
                    octopusContext.setContent("person", person);
                    if (person.id == null) {
                        octopusContext.setStatus("notcorrect");
                        octopusContext.setContent("newPersonErrors", person.getErrors());
                        octopusContext.setContent("person-iscompany", person.iscompany);
                    }
                }
            }
        }
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #saveDetail(OctopusContext, Person)}
     */
    public static final String INPUT_saveDetail[] = { "person" };
    /**
     * Ausgabe-Parameter der Octopus-Aktion
     * {@link #saveDetail(OctopusContext, Person)}
     */
    public static final String OUTPUT_saveDetail = "person";
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion
     * {@link #saveDetail(OctopusContext, Person)}
     */
    public static final boolean MANDATORY_saveDetail[] = { false };

    /**
     * Diese Octopus-Aktions speichert die übergebenen Person oder die Person
     * aus dem Octopus-Request unterhalb des Schlüssels "person" in der
     * Datenbank. Hierbei werden gegebenenfalls änderungen im Latin-Zeichensatz
     * in die anderen Zeichensätze übertragen.<br>
     * Die gespeicherte Person wird zurückgegeben, "countInsert" oder
     * "countUpdate" im Octopus-Content wird mit 1 und "person-diplodatetime"
     * mit einem Flag, ob das Akkreditierungsdatum einen Zeitanteil hat, belegt,
     * und die Werte unter "personTab", "personMemberTab",
     * "personAddresstypeTab" und "personLocaleTab" im Octopus-Request werden
     * unter den gleichen Schlüsseln in den -Content kopiert.
     *
     * @param octopusContext
     *            Octopus-Kontext
     * @return die abgespeicherte Person
     */
    public Person saveDetail(final OctopusContext octopusContext, Person person) throws BeanException, IOException {
        octopusContext.setContent("personTab", octopusContext.requestAsString("personTab"));
        octopusContext.setContent("personMemberTab", octopusContext.requestAsString("personMemberTab"));
        octopusContext.setContent("personAddresstypeTab", octopusContext.requestAsString("personAddresstypeTab"));
        octopusContext.setContent("personLocaleTab", octopusContext.requestAsString("personLocaleTab"));

        Database database = new DatabaseVeraWeb(octopusContext);
        TransactionContext context = database.getTransactionContext();

        Integer originalPersonId = octopusContext.requestAsInteger("originalPersonId");

        try {
            if (person == null) {
                Request request = new RequestVeraWeb(octopusContext);
                person = (Person) request.getBean("Person", "person");
            }

            if (person.isModified()) {
                PersonNameTrimmer.trimAllPersonNames(person);

                /*
                 * fix for bug 1013 cklein 2008-03-12
                 */
                person.verify(octopusContext);
                if (!person.isCorrect()) {
                    octopusContext.setStatus("notcorrect");

                    // is this a new record?
                    if (person.id == null) {
                        /*
                         * 2009-06-08 cklein fixing issue with new persons
                         * losing all state and data when entering an invalid
                         * date and trying to store the person part of fix to
                         * issue #1529, as it first showed up when testing the
                         * fixes to that issue
                         */
                        // we transfer the errors from the
                        // person to the template parameter newPersonErrors
                        octopusContext.setContent("newPersonErrors", person.getErrors());
                    }

                    return person;
                }

                if (octopusContext.requestAsBoolean("forcedupcheck")) {
                    return person;
                }

                /*
                 * person was copied fix for bug 1011 cklein 2008-03-12
                 */
                if (originalPersonId != null && originalPersonId > 0) {
                    person.setModified(true);
                }

                /*
                 * added support for workarea assignment
                 *
                 * cklein 2008-02-20
                 */
                person.workarea = octopusContext.requestAsInteger("workarea-id");

                savePersonDetail(octopusContext, person, database, context, originalPersonId);
            } else {
                VerawebMessages verawebMessages = new VerawebMessages(octopusContext);
                octopusContext.setContent("noChangesMessage", verawebMessages.getMessageNoChanges());
            }
        } catch (BeanException e) {
            context.rollBack();
            throw new BeanException("Die Person konnte nicht gespeichert werden.", e);
        }

        return person;
    }

    private void savePersonDetail(final OctopusContext octopusContext, Person person, Database database, TransactionContext transactionContext,
            Integer originalPersonId) throws BeanException, IOException {
        Person personOld = null;
        if (person != null && person.id != null) {
            personOld = (Person) database.getBean("Person", person.id, transactionContext);
        }

        DateHelper.addTimeToDate(person.diplodate_a_e1, octopusContext.requestAsString("person-diplotime_a_e1"), person.getErrors());
        person.orgunit = ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId();
        person.updateHistoryFields(((PersonalConfigAA) octopusContext.personalConfig()).getRoleWithProxy());
        if (personOld != null) {
            setEntityCreationData(person, personOld);
        }
        AddressHelper.checkPersonSalutation(person, database, transactionContext);

        updateExpireDate(person, personOld);

        // must reverify due to above changes
        person.verify(octopusContext);
        if (person.isModified() && person.isCorrect()) {
            createOrUpdatePerson(octopusContext, person, database, transactionContext, originalPersonId, personOld);
        } else if (person.isModified()) {
            octopusContext.setStatus("notcorrect");
        }
        transactionContext.commit();

        // must reset the originalPersonId here, otherwise restoreNavigation
        // will fail
        octopusContext.setContent("originalPersonId", (Integer) null);

        octopusContext.setContent("person-diplodatetime", DateHelper.isTimeInDate(person.diplodate_a_e1));

        /*
         * added for support of direct search result list navigation, see below
         * cklein 2008-03-12
         */
        this.restoreNavigation(octopusContext, person, database);
    }

    private void setEntityCreationData(Person person, Person personOld) {
        person.created = personOld.created;
        person.createdby = personOld.createdby;
    }

    private void updateExpireDate(Person person, Person personOld) {
        // Updatet das Gueltigkeitsdatum automatisch auf "in 3 Jahre"
        // wenn dieses nicht verändert wurde.
        if (person.expire != null && personOld != null && personOld.expire != null) {
            Calendar e1 = Calendar.getInstance();
            Calendar e2 = Calendar.getInstance();
            Calendar ty = Calendar.getInstance();
            ty.add(Calendar.YEAR, 3);

            e1.setTimeInMillis(person.expire.getTime());
            e2.setTimeInMillis(personOld.expire.getTime());

            boolean notModified = e1.get(Calendar.YEAR) == e2.get(Calendar.YEAR) && e1.get(Calendar.MONTH) == e2.get(Calendar.MONTH)
                    && e1.get(Calendar.DAY_OF_MONTH) == e2.get(Calendar.DAY_OF_MONTH);

            if (notModified && person.expire.getTime() < ty.getTimeInMillis()) {
                person.expire = new Timestamp(ty.getTimeInMillis());
                person.setModified(true);
            }
        }
    }

    private void createOrUpdatePerson(OctopusContext octopusContext, Person person, Database database, TransactionContext transactionContext,
            Integer originalPersonId, Person personOld) throws BeanException, IOException {
        // Commented for Bugfix #19336
        // checkConversionFromFirmaToPerson(person);
        AddressHelper.copyAddressData(octopusContext, person, personOld);

        /*
         * modified to support change logging cklein 2008-02-12
         */
        BeanChangeLogger clogger = new BeanChangeLogger(database, transactionContext);


        if (person.id == null) {
            createNewPerson(octopusContext, person, database, transactionContext, originalPersonId, clogger);
        } else {
            updateExistingPerson(octopusContext, person, database, transactionContext, personOld, clogger);
        }

        getPersonId(octopusContext, person.id, true);
    }

    private void updateExistingPerson(OctopusContext octopusContext, Person person, Database database, TransactionContext transactionContext,
            Person personOld, BeanChangeLogger clogger) throws BeanException, IOException {
        octopusContext.setContent("countUpdate", 1);
        person.changed = new Timestamp(new Date().getTime());
        Update update = database.getUpdate(person);
        if (!((PersonalConfigAA) octopusContext.personalConfig()).getGrants().mayReadRemarkFields()) {
            update.remove("note_a_e1");
            update.remove("note_b_e1");
            update.remove("notehost_a_e1");
            update.remove("notehost_b_e1");
            update.remove("noteorga_a_e1");
            update.remove("noteorga_b_e1");
        }
        transactionContext.execute(update);
        transactionContext.commit();

        clogger.logUpdate(octopusContext.personalConfig().getLoginname(), personOld, person);
    }

    private void createNewPerson(OctopusContext octopusContext, Person person, Database database, TransactionContext transactionContext,
            Integer originalPersonId, BeanChangeLogger clogger) throws BeanException, IOException {
        octopusContext.setContent("countInsert", 1);
        person.created = new Timestamp(new Date().getTime());
        database.getNextPk(person, transactionContext);
        Insert insert = database.getInsert(person);
        insert.insert("pk", person.id);
        if (!((PersonalConfigAA) octopusContext.personalConfig()).getGrants().mayReadRemarkFields()) {
            insert.remove("note_a_e1");
            insert.remove("note_b_e1");
            insert.remove("notehost_a_e1");
            insert.remove("notehost_b_e1");
            insert.remove("noteorga_a_e1");
            insert.remove("noteorga_b_e1");
        }
        transactionContext.execute(insert);
        transactionContext.commit();

        // Bug 1592 Wenn die person kopiert wurde, dann die Kategorien der
        // original Person an neue Person kopieren
        if (originalPersonId != null && originalPersonId != 0) {
            copyCategories(originalPersonId, person.id, database, transactionContext);
        }

        clogger.logInsert(octopusContext.personalConfig().getLoginname(), person);
    }

    /**
     * kopiert alle Kategorie-Relationen der original-Person an die new-Person
     *
     * @param originalPersonId   Id der original-Person
     * @param newPersonId        Id der new-Person
     * @param database           Datenbank
     * @param transactionContext Transaktionskontext der Datenbank
     * @throws IOException FIXME
     */
    private void copyCategories(Integer originalPersonId, Integer newPersonId, Database database, TransactionContext transactionContext)
            throws IOException {
        assert originalPersonId != null;
        assert newPersonId != null;
        assert database != null;
        assert transactionContext != null;

        if (originalPersonId.equals(newPersonId)) {
            return;
        }

        try {
            final Select select = database.getSelect("PersonCategorie").where(Expr.equal("fk_person", originalPersonId));
            // order by geht auf andere Tabelle; ist nur fuer join gedacht
            select.orderBy(null);
            final List result = database.getBeanList("PersonCategorie", select);
            // if (result.isEmpty()) return;

            Iterator i = result.iterator();
            while (i.hasNext()) {
                PersonCategorie bean = (PersonCategorie) i.next();
                bean.person = newPersonId;
                /*
                 * obsolete, die vera-DB setzt den pk automatisch
                 * database.getNextPk(bean, context);
                 */
                Insert insert = database.getInsert(bean);
                transactionContext.execute(insert);
                transactionContext.commit();
            }
        } catch (BeanException e) {
            LOGGER.warn("Beim Kopieren einer Person konnten Kategorien nicht uebernommen werden", e);
        }
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #updatePerson(OctopusContext, Person, Integer)}
     */
    public static final String INPUT_updatePerson[] = { "person", "person-id" };
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion
     * {@link #updatePerson(OctopusContext, Person, Integer)}
     */
    public static final boolean MANDATORY_updatePerson[] = { false, false };

    /**
     * Diese Octopus-Aktion aktualisiert die Historisierungsdaten der Person mit
     * der übergebenen ID (es wird die übergebene genommen oder eine Instanz aus
     * der DB geladen).
     *
     * @param octopusContext Octopus-Kontext
     * @param person         Person; wird benutzt, falls sie die richtige ID hat
     * @param personId       Personen-ID
     */
    public void updatePerson(OctopusContext octopusContext, Person person, Integer personId) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);

        if (person == null) {
            person = (Person) octopusContext.contentAsObject("person"); // ???
        }
        if (person == null || !person.id.equals(personId)) {
            person = (Person) database.getBean("Person", personId);
        }
        if (person != null && person.id != null) {
            person.updateHistoryFields(((PersonalConfigAA) octopusContext.personalConfig()).getRoleWithProxy());
            Update update = database.getUpdate("Person");
            update.update(database.getProperty(person, "created"), person.created);
            update.update(database.getProperty(person, "createdby"), person.createdby);
            update.update(database.getProperty(person, "changed"), person.changed);
            update.update(database.getProperty(person, "changedby"), person.changedby);
            update.where(Expr.equal(database.getProperty(person, "id"), person.id));
            final TransactionContext transactionContext = database.getTransactionContext();
            transactionContext.execute(update);
            transactionContext.commit();

            // get the original version of the object for logging purposes
            Person personOld = (Person) database.getBean("Person", personId);
            BeanChangeLogger clogger = new BeanChangeLogger(database);
            clogger.logUpdate(octopusContext.personalConfig().getLoginname(), personOld, person);
        }
    }

    /**
     * Diese Methode erzeugt eine Test-Person und liefert diese zurück.
     *
     * @param partner bei "only" werden nur Daten zur Partnerperson, bei "without"
     *                nur Daten zur Hauptperson und sonst Daten zu beiden erzeugt.
     */
    public static Person getTestPerson(String partner) throws BeanException {
        Person person = new Person();
        String suffix = " [test-" + new Random().nextInt(10000) + "]";
        if (partner == null || !partner.equals("only")) {
            showTestPerson(person.getMainLatin(), suffix + " (Hauptperson L)");
            showTestPerson(person.getMainExtra1(), suffix + " (Hauptperson ZS1)");
            showTestPerson(person.getMainExtra2(), suffix + " (Hauptperson ZS2)");
        }
        if (partner == null || !partner.equals("without")) {
            showTestPerson(person.getPartnerLatin(), suffix + " (Partner L)");
            showTestPerson(person.getPartnerExtra1(), suffix + " (Partner ZS1)");
            showTestPerson(person.getPartnerExtra2(), suffix + " (Partner ZS2)");
        }
        showTestPerson(person.getBusinessLatin(), suffix + " (Geschäftlich L)");
        showTestPerson(person.getBusinessExtra1(), suffix + " (Geschäftlich ZS1)");
        showTestPerson(person.getBusinessExtra2(), suffix + " (Geschäftlich ZS2)");
        showTestPerson(person.getPrivateLatin(), suffix + " (Privat L)");
        showTestPerson(person.getPrivateExtra1(), suffix + " (Privat ZS1)");
        showTestPerson(person.getPrivateExtra2(), suffix + " (Privat ZS2)");
        showTestPerson(person.getOtherLatin(), suffix + " (Weitere L)");
        showTestPerson(person.getOtherExtra1(), suffix + " (Weitere ZS1)");
        showTestPerson(person.getOtherExtra2(), suffix + " (Weitere ZS2)");
        person.importsource = "Test-Person" + suffix;
        person.verify();
        person.setModified(true);
        return person;
    }

    /**
     * Diese Methode füllt die Personen-Member-Facade mit Testwerten.
     *
     * @param suffix Suffix für Text-wertige Attribute
     */
    protected static void showTestPerson(PersonMemberFacade facade, String suffix) {
        facade.setBirthday(new Timestamp(System.currentTimeMillis()));
        facade.setDiplodate(new Timestamp(System.currentTimeMillis()));
        facade.setFirstname("Vorname" + suffix);
        facade.setLanguages("Sprachen" + suffix);
        facade.setLastname("Nachname" + suffix);
        facade.setNationality("Nationalität" + suffix);
        facade.setNote("Bemerkung" + suffix);
        facade.setNoteHost("Bemerkung (Gastgeber)" + suffix);
        facade.setNoteOrga("Bemerkung (Organisation)" + suffix);
        facade.setSalutation("Anrede" + suffix);
        facade.setTitle("Titel" + suffix);
    }

    /**
     * Diese Methode füllt die Personen-Adress-Facade mit Testwerten.
     *
     * @param suffix Suffix für Text-wertige Attribute
     */
    protected static void showTestPerson(PersonAddressFacade facade, String suffix) {
        facade.setCity("Ort" + suffix);
        facade.setCompany("Firma" + suffix);
        facade.setCountry("Land" + suffix);
        facade.setEMail("eMail" + suffix);
        facade.setFax("Fax" + suffix);
        facade.setFunction("Funktion" + suffix);
        facade.setMobile("Mobil" + suffix);
        facade.setPhone("Telefon" + suffix);
        facade.setPOBox("Postfach" + suffix);
        facade.setPOBoxZipCode("Postfach PLZ" + suffix);
        facade.setStreet("Straße" + suffix);
        facade.setSuffix1("Adresszusatz 1" + suffix);
        facade.setSuffix2("Adresszusatz 2" + suffix);
        facade.setUrl("www" + suffix);
        facade.setZipCode("PLZ" + suffix);
        facade.setState("Bundesland" + suffix);
    }

    /**
     * Diese Methode liefert eine aktuelle Personen-ID, wahlweise die übergebene
     * oder die unter dem Schlüssel "person-id" in der Session. Falls die
     * übergebene genommen wird, wird sie unter "person-id" in die Session
     * geschrieben.<br>
     * Die übergebene ID wird genutzt, wenn sie nicht <code>null</code> ist oder
     * der Parameter <code>forceset</code> <code>true</code> ist.
     *
     * @param octopusContext Octopus-Kontext
     * @param id             neue aktuelle ID
     * @param forceSet       erzwingt das Nutzen der übergebenen ID, selbst wenn sie
     *                       <code>null</code> ist.
     * @return die aktuelle Personen-ID
     */
    private Integer getPersonId(OctopusContext octopusContext, Integer id, boolean forceSet) {
        if (forceSet || id != null) {
            octopusContext.setSession("person-id", id);
            return id;
        }
        return (Integer) octopusContext.sessionAsObject("person-id");
    }

    /**
     * <p>
     * Löscht eine Person aus der Tabelle <code>tperson</code>, wenn auf diese
     * keine Referenzen mehr in <code>tguest</code> existieren. Dabei werden
     * auch alle abhängigen Tabelleneinträge gelöscht.
     * </p>
     * <p>
     * Wenn entsprechende Einträge noch existieren, wird lediglich die Spalte
     * <code>deleted</code> auf @link PersonConstants#DELETED_TRUE gesetzt,
     * entsprechende Einträge werden bei der Suche, etc. nicht mehr
     * berücksichtigt.
     * </p>
     *
     * @param octopusContext Aktueller Octopus Kontext
     * @param person         Die zu löschende Person
     * @throws BeanException inkl. Datenbank-Fehler
     * @throws IOException
     */
    void removePerson(OctopusContext octopusContext, Person person, String username) throws BeanException, IOException {
        // Datenbank-Einträge inkl. Abhängigkeiten löschen.
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Person l\u00f6schen: Person #" + person.id + " wird vollst\u00e4ndig gel\u00f6scht.");
        }

        executeDeleteStatements(octopusContext, person.id, username);
        executeUpdateStatements(person.id);
        transactionalContext.commit();

        /*
         * modified to support change logging cklein 2008-02-12
         */
        BeanChangeLogger clogger = new BeanChangeLogger(database, transactionalContext);
        clogger.logDelete(octopusContext.personalConfig().getLoginname(), person);
    }

    private void executeUpdateStatements(Integer personid) throws BeanException {
        updatePerson(personid);
        updateEvent(personid);
    }

    private void updateEvent(Integer personid) throws BeanException {
        final Statement fullUpdateEventStatement = updateEventStatement.where(Expr.equal("fk_host", personid));
        transactionalContext.execute(fullUpdateEventStatement);
    }

    /**
     * Die Person wird nicht wirklich gelöscht, sondern als gelöscht markiert.
     *
     * @param personid Die ID von der Person
     * @throws BeanException Falls das update doch nicht funktioniert
     */
    private void updatePerson(Integer personid) throws BeanException {
        final Statement fullDeletePersonStatement = deletePerson.where(Expr.equal("pk", personid));
        transactionalContext.execute(fullDeletePersonStatement);
    }

    private void executeDeleteStatements(OctopusContext octopusContext, Integer personid, String username) throws BeanException {
        deletePersonCategory(personid);
        deletePersonMailingList(personid);
        deletePersonTasks(personid);
        deleteOsiamUser(octopusContext, username);
        deleteGuest(personid);
    }

    private void deleteGuest(Integer personid) throws BeanException {
        final Delete fillDeleteGuestStatement = deleteGuest.where(Expr.equal("fk_person", personid));
        transactionalContext.execute(fillDeleteGuestStatement);
    }

    private void deletePersonTasks(Integer personid) throws BeanException {
        final Delete deleteTaskFullStatement = deletePersonTasks.where(Expr.equal("fk_person", personid));
        transactionalContext.execute(deleteTaskFullStatement);
    }

    private void deletePersonMailingList(Integer personid) throws BeanException {
        final Delete fullDeletePersonMailingListStatement = deletePersonMailinglist.where(Expr.equal("fk_person", personid));
        transactionalContext.execute(fullDeletePersonMailingListStatement);
    }

    private void deletePersonCategory(Integer personid) throws BeanException {
        final Delete currentDeletePersonCategoryStatement = deletePersonCategory.where(Expr.equal("fk_person", personid));
        transactionalContext.execute(currentDeletePersonCategoryStatement);
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #createOsiamUser(OctopusContext, ExecutionContext, Person)}
     */
    public static final String INPUT_createOsiamUser[] = { "personId" };
    /**
     * Ausgabe-Parameter der Octopus-Aktion
     * {@link #createOsiamUser(OctopusContext, ExecutionContext, Person)}
     */
    public static final String OUTPUT_createOsiamUser = "person";
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion
     * {@link #createOsiamUser(OctopusContext, ExecutionContext, Person)}
     */
    public static final boolean MANDATORY_createOsiamUser[] = { false };

    /**
     * Creates an OSIAM user with random username and password.
     *
     * @param octopusContext
     *            The {@link de.tarent.octopus.server.OctopusContext}
     */
    @SuppressWarnings("rawtypes")
    public Person createOsiamUser(OctopusContext octopusContext, Integer personId) throws BeanException, IOException {

        Person person = getPersonById(octopusContext, personId);
        if (!hasUsername(octopusContext, personId)) {

            final String username = getOsiamUsername(person, octopusContext);

            // Update in tperson
            person.username = username;
            this.updateUsernameInVeraweb(person, octopusContext);

            // Saving uuid to generate the reset-password url
            saveLinkUUID(personId, octopusContext);

            handleOsiamUserErrors(person, username, octopusContext);

            octopusContext.setContent("osiam-user-created", true);
        } else {
            octopusContext.setContent("osiam-user-exists", true);
        }

        return person;
    }

    private void handleOsiamUserErrors(Person person, final String username, OctopusContext octopusContext) {
        final Database database = new DatabaseVeraWeb(octopusContext);
        try {
            Object object = SQL.Select(database).from("veraweb.tperson").where(Expr.equal("pk", person.id)).add("username", String.class)
                    .getList(database).get(0);
            if (!username.equals(object)) {
                throw new RuntimeException("Somehow the username was not persisted?!");
            }
            if (SQL.Select(database).from("veraweb.link_uuid").select("pk").where(Expr.equal("personid", person.id)).getList(database).size() != 1) {
                throw new RuntimeException("Somehow the link was not persisted?!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getOsiamUsername(Person person, OctopusContext octopusContext) {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final OsiamLoginCreator osiamLoginCreator = new OsiamLoginCreator(database);

        return addOsiamUser(person, database, osiamLoginCreator);
    }

    private String addOsiamUser(Person person, Database database, OsiamLoginCreator osiamLoginCreator) {
        final OsiamConnector connector = getConnector();
        final String username = generateOsiamUsername(person, osiamLoginCreator, connector);
        final String password = osiamLoginCreator.generatePassword();

        createUser(username, password, connector, database);
        return username;
    }

    private String generateOsiamUsername(Person person, OsiamLoginCreator osiamLoginCreator, OsiamConnector connector) {
        if (person.iscompany.equals("t")) {
            return executeCompanyUsernameGeneration(person, osiamLoginCreator, connector);
        } else {
            return executePersonUsernameGeneration(person, osiamLoginCreator, connector);
        }
    }

    private String executeCompanyUsernameGeneration(Person person, OsiamLoginCreator osiamLoginCreator, OsiamConnector connector) {
        final String companyname = person.company_a_e1;
        return osiamLoginCreator.generateCompanyUsername(companyname, connector);
    }

    private String executePersonUsernameGeneration(Person person, OsiamLoginCreator osiamLoginCreator, OsiamConnector connector) {
        final String firstname = person.firstname_a_e1;
        final String lastname = person.lastname_a_e1;
        return osiamLoginCreator.generatePersonUsername(firstname, lastname, connector);
    }

    /**
     * Save new instance LinkUUID to allow having a reset password url
     *
     * @param personId FIXME
     * @throws BeanException
     * @throws IOException
     */
    private void saveLinkUUID(Integer personId, OctopusContext octopusContext) {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();
        try {
            transactionContext.execute(SQL.Insert(database).table("veraweb.link_uuid").insert("uuid", getNewPersonUUID()).insert("linktype", LinkType.PASSWORDRESET.getText()).insert("personid", personId));
            transactionContext.commit();
        } catch (BeanException e) {
            LOGGER.error("Persisting uuid for link generation failed", e);
        }
    }

    /**
     * Deletes an OSIAM user with the given username.
     *
     * @param octopusContext The {@link de.tarent.octopus.server.OctopusContext}
     * @param username       The username
     */
    public void deleteOsiamUser(OctopusContext octopusContext, String username) {
        if (OnlineRegistrationHelper.isOnlineregActive(octopusContext)) {
            final OsiamConnector connector = getConnector();
            final AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
            final OsiamLoginRemover osiamLoginRemover = new OsiamLoginRemover(connector);
            osiamLoginRemover.deleteOsiamUser(accessToken, username);
        }
    }

    private void updateUsernameInVeraweb(Person person, OctopusContext octopusContext) {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();
        try {
            transactionContext.execute(SQL.Update(database).table("veraweb.tperson").update("username", person.username).where(Expr.equal("pk", person.id)));
            transactionContext.commit();
        } catch (BeanException e) {
            LOGGER.error("Persisting username failed", e);
        }
    }

    private Boolean hasUsername(OctopusContext octopusContext, Integer personId) throws BeanException, IOException {

        final Database database = new DatabaseVeraWeb(octopusContext);
        Integer counter = database.getCount(database.getCount("Person").where(Where.and(Expr.equal("pk", personId), Expr.isNotNull("username"))));

        return (counter == 1);
    }

    private Person getPersonById(OctopusContext octopusContext, Integer personId) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);
        return (Person) database.getBean("Person", personId);
    }

    private Properties getProperties() {
        final PropertiesReader propertiesReader = new PropertiesReader();
        return propertiesReader.getProperties();
    }

    private void createUser(String username, String password, OsiamConnector connector, Database database) {
        final AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
        final OsiamLoginCreator osiamLoginCreator = new OsiamLoginCreator(database);

        osiamLoginCreator.createOsiamUser(accessToken, username, password, connector);
    }

    /**
     * Getting OSIAM Connector to execute updates over OSIAM's Database
     *
     * @return OsiamConnector the connector
     */
    private OsiamConnector getConnector() {
        final Properties properties = getProperties();

        return new OsiamConnector.Builder()
                .setClientRedirectUri(properties.getProperty(OSIAM_CLIENT_REDIRECT_URI))
                .setClientSecret(properties.getProperty(OSIAM_CLIENT_SECRET))
                .setClientId(properties.getProperty(OSIAM_CLIENT_ID))
                .setAuthServerEndpoint(properties.getProperty(OSIAM_AUTH_SERVER_ENDPOINT))
                .setResourceServerEndpoint(properties.getProperty(OSIAM_RESOURCE_SERVER_ENDPOINT))
                .build();
    }

    /**
     * New hash for persons
     */
    private String getNewPersonUUID() {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }

    public void setDatabase(Database database) {
        PersonDetailWorker.database = database;
    }

    public void setTransactionalContext(TransactionContext transactionalContext) {
        this.transactionalContext = transactionalContext;
    }
}
