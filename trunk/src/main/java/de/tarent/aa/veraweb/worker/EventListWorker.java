/*
 * $Id: EventListWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 14.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.config.PersonalConfigImpl;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.BeanListWorker;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

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
    // öffentliche Konstanten
    //
    /** Parameter: Wessen Ereignisse? */
    public final static String PARAM_DOMAIN = "domain";
    
    /** Parameterwert: beliebige Ereignisse */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Ereignisse des gleichen Mandanten */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";

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
     * @param cntx Octopus-Kontext
     * @param select Event-Select
     * @throws BeanException wenn keine testbaren Benutzerinformationen vorliegen.
     * @see BeanListWorker#getAll(OctopusContext)
     * @see BeanListWorker#extendAll(OctopusContext, Select)
     */
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        super.extendAll(cntx, select);
        PersonalConfigImpl pConfig = cntx.configImpl();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = cntx.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)))
                select.where(Expr.equal("fk_orgunit", aaConfig.getOrgUnitId()));
        } else
            throw new BeanException("Missing user information");
        
        // Dreht die Sortierung beim Export von Personen-Daten
        // um, um erst die "ältesten" Veranstaltungen zu sehen,
        // da diese am wahrscheinlichsten für einen Export
        // in Frage kommen.
        String invertOrder = cntx.contentAsString("invertOrder");
        if ("true".equals(invertOrder)) {
        	select.orderBy(Order.desc("datebegin").andAsc("shortname"));
        }
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
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		Event search = getSearch(cntx);
		
		// WHERE - Filtert das Datenbank Ergebnis anhand der Benutzereingaben.
		WhereList where = new WhereList();
        
        PersonalConfigImpl pImpl = cntx.configImpl();
        if (pImpl instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pImpl;
            String domain = cntx.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pImpl.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)))
                where.addAnd(Expr.equal("fk_orgunit", aaConfig.getOrgUnitId()));
        } else
            throw new BeanException("Missing user information");
        
		if (search.shortname != null && search.shortname.length() != 0) {
			where.addAnd(DatabaseHelper.getWhere(search.shortname, new String[] {
					"shortname" }));
		}
		if (search.eventname != null && search.eventname.length() != 0) {
			where.addAnd(DatabaseHelper.getWhere(search.eventname, new String[] {
					"eventname" }));
		}
		if (search.hostname != null && search.hostname.length() != 0) {
			where.addAnd(DatabaseHelper.getWhere(search.hostname, new String[] {
					"hostname" }));
		}
		if (search.location != null && search.location.length() != 0) {
			where.addAnd(DatabaseHelper.getWhere(search.location, new String[] {
					"location" }));
		}
		if (search.begin != null) {
			Timestamp nextDay = new Timestamp(search.begin.getTime() + 86400000); // nächster tag
			where.addAnd(Where.and(
					Expr.greaterOrEqual("datebegin", search.begin),
					Expr.less("datebegin", nextDay)));
		}
		if (search.end != null) {
			where.addAnd(new RawClause(
					"((datebegin IS NOT NULL AND datebegin>=now()::date) OR" +
					" (dateend IS NOT NULL AND dateend>=now()::date))"));
//			where.addAnd(Where.or(
//					Expr.greaterOrEqual("datebegin", search.end),
//					Expr.greaterOrEqual("dateend", search.end)));
		}
		
        if (where.size() > 0)
            select.where(where);
	}

	/**
	 * Überprüft ob eine Veranstaltung nicht mindestens ein Jahr in der
	 * Vergangheit liegt und fragt ggf. ob diese trotzdem gelöscht werden soll.
	 * <br><br>
	 * siehe Anwendungsfall: UC.VERA.LOESCH
	 */
	protected int removeSelection(OctopusContext cntx, List errors, List selection) throws BeanException, IOException {
		int count = 0;
		if (selection == null || selection.size() == 0) return count;
		Database database = new DatabaseVeraWeb(cntx);
		Map questions = new HashMap();
		
		Calendar notExpire = Calendar.getInstance();
		notExpire.add(Calendar.YEAR, -1);
		
		List eventIsNotExpire =
				database.getBeanList("Event",
				database.getSelect("Event").
				where(Where.and(
						Expr.greaterOrEqual("datebegin", notExpire.getTime()),
						Expr.in("pk", selection))));
		List removeExpireEvents = new ArrayList();
		for (Iterator it = eventIsNotExpire.iterator(); it.hasNext(); ) {
			Event event = (Event)it.next();
			if (cntx.requestAsBoolean("remove-event" + event.id).booleanValue()) {
				removeExpireEvents.add(event.id);
			} else {
				questions.put("remove-event" + event.id, "Die Veranstaltung '" + event.shortname + "' ist noch nicht veraltet, soll diese trotzdem gel&ouml;scht werden?");
			}
		}
		
		if (!questions.isEmpty()) {
			cntx.setContent("listquestions", questions);
		}
		
		Event event = (Event)database.createBean("Event");
		Clause clause = Where.and(
				Expr.less("datebegin", notExpire.getTime()),
				Expr.in("pk", selection));
		if (removeExpireEvents.size() > 0) {
			clause = Where.or(clause, Expr.in("pk", removeExpireEvents));
		}
		Select select = database.getSelectIds(event).where(clause);
		
		Map data;
		for (Iterator it = database.getList(select).iterator(); it.hasNext(); ) {
			data = (Map)it.next();
			event.id = (Integer)data.get("id");
			if (removeBean(cntx, event)) {
				selection.remove(event.id);
				count++;
			}
		}
		return count;
	}

	/**
	 * Löscht Veranstaltungen und die zugeordneten Gäste.
	 */
	protected boolean removeBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		Event event = (Event)bean;
		database.execute(
				SQL.Delete().
				from("veraweb.tguest_doctype").
				where(Expr.in("fk_guest",
						SQL.Select().
						from("veraweb.tguest").
						selectAs("pk", "id").
						where(Expr.equal("fk_event", event.id)))));
		database.execute(
				SQL.Delete().
				from("veraweb.tguest").
				where(Expr.equal("fk_event", event.id)));
		database.execute(PersonListWorker.getPersonClear());
		
		database.execute(
				SQL.Delete().
				from("veraweb.tevent_doctype").
				where(Expr.equal("fk_event", event.id)));
		return super.removeBean(cntx, bean);
	}

    /** Octopus-Eingabe-Parameter für {@link #getSearch(OctopusContext)} */
	public static final String INPUT_getSearch[] = {};
    /** Octopus-Ausgabe-Parameter für {@link #getSearch(OctopusContext)} */
	public static final String OUTPUT_getSearch = "search";

	/**
	 * Spiegelt die vom Benutzer eingebene Suche nach Veranstaltungen wieder.
	 * Entsprechende Eingaben werden in der Session gespeichert.
	 * 
	 * @param cntx Octopus-Context
	 * @return Event-Instanz die die aktuelle Suche repräsentiert.
	 * @throws BeanException
	 */
	public Event getSearch(OctopusContext cntx) throws BeanException {
		Event search = null;
		if ("reset".equals(cntx.requestAsString("search"))) {
			search = (Event)new RequestVeraWeb(cntx).getBean("Event");
			if (cntx.requestAsBoolean("current").booleanValue()) {
				long now = System.currentTimeMillis();
				search.end = new Timestamp(now - (now % 86400000) - 86400000);
			}
		} else if ("current".equals(cntx.requestAsString("search"))) {
			long now = System.currentTimeMillis();
			search = new Event();
			search.end = new Timestamp(now - (now % 86400000) - 86400000);
		}
		if (search == null)
			search = (Event)cntx.sessionAsObject("search" + BEANNAME);
		if (search == null)
			search = new Event();
		cntx.setSession("search" + BEANNAME, search);
		return search;
	}
}
