/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
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

/**
 * Verwaltet eine G�steliste.
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

    @Override
	public void saveList(OctopusContext cntx) throws BeanException, IOException
	{
    	String categoryAssignmentAction = cntx.requestAsString( "categoryAssignmentAction" );
    	String workareaAssignmentAction = cntx.requestAsString( "workareaAssignmentAction" );
    	
		// does the user request categories to be assigned or unassigned?
		if ( categoryAssignmentAction != null && categoryAssignmentAction.length() > 0 )
		{
			Database database = getDatabase(cntx);
			TransactionContext context = database.getTransactionContext();
			Integer categoryId = cntx.requestAsInteger( "categoryAssignmentId" );
			List selection = this.getSelection( cntx, this.getCount( cntx, database ) );
			Iterator iter = selection.iterator();
			while( iter.hasNext() )
			{
				Guest guest = ( Guest ) database.getBean( "Guest", ( Integer ) iter.next(), context );
				if ( "assign".compareTo( categoryAssignmentAction ) == 0 && categoryId.intValue() > 0 )
				{
					guest.category = categoryId;
				}
				else
				{
					guest.category = null;
				}
				database.saveBean(guest, context, false);
				iter.remove();
			}
			context.commit();
			cntx.setSession( "selection" + BEANNAME, selection );
		}
/*
 * currently disabled as part of fix to issue #1530
		// does the user request workareas to be assigned or unassigned?
		else if(workareaAssignmentAction != null && workareaAssignmentAction.length() > 0)
		{
			Database database = getDatabase(cntx);
			List<Integer> selection = getSelection(cntx, getCount(cntx, database));
			if(!selection.isEmpty())
			{
				Integer workareaId = cntx.requestAsInteger( "workareaAssignmentId" );
				if("assign".compareTo(workareaAssignmentAction) == 0)
				{
					assignWorkArea(cntx, selection, workareaId);
				}
				else if ("unassign".compareTo(workareaAssignmentAction) == 0)
				{
					unassignWorkArea(cntx, selection, workareaId);
				}
				cntx.setSession( "selection" + BEANNAME, Collections.emptyList() );
			}
		}
*/
		else
		{
			super.saveList( cntx );
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
	private void unassignWorkArea(OctopusContext cntx, List<Integer> guestIds, Integer workAreaId) throws BeanException, IOException
	{
		Database database = getDatabase(cntx);
		TransactionContext context = database.getTransactionContext();
		
		try
		{
			for(Integer personId : guestIds)
			{
				Guest guest = (Guest) database.getBean(BEANNAME, personId);
				Person person = (Person) database.getBean("Person", guest.person);
				if(person != null && (person.workarea.intValue() == workAreaId.intValue() || workAreaId.intValue() == 0))
				{
					person.workarea = new Integer(0);
					database.saveBean(person, context, false);
				}
			}
			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
		}
	}
	
	/**
	 * Ordnet den übergebenen Arbeitsbereich der Liste von Gästen hinzu.
	 * 
	 * @param cntx OctopusContext
	 * @param guestIds Liste von Guest IDs für die die neue Zuordnung gilt
	 * @param workAreaId ID des Arbeitsbereiches der zugeordnet werden soll
	 * @throws BeanException
	 * @throws IOException
	 */
	/*
     * currently made private (disabled) as part of fix to issue #1530
     */
	private void assignWorkArea(OctopusContext cntx, List<Integer> guestIds, Integer workAreaId) throws BeanException, IOException
	{
		Database database = getDatabase(cntx);
		TransactionContext context = database.getTransactionContext();
		
		try 
		{
			for(Integer personId : guestIds)
			{
				Guest guest = (Guest) database.getBean(BEANNAME, personId);
				Person person = (Person) database.getBean("Person", guest.person);
				if(person != null && person.workarea.intValue() != workAreaId.intValue())
				{
					person.workarea = workAreaId;
					database.saveBean(person, context, false);
				}
			}
			context.commit();
		} 
		finally 
		{
			context.rollBack();
		}
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

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		GuestSearch search = getSearch(cntx);
		Integer configFreitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		Integer freitextfeld = null;

		if(configFreitextfeld != null) {
			Database database = getDatabase(cntx);
			
			Select eventDoctypeSelect = database.getCount("EventDoctype");
			eventDoctypeSelect.where(Expr.equal("fk_event", cntx.requestAsInteger("search-event")));
			eventDoctypeSelect.whereAnd(Expr.equal("fk_doctype", configFreitextfeld));
	
			if(database.getCount(eventDoctypeSelect) != 0) {
				freitextfeld = configFreitextfeld;
			}
		}
		
		select.joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk");
		select.joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");
		select.selectAs("CASE WHEN tguest.orderno IS NOT NULL THEN NULLIF(tguest.orderno, 0) ELSE NULLIF(tguest.orderno_p, 0) END", "someorderno");
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
		return SQL.SelectDistinct( database ).
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
		return database.getList(select, database);
	}

	/*

	DELETE FROM tguest_doctype WHERE tguest.fk_guest IN ({0});

    personIds = SELECT tperson.pk FROM tperson LEFT JOIN tguest ON tperson.pk = tguest.fk_person WHERE tguest.pk IN ({0}) AND tperson.deleted = ''t''; 

	DELETE FROM tguest WHERE tguest.pk IN ({0});

    COMMIT

    personIds = SELECT tperson.pk FROM tperson WHERE tperson.pk NOT IN (SELECT DISTINCT tguest.fk_person FROM tguest) AND tperson.deleted = ''t''; 

    DELETE FROM tperson WHERE tperson.deleted = ''t'' AND tperson.pk IN ({0}) AND tperson.pk NOT IN (SELECT DISTINCT tguest.fk_person FROM tguest)
    DELETE FROM tperson_categorie WHERE tperson_categorie.fk_person IN ({0})
    DELETE FROM tperson_doctype WHERE tperson_doctype.fk_person IN ({0})
    DELETE FROM tperson_mailinglist WHERE tperson_mailinglist.fk_person IN ({0})
    UPDATE tevent SET tevent.fk_host = NULL, tevent.hostname = NULL WHERE tevent.fk_host IN ({0}) 

	 */

	private final static String DELETE_ALL_STALE_GUEST_DOCTYPES = 
		"DELETE FROM tguest_doctype WHERE fk_guest IN ({0})";
	private final static MessageFormat DELETE_ALL_STALE_GUEST_DOCTYPES_FORMAT = new MessageFormat( DELETE_ALL_STALE_GUEST_DOCTYPES );

	private final static String DELETE_ALL_STALE_GUESTS = 
		"DELETE FROM tguest WHERE pk IN ({0})";
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

	protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException
	{
		try
		{
			String ids = DatabaseHelper.listsToIdListString( new List[] { selection } );
			DB.insert( context, DELETE_ALL_STALE_GUEST_DOCTYPES_FORMAT.format( new Object[] { ids } ) );
			DB.insert( context, DELETE_ALL_STALE_GUESTS_FORMAT.format( new Object[] { ids } ) );
			DB.insert( context, BULK_INSERT_CHANGELOG_ENTRIES_FORMAT.format( new Object[] { cntx.personalConfig().getLoginname(), ids }	) );
			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Das Löschen aller zum löschen markierten Gäste ist fehlgeschlagen.", e );
		}
		catch ( SQLException e )
		{
			context.rollBack();
			throw new BeanException( "Das Löschen aller zum löschen markierten Personen ist fehlgeschlagen.", e );
		}

		PersonDetailWorker.removeAllDeletedPersonsHavingNoEvent( cntx, context );

		return selection.size();
	}

/*	
	protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		PersonDetailWorker personDetailWorker = WorkerFactory.getPersonDetailWorker(cntx);
		Database database = context.getDatabase();
		
		if (selection == null || selection.size() == 0)
			return count;
		
		// Abfrage ob auch entsprechende Person gel�scht werden soll.
		Select select = SQL.Select( context ).
				from("veraweb.tguest").
				selectAs("tguest.pk", "guest").
				selectAs("tperson.pk", "person").
				selectAs("tperson.deleted", "deleted").
				joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
				where(Expr.in("tguest.pk", selection));
		
		// Entsprechende G�ste und Personen l�schen
		Guest guest = new Guest();
		try
		{
			for (Iterator it = database.getList(select, context).iterator(); it.hasNext(); ) {
				Map data = (Map)it.next();
				guest.id = (Integer)data.get("guest");
				if (removeBean(cntx, guest, context)) {
					count++;
				}
				if ("t".equals(data.get("deleted"))) {
					personDetailWorker.removePerson(cntx, context, (Integer)data.get("person"));
				}
			}
			// TODO updates Event, change must be logged
			// TODO should be refactor-moved to Event
			Event event = (Event)cntx.contentAsObject("event");
			boolean noHost = 0 ==
					database.getCount(
					database.getCount("Guest").where(Where.and(
							Expr.equal("fk_event", event.id),
							Expr.equal("ishost", new Integer(1))))).intValue();
			if (noHost) {
				context.execute(SQL.Update( context).
						table("veraweb.tevent").
						update("fk_host", null).
						update("hostname", null).
						where(Expr.equal("pk", event.id)));
			}
			
			context.commit();

			selection.clear();
		}
		catch( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die ausgewählten Gäste konnten nicht gelöscht werden.", e );
		}

		return count;
	}
*/

	protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		Database database = context.getDatabase();
		Guest guest = (Guest)bean;
		guest.updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());

		/* restore old guest state for logging purposes
		 * cklein
		 * 2008-02-20
		 */
		Guest guestOld = ( Guest ) database.getBean( "Guest", guest.id );
		// restore foreign keys
		//guest.person = guestOld.person;
		//guest.category = guestOld.category;
		//guest.color_a = guestOld.color_a;
		//guest.color_b = guestOld.color_b;
		//guest.event = guestOld.event;
		
		Update update = SQL.Update( database ).
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
		
		context.execute(update);
		
		/*
		 * modified to support change logging
		 * cklein 2008-02-12
		 */
		BeanChangeLogger clogger = new BeanChangeLogger( database, context );
		clogger.logUpdate( cntx.personalConfig().getLoginname(), guestOld, guest );
	}

	protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		Database database = context.getDatabase();
		/*
		 * modified to support change logging
		 * cklein 2008-02-12
		 */
		BeanChangeLogger clogger = new BeanChangeLogger( database, context );
		/*
		 * see bug #1033
		 */
		if ( ( ( Guest ) bean ).person == null )
		{
			// need to load the guest entity in order to retrieve the person
			bean = database.getBean( "Guest", ( ( Guest ) bean ).id, context );
		}
		clogger.logDelete( cntx.personalConfig().getLoginname(), bean );
		
		context.execute(SQL.Delete( database ).
				from("veraweb.tguest_doctype").
				where(Expr.equal("fk_guest", ((Guest)bean).id)));
		context.execute(SQL.Delete( database ).
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
     * Diese Octopus-Aktion liefert die Gesamtzahlen der aktuellen G�steliste.
     * 
     * @param cntx Octopus-Kontext
     * @return {@link Map} mit Gesamtzahlen unter den Schl�sseln "platz", "reserve",
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
     * G�stesuchkriterien enth�lt. Diese stammen entweder aus dem Octopus-Content (unter
     * "search"), aus dem Octopus-Request oder aus der Octopus-Session (unter "searchGuest").
     * Vor der R�ckgabe wird die Instanz unter "searchGuest" in die Octopus-Session
     * gestellt.
     * 
     * @param cntx Octopus-Kontext
     * @return {@link GuestSearch}-Instanz zur aktuellen G�stesuche
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
        
        /*
         * fix for the case that the user requests guest with no search object being available
         * in that case an NPE was thrown
         * cklein 2008-03-27
         */
        if ( search == null )
        {
        	search = new GuestSearch();
        }
        
        cntx.setSession("search" + BEANNAME, search);
        return search;
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #getEvent(OctopusContext)} */
    public static final String INPUT_getEvent[] = {};
    /** Ausgabe-Parameter der Octopus-Aktion {@link #getEvent(OctopusContext)} */
    public static final String OUTPUT_getEvent = "event";
    /**
     * Diese Octopus-Aktion liefert das Ereignis aus der aktuellen G�stesuche,
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
    // �ffentliche Hilfsfunktionen
    //
    /**
     * Diese Methode �bertr�gt G�stesuchkriterien aus einer {@link GuestSearch}-Instanz
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

    //
    // gesch�tzte Hilfsfunktionen
    //
	/**
	 * Berechnet die Gesamtzahlen der aktuellen G�steliste.
	 * 
	 * Vor Version 1.50 wurden "Auf Platz" und "Auf Reserve"
	 * pro Datensatz berechnet, die aktuelle Umsetzung z�hlt
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
		
		Select select = SQL.Select( database ).
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
		select.selectAs(
				"SUM(CASE WHEN reserve != 1 AND invitationstatus   = 3 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
				"SUM(CASE WHEN reserve != 1 AND invitationstatus_p = 3 AND invitationtype != 2 THEN 1 ELSE 0 END)", "teilnahmen");
//		select.selectAs(
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus   = 1 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus_p = 1 AND invitationtype != 2 THEN 1 ELSE 0 END)", "zusagenReserve");
//		select.selectAs(
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus   = 2 AND invitationtype != 3 THEN 1 ELSE 0 END) + " +
//				"SUM(CASE WHEN reserve = 1  AND invitationstatus_p = 2 AND invitationtype != 2 THEN 1 ELSE 0 END)", "absagenReserve");
		
		Map result = (Map)database.getList(select, database).iterator().next();
		Long platz = (Long)result.get("platz");
		Long reserve = (Long)result.get("reserve");
		Long zusagen = (Long)result.get("zusagen");
		Long absagen = (Long)result.get("absagen");
		Long teilnahmen = (Long)result.get("teilnahmen");
//		Long zusagenReserve = (Long)result.get("zusagenReserve");
//		Long absagenReserve = (Long)result.get("absagenReserve");
		if (platz == null) platz = new Long(0);
		if (reserve == null) reserve = new Long(0);
		if (zusagen == null) zusagen = new Long(0);
		if (absagen == null) absagen = new Long(0);
		if (teilnahmen == null) teilnahmen = new Long(0);
//		if (zusagenReserve == null) zusagenReserve = new Long(0);
//		if (absagenReserve == null) absagenReserve = new Long(0);
		
		data.put("platz", platz);
		data.put("reserve", reserve);
		data.put("all", new Long(platz.longValue() + reserve.longValue()));
		data.put("offen", new Long(platz.longValue() - zusagen.longValue() - absagen.longValue() - teilnahmen.longValue()));
		data.put("zusagen", zusagen);
		data.put("absagen", absagen);
		data.put("teilnahmen", teilnahmen);
//		data.put("offenReserve", new Long(reserve.longValue() - zusagenReserve.longValue() - absagenReserve.longValue()));
//		data.put("zusagenReserve", zusagenReserve);
//		data.put("absagenReserve", absagenReserve);
	}
}
