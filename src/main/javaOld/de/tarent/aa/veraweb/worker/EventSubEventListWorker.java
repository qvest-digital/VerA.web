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
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
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
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.server.OctopusContext;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author sweiz - tarent solutions GmbH on 30.07.15.
 */
public class EventSubEventListWorker extends ListWorkerVeraWeb {

	
	
	  //
    // Öffentliche Konstanten
    //
    /** Parameter: Wessen Ereignisse? */
    public final static String PARAM_DOMAIN = "domain";

    /** Parameterwert: beliebige Ereignisse */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Ereignisse des gleichen Mandanten */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";

    public static final String INPUT_getEvent[] = {};

    public static final String OUTPUT_getEvent = "event";
    
    public static final String INPUT_passParentEventId[] = {};
    
    //
    // Konstruktoren
    //
	/** Default-Kontruktor der den Beannamen festlegt. */
	public EventSubEventListWorker() {
		super("Event");
	}
	
	public void getEvent(OctopusContext cntx, Integer id) throws BeanException, IOException {
		if (id != null) {
			Database database = new DatabaseVeraWeb(cntx);
			Event event = (Event)database.getBean("Event", id);
			
	        if (event != null) {
	        	
	        }
		}
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
    private static final Logger LOGGER = Logger.getLogger(EventSubEventListWorker.class.getCanonicalName());

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tevent.parent_event_id", getEvent(cntx).id));
        
        System.out.println("breakpoint");
    }

    @Override
   protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {        
        select.selectAs("tevent.parent_event_id", "parent_event_id");
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        select.where(Expr.equal("tevent.parent_event_id", getEvent(cntx).id));
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
        try {
            super.saveBean(octopusContext, bean, context);
        } catch (BeanException e) {
            LOGGER.error("Fehler beim speichern der neuen Vorbedingung", e);
        } catch (IOException e) {
            LOGGER.error("Fehler beim speichern der neuen Vorbedingung", e);
        }
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }

    @Override
	public List showList(OctopusContext cntx) throws BeanException, IOException {
    	String val = cntx.getRequestObject().get("searchTask");
    	cntx.setContent("searchTask", val);
		return super.showList(cntx);
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
    
    
    public void passParentEventId(OctopusContext octopusContext) {
    	octopusContext.setContent("parentId", octopusContext.getRequestObject().getRequestParameters().get("parentId"));
    }

	/**
	 * Überprüft ob es noch laufende oder zukünftige Veranstaltungen und fragt ggf. ob diese trotzdem gelöscht werden sollen.
	 */
	@Override
    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		if (selection == null || selection.size() == 0) return count;
		Database database = context.getDatabase();
		Map questions = new HashMap();
		Map questions2 = new HashMap();


		if (!cntx.getRequestObject().getParameterAsBoolean("force-remove-events")) {
    		/*
             * determine events which are not expired and add question
             */
    		Calendar today = Calendar.getInstance();
    		today.set(Calendar.HOUR_OF_DAY, 23);
    		today.set(Calendar.MINUTE, 59);

    		Integer countOfNotExpiredEvents =
                    database.getCount(database.getCount("Event").
                    where(Where.and(
                            Expr.in("pk", selection),
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

    		if (countOfNotExpiredEvents != null && countOfNotExpiredEvents.intValue() > 0) {
    			questions.put("force-remove-events", "Mindestens eine markierte Veranstaltung l\u00e4uft aktuell oder liegt in der Zukunft.");
    			questions2.put("force-remove-events", "Wenn Sie Ihre Auswahl anpassen wollen, brechen Sie bitte das L\u00f6schen ab.");
    		} else {
    		    questions.put("force-remove-events", "Sollen alle markierten Veranstaltungen gel\u00f6scht werden? Diese Aktion kann nicht r\u00fcckg\u00e4ngig gemacht werden.");
    		}
    		cntx.setContent("listquestions", questions);
    		cntx.setContent("listquestions2", questions2);
            return -1;
		}

		/*
		 * remove events
		 */
		Event event = (Event)database.createBean("Event");

		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			event.id = (Integer) iter.next();
			if (removeBean(cntx, event, context)) {
				count++;
			}
		}
		selection.clear();

		try {
			// will commit here so that the following call to
			// PersonDetailWorker.removeAllDeletedPersonsHavingNoEvent()
			// succeeds
			context.commit();
		} catch ( BeanException e ) {
			context.rollBack();
			throw new BeanException( "Die Veranstaltungen konnten nicht gel\u00f6scht werden.", e );
		}

		return count;
	}

	/**
	 * Löscht Veranstaltungen inkl. der zugehörigen Aufgaben und der zugeordneten Gäste.
	 */
	@Override
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		Database database = context.getDatabase();
		OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(cntx);

		Event event = (Event)bean;

		List<OptionalField> optionalFields;
		try {
			optionalFields = optionalFieldsWorker.getOptionalFieldsByEvent(event.id);
			for (OptionalField optionalField : optionalFields) {
				context.execute(
					SQL.Delete(database)
					.from("veraweb.toptional_fields_delegation_content")
					.where(new Where("fk_delegation_field", optionalField.getId(), "="))
				);

				context.execute(
						SQL.Delete(database)
						.from("veraweb.toptional_fields")
						.where(new Where("pk", optionalField.getId(), "="))
					);
			}


		} catch (SQLException e) {
			throw new BeanException("SQL Exception while deleting OptionalFields from Event", e);
		}





		context.execute(
		        SQL.Delete(database)
		        .from("veraweb.ttask")
		        .where(Expr.equal("fk_event", event.id))
		        );
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
 