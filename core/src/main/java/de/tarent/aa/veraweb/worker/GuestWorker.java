package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.GuestSerialNumber;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen für Gäste von
 * Veranstaltungen zur Verfügung.
 *
 * @author christoph
 * @author mikel
 * @author Max Marche
 */
public class GuestWorker {
    //
    // Octopus-Aktionen
    //
    /**
     * Octopus-Eingabe-Parameter für {@link #addGuestList(OctopusContext)}
     */
    public static final String[] INPUT_addGuestList = {};
    /**
     * Diese Octopus-Aktion fügt eine Reihe von Gästen einer
     * Veranstaltung hinzu.<br>
     */
    protected static final String COUNT_INVITED_NOT_INVITED_2_PATTERN =
            "select (select count(*) from tperson p where p.pk not in (select fk_person from tguest "
                    + "where fk_event = {0}) and p.deleted = ''f'' and p.pk in ({1})) as invited, "
                    + "(select count(*) from tguest g left join tperson p on g.fk_person = p.pk "
                    + "where g.fk_event = {0} and (g.fk_person in ({1}) or (g.fk_person in ({1})"
                    + "and p.deleted=''f''))) as notinvited;";
    protected static final MessageFormat COUNT_INVITED_NOT_INVITED_2_FORMAT =
            new MessageFormat(COUNT_INVITED_NOT_INVITED_2_PATTERN);

    protected static final String ADD_PERSONS_TO_GUESTLIST_PATTERN =
            "insert into tguest ( fk_person, fk_event, fk_category, invitationtype, invitationstatus, "
                    + "ishost, diplodate, rank, reserve, delegation, notehost, noteorga, \"language\", "
                    + "gender, nationality, domestic_a, invitationstatus_p, notehost_p, "
                    + "noteorga_p, language_p, gender_p, nationality_p, domestic_b, createdby, created, osiam_login ) "
                    + "select p.pk as fk_person, {0} as fk_event, 0 as fk_category, "
                    +
                    "0 as invitationtype, 0 as invitationstatus, 0 as ishost, p.diplodate_a_e1 as diplodate, 0 as rank, 0 as " +
                    "reserve, 0 as " +
                    "delegation, "
                    +
                    "p.notehost_a_e1 as notehost, p.noteorga_a_e1 as noteorga, p.languages_a_e1 as \"language\", p.sex_a_e1 as " +
                    "gender, "
                    + "p.nationality_a_e1 as nationality, p.domestic_a_e1 as domestic_a, 0 as "
                    +
                    "invitationstatus_p, p.notehost_b_e1 as notehost_p, p.noteorga_b_e1 as noteorga_p, p.languages_b_e1 as " +
                    "language_p, "
                    + "p.sex_b_e1 as gender_p, p.nationality_b_e1 as nationality_p, p.domestic_b_e1 as domestic_b, "
                    + "''{1}'' as createdby, current_timestamp as created, p.username as osiam_login from tperson p "
                    + "where p.pk in ({2}) and p.deleted=''f'' and p.pk not in (select g.fk_person from tguest g "
                    + "where g.fk_event = {0});";
    protected static final MessageFormat ADD_PERSONS_TO_GUESTLIST_FORMAT = new MessageFormat(ADD_PERSONS_TO_GUESTLIST_PATTERN);

    protected static final String UPDATE_PERSON_TO_GUEST_LIST_PATTERN =
            "update tguest set fk_category={0}, invitationtype={1}, delegation={3},"
                    + "rank=(select rank from tperson_categorie where pk={0}), reserve={2}"
                    + "where fk_person={4} and fk_event={5};";
    protected static final MessageFormat UPDATE_PERSON_TO_GUEST_LIST_FORMAT =
            new MessageFormat(UPDATE_PERSON_TO_GUEST_LIST_PATTERN);

    public void addGuestList(OctopusContext cntx) throws BeanException, IOException {
        final Database database = new DatabaseVeraWeb(cntx);
        final TransactionContext context = database.getTransactionContext();

        try {
            final Event event = (Event) cntx.contentAsObject("event");
            final List invitemain = (List) cntx.sessionAsObject("selectionPerson");
            final List invitepartner = (List) cntx.sessionAsObject("addguest-invitepartner");
            final List selectreserve = (List) cntx.sessionAsObject("addguest-selectreserve");
            final List selectdelegation = (List) cntx.sessionAsObject("addguest-selectdelegation");
            Map invitecategory = (Map) cntx.sessionAsObject("addguest-invitecategory");
            if (invitecategory == null) {
                invitecategory = new HashMap();
            }

            final String personIds =
                    DatabaseHelper.listsToIdListString(new List[] { invitemain, invitepartner, selectreserve, selectdelegation });

            setInvitationStatistics(cntx, context, event, personIds);

            // prepare third step, fill in missing data into guest tupels
            StringBuffer updateGuestStatement = new StringBuffer();
            try {
                if (!personIds.equals("NULL")) {
                    // not optimized due to dynamic creation of doctype content from configuration
                    // must still instantiate person beans from database, which may lead to destabilization
                    // of the system once more
                    List<Person> persons = database.getBeanList("Person", database.getSelect("Person").
                            where(new RawClause("tperson.pk in (" + personIds + ") and tperson.deleted='f'"
                                    + " and tperson.pk not in (select fk_person from tguest where fk_event = " + event.id + ")"
                            )));

                    for (Person person : persons) {
                        Integer fk_category = (Integer) invitecategory.get(person.id);
                        if (fk_category != null && fk_category == 0) {
                            fk_category = null;
                        }

                        updateGuestStatement.append(UPDATE_PERSON_TO_GUEST_LIST_FORMAT.format(new Object[] {
                                fk_category != null ? fk_category.toString() : null,
                                new Integer(
                                        invitepartner.indexOf(person.id) != -1 ? EventConstants.TYPE_MITPARTNER :
                                                EventConstants.TYPE_OHNEPARTNER),
                                (selectreserve.indexOf(person.id) != -1) ? 1 : 0,
                                (selectdelegation.indexOf(person.id) != -1) ? "'" + UUID.randomUUID() + "'" : null,
                                person.id.toString(), event.id.toString()
                        }));
                        updateGuestStatement.append(';');
                    }
                    context.commit();
                }
            } catch (BeanException e) {
                // will silently fail here as the following transaction
                // must be run under all cases, even if individual
                // person document types have not been updated
                logger.warn("Transaktion fehlgeschlagen. Die Dokumenttypen der Personen wurden nicht aktualisiert.", e);
            } catch (OutOfMemoryError e) {
                // will silently fail here as the following transaction
                // must be run under all cases, even if individual
                // person document types have not been updated

                // enforce garbage collection so that the following code
                // may continue
                logger.fatal("Nicht gen\u00fcgend Speicher. Forciere Garbage-Collection.", e);
                System.gc();
            }

            if (!personIds.equals("NULL")) {
                addGuests(cntx, database, context, event, personIds, updateGuestStatement);
            }

            // TODO bulk log guest create event

            // prevent alert message in case of invited == 0 and notinvited == 0
            cntx.setContent("doNotAlert", true);
        } catch (BeanException | SQLException e) {
            context.rollBack();
            throw new BeanException("Die G\u00e4ste konnten nicht auf die G\u00e4steliste gesetzt werden.", e);
        } catch (OutOfMemoryError e) {
            context.rollBack();
            // just rethrow
            throw e;
        }
    }

    private void addGuests(OctopusContext cntx, Database database, TransactionContext context, Event event, String personIds,
            StringBuffer updateGuestStatement) throws SQLException, BeanException {
        addGuestsInitial(cntx, context, event, personIds);
        if (!event.login_required) {
            addLoginUUIDtoGuests(database, context, event.id, personIds);
        }

        if (updateGuestStatement.length() > 0) {
            DB.insert(context, updateGuestStatement.toString());
            context.commit();
        }
    }

    private void addLoginUUIDtoGuests(Database database,
            TransactionContext transactionContext,
            Integer eventId,
            String personIds) throws BeanException, SQLException {
        String[] personIdsAsList = personIds.split(",");

        for (String aPersonIdsAsList : personIdsAsList) {
            if (isStandardGuest(database, Integer.valueOf(aPersonIdsAsList), eventId)) {
                updateGuestByNoLoginRequiredUUID(transactionContext, aPersonIdsAsList, eventId);
            }
        }
    }

    private Boolean isStandardGuest(Database database, Integer personId, Integer eventId) throws SQLException {
        final Select selectStatement = getQueryToCheckStandardGuestsExists(database, personId, eventId);
        final List resultList = selectStatement.getList(database);

        return !resultList.isEmpty();
    }

    private Select getQueryToCheckStandardGuestsExists(Database database, Integer personId, Integer eventId) {
        final Clause clauseCompanyName = Where.or(Expr.isNull("p.company_a_e1"), Expr.equal("p.company_a_e1", ""));
        final Clause osiamLoginNull = Expr.isNull("g.osiam_login");

        final Select selectStatement = SQL.Select(database);
        selectStatement.select("g.*");
        selectStatement.from("veraweb.tguest g");
        selectStatement.joinLeftOuter("veraweb.tperson p", "p.pk", "g.fk_person");
        selectStatement.whereAndEq("p.isCompany", "f");
        selectStatement.whereAndEq("g.fk_event", eventId);
        selectStatement.whereAndEq("p.pk", personId);
        selectStatement.whereAnd(osiamLoginNull);
        selectStatement.whereAnd(clauseCompanyName);

        return selectStatement;
    }

    private void updateGuestByNoLoginRequiredUUID(TransactionContext transactionContext, String personId, Integer eventId)
            throws BeanException {
        final Update updateStatement = SQL.Update(transactionContext);
        updateStatement.table("veraweb.tguest");
        updateStatement.update("veraweb.tguest.login_required_uuid", UUID.randomUUID().toString());
        updateStatement.whereAndEq("fk_person", personId);
        updateStatement.whereAndEq("fk_event", eventId);

        transactionContext.execute(updateStatement);
        transactionContext.commit();
    }

    private void addGuestsInitial(OctopusContext cntx, TransactionContext context, Event event, String personIds)
            throws SQLException, BeanException {
        // second step, create guest tupels
        final String sql = ADD_PERSONS_TO_GUESTLIST_FORMAT.format(new Object[] {
                        event.id.toString(),
                        ((PersonalConfigAA) cntx.personalConfig()).getRoleWithProxy(),
                        personIds
                }
        );
        DB.insert(context, sql);
        context.commit();
    }

    private void setInvitationStatistics(OctopusContext oc, TransactionContext context, Event event, String personIds)
            throws SQLException {
        final String sql = COUNT_INVITED_NOT_INVITED_2_FORMAT.format(new Object[] { event.id.toString(), personIds });
        final Result result = DB.result(context, sql);

        final ResultSet rs = result.resultSet();
        rs.first();

        final Integer counterInvited = rs.getInt("invited");
        final Integer counterNotInvited = rs.getInt("notinvited");

        oc.setContent("invited", counterInvited);
        oc.setContent("notInvited", counterNotInvited);

        rs.close();
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #addEvent(OctopusContext, Integer)}
     */
    public static final String INPUT_addEvent[] = { "id" };
    /**
     * Fügt die Gäste einer Veranstaltung einer anderen Veranstaltung hinzu.
     */
    protected static final String COUNT_INVITED_NOT_INVITED_PATTERN =
            "select (select count(*) from tguest g left join tperson p on g.fk_person = p.pk "
                    + "where g.fk_event = {1} and g.fk_person not in (select fk_person from tguest "
                    + "where fk_event = {0}) and p.deleted = ''f'') as invited, "
                    + "(select count(*) from tguest g left join tperson p on g.fk_person = p.pk "
                    + "where g.fk_event = {1} and (g.fk_person in (select fk_person from tguest "
                    + "where fk_event = {0}) or (g.fk_person in (select fk_person from tguest "
                    + "where fk_event = {0} and p.deleted=''f'')))) as notinvited;";
    protected static final MessageFormat COUNT_INVITED_NOT_INVITED_FORMAT = new MessageFormat(COUNT_INVITED_NOT_INVITED_PATTERN);

    protected static final String ADD_FROM_EVENT_PATTERN =
            "insert into tguest ( fk_person, fk_event, fk_category, invitationtype, invitationstatus, "
                    + "ishost, diplodate, rank, reserve, tableno, seatno, orderno, notehost, noteorga, \"language\", "
                    + "gender, nationality, domestic_a, invitationstatus_p, tableno_p, seatno_p, orderno_p, notehost_p, "
                    +
                    "noteorga_p, language_p, gender_p, nationality_p, domestic_b, createdby, created, delegation, osiam_login, " +
                    "login_required_uuid, image_uuid, image_uuid_p, keywords) "
                    + "select p.pk as fk_person, {0} as fk_event, g.fk_category as fk_category,"
                    +
                    " CASE WHEN {1} <> g.invitationtype AND {1} <> {2} THEN g.invitationtype ELSE {1} END as invitationtype, 0 " +
                    "as invitationstatus, "
                    + "0 as ishost, p.diplodate_a_e1 as diplodate, g.rank as rank, g.reserve as reserve, "
                    + "g.tableno as tableno, g.seatno as seatno, g.orderno as orderno, p.notehost_a_e1 as notehost, "
                    + "p.noteorga_a_e1 as noteorga, p.languages_a_e1 as \"language\", p.sex_a_e1 as gender, "
                    + "p.nationality_a_e1 as nationality, p.domestic_a_e1 as domestic_a, 0 as "
                    + "invitationstatus_p, g.tableno_p as tableno_p, g.seatno_p as seatno_p, g.orderno_p as orderno_p, "
                    + "p.notehost_b_e1 as notehost_p, p.noteorga_b_e1 as noteorga_p, p.languages_b_e1 as language_p, "
                    + "p.sex_b_e1 as gender_p, p.nationality_b_e1 as nationality_p, p.domestic_b_e1 as domestic_b, "
                    +
                    "''{3}'' as createdby, current_timestamp as created, g.delegation as delegation, g.osiam_login as " +
                    "osiam_login, g" +
                    ".login_required_uuid as login_required_uuid, g.image_uuid as image_uuid, g.image_uuid_p as image_uuid_p, g" +
                    ".keywords as " +
                    "keywords from tperson p "
                    + "left join tguest g on p.pk = g.fk_person and g.fk_event = {4} "
                    + "where p.pk in (select g.fk_person from tguest g "
                    + "where g.fk_event = {4}) and p.deleted=''f'' and p.pk not in (select g.fk_person from tguest g "
                    + "where g.fk_event = {0});";
    protected static final MessageFormat ADD_FROM_EVENT_FORMAT = new MessageFormat(ADD_FROM_EVENT_PATTERN);

    public void addEvent(OctopusContext cntx, Integer eventId) throws BeanException, IOException {
        final Database database = new DatabaseVeraWeb(cntx);
        final TransactionContext context = database.getTransactionContext();
        try {
            Event event = (Event) cntx.contentAsObject("event");
            logger.debug("F\u00fcge G\u00e4ste der Veranstaltung #" + eventId + " der Verstanstaltung #" + event.id + " hinzu.");
            String sql = COUNT_INVITED_NOT_INVITED_FORMAT.format(new Object[] { event.id.toString(), eventId.toString() });
            Result res = DB.result(context, sql);

            ResultSet rs = res.resultSet();
            rs.first();
            cntx.setContent("invited", rs.getInt("invited"));
            cntx.setContent("notInvited", rs.getInt("notinvited"));
            rs.close();

            try {
                context.commit();
            } catch (BeanException e) {
                // will silently fail here as the following transaction
                // must be run under all cases, even if individual
                // person document types have not been updated
                logger.warn("Transaktion fehlgeschlagen. Die Dokumenttypen der Personen wurden nicht aktualisiert.", e);
            } catch (OutOfMemoryError e) {
                // will silently fail here as the following transaction
                // must be run under all cases, even if individual
                // person document types have not been updated

                // enforce garbage collection so that the following code
                // may continue
                logger.fatal("Nicht gen\u00fcgend Speicher. Forciere Garbage-Collection.", e);
                System.gc();
            }

            sql = ADD_FROM_EVENT_FORMAT
                    .format(new Object[] { event.id.toString(), event.invitationtype, EventConstants.TYPE_OHNEPARTNER,
                            ((PersonalConfigAA) cntx.personalConfig()).getRoleWithProxy(), eventId.toString() });
            DB.insert(context, sql);
            context.commit();
            // TODO bulk log guest create event
        } catch (SQLException e) {
            context.rollBack();
            throw new BeanException("Die G\u00e4ste aus der G\u00e4steliste konnten nicht \u00fcbernommen werden.", e);
        }
    }

    /*
     * Octopus-Eingabe-Parameter für {@link #addPerson(OctopusContext, Integer)}
     */
    public static final String[] INPUT_addPerson = { "event-id" };

    /*
     * Fügt eine Person aus dem Content zu einer Veranstaltung hinzu.
     *
     * Wird offenbar nur verwendet wenn aus der Detail-Sicht einer Person diese Person einer Veranstaltung zugewiesen
     * wird. Falls der Person nur eine einzige Kategorie zugeordnet ist, wird diese mit in die Veranstaltung uebernommen
     * (Bug 1593)
     */
    public void addPerson(OctopusContext cntx, Integer eventId) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        Event event = (Event) database.getBean("Event", eventId, context);
        Person person = (Person) cntx.contentAsObject("person");

        try {
            int invited = 0;
            int notInvited = 0;
            boolean invite;

            if (event != null && person != null) {
                // Falls der Person nur eine einzige Kategorie zugeordnet ist, wird diese mit in die Veranstaltung uebernommen
                // (Bug 1593)
                Integer catId = null;
                try {
                    Select select = database.getSelect("PersonCategorie")
                            .where(Expr.equal("fk_person", person.id))
                            .orderBy(null); //TODO beans.property von PersonCategorie ist nicht korrekt!
                    List list = database.getBeanList("PersonCategorie", select, context);
                    if (list.size() == 1) {
                        catId = ((PersonCategorie) list.get(0)).id;
                    }
                } catch (Exception e) {
                    logger.warn("addPerson: Konnte fuer Person: " + person + " beim Hinzufügen zur Veranstaltung: " + event
                            + " keine PersonCategorie ermitteln", e);
                }
                invite = addGuest(cntx, database, context, event, person.id, catId, Boolean.FALSE, event.invitationtype,
                        Boolean.FALSE);
                if (invite) {
                    invited++;
                } else {
                    notInvited++;
                }
            } else {
                logger.error("addPerson: Konnte Person: " + person + " der Veranstaltung: " + event + " nicht hinzufügen.");
            }

            cntx.setContent("event", event);
            cntx.setContent("invited", invited);
            cntx.setContent("notInvited", notInvited);

            context.commit();
        } catch (BeanException e) {
            context.rollBack();
            throw new BeanException("Die Person konnte nicht auf die G\u00e4steliste gesetzt werden.", e);
        }
    }

    /*
     * Octopus-Eingabe-Parameter für {@link #reloadData(OctopusContext, Integer)}
     */
    public static final String INPUT_reloadData[] = { "guest-id" };

    /*
     * Diese Octopus-Aktion aktualisiert die Daten eines Gastes
     * aus den Stammdaten und erzeugt die Dokumenttypen neu.
     */
    public void reloadData(OctopusContext cntx, Integer guestId) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        Event event = (Event) cntx.contentAsObject("event");

        try {
            updateGuest(cntx, database, context, event, guestId);
            context.commit();
        } catch (BeanException e) {
            context.rollBack();
            throw new BeanException("Die Daten der Person konnten nicht aktualisiert werden.", e);
        }
    }

    /*
     * Octopus-Eingabe-Parameter für {@link #reloadAllData(OctopusContext)}
     */
    public static final String INPUT_reloadAllData[] = {};

    /*
     * Diese Octopus-Aktion aktuallisiert die Dokumenttypen
     * des aktuellen Gastes zu einer Veranstaltung.
     */
    public void reloadAllData(OctopusContext cntx) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        Event event = (Event) cntx.contentAsObject("event");

        try {
            List selection = (List) cntx.contentAsObject("listselection");
            if (selection != null && selection.size() != 0) {
                for (Object currentEntry : selection) {
                    Integer guestId = (Integer) currentEntry;
                    updateGuest(cntx, database, context, event, guestId);
                }
            } else {
                List list =
                        database.getList(
                                database.getSelectIds(new Guest()).
                                        where(Expr.equal("fk_event", event.id)), database);
                for (Object entry : list) {
                    Integer guestId = (Integer) ((Map) entry).get("id");
                    updateGuest(cntx, database, context, event, guestId);
                }
            }
            context.commit();
        } catch (BeanException e) {
            context.rollBack();
            throw new BeanException("Die Personendaten konnten aktualisiert werden.", e);
        }
    }

    /*
     * Octopus-Eingabe-Parameter für {@link #calcSerialNumber(OctopusContext)}
     */
    public static final String INPUT_calcSerialNumber[] = {};

    /**
     * Diese Octopus-Aktion berechnet für eine Veranstaltung die 'Laufende Nummer'.
     *
     * @param cntx The {@link OctopusContext}
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    public void calcSerialNumber(OctopusContext cntx) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(cntx);

        Event event = (Event) cntx.contentAsObject("event");
        logger.debug("calc order number for event #" + event.id);

        Map<String, String> questions = new HashMap<String, String>();
        if (event.begin.before(new Date()) && !cntx.requestAsBoolean("calc-serialno")) {
            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);

            questions.put("calc-serialno", languageProvider.getProperty("GUEST_CALC_SERIALNO"));
        } else {
            TransactionContext context = database.getTransactionContext();
            try {
                (new GuestSerialNumber.CalcSerialNumberImpl3(context, event)).calcSerialNumber();
                context.commit();
            } catch (BeanException e) {
                context.rollBack();
                throw new BeanException("Die laufende Nummer konnte nicht berechnet werden.", e);
            }
        }
        if (!questions.isEmpty()) {
            cntx.setContent("listquestions", questions);
        }
    }

    //
    // Geschützte Methoden
    //

    /*
     * Neuen Gast hinzufügen.
     *
     * @see #saveGuest(OctopusContext, Database, ExecutionContext, Event, Integer, Integer, Integer, Boolean, Integer, Boolean)
     */
    boolean addGuest(OctopusContext cntx, Database database, ExecutionContext context, Event event, Integer personId,
            Integer categoryId,
            Boolean reserve, Integer invitationtype, Boolean ishost) throws BeanException, IOException {
        return saveGuest(cntx, database, context, event, null, personId, categoryId, reserve, invitationtype, ishost);
    }

    /*
     * Bestehnden Gast aus Stammdaten neuladen.
     *
     * @see #saveGuest(OctopusContext, Database, ExecutionContext, Event, Integer, Integer, Integer, Boolean, Integer, Boolean)
     */
    boolean updateGuest(OctopusContext cntx, Database database, ExecutionContext context, Event event, Integer guestId)
            throws BeanException, IOException {
        if (guestId == null) {
            return false;
        }
        return saveGuest(cntx, database, context, event, guestId, null, null, null, null, null);
    }

    /**
     * Diese Methode fügt eine Person einer Veranstaltung hinzu.<br><br>
     *
     * <strong>Achtung</strong> Wenn die Gast-ID null ist wird ein neuer
     * Gast angelegt wenn dieser noch nicht dieser Veranstaltung zugeordnet
     * war. Wenn die Gast-ID übergeben wird, wird dieser Gast aktuallisiert!
     *
     * @param cntx           Octopus-Context
     * @param database       Datenbank
     * @param executionContext executionContext
     * @param event          Veranstaltung
     * @param guestId        Gast der bearbeitet werden soll, null zum hinzufügen.
     * @param personId       Person mit dessen Daten der Gast gefüllt werden soll.
     * @param categoryId     Kategorie nach der gefiltert wurde.
     * @param reserve        Gibt an ob dieser Gast auf Reserve gesetzt werden soll.
     * @param invitationtype Gibt an ob dieser Gast mit/ohne Partner eingeladen werden soll.
     * @param ishost         Gibt an ob dieser Gast gleichzeitig Gastgeber ist.
     * @throws BeanException beanexception
     * @throws IOException ioexceoption
     *
     * @return true if save successful
     * 2009-05-12 cklein
     *
     * fixed as part of issue #1531 - personCategorie was always null due to malformed query
     */
    protected boolean saveGuest(OctopusContext cntx, Database database, ExecutionContext executionContext, Event event,
            Integer guestId, Integer personId, Integer categoryId, Boolean reserve,
            Integer invitationtype, Boolean ishost) throws BeanException, IOException {
        if (event == null) {
            return false;
        }

        if (guestId == null) {
            logger.debug("F\u00fcge Person #" + personId + " der Veranstaltung #" + event.id + " hinzu.");
        }

        // Keinen neuen Gast hinzufügen wenn diese Person bereits zugeordnet war.
        if (guestId == null) {
            if (getNumberOfGuests(database, executionContext, event, personId) > 0) {
                return false;
            }
        }

        Guest guest = null;
        // Gast laden
        if (guestId != null) {
            guest = getGuestById(database, executionContext, guestId);
            if (guest == null) {
                logger.warn("Gast #" + guestId + " konnte nicht gefunden werden.");
                return false;
            }
            personId = guest.person;
        }

        Person person = getPersonById(database, executionContext, personId);

        if (person == null) {
            logger.warn("Person #" + personId + " konnte nicht gefunden und daher der Veranstaltung #" + event.id +
                    " nicht hinzugefügt werden.");
            return false;
        }

        // Neuen Gast anlegen.
        if (guest == null) {
            guest = new Guest();
            guest.event = event.id;
            guest.person = person.id;
            guest.ishost = ishost ? 1 : 0;
            guest.reserve = reserve;
            guest.invitationtype = invitationtype;

            if (person.username != null) {
                guest.osiam_login = person.username;
            }
            setGuestInvitationStatus(event, guest);

            if (personId != null && categoryId != null) {
                PersonCategorie personCategorie = new PersonCategorie();
                personCategorie.person = personId;
                personCategorie.categorie = categoryId;
                personCategorie = (PersonCategorie)
                        database.getBean("PersonCategorie",
                                database.getSelect(personCategorie).where(
                                        database.getWhere(personCategorie)).
                                        select("tcategorie.rank").
                                        select("tcategorie.catname").
                                        joinLeftOuter("veraweb.tcategorie",
                                                "tperson_categorie.fk_categorie", "tcategorie.pk"), executionContext);

                if (personCategorie != null) {
                    guest.category = personCategorie.categorie;
                    guest.rank = personCategorie.rank;
                } else {
                    guest.category = categoryId;
                    guest.rank = null;
                }
            } else {
                guest.category = null;
                guest.rank = null;
            }
        }
        guest.domestic_a = person.domestic_a_e1;
        guest.domestic_b = person.domestic_b_e1;
        guest.sex_a = person.sex_a_e1;
        guest.sex_b = person.sex_b_e1;
        guest.nationality_a = person.nationality_a_e1;
        guest.nationality_b = person.nationality_b_e1;
        guest.language_a = person.languages_a_e1;
        guest.language_b = person.languages_b_e1;
        guest.notehost_a = person.notehost_a_e1;
        guest.notehost_b = person.notehost_b_e1;
        guest.noteorga_a = person.noteorga_a_e1;
        guest.noteorga_b = person.noteorga_b_e1;

        guest.verify();
        if (guest.isCorrect()) {

            /*
             * modified for change logging support
             * cklein
             * 2008-02-12
             */
            BeanChangeLogger clogger = new BeanChangeLogger(database);
            if (guest.id == null) {
                database.getNextPk(guest, executionContext);
                Insert insert = database.getInsert(guest);
                insert.insert("pk", guest.id);
                if (!((PersonalConfigAA) cntx.personalConfig()).getGrants().mayReadRemarkFields()) {
                    insert.remove("notehost_a");
                    insert.remove("notehost_b");
                    insert.remove("noteorga_a");
                    insert.remove("noteorga_b");
                }
                executionContext.execute(insert);

                clogger.logInsert(cntx.personalConfig().getLoginname(), guest);
            } else {
                Update update = database.getUpdate(guest);
                if (!((PersonalConfigAA) cntx.personalConfig()).getGrants().mayReadRemarkFields()) {
                    update.remove("notehost_a");
                    update.remove("notehost_b");
                    update.remove("noteorga_a");
                    update.remove("noteorga_b");
                }
                executionContext.execute(update);

                Guest guestOld = (Guest) database.getBean("Guest", guest.id);
                clogger.logUpdate(cntx.personalConfig().getLoginname(), guestOld, guest);
            }

            return true;
        }
        return false;
    }

    private void setGuestInvitationStatus(Event event, Guest guest) {
        if (event.eventtype != null && event.eventtype.equals(EventConstants.EVENT_TYPE_OPEN_EVENT)) {
            guest.invitationstatus_a = EventConstants.STATUS_ACCEPT;
        } else {
            guest.invitationstatus_a = EventConstants.STATUS_OPEN;
        }
    }

    private Person getPersonById(Database database, ExecutionContext context, Integer personId)
            throws BeanException, IOException {
        // Vollständige Personendaten laden.
        return (Person)
                database.getBean("Person",
                        database.getSelect("Person").
                                where(Expr.equal("pk", personId)), context);
    }

    private Guest getGuestById(Database database, ExecutionContext context, Integer guestId) throws BeanException, IOException {
        Guest guest;
        guest = (Guest)
                database.getBean("Guest",
                        database.getSelect("Guest").
                                where(Expr.equal("pk", guestId)), context);
        return guest;
    }

    /**
     * Get the number of guests by event id and person id.
     *
     * @param database The database
     * @param context  TODO
     * @param event    The event
     * @param personId Person id
     * @return Total number of the guests
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    private int getNumberOfGuests(Database database, ExecutionContext context, Event event, Integer personId)
            throws BeanException, IOException {
        return database.getCount(database.getCount("Guest").where(Where.and(
                Expr.equal("fk_event", event.id),
                Expr.equal("fk_person", personId))), context);
    }

    //
    // Variablen
    //
    /*
     * Logger für diese Klasse
     */
    private final static Logger logger = LogManager.getLogger(GuestWorker.class);
}
