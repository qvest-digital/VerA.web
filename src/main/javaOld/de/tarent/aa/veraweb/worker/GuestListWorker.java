/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.EventURLHandler;
import de.tarent.aa.veraweb.utils.MediaRepresentativesUtilities;
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
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

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

    private final static String DELETE_ALL_STALE_GUEST_DOCTYPES = "DELETE FROM tguest_doctype WHERE fk_guest IN ({0})";
    private final static MessageFormat DELETE_ALL_STALE_GUEST_DOCTYPES_FORMAT = new MessageFormat( DELETE_ALL_STALE_GUEST_DOCTYPES );

    private final static String DELETE_TOPTIONAL_FIELDS_DELEGATION_CONTENT ="DELETE FROM toptional_fields_delegation_content WHERE fk_guest IN ({0})";
    private final static MessageFormat DELETE_ALL_OPTIONAL_DELEGATION_FIELDS_FOR_GUEST = new MessageFormat(DELETE_TOPTIONAL_FIELDS_DELEGATION_CONTENT);

    private final static String DELETE_ALL_STALE_GUESTS = "DELETE FROM tguest WHERE pk IN ({0})";
    private final static MessageFormat DELETE_ALL_STALE_GUESTS_FORMAT = new MessageFormat( DELETE_ALL_STALE_GUESTS );

    private final static String BULK_INSERT_CHANGELOG_ENTRIES =
            "INSERT INTO tchangelog (username, objname, objtype, objid, op, attributes, date) "
                    + "SELECT DISTINCT "
                    + "''{0}'' AS username, p.lastname_a_e1 || CASE WHEN p.firstname_a_e1 IS NOT NULL "
                    + "THEN '', '' || p.firstname_a_e1 ELSE '''' END AS objname, "
                    + "''de.tarent.aa.veraweb.beans.Guest'' AS objtype, g.pk AS objid, "
                    + "''delete'' AS op, ''*'' AS attributes, NOW() AS date "
                    + "FROM tperson p LEFT JOIN tguest g ON g.fk_person = p.pk "
                    + "WHERE g.pk IN ({1})";
    private final static MessageFormat BULK_INSERT_CHANGELOG_ENTRIES_FORMAT = new MessageFormat( BULK_INSERT_CHANGELOG_ENTRIES );

    private final static Logger logger = Logger.getLogger(GuestListWorker.class);

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public GuestListWorker() {
		super("Guest");
	}

    @Override
    public void saveList(OctopusContext cntx) throws BeanException, IOException {
        final EventURLHandler eventURLHandler = new EventURLHandler();
    	eventURLHandler.setEventUrl(cntx, (String) cntx.getContextField("event.hash"));
        final String categoryAssignmentAction = cntx.requestAsString("categoryAssignmentAction");
        final String workareaAssignmentAction = cntx.requestAsString("workareaAssignmentAction");

        // does the user request categories to be assigned or unassigned?
        saveGuestWithCategories(cntx, categoryAssignmentAction);
    }

    private void saveGuestWithCategories(OctopusContext cntx, final String categoryAssignmentAction)
            throws BeanException, IOException {
        if (categoryAssignmentAction != null && categoryAssignmentAction.length() > 0) {
            final Database database = getDatabase(cntx);
            final TransactionContext context = database.getTransactionContext();
            final Integer categoryId = cntx.requestAsInteger("categoryAssignmentId");
            final List selection = this.getSelection(cntx, this.getCount(cntx, database));
            final Iterator iter = selection.iterator();
            while (iter.hasNext()) {
                final Guest guest = (Guest) database.getBean("Guest", (Integer) iter.next(), context);
                if ("assign".compareTo(categoryAssignmentAction) == 0 && categoryId.intValue() > 0) {
                    guest.category = categoryId;
                } else {
                    guest.category = null;
                }
                database.saveBean(guest, context, false);
                iter.remove();
            }
            context.commit();
            cntx.setSession("selection" + BEANNAME, selection);
        }
        else {
            super.saveList(cntx);
        }
    }

    /**
	 * Entfernt die Zuordnungen von Arbeitsbereichen der übergebenen Liste von Gästen (IDs).
	 *
	 * @param cntx Octopus-Context
	 * @param guestIds Liste von Gast IDs für die das entfernen der Zuordnung gilt
	 * @param workAreaId ID des Arbeitsbereiches deren Zuordnung entfernt werden soll
	 * @throws BeanException
	 * @throws IOException
	 *
	 */
    /*
     * currently made private (disabled) as part of fix to issue #1530
     */
	private void unassignWorkArea(OctopusContext cntx, List<Integer> guestIds, Integer workAreaId)
            throws BeanException, IOException {
        final Database database = getDatabase(cntx);
        final TransactionContext context = database.getTransactionContext();

        try {
            for (Integer personId : guestIds) {
                final Guest guest = (Guest) database.getBean(BEANNAME, personId);
                final Person person = (Person) database.getBean("Person", guest.person);
                if (person != null && (person.workarea.intValue() == workAreaId.intValue() || workAreaId.intValue() == 0)) {
                    person.workarea = new Integer(0);
                    database.saveBean(person, context, false);
                }
            }
            context.commit();
        } catch (BeanException e) {
            context.rollBack();
        }
    }

	/**
	 * Ordnet den übergebenen Arbeitsbereich der Liste von Gästen hinzu.
	 *
	 * @param octopusContext OctopusContext
	 * @param guestIds Liste von Guest IDs für die die neue Zuordnung gilt
	 * @param workAreaId ID des Arbeitsbereiches der zugeordnet werden soll
	 * @throws BeanException
	 * @throws IOException
	 */
	/*
     * currently made private (disabled) as part of fix to issue #1530
     */
	private void assignWorkArea(OctopusContext octopusContext, List<Integer> guestIds, Integer workAreaId)
            throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final TransactionContext context = database.getTransactionContext();

        try {
            for (Integer personId : guestIds) {
                final Guest guest = (Guest) database.getBean(BEANNAME, personId);
                final Person person = (Person) database.getBean("Person", guest.person);
                if (person != null && person.workarea.intValue() != workAreaId.intValue()) {
                    person.workarea = workAreaId;
                    database.saveBean(person, context, false);
                }
            }
            context.commit();
        } finally {
            context.rollBack();
        }
    }

	protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException {
        final GuestSearch search = getSearch(octopusContext);

        final WhereList list = new WhereList();
        addGuestListFilter(search, list);
        select.where(list);
    }

	protected void extendColumns(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        final GuestSearch search = getSearch(octopusContext);
        final Integer configFreitextfeld = ConfigWorker.getInteger(octopusContext, "freitextfeld");
        Integer freitextfeld = null;

        if (configFreitextfeld != null) {
            final Database database = getDatabase(octopusContext);

            freitextfeld = selectEventDoctype(octopusContext, configFreitextfeld, freitextfeld, database);
        }

        buildGuestSelect(select, freitextfeld);

        final List order = buildOrderedGuestList(octopusContext, search, freitextfeld);

        select.orderBy(DatabaseHelper.getOrder(order));
    }

    private List buildOrderedGuestList(OctopusContext octopusContext, final GuestSearch search, Integer freitextfeld) {
        final List order = new ArrayList();
        order.add("ishost");

        getSortDirection(search);
        octopusContext.setSession("search" + BEANNAME, search);

        if (search == null || search.listorder == null) {
            order.add("someorderno");
            order.add("tcategorie.rank");
            order.add("tguest.rank");
            if (freitextfeld != null) {
                setOrderOfGastgeberNames(search, order);
            } else {
                order.add("lastname_a_e1");
                order.add("firstname_a_e1");
            }
        } else if (search.listorder.equals("orderno")) {
            order.add("someorderno");
            order.add(search.sortDirection);
        } else if (search.listorder.equals("lastname_a_e1")) {
            if (freitextfeld != null) {
                setOrderOfGastgeberNames(search, order);
            } else {
                setOrderOfNames(search, order);
            }

        } else if (search.listorder.equals("firstname_a_e1")) {
            if (freitextfeld != null) {
                setOrderOfGastgeberNames(search, order);
            } else {
                order.add("firstname_a_e1");
                order.add(search.sortDirection);
                order.add("lastname_a_e1");
                order.add(search.sortDirection);
            }

        } else if (search.listorder.equals("mail_a_e1")) {
            order.add("mail_a_e1");
            order.add(search.sortDirection);
            if (freitextfeld != null) {
                setOrderOfGastgeberNames(search, order);
            } else {
                setOrderOfNames(search, order);
            }

        } else if (search.listorder.equals("companyname")) {
            order.add("company_a_e1");
            order.add(search.sortDirection);
            if (freitextfeld != null) {
                setOrderOfGastgeberNames(search, order);
            } else {
                setOrderOfNames(search, order);
            }

        }
        return order;
    }

    private void setOrderOfNames(GuestSearch search, List order) {
        order.add("lastname_a_e1");
        order.add(search.sortDirection);
        order.add("firstname_a_e1");
        order.add(search.sortDirection);
    }

    private void setOrderOfGastgeberNames(GuestSearch search, List order) {
        order.add("lastname_a_gd");
        order.add(search.sortDirection);
        order.add("firstname_a_gd");
        order.add(search.sortDirection);
    }

    private void getSortDirection(GuestSearch search) {
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
            logger.warning("search.sortDirection is nil");
            search.sortDirection = "ASC";
        }
    }

    private void buildGuestSelect(Select select, Integer freitextfeld) {
        select.joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk");
        select.joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");
        select.selectAs("CASE WHEN tguest.orderno IS NOT NULL THEN NULLIF(tguest.orderno, 0) " +
                "ELSE NULLIF(tguest.orderno_p, 0) END", "someorderno");
        select.selectAs("tcategorie.rank", "catrank");
        select.select("firstname_a_e1");
        select.select("lastname_a_e1");
        select.select("firstname_b_e1");
        select.select("lastname_b_e1");
        select.select("function_a_e1");
        select.select("tperson.company_a_e1");
        select.select("tperson.company_a_e2");
        select.select("tperson.company_a_e3");
        select.select("city_a_e1");
        select.select("zipcode_a_e1");
        select.select("fon_a_e1");
        select.select("mail_a_e1");
        select.select("delegation");

        if (freitextfeld != null) {
            select.joinLeftOuter("veraweb.tguest_doctype", "tguest.pk", "tguest_doctype.fk_guest AND fk_doctype = "
                    + freitextfeld);
            select.selectAs("tguest_doctype.pk IS NOT NULL", "showdoctype");
            select.selectAs("firstname", "firstname_a_gd");
            select.selectAs("lastname", "lastname_a_gd");
            select.selectAs("firstname_p", "firstname_b_gd");
            select.selectAs("lastname_p", "lastname_b_gd");
            select.selectAs("mail", "mail_a_gd");
            select.selectAs("function", "function_a_gd");
            select.selectAs("city", "city_a_gd");
            select.selectAs("zipcode", "zipcode_a_gd");
            select.selectAs("fon", "fon_a_gd");
            select.selectAs("mail", "mail_a_gd");
        } else {
            select.selectAs("FALSE", "showdoctype");
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
    }

    private Integer selectEventDoctype(OctopusContext octopusContext, final Integer configFreitextfeld,
                                       Integer freitextfeld, final Database database)
            throws BeanException, IOException {
        final Select eventDoctypeSelect = database.getCount("EventDoctype");
        eventDoctypeSelect.where(Expr.equal("fk_event", octopusContext.requestAsInteger("search-event")));
        eventDoctypeSelect.whereAnd(Expr.equal("fk_doctype", configFreitextfeld));

        if (database.getCount(eventDoctypeSelect) != 0) {
            freitextfeld = configFreitextfeld;
        }
        return freitextfeld;
    }

	protected Integer getAlphaStart(OctopusContext octopusContext, String start) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final GuestSearch search = getSearch(octopusContext);

        final WhereList list = new WhereList();
        addGuestListFilter(search, list);

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
        return new Integer(i.intValue() - (i.intValue() % getLimit(octopusContext).intValue()));
    }

	protected Select getSelect(Database database) throws BeanException, IOException {
        return SQL.SelectDistinct(database).
                from("veraweb.tguest").
                selectAs("tguest.pk", "id").
                selectAs("tguest.rank", "rank").
                select("deleted").
                select("delegation").
                select("ishost").
                select("iscompany").
                select("invitationtype").
                selectAs("invitationstatus", "invitationstatus_a").
                selectAs("invitationstatus_p", "invitationstatus_b").
                selectAs("reserve", "reserve").
                selectAs("orderno", "orderno_a").
                selectAs("orderno_p", "orderno_b");
    }

    protected List getResultList(Database database, Select select) throws BeanException, IOException {
        final List fullList = getAllGuests(database, select);

        final List modifiedList = new ArrayList();
        for (int i = 0; i < fullList.size(); i++) {
            final Map guest = (Map) fullList.get(i);
            final String uuid = (String) guest.get("delegation");
            final String iscompany = (String) guest.get("iscompany");
            modifiedList.add(guest);
        }

        return modifiedList;
    }

    private List getAllGuests(Database database, Select select) throws BeanException {
        final ResultList allResults = database.getList(select, database);
        return copyResultSetToArrayList(allResults);
    }

    private List copyResultSetToArrayList(ResultList allResults) {
        final ArrayList fullList = new ArrayList();
        final int size = allResults.size();
        for (int i = 0; i < size; i++) {
            final ResultMap resultMap = (ResultMap) allResults.get(i);
            final HashMap map = new HashMap();
            map.putAll(resultMap);
            fullList.add(map);
        }

        return fullList;
    }

    protected int removeSelection(OctopusContext octopusContext, List errors, List selection,
                                  TransactionContext transactionContext) throws BeanException, IOException {
        try {
            final String ids = DatabaseHelper.listsToIdListString(new List[]{selection});
            DB.insert(transactionContext, DELETE_ALL_STALE_GUEST_DOCTYPES_FORMAT.format(new Object[]{ids}));
            DB.insert(transactionContext, DELETE_ALL_OPTIONAL_DELEGATION_FIELDS_FOR_GUEST.format(new Object[]{ids}));
            DB.insert(transactionContext, DELETE_ALL_STALE_GUESTS_FORMAT.format(new Object[]{ids}));
            DB.insert(transactionContext, BULK_INSERT_CHANGELOG_ENTRIES_FORMAT.format(new Object[]{octopusContext.personalConfig().getLoginname(), ids}));
            transactionContext.commit();
        } catch (BeanException e) {
            transactionContext.rollBack();
            throw new BeanException("Das L\u00f6schen aller zum l\u00f6schen markierten G\u00e4ste ist fehlgeschlagen.", e);
        } catch (SQLException e) {
            transactionContext.rollBack();
            throw new BeanException("Das L\u00f6schen aller zum l\u00f6schen markierten Personen ist fehlgeschlagen.", e);
        }

        return selection.size();
    }

	protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
            throws BeanException, IOException {
        final Database database = transactionContext.getDatabase();
        final Guest guest = (Guest) bean;
        guest.updateHistoryFields(null, ((PersonalConfigAA) octopusContext.personalConfig()).getRoleWithProxy());

		/* restore old guest state for logging purposes
         * cklein
		 * 2008-02-20
		 */
        final Guest guestOld = (Guest) database.getBean("Guest", guest.id);

        final Update update = createGuestUpdateStatement(database, guest);

        transactionContext.execute(update);

		/*
		 * modified to support change logging
		 * cklein 2008-02-12
		 */
        final BeanChangeLogger clogger = new BeanChangeLogger(database, transactionContext);
        clogger.logUpdate(octopusContext.personalConfig().getLoginname(), guestOld, guest);
    }

    private Update createGuestUpdateStatement(final Database database, final Guest guest) {
        final Update update = SQL.Update(database).
                table("veraweb.tguest").
                update("invitationstatus", guest.invitationstatus_a).
                update("invitationstatus_p", guest.invitationstatus_b).
                update("created", guest.created).
                update("createdby", guest.createdby).
                update("changed", guest.changed).
                update("changedby", guest.changedby).
                update("fk_category", guest.category).
                where(Expr.equal("pk", guest.id));

        if (guest.invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
            if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2) {
                update.update("orderno", null);
            }
            if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2) {
                update.update("orderno_p", null);
            }
        } else if (guest.invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
            if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2) {
                update.update("orderno", null);
            }
            update.update("orderno_p", null);
        } else if (guest.invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
            update.update("orderno", null);
            if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2) {
                update.update("orderno_p", null);
            }
        }
        return update;
    }

	protected boolean removeBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
            throws BeanException, IOException {
        final Database database = transactionContext.getDatabase();
        /*
		 * modified to support change logging
		 * cklein 2008-02-12
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

        transactionContext.execute(SQL.Delete(database).from("veraweb.tguest_doctype").
                where(Expr.equal("fk_guest", ((Guest) bean).id)));
        transactionContext.execute(SQL.Delete(database).from("veraweb.tguest").where(Expr.equal("pk", ((Guest) bean).id)));

        return true;
    }

    /**
     * Diese Octopus-Aktion liefert die Gesamtzahlen der aktuellen Gästeliste.
     *
     * @param octopusContext Octopus-Kontext
     * @return {@link Map} mit Gesamtzahlen unter den Schlüsseln "platz", "reserve",
     *  "all", "offen", "zusagen" und "absagen".
     */
    public Map getSums(OctopusContext octopusContext) throws BeanException {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final GuestSearch search = getSearch(octopusContext);
        final Map result = new HashMap();
        getSums(database, result, search, null);
        return result;
    }

    /**
     * Diese Octopus-Aktion liefert eine {@link GuestSearch}-Instanz, die die aktuellen
     * Gästesuchkriterien enthält. Diese stammen entweder aus dem Octopus-Content (unter
     * "search"), aus dem Octopus-Request oder aus der Octopus-Session (unter "searchGuest").
     * Vor der Rückgabe wird die Instanz unter "searchGuest" in die Octopus-Session
     * gestellt.
     *
     * @param octopusContext Octopus-Kontext
     * @return {@link GuestSearch}-Instanz zur aktuellen Gästesuche
     * @throws BeanException
     */
    public GuestSearch getSearch(OctopusContext octopusContext) throws BeanException {
        PropertiesReader propertiesReader = new PropertiesReader();
        Boolean sortList = octopusContext.requestAsBoolean("sortList");


        if (propertiesReader.propertiesAreAvailable()) {
            octopusContext.setContent("delegationCanBeUsed", true);
        }

        if (octopusContext.contentContains("search") &&
                octopusContext.contentAsObject("search") instanceof GuestSearch) {
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
         * fix for the case that the user requests guest with no search object being available
         * in that case an NPE was thrown
         * cklein 2008-03-27
         */
        if (search == null) {
            search = new GuestSearch();
        }


        GuestSearch sessionSearchGuest = (GuestSearch) octopusContext.sessionAsObject("search" + BEANNAME);

        if (sessionSearchGuest != null) {
        	/*
        	 * Gets the last string order of the session SearchPerson object and set it to the new session.
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
     * @throws BeanException
     * @throws IOException
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

        final MediaRepresentativesUtilities mediaRepresentativesUtilities = new MediaRepresentativesUtilities(octopusContext, event);
        mediaRepresentativesUtilities.setUrlForMediaRepresentatives();

        return event;
    }

    /**
     * // TODO
     * @param octopusContext The {@link de.tarent.octopus.server.OctopusContext}
     *
     * @throws IOException
     * @throws BeanException
     * @throws SQLException
     */
    public void getAllCategories(OctopusContext octopusContext) throws BeanException, IOException, SQLException {
        final Database database = new DatabaseVeraWeb(octopusContext);

        final Select select = SQL.Select( database ).from("veraweb.tcategorie");
		select.select("catname");
		select.setDistinctOn("catname");

        final ResultList result = database.getList(select, database);
        final List<String> catnames = new ArrayList<String>();

		for (final Iterator<ResultMap> iterator = result.iterator(); iterator.hasNext();) {
            final ResultMap object = iterator.next();
			catnames.add((String)object.get("catname"));
		}

		octopusContext.setContent("categories", catnames);
	}

    /**
     * Diese Methode überträgt Gästesuchkriterien aus einer {@link GuestSearch}-Instanz
     * in einer WHERE-Statement-Liste.
     */
    public static void addGuestListFilter(GuestSearch guestSearch, WhereList where) {
        where.addAnd(Expr.equal("tguest.fk_event", guestSearch.event));


        if (guestSearch.category != null && !guestSearch.category.trim().equals("")) {
            where.addAnd(new RawClause("fk_category IN (SELECT pk FROM veraweb.tcategorie WHERE catname = '" +
                    Escaper.escape(guestSearch.category) + "')"));
        }

        if (guestSearch.reserve != null) {
            switch (guestSearch.reserve.intValue()) {
                case 1:
                    where.addAnd(Expr.nullOrInt0("reserve"));
                    break;
                case 2:
                    where.addAnd(Expr.equal("reserve", new Integer(1)));
                    break;
            }
        }

        if (guestSearch.invitationstatus != null) {
            switch (guestSearch.invitationstatus.intValue()) {
                case 1:
                    // nur Offen
                    where.addAnd(new RawClause("(" +
                            // Mit Partner
                            "(invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR " +
                            "invitationstatus_p IS NULL OR invitationstatus_p=0)) OR" +
                            // Ohne Partner
                            "(invitationtype = 2 AND (invitationstatus IS NULL OR invitationstatus=0)) OR" +
                            // Nur Partner
                            "(invitationtype = 3 AND (invitationstatus_p IS NULL OR invitationstatus_p=0))" +
                            ")"));
                    break;
                case 2:
                    // nur Zusagen
                    where.addAnd(new RawClause("(" +
                            // Mit Partner
                            "(invitationtype = 1 AND (invitationstatus=1 OR invitationstatus_p=1)) OR" +
                            // Ohne Partner
                            "(invitationtype = 2 AND invitationstatus=1) OR" +
                            // Nur Partner
                            "(invitationtype = 3 AND invitationstatus_p=1)" +
                            ")"));
                    break;
                case 3:
                    // nur Absagen
                    where.addAnd(new RawClause("(" +
                            // Mit Partner
                            "(invitationtype = 1 AND (invitationstatus=2 OR invitationstatus_p=2)) OR" +
                            // Ohne Partner
                            "(invitationtype = 2 AND invitationstatus=2) OR" +
                            // Nur Partner
                            "(invitationtype = 3 AND invitationstatus_p=2)" +
                            ")"));
                    break;
                case 4:
                    // nur Teilnahmen
                    where.addAnd(new RawClause("(" +
                            // Mit Partner
                            "(invitationtype = 1 AND (invitationstatus=3 OR invitationstatus_p=3)) OR" +
                            // Ohne Partner
                            "(invitationtype = 2 AND invitationstatus=3) OR" +
                            // Nur Partner
                            "(invitationtype = 3 AND invitationstatus_p=3)" +
                            ")"));
                    break;
            }
        }
    }

	/**
	 * Berechnet die Gesamtzahlen der aktuellen Gästeliste.
	 *
	 * Vor Version 1.50 wurden "Auf Platz" und "Auf Reserve"
	 * pro Datensatz berechnet, die aktuelle Umsetzung zählt
	 * diese pro eingeladenen Member. (Vgl. Bug 1480)
	 *
	 * @param database
	 * @param data
	 * @param guestSearch
	 * @param selection
	 * @throws BeanException
	 */
	protected void getSums(Database database, Map data, GuestSearch guestSearch, List selection) throws BeanException {
        final Select select = buildAndCountListFromGuests(database, guestSearch, selection);

        final Map result = (Map) database.getList(select, database).iterator().next();
        Long platz = (Long) result.get("platz");
        Long reserve = (Long) result.get("reserve");
        Long zusagen = (Long) result.get("zusagen");
        Long absagen = (Long) result.get("absagen");
        Long teilnahmen = (Long) result.get("teilnahmen");
        Long delegationen = (Long) result.get("delegationen");

        if (platz == null) {
            platz = new Long(0);
        }
        if (reserve == null) {
            reserve = new Long(0);
        }
        if (zusagen == null) {
            zusagen = new Long(0);
        }
        if (absagen == null) {
            absagen = new Long(0);
        }
        if (teilnahmen == null) {
            teilnahmen = new Long(0);
        }
        if (delegationen == null) {
            delegationen = new Long(0);
        }

        data.put("platz", new Long(platz.longValue() - delegationen.longValue()));
        data.put("reserve", reserve);
        data.put("all", new Long(platz.longValue() - delegationen.longValue()));
        data.put("offen", new Long(platz.longValue() - zusagen.longValue() - absagen.longValue() -
                teilnahmen.longValue()) - delegationen.longValue());
        data.put("zusagen", zusagen);
        data.put("absagen", absagen);
        data.put("teilnahmen", teilnahmen);
    }

    private Select buildAndCountListFromGuests(Database database, GuestSearch guestSearch, List selection) {
        final WhereList where = new WhereList();
        GuestListWorker.addGuestListFilter(guestSearch, where);

        if (selection != null && selection.size() != 0) {
            where.addAnd(Expr.in("tguest.pk", selection));
        }

        final Select select = SQL.Select(database).
                from("veraweb.tguest").
                where(where);

        buildSelectSumFromGuestList(select);
        return select;
    }

    private void buildSelectSumFromGuestList(final Select select) {
        select.selectAs(
                "SUM(CASE WHEN invitationtype = 1 THEN 2 ELSE 1 END)", "platz");
        select.selectAs(
                "SUM(CASE WHEN reserve != 1 THEN 0 ELSE CASE WHEN invitationtype = 1 THEN 2 ELSE 1 END END)",
                "reserve");
        select.selectAs(
                "SUM(CASE WHEN invitationstatus   = 1 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
                        "SUM(CASE WHEN invitationstatus_p = 1 AND invitationtype != 2 THEN 1 ELSE 0 END)", "zusagen");
        select.selectAs(
                "SUM(CASE WHEN invitationstatus   = 2 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
                        "SUM(CASE WHEN invitationstatus_p = 2 AND invitationtype != 2 THEN 1 ELSE 0 END)", "absagen");
        select.selectAs(
                "SUM(CASE WHEN invitationstatus   = 3 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
                        "SUM(CASE WHEN invitationstatus_p = 3 AND invitationtype != 2 THEN 1 ELSE 0 END)",
                "teilnahmen");

        select.selectAs(
                "SUM(CASE WHEN tperson.iscompany = 't' THEN 1 ELSE 0 END)", "delegationen");

        select.joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk");
    }

}
