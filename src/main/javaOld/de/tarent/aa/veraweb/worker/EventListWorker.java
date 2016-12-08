/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.VerawebUtils;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanListWorker;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige von
 * Veranstaltungslisten zur Verfügung. Details zur Verwendung
 * bitte dem {@link BeanListWorker} entnehmen.<br><br>
 *
 * Verwendet als Filter der Veranstaltungen ein Event-Object
 * das in der Session gehalten wird, siehe getSearch.
 *
 * @see #extendWhere(OctopusContext, Select)
 * @see #getSearch(OctopusContext)
 *
 * @author Christoph Jerolimov
 */
public class EventListWorker extends ListWorkerVeraWeb {
    //
    // Öffentliche Konstanten
    //
    /** Parameter: Wessen Ereignisse? */
    private final static String PARAM_DOMAIN = "domain";

    /** Parameterwert: beliebige Ereignisse */
    private final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Ereignisse des gleichen Mandanten */
    private final static String PARAM_DOMAIN_VALUE_OU = "ou";

    //
    // Konstruktoren
    //

    /** Default-Kontruktor der den Beannamen festlegt. */
    public EventListWorker() {
        super("Event");
    }

    //
    // Basisklasse BeanListWorker
    //

    /**
     * Diese Methode fügt eine Bedingung zum Filtern nach dem Mandanten hinzu, wenn der
     * aktuelle Benutzer nicht Superadmin ist.
     *
     * @param octopusContext Octopus-Kontext
     * @param select Event-Select
     * @throws BeanException wenn keine testbaren Benutzerinformationen vorliegen.
     * @see BeanListWorker#getAll(OctopusContext)
     * @see BeanListWorker#extendAll(OctopusContext, Select)
     */
    @Override
    protected void extendAll(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        super.extendAll(octopusContext, select);
        TcPersonalConfig pConfig = octopusContext.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = octopusContext.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)))
                select.where(Expr.equal("fk_orgunit", aaConfig.getOrgUnitId()));
        } else
            throw new BeanException("Missing user information");

        // Dreht die Sortierung beim Export von Personen-Daten
        // um, um erst die "ältesten" Veranstaltungen zu sehen,
        // da diese am wahrscheinlichsten für einen Export
        // in Frage kommen.
        String invertOrder = octopusContext.contentAsString("invertOrder");
        if ("true".equals(invertOrder)) {
            select.orderBy(Order.desc("datebegin").andAsc("shortname"));
        }
    }


    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
        String val = octopusContext.getRequestObject().get("searchTask");
        octopusContext.setContent("searchTask", val);
        return super.showList(octopusContext);
    }

    /**
     * Wenn das Event <code>search</code> einen Start-Termin hat,
     * werden nur Veranstaltungen angezeigt die <strong>genau</strong>
     * diesem Termin <strong>beginnen</strong>.<br><br>
     *
     * Wenn das Event <code>search<code> einen Ende-Termin hat,
     * werden nur Veranstaltungen angezeigt die <strong>nach</strong>
     * diesem Termin <strong>beginnen oder enden</strong>.<br><br>
     *
     * Siehe hierzu im 'Veranstaltung suchen'-Dialog das Eingabe Feld
     * 'Datum-Beginn', sowie die Funktion 'Aktuelle Veranstaltung anzeigen'.
     *
     * @see #getSearch(OctopusContext)
     */
    @Override
    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException {
        final Event event = getSearch(octopusContext);

        // WHERE - Filtert das Datenbank Ergebnis anhand der Benutzereingaben.
        final WhereList where = new WhereList();

        final TcPersonalConfig pConfig = octopusContext.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            final PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            final String domain = octopusContext.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))) {
                where.addAnd(Expr.equal("fk_orgunit", aaConfig.getOrgUnitId()));
            }
        } else {
            throw new BeanException("Missing user information");
        }

        if (event.shortname != null && event.shortname.length() != 0) {
            extendWhereClauseByShortname(event, where);
        }
        if (event.eventname != null && event.eventname.length() != 0) {
            extendWhereClauseByEventName(event, where);
        }
        if (event.hostname != null && event.hostname.length() != 0) {
            extendWhereClauseByHostname(event, where);
        }
        if (event.location != null) {
            extendWhereClauseByLocation(event, where);
        }
        if (event.begin != null) {
            extendWhereClauseByBeginDate(event, where);
        }
        if (event.end != null) {
            extendWhereClauseByEndDate(where);
        }

        final String internalId = octopusContext.requestAsString("person-internalId");
        if (internalId != null && !internalId.equals("")) {
            final ResultList eventIds = getEventIdsByPersonInternalId(octopusContext, internalId);
            final List list = VerawebUtils.copyResultListToArrayList(eventIds);
            final List onlyIds = new ArrayList();
            for (Object entry : list) {
                onlyIds.add(((HashMap) entry).get("fk_event"));
            }
            select.whereAnd(Expr.in("tevent.pk", onlyIds));
        }

        if (where.size() > 0) {
            select.whereAnd(where);
        }
    }

    private ResultList getEventIdsByPersonInternalId(OctopusContext octopusContext, String internalId) throws BeanException {
        final Select selectEventIdsByPersonInternalId = SQL.Select(getDatabase(octopusContext));
        selectEventIdsByPersonInternalId.select("tguest.fk_event");
        selectEventIdsByPersonInternalId.from("veraweb.tperson");
        selectEventIdsByPersonInternalId.join("veraweb.tguest", "tperson.pk", "tguest.fk_person");
        selectEventIdsByPersonInternalId.whereAndEq("internal_id", internalId);
        final Database database = getDatabase(octopusContext);
        return database.getList(selectEventIdsByPersonInternalId, database);
    }

    private void extendWhereClauseByLocation(Event search, WhereList where) {
        where.addAnd(Expr.equal("fk_location", search.location));
    }

    private void extendWhereClauseByEventName(Event search, WhereList where) {
        where.addAnd(DatabaseHelper.getWhere(search.eventname, new String[]{
                "eventname"}));
    }

    private void extendWhereClauseByShortname(Event search, WhereList where) {
        where.addAnd(DatabaseHelper.getWhere(search.shortname, new String[]{
                "shortname"}));
    }

    private void extendWhereClauseByHostname(Event search, WhereList where) {
        where.addAnd(DatabaseHelper.getWhere(search.hostname, new String[]{"hostname"}));
    }

    private void extendWhereClauseByBeginDate(Event search, WhereList where) {
        Timestamp nextDay = new Timestamp(search.begin.getTime() + 86400000); // nächster tag
        where.addAnd(Where.and(
                Expr.greaterOrEqual("datebegin", search.begin),
                Expr.less("datebegin", nextDay)));
    }

    private void extendWhereClauseByEndDate(WhereList where) {
        final String dateClause = "((datebegin IS NOT NULL AND datebegin>=now()::date) OR (dateend IS NOT NULL AND dateend>=now()::date))";
        where.addAnd(new RawClause(dateClause));
    }

    /**
     * Überprüft ob es noch laufende oder zukünftige Veranstaltungen und fragt ggf. ob diese trotzdem gelöscht werden sollen.
     */
    @Override
    protected int removeSelection(OctopusContext octopusContext, List errors, List selectionList, TransactionContext context) throws BeanException, IOException {
        int count = 0;
        if (selectionList == null || selectionList.size() == 0) return count;
        Database database = context.getDatabase();
        Map questions = new HashMap();
        Map questions2 = new HashMap();


        if (!octopusContext.getRequestObject().getParameterAsBoolean("force-remove-events")) {
            /*
             * determine events which are not expired and add question
             */
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 23);
            today.set(Calendar.MINUTE, 59);

            Integer countOfNotExpiredEvents =
                    database.getCount(database.getCount("Event").
                                    where(Where.and(
                                            Expr.in("pk", selectionList),
                                            Where.or(
                                                    Expr.greaterOrEqual("datebegin", today.getTime()),
                                                    Where.and(
                                                            Expr.less("datebegin", today.getTime()),
                                                            Where.or(
                                                                    Expr.isNull("dateend"),
                                                                    Expr.greater("dateend", today.getTime())
                                                            )
                                                    )
                                            ))),
                            context);

            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);

            if (countOfNotExpiredEvents != null && countOfNotExpiredEvents > 0) {
                questions.put("force-remove-events", languageProvider.getProperty("EVENT_LIST_WARNING_EVENTS_IN_FUTURE"));
                questions2.put("force-remove-events", languageProvider.getProperty("EVENT_LIST_WARNING_SELECTION_CHANGING"));
            } else {
                questions.put("force-remove-events", languageProvider.getProperty("EVENT_LIST_DELETE_CONFIRMATION_MESSAGE"));
            }
            octopusContext.setContent("listquestions", questions);
            octopusContext.setContent("listquestions2", questions2);
            return -1;
        }

		/*
		 * remove events
		 */
        Event event = (Event) database.createBean("Event");

        for (Object selection : selectionList) {
            event.id = (Integer) selection;
            if (removeBean(octopusContext, event, context)) {
                count++;
            }
        }
        selectionList.clear();

        try {
            // will commit here so that the following call to
            // PersonDetailWorker.removeAllDeletedPersonsHavingNoEvent()
            // succeeds
            context.commit();
        } catch (BeanException e) {
            context.rollBack();
            throw new BeanException("Die Veranstaltungen konnten nicht gel\u00f6scht werden.", e);
        }

        return count;
    }

    /**
     * Löscht Veranstaltungen inkl. der zugehörigen Aufgaben, zusätzliche Felder und der zugeordneten Gäste.
     */
    @Override
    protected boolean removeBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext) throws BeanException, IOException {
        final Event event = (Event) bean;
        try {
            final boolean eventSuccessfullDeleted = executeEventDeletion(octopusContext, transactionContext, event);
            return eventSuccessfullDeleted;
        } catch (SQLException e) {
            throw new BeanException("SQL Exception while deleting OptionalFields from Event", e);
        }
    }

    private boolean executeEventDeletion(OctopusContext octopusContext, TransactionContext transactionContext, Event event) throws BeanException, SQLException, IOException {
        final Database database = transactionContext.getDatabase();
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        deleteOptionalFields(transactionContext, database, optionalFieldsWorker, event);
        deleteEventTasks(transactionContext, database, event);
        deleteGuests(transactionContext, database, event);
        transactionContext.commit();
        return deleteLogEntriesForEvent(octopusContext, transactionContext, event);
    }

    private void deleteOptionalFields(TransactionContext transactionContext, Database database, OptionalFieldsWorker optionalFieldsWorker, Event event) throws BeanException, SQLException {
        final List<OptionalField> optionalFields = optionalFieldsWorker.getOptionalFieldsByEvent(event.id);
        for (OptionalField optionalField : optionalFields) {
            executeOptionalFieldsDeletion(transactionContext, database, optionalField);
        }
    }

    private boolean deleteLogEntriesForEvent(OctopusContext octopusContext, TransactionContext transactionContext, Event event) throws BeanException, IOException {
        boolean result = super.removeBean(octopusContext, event, transactionContext);
        if (result) {
            final Database database = transactionContext.getDatabase();
            final BeanChangeLogger clogger = new BeanChangeLogger(database);
            clogger.logDelete(octopusContext.personalConfig().getLoginname(), event);
        }
        return result;
    }

    private void deleteGuests(TransactionContext transactionContext, Database database, Event event) throws BeanException {
        transactionContext.execute(
                SQL.Delete(database).
                        from("veraweb.tguest").
                        where(Expr.equal("fk_event", event.id)));
    }

    private void deleteEventTasks(TransactionContext transactionContext, Database database, Event event) throws BeanException {
        transactionContext.execute(
                SQL.Delete(database)
                        .from("veraweb.ttask")
                        .where(Expr.equal("fk_event", event.id))
        );
    }

    private void executeOptionalFieldsDeletion(TransactionContext transactionContext, Database database, OptionalField optionalField) throws BeanException {
        transactionContext.execute(
                SQL.Delete(database)
                        .from("veraweb.toptional_fields_delegation_content")
                        .where(new Where("fk_delegation_field", optionalField.getId(), "="))
        );

        transactionContext.execute(
                SQL.Delete(database)
                        .from("veraweb.toptional_fields")
                        .where(new Where("pk", optionalField.getId(), "="))
        );
    }

    /** Octopus-Eingabe-Parameter für {@link #getSearch(OctopusContext)} */
    public static final String INPUT_getSearch[] = {};
    /** Octopus-Ausgabe-Parameter für {@link #getSearch(OctopusContext)} */
    public static final String OUTPUT_getSearch = "search";

    /**
     * Spiegelt die vom Benutzer eingebene Suche nach Veranstaltungen wieder.
     * Entsprechende Eingaben werden in der Session gespeichert.
     *
     * @param octopusContext Octopus-Context
     * @return Event-Instanz die die aktuelle Suche repräsentiert.
     * @throws BeanException FIXME
     */
    public Event getSearch(OctopusContext octopusContext) throws BeanException {
        Event search = null;
        if ("reset".equals(octopusContext.requestAsString("search"))) {
            search = (Event) new RequestVeraWeb(octopusContext).getBean("Event");
            if (octopusContext.requestAsBoolean("current")) {
                long now = System.currentTimeMillis();
                search.end = new Timestamp(now - (now % 86400000) - 86400000);
            }
        } else if ("current".equals(octopusContext.requestAsString("search"))) {
            long now = System.currentTimeMillis();
            search = new Event();
            search.end = new Timestamp(now - (now % 86400000) - 86400000);
        }
        if (search == null) {
            search = (Event) octopusContext.sessionAsObject("search" + BEANNAME);
        }
        if (search == null) {
            search = new Event();
        }
        octopusContext.setSession("search" + BEANNAME, search);
        return search;
    }
}
