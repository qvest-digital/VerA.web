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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
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
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.EventURLHandler;
import de.tarent.aa.veraweb.utils.MediaRepresentativesUtilities;
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.aa.veraweb.utils.VerawebUtils;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Verwaltet eine Gästeliste.
 *
 * @author Mikel, Christoph
 * @author sweiz
 * @version $Revision: 1.1 $
 */
public class GuestListWorker extends ListWorkerVeraWeb {

    public static final String INPUT_getSearch[] = {};

    public static final String OUTPUT_getSearch = "search";

    public static final String INPUT_getSums[] = {};

    public static final String OUTPUT_getSums = "guestlist-sums";

    public static final String INPUT_getEvent[] = {};

    public static final String OUTPUT_getEvent = "event";

    public static final String INPUT_getAllCategories[] = {};

    private final static String DELETE_TOPTIONAL_FIELDS_DELEGATION_CONTENT =
            "DELETE FROM toptional_fields_delegation_content WHERE fk_guest IN ({0})";
    private final static MessageFormat DELETE_ALL_OPTIONAL_DELEGATION_FIELDS_FOR_GUEST =
            new MessageFormat(DELETE_TOPTIONAL_FIELDS_DELEGATION_CONTENT);
    private final static String DELETE_ALL_STALE_GUESTS = "DELETE FROM tguest WHERE pk IN ({0})";
    private final static MessageFormat DELETE_ALL_STALE_GUESTS_FORMAT = new MessageFormat(DELETE_ALL_STALE_GUESTS);

    private final static String BULK_INSERT_CHANGELOG_ENTRIES =
            "INSERT INTO tchangelog (username, objname, objtype, objid, op, attributes, date) "
                    + "SELECT DISTINCT " + "''{0}'' AS username, p.lastname_a_e1 || CASE WHEN p.firstname_a_e1 IS NOT NULL "
                    + "THEN '', '' || p.firstname_a_e1 ELSE '''' END AS objname, " +
                    "''de.tarent.aa.veraweb.beans.Guest'' AS objtype, g.pk AS objid, "
                    + "''delete'' AS op, ''*'' AS attributes, NOW() AS date " +
                    "FROM tperson p LEFT JOIN tguest g ON g.fk_person = p.pk "
                    + "WHERE g.pk IN ({1})";
    private final static MessageFormat BULK_INSERT_CHANGELOG_ENTRIES_FORMAT = new MessageFormat(BULK_INSERT_CHANGELOG_ENTRIES);

    private final static Logger logger = LogManager.getLogger(GuestListWorker.class);

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public GuestListWorker() {
        super("Guest");
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException, IOException {
        final EventURLHandler eventURLHandler = new EventURLHandler();
        eventURLHandler.setEventUrl(octopusContext, (String) octopusContext.getContextField("event.hash"));
        final String categoryAssignmentAction = octopusContext.requestAsString("categoryAssignmentAction");

        // does the user request categories to be assigned or unassigned?
        try {
            saveGuestWithCategories(octopusContext, categoryAssignmentAction);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String commaSeparated(Collection<?> elements) {
        StringBuilder sb = new StringBuilder();
        boolean comma = false;
        for (Object element : elements) {
            if (comma) {
                sb.append(", ");
            }
            comma = true;
            sb.append(element);
        }
        return sb.toString();
    }

    private void saveGuestWithCategories(OctopusContext octopusContext, final String categoryAssignmentAction)
            throws BeanException, IOException, SQLException {
        if (categoryAssignmentAction != null && categoryAssignmentAction.length() > 0) {
            final Database database = getDatabase(octopusContext);
            final TransactionContext context = database.getTransactionContext();
            final Integer categoryId = octopusContext.requestAsInteger("categoryAssignmentId");

            @SuppressWarnings("unchecked") final List<Integer> selection =
                    this.getSelection(octopusContext, this.getCount(octopusContext, database));
            final Clause whereClause;
            if (octopusContext.requestAsString("select-all") != null &&
                    octopusContext.requestAsString("select-all").equals("on")) {
                whereClause = getCurrenGuestFilter(octopusContext);
            } else {
                whereClause = new RawClause("pk in (" + commaSeparated(selection) + ")");
            }

            Update update0 = SQL.Update(database).table("tguest").where(whereClause);
            final Update update;
            if ("assign".equals(categoryAssignmentAction) && categoryId != null) {
                update = update0.update("fk_category", categoryId);
            } else {
                update = update0.update("fk_category", null);
            }
            update.execute();
            context.commit();

            // TODO: should we clear the selection?
            octopusContext.setSession("selection" + BEANNAME, selection);
        } else {
            super.saveList(octopusContext);
        }
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #countRecipients(OctopusContext)}
     */
    public static final String INPUT_countRecipients[] = {};
    /**
     * Octopus-Ausgabe-Parameter für {@link #countRecipients(OctopusContext)}
     */
    public static final String OUTPUT_countRecipients = "mailinglistParams";

    /**
     * Schätzt, wie groß der neue Verteiler werden wird, und erweitert die Map
     * <code>mailinglistParam</code> im Content um den Key <code>count</code>.
     *
     * @param octopusContext Octopus-Context
     * @return Map mit dem Key <code>count</code>
     * @throws BeanException beanexception
     * @throws IOException   ioexception
     */
    public Map countRecipients(OctopusContext octopusContext) throws BeanException, IOException {
        final Integer countGuests;
        Map result = (Map) octopusContext.contentAsObject("mailinglistParams");
        if (result == null) {
            result = new HashMap();
        }
        countGuests = countSelectedGuests(octopusContext);

        if (countGuests != null) {
            result.put("count", countGuests);
            // falls nix ausgewählt ist, fängt der js code das ab,
            // und wenn nix ausgewählt ist, ist ja nix ausgewählt,
            // dann soll er nicht selber in der datenbank suchen

            // sollte es doch irgendwie zu der ausführung ges codes kommen,
            // kann ja ruhig in der MAP count=0 stehen, oder?
        }
        return result;
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #countRecipients(OctopusContext)}
     */
    public static final String INPUT_countSelectedGuests[] = {};
    /**
     * Octopus-Ausgabe-Parameter für {@link #countRecipients(OctopusContext)}
     */
    public static final String OUTPUT_countSelectedGuests = "count";

    public Integer countSelectedGuests(OctopusContext octopusContext) throws BeanException, IOException {
        final Integer countGuests;
        if (octopusContext.requestAsString("select-all") != null && octopusContext.requestAsString("select-all").equals("on")) {
            final WhereList currenGuestFilter = getCurrenGuestFilter(octopusContext);
            final Database database = getDatabase(octopusContext);
            final Select select = database.getCount("Guest").where(currenGuestFilter);
            countGuests = database.getCount(select);
        } else {

            countGuests = getSelection(octopusContext, null).size();
        }
        return countGuests;
    }

    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException {
        final GuestSearch search = getSearch(octopusContext);

        final WhereList list = new WhereList();
        search.addGuestListFilter(list);
        select.where(list);
    }

    protected void extendColumns(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        final GuestSearch search = getSearch(octopusContext);
        buildGuestSelect(select);

        final List order = buildOrderedGuestList(octopusContext, search);

        select.orderBy(DatabaseHelper.getOrder(order));
    }

    private List buildOrderedGuestList(OctopusContext octopusContext, final GuestSearch search) {
        final List<String> order = new ArrayList<String>();
        order.add("ishost");

        octopusContext.setSession("search" + BEANNAME, search);

        if (search == null || search.listorder == null) {
            order.add("someorderno");
            order.add("tcategorie.rank");
            order.add("tguest.rank");
            order.add("lastname_a_e1");
            order.add("firstname_a_e1");
        } else if (search.listorder.equals("orderno")) {
            order.add("someorderno");
            order.add(search.sortDirection);
        } else if (search.listorder.equals("lastname_a_e1")) {
            setOrderOfNames(search, order);
        } else if (search.listorder.equals("firstname_a_e1")) {
            order.add("firstname_a_e1");
            order.add(search.sortDirection);
            order.add("lastname_a_e1");
            order.add(search.sortDirection);
        } else if (search.listorder.equals("mail_a_e1")) {
            order.add("mail_a_e1");
            order.add(search.sortDirection);
            setOrderOfNames(search, order);

        } else if (search.listorder.equals("companyname")) {
            order.add("company_a_e1");
            order.add(search.sortDirection);
            setOrderOfNames(search, order);
        }
        return order;
    }

    private void setOrderOfNames(GuestSearch search, List<String> order) {
        order.add("lastname_a_e1");
        order.add(search.sortDirection);
        order.add("firstname_a_e1");
        order.add(search.sortDirection);
    }

    public String[] INPUT_setupSortDirection = {};

    public void setupSortDirection(OctopusContext octopusContext) throws BeanException {
        final GuestSearch search = getSearch(octopusContext);
        if (search.sortList) {
            if (search.sortDirection == null || search.lastlistorder == null || !search.lastlistorder.equals(search.listorder)) {
                search.sortDirection = "ASC";
            } else if ("ASC".equals(search.sortDirection)) {
                search.sortDirection = "DESC";
            } else if ("DESC".equals(search.sortDirection)) {
                search.sortDirection = "ASC";
            }
        }
        /* default if anything else fails */
        if (search.sortDirection == null) {
            logger.warn("search.sortDirection is nil");
            search.sortDirection = "ASC";
        }
    }

    private void buildGuestSelect(Select select) {
        select.joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk");
        select.joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");
        select.selectAs(
                "CASE WHEN tguest.orderno IS NOT NULL THEN NULLIF(tguest.orderno, 0) " + "ELSE NULLIF(tguest.orderno_p, 0) END",
                "someorderno");
        select.selectAs("tcategorie.rank", "catrank");
        select.select("firstname_a_e1");
        select.select("lastname_a_e1");
        select.select("firstname_b_e1");
        select.select("lastname_b_e1");
        select.select("firstname_b_e2");
        select.select("lastname_b_e2");
        select.select("firstname_b_e3");
        select.select("lastname_b_e3");
        select.select("function_a_e1");
        select.select("tperson.company_a_e1");
        select.select("tperson.company_a_e2");
        select.select("tperson.company_a_e3");
        select.select("city_a_e1");
        select.select("zipcode_a_e1");
        select.select("fon_a_e1");
        select.select("mail_a_e1");
        select.select("delegation");
        select.selectAs("NULL", "firstname_a_gd");
        select.selectAs("NULL", "lastname_a_gd");
        select.selectAs("NULL", "firstname_b_gd");
        select.selectAs("NULL", "lastname_b_gd");
        select.selectAs("NULL", "mail_a_gd");
        select.selectAs("NULL", "function_a_gd");
        select.selectAs("NULL", "city_a_gd");
        select.selectAs("NULL", "zipcode_a_gd");
        select.selectAs("NULL", "fon_a_gd");
        select.selectAs("NULL", "mail_a_gd");
    }

    protected Integer getAlphaStart(OctopusContext octopusContext, String start) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final GuestSearch search = getSearch(octopusContext);

        final WhereList list = new WhereList();
        search.addGuestListFilter(list);

        final StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        // TODO NOT USE obsolete methods
        buffer.append(list.clauseToString());
        buffer.append(") AND ");
        buffer.append(search.listorder);
        buffer.append(" < '");
        Escaper.escape(buffer, start);
        buffer.append("'");

        final Select select = database.getCount(BEANNAME);
        select.joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk");
        select.where(new RawClause(buffer));

        final Integer i = database.getCount(select);
        return i - (i % getLimit(octopusContext));
    }

    protected Select getSelect(Database database) throws BeanException, IOException {
        return SQL.SelectDistinct(database).from("veraweb.tguest").selectAs("tguest.pk", "id").selectAs("tguest.rank", "rank")
                .select("deleted")
                .select("delegation").select("ishost").select("iscompany").select("invitationtype")
                .selectAs("invitationstatus", "invitationstatus_a")
                .selectAs("invitationstatus_p", "invitationstatus_b").selectAs("reserve", "reserve")
                .selectAs("orderno", "orderno_a")
                .selectAs("orderno_p", "orderno_b");
    }

    protected List getResultList(Database database, Select select) throws BeanException, IOException {
        final List allGuests = getAllGuests(database, select);
        final List<Map> modifiedList = new ArrayList<Map>();
        for (Object guest : allGuests) {
            modifiedList.add((Map) guest);
        }
        return modifiedList;
    }

    private List getAllGuests(Database database, Select select) throws BeanException {
        final ResultList allResults = database.getList(select, database);
        return VerawebUtils.copyResultListToArrayList(allResults);
    }

    /**
     * Remove guests from the guest list.
     */
    protected int removeSelection(OctopusContext octopusContext, List errors, List selection,
            TransactionContext transactionContext)
            throws BeanException, IOException {
        int count = 0;
        try {
            if (octopusContext.requestAsString("select-all") != null &&
                    octopusContext.requestAsString("select-all").equals("on")) {
                count = deleteAllFilteredGuests(octopusContext, transactionContext);
            } else {
                count = deleteSelectedGuests(octopusContext, selection, transactionContext);
            }
            transactionContext.commit();
        } catch (BeanException e) {
            transactionContext.rollBack();
            throw new BeanException("Das L\u00f6schen aller zum l\u00f6schen markierten G\u00e4ste ist fehlgeschlagen.", e);
        } catch (SQLException e) {
            transactionContext.rollBack();
            throw new BeanException("Das L\u00f6schen aller zum l\u00f6schen markierten Personen ist fehlgeschlagen.", e);
        }

        return count;
    }

    private Integer deleteSelectedGuests(OctopusContext octopusContext, List selection, TransactionContext transactionContext)
            throws SQLException {
        final String ids = DatabaseHelper.listsToIdListString(new List[] { selection });
        DB.insert(transactionContext, DELETE_ALL_OPTIONAL_DELEGATION_FIELDS_FOR_GUEST.format(new Object[] { ids }));
        DB.insert(transactionContext, DELETE_ALL_STALE_GUESTS_FORMAT.format(new Object[] { ids }));
        DB.insert(transactionContext,
                BULK_INSERT_CHANGELOG_ENTRIES_FORMAT
                        .format(new Object[] { octopusContext.personalConfig().getLoginname(), ids }));

        return selection.size();
    }

    private int deleteAllFilteredGuests(OctopusContext octopusContext, TransactionContext transactionContext)
            throws BeanException, IOException, SQLException {
        final WhereList whereList = getCurrenGuestFilter(octopusContext);
        final Database database = getDatabase(octopusContext);
        final int count = database.getCount("Guest").where(whereList).getFirstCellAsInteger();
        final Delete delete = database.getDelete("Guest").where(whereList);
        transactionContext.execute(delete);

        return count;
    }

    private WhereList getCurrenGuestFilter(OctopusContext octopusContext) throws BeanException {
        final WhereList whereList = new WhereList();
        final GuestSearch search = getSearch(octopusContext);
        search.addGuestListFilter(whereList);
        return whereList;
    }

    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
            throws BeanException, IOException {
        final Database database = transactionContext.getDatabase();
        final Guest guest = (Guest) bean;
        guest.updateHistoryFields(((PersonalConfigAA) octopusContext.personalConfig()).getRoleWithProxy());

        /*
         * restore old guest state for logging purposes cklein 2008-02-20
         */
        final Guest guestOld = (Guest) database.getBean("Guest", guest.id);

        final Update update = createGuestUpdateStatement(database, guest);

        transactionContext.execute(update);
        transactionContext.commit();

        /*
         * modified to support change logging cklein 2008-02-12
         */
        final BeanChangeLogger clogger = new BeanChangeLogger(database, transactionContext);
        clogger.logUpdate(octopusContext.personalConfig().getLoginname(), guestOld, guest);
    }

    private Update createGuestUpdateStatement(final Database database, final Guest guest) {
        final Update update = SQL.Update(database).table("veraweb.tguest").update("invitationstatus", guest.invitationstatus_a)
                .update("invitationstatus_p", guest.invitationstatus_b).update("created", guest.created)
                .update("createdby", guest.createdby)
                .update("changed", guest.changed).update("changedby", guest.changedby).update("fk_category", guest.category)
                .where(Expr.equal("pk", guest.id));

        if (guest.invitationtype == EventConstants.TYPE_MITPARTNER) {
            if (guest.invitationstatus_a != null && guest.invitationstatus_a == 2) {
                update.update("orderno", null);
            }
            if (guest.invitationstatus_b != null && guest.invitationstatus_b == 2) {
                update.update("orderno_p", null);
            }
        } else if (guest.invitationtype == EventConstants.TYPE_OHNEPARTNER) {
            if (guest.invitationstatus_a != null && guest.invitationstatus_a == 2) {
                update.update("orderno", null);
            }
            update.update("orderno_p", null);
        } else if (guest.invitationtype == EventConstants.TYPE_NURPARTNER) {
            update.update("orderno", null);
            if (guest.invitationstatus_b != null && guest.invitationstatus_b == 2) {
                update.update("orderno_p", null);
            }
        }
        return update;
    }

    protected boolean removeBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
            throws BeanException, IOException {
        final Database database = transactionContext.getDatabase();
        /*
         * modified to support change logging cklein 2008-02-12
         */
        final BeanChangeLogger clogger = new BeanChangeLogger(database, transactionContext);
        /*
         * see bug #1033
         */
        if (((Guest) bean).person == null) {
            // need to load the guest entity in order to retrieve the person
            bean = database.getBean("Guest", ((Guest) bean).id, transactionContext);
        }
        clogger.logDelete(octopusContext.personalConfig().getLoginname(), bean);
        transactionContext.execute(SQL.Delete(database).from("veraweb.tguest").where(Expr.equal("pk", ((Guest) bean).id)));
        transactionContext.commit();
        return true;
    }

    /**
     * Diese Octopus-Aktion liefert die Gesamtzahlen der aktuellen Gästeliste.
     *
     * @param octopusContext Octopus-Kontext
     * @return {@link Map} mit Gesamtzahlen unter den Schlüsseln "platz",
     * "reserve", "all", "offen", "zusagen" und "absagen".
     * @throws BeanException beanexception
     */
    public Map getSums(OctopusContext octopusContext) throws BeanException {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final GuestSearch search = getSearch(octopusContext);
        final Map<String, Long> result = new HashMap<String, Long>();
        getSums(database, result, search, null);
        return result;
    }

    /**
     * Diese Octopus-Aktion liefert eine {@link GuestSearch}-Instanz, die die
     * aktuellen Gästesuchkriterien enthält. Diese stammen entweder aus dem
     * Octopus-Content (unter "search"), aus dem Octopus-Request oder aus der
     * Octopus-Session (unter "searchGuest"). Vor der Rückgabe wird die Instanz
     * unter "searchGuest" in die Octopus-Session gestellt.
     *
     * @param octopusContext Octopus-Kontext
     * @return {@link GuestSearch}-Instanz zur aktuellen Gästesuche
     * @throws BeanException beanexception
     */
    public GuestSearch getSearch(OctopusContext octopusContext) throws BeanException {
        PropertiesReader propertiesReader = new PropertiesReader();
        Boolean sortList = octopusContext.requestAsBoolean("sortList");

        if (propertiesReader.propertiesAreAvailable()) {
            octopusContext.setContent("delegationCanBeUsed", true);
        }

        if (octopusContext.contentContains("search") && octopusContext.contentAsObject("search") instanceof GuestSearch) {
            return (GuestSearch) octopusContext.contentAsObject("search");
        }

        final Request request = new RequestVeraWeb(octopusContext);
        GuestSearch search = (GuestSearch) request.getBean("GuestSearch", "search");
        if (search == null || search.event == null) {
            search = (GuestSearch) octopusContext.sessionAsObject("search" + BEANNAME);
        }

        if (search != null && !("lastname_a_e1".equals(search.listorder) || !"firstname_a_e1".equals(search.listorder)
                || !"mail_a_e1".equals(search.listorder) || !"orderno".equals(search.listorder))) {
            search.listorder = null;
        }

        /*
         * fix for the case that the user requests guest with no search object
         * being available in that case an NPE was thrown cklein 2008-03-27
         */
        if (search == null) {
            search = new GuestSearch();
        }

        GuestSearch sessionSearchGuest = (GuestSearch) octopusContext.sessionAsObject("search" + BEANNAME);

        if (sessionSearchGuest != null) {
            /*
             * Gets the last string order of the session SearchPerson object and
             * set it to the new session.
             */
            search.lastlistorder = sessionSearchGuest.listorder;
            search.sortDirection = sessionSearchGuest.sortDirection;
        }
        search.sortList = sortList;

        octopusContext.setSession("search" + BEANNAME, search);
        return search;
    }

    /**
     * Diese Octopus-Aktion liefert das Ereignis aus der aktuellen Gästesuche,
     * siehe Aktion {@link #getSearch(OctopusContext)}.
     *
     * @param octopusContext Octopus-Kontext
     * @return eine {@link Event}-Instanz oder <code>null</code>.
     * @throws BeanException beanexception
     * @throws IOException   ioexception
     */
    public Event getEvent(OctopusContext octopusContext) throws BeanException, IOException {
        final GuestSearch search = getSearch(octopusContext);
        if (search == null) {
            return null;
        }

        final Event event = EventDetailWorker.getEvent(octopusContext, search.event);
        if (event == null) {
            octopusContext.setStatus("noevent");
        }

        final MediaRepresentativesUtilities mediaRepresentativesUtilities =
                new MediaRepresentativesUtilities(octopusContext, event);
        mediaRepresentativesUtilities.setUrlForMediaRepresentatives();

        return event;
    }

    /**
     * // TODO
     *
     * @param octopusContext The {@link de.tarent.octopus.server.OctopusContext}
     * @throws BeanException beanexception
     */
    public void getAllCategories(OctopusContext octopusContext) throws BeanException {
        final ResultList categories = getCategoriesForCurrentOrgunit(octopusContext);
        final List<String> categoryNames = getCategoryNames(categories);
        octopusContext.setContent("categories", categoryNames);
    }

    private List<String> getCategoryNames(ResultList categories) {
        final List<String> catnames = new ArrayList<String>();
        for (Object category : categories) {
            final String currentCategory = ((ResultMap) category).get("catname").toString();
            catnames.add(currentCategory);
        }
        return catnames;
    }

    private ResultList getCategoriesForCurrentOrgunit(OctopusContext octopusContext) throws BeanException {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final Integer eventId = ((Event) octopusContext.contentAsObject("event")).orgunit;
        final Select select = buildSelectStatement(database, eventId);
        return database.getList(select, database);
    }

    private Select buildSelectStatement(Database database, Integer eventId) {
        final Select select = SQL.Select(database).from("veraweb.tcategorie");
        select.select("catname");
        select.setDistinctOn("catname");
        select.whereAnd(Expr.equal("fk_orgunit", eventId));
        return select;
    }

    /**
     * Diese Methode überträgt Gästesuchkriterien aus einer {@link GuestSearch}
     * -Instanz in einer WHERE-Statement-Liste.
     *
     * @param guestSearch guestSearch
     * @param where       werelist
     * @deprecated Use {@link GuestSearch#addGuestListFilter(WhereList)}  instead
     */
    public static void addGuestListFilter(GuestSearch guestSearch, WhereList where) {
        guestSearch.addGuestListFilter(where);
    }

    /**
     * Berechnet die Gesamtzahlen der aktuellen Gästeliste.
     *
     * Vor Version 1.50 wurden "Auf Platz" und "Auf Reserve" pro Datensatz
     * berechnet, die aktuelle Umsetzung zählt diese pro eingeladenen Member.
     * (Vgl. Bug 1480)
     *
     * @param database    The {@link Database}
     * @param data        FIXME
     * @param guestSearch The {@link GuestSearch}
     * @param selection   FIXME
     * @throws BeanException beanexception
     */
    protected void getSums(Database database, Map<String, Long> data, GuestSearch guestSearch, List selection)
            throws BeanException {
        final Select select = buildAndCountListFromGuests(database, guestSearch, selection);

        final Map result = (Map) database.getList(select, database).iterator().next();
        Long platz = (Long) result.get("platz");
        Long reserve = (Long) result.get("reserve");
        Long zusagen = (Long) result.get("zusagen");
        Long absagen = (Long) result.get("absagen");
        Long teilnahmen = (Long) result.get("teilnahmen");
        Long delegationen = (Long) result.get("delegationen");

        if (platz == null) {
            platz = 0L;
        }
        if (reserve == null) {
            reserve = 0L;
        }
        if (zusagen == null) {
            zusagen = 0L;
        }
        if (absagen == null) {
            absagen = 0L;
        }
        if (teilnahmen == null) {
            teilnahmen = 0L;
        }
        if (delegationen == null) {
            delegationen = 0L;
        }

        data.put("platz", platz - delegationen);
        data.put("reserve", reserve);
        data.put("all", platz - delegationen);
        data.put("offen", platz - zusagen - absagen - teilnahmen - delegationen);
        data.put("zusagen", zusagen);
        data.put("absagen", absagen);
        data.put("teilnahmen", teilnahmen);
    }

    private Select buildAndCountListFromGuests(Database database, GuestSearch guestSearch, List selection) throws BeanException {
        final WhereList where = new WhereList();
        guestSearch.addGuestListFilter(where);

        if (selection != null && selection.size() != 0) {
            where.addAnd(Expr.in("tguest.pk", selection));
        }
        final Select select = SQL.Select(database).from("veraweb.tguest").where(where);

        buildSelectSumFromGuestList(select);
        return select;
    }

    private void buildSelectSumFromGuestList(final Select select) {
        select.selectAs("SUM(CASE WHEN invitationtype = 1 THEN 2 ELSE 1 END)", "platz");
        select.selectAs("SUM(CASE WHEN reserve != 1 THEN 0 ELSE CASE WHEN invitationtype = 1 THEN 2 ELSE 1 END END)", "reserve");
        select.selectAs("SUM(CASE WHEN invitationstatus   = 1 AND invitationtype != 3 THEN 1 ELSE 0 END) + "
                + "SUM(CASE WHEN invitationstatus_p = 1 AND invitationtype != 2 THEN 1 ELSE 0 END)", "zusagen");
        select.selectAs("SUM(CASE WHEN invitationstatus   = 2 AND invitationtype != 3 THEN 1 ELSE 0 END) + "
                + "SUM(CASE WHEN invitationstatus_p = 2 AND invitationtype != 2 THEN 1 ELSE 0 END)", "absagen");
        select.selectAs("SUM(CASE WHEN invitationstatus   = 3 AND invitationtype != 3 THEN 1 ELSE 0 END) + "
                + "SUM(CASE WHEN invitationstatus_p = 3 AND invitationtype != 2 THEN 1 ELSE 0 END)", "teilnahmen");

        select.selectAs("SUM(CASE WHEN tperson.iscompany = 't' THEN 1 ELSE 0 END)", "delegationen");

        select.joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk");
    }

    @Override
    protected String getJumpOffsetsColumn(OctopusContext octopusContext) throws BeanException {
        final String col = getSearch(octopusContext).listorder;
        if (Arrays.asList("lastname_a_e1", "firstname_a_e1", "mail_a_e1").contains(col)) {
            return col;
        }
        return null;
    }
}
