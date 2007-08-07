/* 
 * $Id: GuestListWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.Request;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Verwaltet eine Gästeliste.
 * 
 * @author Mikel, Christoph
 * @version $Revision: 1.1 $
 */
public class GuestListWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public GuestListWorker() {
		super("Guest");
	}

    //
    // Oberklasse BeanListWorker
    //
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		GuestSearch search = getSearch(cntx);
		
		WhereList list = new WhereList();
		addGuestListFilter(search, list);
		select.where(list);
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		GuestSearch search = getSearch(cntx);
		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		
		select.joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk");
		select.joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");
		select.selectAs("CASE WHEN tguest.orderno IS NOT NULL THEN tguest.orderno ELSE tguest.orderno_p END", "someorderno");
		select.selectAs("tcategorie.rank", "catrank");
		select.select("firstname_a_e1");
		select.select("lastname_a_e1");
		select.select("firstname_b_e1");
		select.select("lastname_b_e1");
		select.select("function_a_e1");
		select.select("city_a_e1");
		select.select("zipcode_a_e1");
		select.select("fon_a_e1");
		select.select("mail_a_e1");
		
		if (freitextfeld != null) {
			select.joinLeftOuter("veraweb.tguest_doctype", "tguest.pk", "tguest_doctype.fk_guest AND fk_doctype = " + freitextfeld);
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
		
		List order = new ArrayList();
		order.add("ishost");
		order.add("DESC");
		if (search == null || search.listorder == null) {
			order.add("someorderno");
			order.add("tcategorie.rank");
			order.add("tguest.rank");
			if (freitextfeld != null) {
				order.add("lastname_a_gd");
				order.add("firstname_a_gd");
			} else {
				order.add("lastname_a_e1");
				order.add("firstname_a_e1");
			}
		} else if (search.listorder.equals("lastname_a_e1")) {
			if (freitextfeld != null) {
				order.add("lastname_a_gd");
				order.add("firstname_a_gd");
			} else {
				order.add("lastname_a_e1");
				order.add("firstname_a_e1");
			}
		} else if (search.listorder.equals("firstname_a_e1")) {
			if (freitextfeld != null) {
				order.add("firstname_a_gd");
				order.add("lastname_a_gd");
			} else {
				order.add("firstname_a_e1");
				order.add("lastname_a_e1");
			}
		} else if (search.listorder.equals("mail_a_e1")) {
			order.add("mail_a_e1");
			if (freitextfeld != null) {
				order.add("lastname_a_gd");
				order.add("firstname_a_gd");
			} else {
				order.add("lastname_a_e1");
				order.add("firstname_a_e1");
			}
		}
		select.orderBy(DatabaseHelper.getOrder(order));
	}

	protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		GuestSearch search = getSearch(cntx);
		
		WhereList list = new WhereList();
		addGuestListFilter(search, list);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(list.clauseToString());
		buffer.append(") AND ");
		buffer.append(search.listorder);
		buffer.append(" < '");
		Escaper.escape(buffer, start);
		buffer.append("'");
		
		Select select = database.getCount(BEANNAME);
		select.joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk");
		select.where(new RawClause(buffer));
		
		Integer i = database.getCount(select);
		return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
	}

	protected Select getSelect(Database database) throws BeanException, IOException {
		return SQL.SelectDistinct().
				from("veraweb.tguest").
				selectAs("tguest.pk", "id").
				selectAs("tguest.rank", "rank").
				select("deleted").
				select("ishost").
				select("invitationtype").
				selectAs("invitationstatus", "invitationstatus_a").
				selectAs("invitationstatus_p", "invitationstatus_b").
				selectAs("reserve", "reserve").
				selectAs("orderno", "orderno_a").
				selectAs("orderno_p", "orderno_b");
	}

	protected List getResultList(Database database, Select select) throws BeanException, IOException {
		return database.getList(select);
	}

	protected int removeSelection(OctopusContext cntx, List errors, List selection) throws BeanException, IOException {
		int count = 0;
		PersonDetailWorker personDetailWorker = WorkerFactory.getPersonDetailWorker(cntx);
		Database database = getDatabase(cntx);
		
		if (selection == null || selection.size() == 0)
			return count;
		
		// Abfrage ob auch entsprechende Person gelöscht werden soll.
		Select select = SQL.Select().
				from("veraweb.tguest").
				selectAs("tguest.pk", "guest").
				selectAs("tperson.pk", "person").
				selectAs("tperson.deleted", "deleted").
				joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
				where(Expr.in("tguest.pk", selection));
		
		// Entsprechende Gäste und Personen löschen
		Guest guest = new Guest();
		for (Iterator it = database.getList(select).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			guest.id = (Integer)data.get("guest");
			if (removeBean(cntx, guest)) {
				count++;
			}
			if ("t".equals(data.get("deleted"))) {
				personDetailWorker.removePerson(database, (Integer)data.get("person"));
			}
		}
		selection.clear();
		
		Event event = (Event)cntx.contentAsObject("event");
		boolean noHost = 0 ==
				database.getCount(
				database.getCount("Guest").where(Where.and(
						Expr.equal("fk_event", event.id),
						Expr.equal("ishost", new Integer(1))))).intValue();
		if (noHost) {
			database.execute(SQL.Update().
					table("veraweb.tevent").
					update("fk_host", null).
					update("hostname", null).
					where(Expr.equal("pk", event.id)));
		}
		return count;
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException {
		Database database = getDatabase(cntx);
		Guest guest = (Guest)bean;
		guest.updateHistoryFields(null, ((PersonalConfigAA)cntx.configImpl()).getRoleWithProxy());
		
		Update update = SQL.Update().
				table("veraweb.tguest").
				update("invitationstatus", guest.invitationstatus_a).
				update("invitationstatus_p", guest.invitationstatus_b).
				update("created", guest.created).
				update("createdby", guest.createdby).
				update("changed", guest.changed).
				update("changedby", guest.changedby).
				where(Expr.equal("pk", guest.id));
		
		if (guest.invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
			if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2)
				update.update("orderno", null);
			if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2)
				update.update("orderno_p", null);
		} else if (guest.invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
			if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2)
				update.update("orderno", null);
			update.update("orderno_p", null);
		} else if (guest.invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
			update.update("orderno", null);
			if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2)
				update.update("orderno_p", null);
		}
		
		database.execute(update);
	}

	protected boolean removeBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		database.execute(SQL.Delete().
				from("veraweb.tguest_doctype").
				where(Expr.equal("fk_guest", ((Guest)bean).id)));
		database.execute(SQL.Delete().
				from("veraweb.tguest").
				where(Expr.equal("pk", ((Guest)bean).id)));
		return true;
	}

    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #getSums(OctopusContext)} */
    public static final String INPUT_getSums[] = {};
    /** Ausgabe-Parameter der Octopus-Aktion {@link #getSums(OctopusContext)} */
    public static final String OUTPUT_getSums = "guestlist-sums";
    /**
     * Diese Octopus-Aktion liefert die Gesamtzahlen der aktuellen Gästeliste.
     * 
     * @param cntx Octopus-Kontext
     * @return {@link Map} mit Gesamtzahlen unter den Schlüsseln "platz", "reserve",
     *  "all", "offen", "zusagen" und "absagen".
     */
    public Map getSums(OctopusContext cntx) throws BeanException {
        Database database = new DatabaseVeraWeb(cntx);
        GuestSearch search = getSearch(cntx);
        Map result = new HashMap();
        getSums(database, result, search, null);
        return result;
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #getSearch(OctopusContext)} */
    public static final String INPUT_getSearch[] = {};
    /** Ausgabe-Parameter der Octopus-Aktion {@link #getSearch(OctopusContext)} */
    public static final String OUTPUT_getSearch = "search";
    /**
     * Diese Octopus-Aktion liefert eine {@link GuestSearch}-Instanz, die die aktuellen
     * Gästesuchkriterien enthält. Diese stammen entweder aus dem Octopus-Content (unter
     * "search"), aus dem Octopus-Request oder aus der Octopus-Session (unter "searchGuest").
     * Vor der Rückgabe wird die Instanz unter "searchGuest" in die Octopus-Session
     * gestellt.
     * 
     * @param cntx Octopus-Kontext
     * @return {@link GuestSearch}-Instanz zur aktuellen Gästesuche
     * @throws BeanException
     */
    public GuestSearch getSearch(OctopusContext cntx) throws BeanException {
        if (cntx.contentContains("search") && cntx.contentAsObject("search") instanceof GuestSearch)
            return (GuestSearch)cntx.contentAsObject("search");
        
        Request request = new RequestVeraWeb(cntx);
        GuestSearch search = (GuestSearch)request.getBean("GuestSearch", "search");
        if (search == null || search.event == null)
            search = (GuestSearch)cntx.sessionAsObject("search" + BEANNAME);
        
        if (search != null && !("lastname_a_e1".equals(search.listorder) || "firstname_a_e1".equals(search.listorder) || "mail_a_e1".equals(search.listorder))) {
            search.listorder = null;
        }
        
        cntx.setSession("search" + BEANNAME, search);
        return search;
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #getEvent(OctopusContext)} */
    public static final String INPUT_getEvent[] = {};
    /** Ausgabe-Parameter der Octopus-Aktion {@link #getEvent(OctopusContext)} */
    public static final String OUTPUT_getEvent = "event";
    /**
     * Diese Octopus-Aktion liefert das Ereignis aus der aktuellen Gästesuche,
     * siehe Aktion {@link #getSearch(OctopusContext)}.
     * 
     * @param cntx Octopus-Kontext
     * @return eine {@link Event}-Instanz oder <code>null</code>.
     * @throws BeanException
     * @throws IOException
     */
    public Event getEvent(OctopusContext cntx) throws BeanException, IOException {
        GuestSearch search = getSearch(cntx);
        if (search == null) return null;
        Event event = EventDetailWorker.getEvent(cntx, search.event);
        if (event == null) cntx.setStatus("noevent");
        return event;
    }

    //
    // öffentliche Hilfsfunktionen
    //
    /**
     * Diese Methode überträgt Gästesuchkriterien aus einer {@link GuestSearch}-Instanz
     * in einer WHERE-Statement-Liste.
     */
    public static void addGuestListFilter(GuestSearch search, WhereList where) {
        where.addAnd(Expr.equal("tguest.fk_event", search.event));
        
        if (search.reserve != null) {
            switch (search.reserve.intValue()) {
            case 1:
                where.addAnd(Expr.nullOrInt0("reserve"));
                break;
            case 2:
                where.addAnd(Expr.equal("reserve", new Integer(1)));
                break;
            }
        }
        
        if (search.invitationstatus != null) {
            switch (search.invitationstatus.intValue()) {
            case 1:
                // nur Offen
                where.addAnd(new RawClause("(" +
                        // Mit Partner
                        "(invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR invitationstatus_p IS NULL OR invitationstatus_p=0)) OR" +
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
            }
        }
    }

    //
    // geschützte Hilfsfunktionen
    //
	/**
	 * Berechnet die Gesamtzahlen der aktuellen Gästeliste.
	 * 
	 * Vor Version 1.50 wurden "Auf Platz" und "Auf Reserve"
	 * pro Datensatz berechnet, die aktuelle Umsetzung zählt
	 * diese pro eingeladenen Member. (Vgl. Bug 1480)
	 * 
	 * @param database
	 * @param data
	 * @param search
	 * @param selection
	 * @throws BeanException
	 * @throws IOException
	 */
	protected void getSums(Database database, Map data, GuestSearch search, List selection) throws BeanException {
		WhereList where = new WhereList();
		GuestListWorker.addGuestListFilter(search, where);
		
		if (selection != null && selection.size() != 0) {
			where.addAnd(Expr.in("pk", selection));
		}
		
		Select select = SQL.Select().
				from("veraweb.tguest").
				where(where);
		
		select.selectAs(
				"SUM(CASE WHEN reserve =  1 THEN 0 ELSE CASE WHEN invitationtype = 1 THEN 2 ELSE 1 END END)", "platz");
		select.selectAs(
				"SUM(CASE WHEN reserve != 1 THEN 0 ELSE CASE WHEN invitationtype = 1 THEN 2 ELSE 1 END END)", "reserve");
		select.selectAs(
				"SUM(CASE WHEN reserve != 1 AND invitationstatus   = 1 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
				"SUM(CASE WHEN reserve != 1 AND invitationstatus_p = 1 AND invitationtype != 2 THEN 1 ELSE 0 END)", "zusagen");
		select.selectAs(
				"SUM(CASE WHEN reserve != 1 AND invitationstatus   = 2 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
				"SUM(CASE WHEN reserve != 1 AND invitationstatus_p = 2 AND invitationtype != 2 THEN 1 ELSE 0 END)", "absagen");
//		select.selectAs(
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus   = 1 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus_p = 1 AND invitationtype != 2 THEN 1 ELSE 0 END)", "zusagenReserve");
//		select.selectAs(
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus   = 2 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus_p = 2 AND invitationtype != 2 THEN 1 ELSE 0 END)", "absagenReserve");
		
		Map result = (Map)database.getList(select).iterator().next();
		Long platz = (Long)result.get("platz");
		Long reserve = (Long)result.get("reserve");
		Long zusagen = (Long)result.get("zusagen");
		Long absagen = (Long)result.get("absagen");
//		Long zusagenReserve = (Long)result.get("zusagenReserve");
//		Long absagenReserve = (Long)result.get("absagenReserve");
		if (platz == null) platz = new Long(0);
		if (reserve == null) reserve = new Long(0);
		if (zusagen == null) zusagen = new Long(0);
		if (absagen == null) absagen = new Long(0);
//		if (zusagenReserve == null) zusagenReserve = new Long(0);
//		if (absagenReserve == null) absagenReserve = new Long(0);
		
		data.put("platz", platz);
		data.put("reserve", reserve);
		data.put("all", new Long(platz.longValue() + reserve.longValue()));
		data.put("offen", new Long(platz.longValue() - zusagen.longValue() - absagen.longValue()));
		data.put("zusagen", zusagen);
		data.put("absagen", absagen);
//		data.put("offenReserve", new Long(reserve.longValue() - zusagenReserve.longValue() - absagenReserve.longValue()));
//		data.put("zusagenReserve", zusagenReserve);
//		data.put("absagenReserve", absagenReserve);
	}
}
