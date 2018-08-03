package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017, 2018 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
 * @author Christoph Jerolimov
 * @see #extendWhere(OctopusContext, Select)
 * @see #getSearch(OctopusContext)
 */
public class EventListWorker extends ListWorkerVeraWeb {
    //
    // Öffentliche Konstanten
    //
    /**
     * Parameter: Wessen Ereignisse?
     */
    private final static String PARAM_DOMAIN = "domain";

    /**
     * Parameterwert: beliebige Ereignisse
     */
    private final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /**
     * Parameterwert: Ereignisse des gleichen Mandanten
     */
    private final static String PARAM_DOMAIN_VALUE_OU = "ou";

    //
    // Konstruktoren
    //

    /**
     * Default-Kontruktor der den Beannamen festlegt.
     */
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
     * @param select         Event-Select
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
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))) {
                select.where(Expr.equal("fk_orgunit", aaConfig.getOrgUnitId()));
            }
        } else {
            throw new BeanException("Missing user information");
        }

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
     * Wenn das Event &lt;code&gt;search&lt;/code&gt; einen Start-Termin hat,
     * werden nur Veranstaltungen angezeigt die <strong>genau</strong>
     * diesem Termin <strong>beginnen</strong>.<br><br>
     *
     * Wenn das Event &lt;code&gt;search&lt;code&gt; einen Ende-Termin hat,
     * werden nur Veranstaltungen angezeigt die <strong>nach</strong>
     * diesem Termin <strong>beginnen oder enden</strong>.<br><br>
     *
     * Siehe hierzu im 'Veranstaltung suchen'-Dialog das Eingabe Feld
     * 'Datum-Beginn', sowie die Funktion 'Aktuelle Veranstaltung anzeigen'.
     *
     * @param octopusContext octopusContext
     * @param select         select
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

        final String internalId = octopusContext.requestAsString("person-internal_id");
        if (internalId != null && !internalId.equals("")) {
            final List eventIds = getEventIds(octopusContext, internalId);
            select.whereAnd(Expr.in("tevent.pk", eventIds));
        }

        if (where.size() > 0) {
            select.whereAnd(where);
        }
    }

    private List getEventIds(OctopusContext octopusContext, String internalId) throws BeanException {
        final ResultList eventIds = getEventIdsByPersonInternalId(octopusContext, internalId);
        final List list = VerawebUtils.copyResultListToArrayList(eventIds);
        final List onlyIds = new ArrayList();
        for (Object entry : list) {
            onlyIds.add(((HashMap) entry).get("fk_event"));
        }
        return onlyIds;
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
        where.addAnd(DatabaseHelper.getWhere(search.eventname, new String[] {
          "eventname" }));
    }

    private void extendWhereClauseByShortname(Event search, WhereList where) {
        where.addAnd(DatabaseHelper.getWhere(search.shortname, new String[] {
          "shortname" }));
    }

    private void extendWhereClauseByHostname(Event search, WhereList where) {
        where.addAnd(DatabaseHelper.getWhere(search.hostname, new String[] { "hostname" }));
    }

    private void extendWhereClauseByBeginDate(Event search, WhereList where) {
        Timestamp nextDay = new Timestamp(search.begin.getTime() + 86400000); // nächster tag
        where.addAnd(Where.and(
          Expr.greaterOrEqual("datebegin", search.begin),
          Expr.less("datebegin", nextDay)));
    }

    private void extendWhereClauseByEndDate(WhereList where) {
        final String dateClause =
          "((datebegin IS NOT NULL AND datebegin>=now()::date) OR (dateend IS NOT NULL AND dateend>=now()::date))";
        where.addAnd(new RawClause(dateClause));
    }

    /**
     * Überprüft ob es noch laufende oder zukünftige Veranstaltungen und fragt ggf. ob diese trotzdem gelöscht werden sollen.
     */
    @Override
    protected int removeSelection(OctopusContext octopusContext, List errors, List selectionList, TransactionContext context)
      throws BeanException, IOException {
        int count = 0;
        if (selectionList == null || selectionList.size() == 0) {
            return count;
        }
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
    protected boolean removeBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
      throws BeanException, IOException {
        final Event event = (Event) bean;
        try {
            return isEventDeletionSuccessfull(octopusContext, transactionContext, event);
        } catch (SQLException e) {
            throw new BeanException("SQL Exception while deleting OptionalFields from Event", e);
        }
    }

    private boolean isEventDeletionSuccessfull(OctopusContext octopusContext, TransactionContext transactionContext, Event event)
      throws BeanException, SQLException, IOException {
        final Database database = transactionContext.getDatabase();
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        deleteOptionalFields(transactionContext, database, optionalFieldsWorker, event);
        deleteEventTasks(transactionContext, database, event);
        deleteGuests(transactionContext, database, event);
        transactionContext.commit();
        return deleteLogEntriesForEvent(octopusContext, transactionContext, event);
    }

    private void deleteOptionalFields(TransactionContext transactionContext, Database database,
      OptionalFieldsWorker optionalFieldsWorker,
      Event event) throws BeanException, SQLException {
        final List<OptionalField> optionalFields = optionalFieldsWorker.getOptionalFieldsByEvent(event.id);
        for (OptionalField optionalField : optionalFields) {
            executeOptionalFieldsDeletion(transactionContext, database, optionalField);
        }
    }

    private boolean deleteLogEntriesForEvent(OctopusContext octopusContext, TransactionContext transactionContext, Event event)
      throws BeanException, IOException {
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

    private void executeOptionalFieldsDeletion(TransactionContext transactionContext, Database database,
      OptionalField optionalField)
      throws BeanException {
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

    /**
     * Octopus-Eingabe-Parameter für {@link #getSearch(OctopusContext)}
     */
    public static final String INPUT_getSearch[] = {};
    /**
     * Octopus-Ausgabe-Parameter für {@link #getSearch(OctopusContext)}
     */
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
