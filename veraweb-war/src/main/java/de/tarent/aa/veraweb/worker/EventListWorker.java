/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
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
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanListWorker;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige von
 * Veranstaltungslisten zur Verf�gung. Details zur Verwendung
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
    // �ffentliche Konstanten
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
     * Diese Methode f�gt eine Bedingung zum Filtern nach dem Mandanten hinzu, wenn der
     * aktuelle Benutzer nicht Superadmin ist.
     * 
     * @param cntx Octopus-Kontext
     * @param select Event-Select
     * @throws BeanException wenn keine testbaren Benutzerinformationen vorliegen.
     * @see BeanListWorker#getAll(OctopusContext)
     * @see BeanListWorker#extendAll(OctopusContext, Select)
     */
    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        super.extendAll(cntx, select);
        TcPersonalConfig pConfig = cntx.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = cntx.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)))
                select.where(Expr.equal("fk_orgunit", aaConfig.getOrgUnitId()));
        } else
            throw new BeanException("Missing user information");
        
        // Dreht die Sortierung beim Export von Personen-Daten
        // um, um erst die "�ltesten" Veranstaltungen zu sehen,
        // da diese am wahrscheinlichsten f�r einen Export
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
	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		Event search = getSearch(cntx);
		
		// WHERE - Filtert das Datenbank Ergebnis anhand der Benutzereingaben.
		WhereList where = new WhereList();
        
        TcPersonalConfig pConfig = cntx.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = cntx.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)))
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
			Timestamp nextDay = new Timestamp(search.begin.getTime() + 86400000); // n�chster tag
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
	 * �berpr�ft ob eine Veranstaltung nicht mindestens ein Jahr in der
	 * Vergangheit liegt und fragt ggf. ob diese trotzdem gel�scht werden soll.
	 * <br><br>
	 * siehe Anwendungsfall: UC.VERA.LOESCH
	 */
	@Override
    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		if (selection == null || selection.size() == 0) return count;
		Database database = context.getDatabase();
		Map questions = new HashMap();
		
		Calendar notExpire = Calendar.getInstance();
		notExpire.add(Calendar.YEAR, -1);
		
		List eventIsNotExpire =
				database.getBeanList("Event",
				database.getSelect("Event").
				where(Where.and(
						Expr.greaterOrEqual("datebegin", notExpire.getTime()),
						Expr.in("pk", selection))), context);
		List removeExpireEvents = new ArrayList();
		for (Iterator it = eventIsNotExpire.iterator(); it.hasNext(); ) {
			Event event = (Event)it.next();
			if (cntx.requestAsBoolean("remove-event" + event.id).booleanValue()) {
				removeExpireEvents.add(event.id);
			} else {
				questions.put("remove-event" + event.id, "Die Veranstaltung '" + event.shortname + "' ist noch nicht veraltet, soll diese trotzdem gelöscht werden?");
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
		for (Iterator it = database.getList(select, context).iterator(); it.hasNext(); ) {
			data = (Map)it.next();
			event.id = (Integer)data.get("id");
			if (removeBean(cntx, event, context)) {
				selection.remove(event.id);
				count++;
			}
		}

		try
		{
			// will commit here so that the following call to 
			// PersonDetailWorker.removeAllDeletedPersonsHavingNoEvent()
			// succeeds
			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die Veranstaltungen konnten nicht gelöscht werden.", e );
		}

		// must also remove all persons marked for deletion, when possible
		PersonDetailWorker.removeAllDeletedPersonsHavingNoEvent( cntx, context );

		return count;
	}

	/**
	 * L�scht Veranstaltungen und die zugeordneten G�ste.
	 */
	@Override
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		Database database = context.getDatabase();
		
		Event event = (Event)bean;
		context.execute(
				SQL.Delete( database ).
				from("veraweb.tguest_doctype").
				where(Expr.in("fk_guest",
						SQL.Select( database ).
						from("veraweb.tguest").
						selectAs("pk", "id").
						where(Expr.equal("fk_event", event.id)))));
		context.execute(
				SQL.Delete( database ).
				from("veraweb.tguest").
				where(Expr.equal("fk_event", event.id)));
		context.execute(PersonListWorker.getPersonClear( database ));
		
		context.execute(
				SQL.Delete( database ).
				from("veraweb.tevent_doctype").
				where(Expr.equal("fk_event", event.id)));

		boolean result = super.removeBean(cntx, bean, context);
		if ( result )
		{
			BeanChangeLogger clogger = new BeanChangeLogger( database );
			clogger.logDelete( cntx.personalConfig().getLoginname(), event );	
		}

		return result;
	}

    /** Octopus-Eingabe-Parameter f�r {@link #getSearch(OctopusContext)} */
	public static final String INPUT_getSearch[] = {};
    /** Octopus-Ausgabe-Parameter f�r {@link #getSearch(OctopusContext)} */
	public static final String OUTPUT_getSearch = "search";

	/**
	 * Spiegelt die vom Benutzer eingebene Suche nach Veranstaltungen wieder.
	 * Entsprechende Eingaben werden in der Session gespeichert.
	 * 
	 * @param cntx Octopus-Context
	 * @return Event-Instanz die die aktuelle Suche repr�sentiert.
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
