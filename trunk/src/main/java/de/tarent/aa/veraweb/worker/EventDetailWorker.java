/*
 * $Id: EventDetailWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 14.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.EventDoctype;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.Location;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.Request;
import de.tarent.octopus.custom.beans.TransactionContext;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker dient der Anzeige und Bearbeitung von Details von
 * Veranstaltungen.
 */
public class EventDetailWorker {
    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext, Integer)} */
	public static final String INPUT_showDetail[] = { "id" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #showDetail(OctopusContext, Integer)} */
	public static final boolean MANDATORY_showDetail[] = { false };
	/**
	 * Diese Octopus-Aktion lädt eine Veranstaltung und legt sie unter dem Schlüssel "event"
     * in den Octopus-Content. Begleitend werden dort zwei Flags unter den Schlüsseln
     * "event-beginhastime" und "event-endhastime" abgelegt, die kennzeichnen, ob
     * Anfang bzw. Ende neben dem eigentlichen Datum einen Zeitanteil haben. 
	 * 
	 * @param cntx Octopus-Kontext
	 * @param id ID der zu ladenden Veranstaltung; falls <code>null</code> oder ungültig,
     *  so wird nichts geliefert
	 */
	public void showDetail(OctopusContext cntx, Integer id) throws BeanException, IOException {
		Event event = getEvent(cntx, id);
		if (event != null) {
			cntx.setContent("event", event);
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_saveDetail[] = { "saveevent" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #saveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/**
	 * Diese Octopus-Aktion speichert eine Veranstaltung, sofern ein ebenfalls zu übergebenes Flag gesetzt ist.<br>
	 * Die zu speichernde Veranstaltung wird dem Octopus-Content unter dem Schlüssel "event" entnommen oder dem
	 * Octopus-Request aus den Parametern unterhalb des Schlüssels "event".<br>
	 * Zunächst wird überprüft, ob es unter dem gleichen Kurznamen bereits eine andere Veranstaltung gibt; falls ja, wird
	 * eine Nachfrage bezüglich dieses Problems erzeugt, sofern im Octopus-Request nicht ein Flag unter dem Schlüssel
	 * "event-samename" auf <code>true</code> gesetzt vorkommt.<br>
	 * Dann wird ein Flag unter dem Schlüssel "addcity-masterdata" aus dem Octopus-Request gelesen und unter dem gleichen
	 * Schlüssel in den Octopus-Content geschrieben. Dann werden zur Gastgeber-ID dessen Namensdaten in die Veranstaltung
	 * eingetragen.<br>
	 * Falls es Nachfragen gab, werden diese unter "listquestions" in den Octopus-Content eingetragen.<br>
	 * Falls keine Veränderungen an der Veranstaltung vorgenommen worden waren, die Daten ungültig (etwa unvollständig)
	 * sind oder Nachfragen vorliegen, wird "notsaved" als Status gesetzt, ansonsten wird die Veranstaltung gespeichert,
	 * wobei nebenbei im Octopus-Content unter "countInsert" oder "countUpdate" eine 1 gesetzt, gegebenenfalls der
	 * Veranstaltungsort in die Stammdaten übernommen wird, Veranstaltungs-Dokumenttyp-Einträge erzeugt werden und die
	 * Gästeliste passend zu eventuellen Gastgeber- und Einladungsänderungen angepasst wird.<br>
	 * Abschließend werden passend Octopus-Content-Einträge unter "event", "event-beginhastime" und "event-endhastime"
	 * erzeugt.
	 * 
	 * @param cntx
	 *          Octopus-Kontext
	 * @param saveevent
	 *          Flag; nur wenn dieses gesetzt ist, passiert tatsächlich etwas
	 */
	public void saveDetail(OctopusContext cntx, Boolean saveevent) throws BeanException, IOException
	{
		if (saveevent == null || !saveevent.booleanValue())
			return;

		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();

		try
		{
			Event event = (Event) cntx.contentAsObject("event");
			if (event == null)
			{
				event = (Event) request.getBean("Event", "event");
				DateHelper.addTimeToDate(event.begin, cntx.requestAsString("event-begintime"), event.getErrors());
				DateHelper.addTimeToDate(event.end, cntx.requestAsString("event-endtime"), event.getErrors());
			}
			event.orgunit = ((PersonalConfigAA) cntx.configImpl()).getOrgUnitId();

			Event oldEvent = (Event) database.getBean("Event", event.id, context);

			List errors = new ArrayList();
			Map questions = new HashMap();

			// Test ob bereits eine Veranstaltung mit diesem Namen existiert.
			if (event.shortname != null && event.shortname.length() != 0)
			{
				WhereList where = new WhereList();
				where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.configImpl()).getOrgUnitId()));
				where.addAnd(Expr.equal("shortname", event.shortname));
				if (event.id != null)
				{
					where.addAnd(Expr.notEqual("pk", event.id));
				}

				if (database.getCount(database.getCount("Event").where(where)).intValue() != 0)
				{
					if (!cntx.requestAsBoolean("event-samename").booleanValue())
					{
						questions.put("event-samename", "Es existiert bereits eine Verstaltung mit diesem Namen '" + event.shortname
							+ "', trotzdem speichern?");
					}
					else
					{
						//QUICKFIX wenn die Frage samename schon gestellt wurde und user trotzdem speichern will, ist der
						//event zwar neu, aber nicht mehr modified. Dann wird weiter unten nicht gespeichert.
						//modified sagt nur aus, ob die letzte site was geaendert hat. Wird eine Rueckfrage gestellt,
						//ist modified false, obwohl das bean noch neu (nicht persistent) sein koennte.
						event.setModified(true);
					}
				}
			}

			/** Gibt an ob der übergebene Ort in die Stammdaten übernommen werden soll. */
			boolean saveLocation = cntx.requestAsBoolean("addcity-masterdata").booleanValue();
			cntx.setContent("addcity-masterdata", Boolean.valueOf(saveLocation));

			/** Wenn ein Gastgeber angegeben worden ist zu diesem die Personendaten laden. */
			if (event.host != null)
			{
				Person person = (Person) database.getBean("Person", database.getSelect("Person").where(Expr.equal("pk", event.host)), context);
				if (person != null)
				{
					event.hostname = person.getMainLatin().getSaveAs();
					event.setModified(true);
				}
			} else
			{
				event.hostname = null;
			}

			/** Gibt an ob es sich um eine neue Veranstaltung handelt. */
			boolean newEvent = event.id == null;
			/** Gibt an ob es sich um einen neuen oder alten Gastgeber handelt. */
			boolean createHost;
			boolean updateHost;
			boolean removeHost;
			if (newEvent)
			{
				// Neue Veranstaltung -> Gastgeber anlegen
				removeHost = false;
				updateHost = false;
				createHost = event.host != null;
			} else
			{
				if (event.host == null)
				{
					// Alte Veranstaltung -> Gastgeber entfernen
					removeHost = database.getCount(
						database.getCount("Guest").where(Where.and(Expr.equal("fk_event", event.id), Expr.equal("ishost", new Integer(1))))).intValue() != 0;
					updateHost = false;
					createHost = false;
				} else
				{
					// Alte Veranstaltung -> Gastgeber hinzufügen
					removeHost = database.getCount(
						database.getCount("Guest").where(
							Where.and(Where.and(Expr.equal("fk_event", event.id), Expr.notEqual("fk_person", event.host)), Expr.equal("ishost",
								new Integer(1))))).intValue() != 0;
					updateHost = database.getCount(
						database.getCount("Guest").where(Where.and(Expr.equal("fk_event", event.id), Expr.equal("fk_person", event.host)))).intValue() != 0;
					createHost = !updateHost;
				}
			}

			if (!questions.isEmpty())
			{
				cntx.setContent("listquestions", questions);
			}

			/** Veranstaltung speichern */
			if (event.isModified() && event.isCorrect() && questions.isEmpty())
			{
				if (event.id == null)
				{
					cntx.setContent("countInsert", new Integer(1));
					database.getNextPk(event, context);
					Insert insert = database.getInsert(event);
					insert.insert("pk", event.id);
					if (!((PersonalConfigAA) cntx.configImpl()).getGrants().mayReadRemarkFields())
					{
						insert.remove("note");
					}
					context.execute(insert);
				} else
				{
					cntx.setContent("countUpdate", new Integer(1));
					Update update = database.getUpdate(event);
					if (!((PersonalConfigAA) cntx.configImpl()).getGrants().mayReadRemarkFields())
					{
						update.remove("note");
					}
					context.execute(update);
				}

				if (saveLocation)
				{
					Location location = new Location();
					location.name = event.location;
					location.setModified(true);
					try
					{
						// Testet hier ob Location geschrieben werden darf, um
						// bei Änderungen später dies Zentral ändern zu können.
						location.checkWrite(cntx);
					} catch (BeanException e)
					{
						saveLocation = false;
					}
					if (saveLocation)
					{
						WorkerFactory.getLocationWorker(cntx).insertBean(cntx, errors, location);
					}
				}

				if (newEvent)
				{
					List list = database.getBeanList("Doctype", database.getSelect("Doctype").where(
						Where
							.or(Expr.equal("flags", new Integer(Doctype.FLAG_IS_STANDARD)), Expr.equal("flags", new Integer(Doctype.FLAG_NO_FREITEXT)))),
						context);
					EventDoctype eventDoctype = new EventDoctype();
					eventDoctype.event = event.id;
					for (Iterator it = list.iterator(); it.hasNext();)
					{
						Doctype doctype = (Doctype) it.next();
						eventDoctype.doctype = doctype.id;
						if (eventDoctype.event != null && eventDoctype.doctype != null)
						{
							database.saveBean(eventDoctype, context, false);
						}
					}
				}

				Integer invitationtype;
				if (event.invitepartner.booleanValue() || event.invitationtype == null)
				{
					invitationtype = new Integer(EventConstants.TYPE_MITPARTNER);
				} else if (event.invitationtype.intValue() == EventConstants.TYPE_NURPARTNER)
				{
					invitationtype = new Integer(EventConstants.TYPE_NURPARTNER);
				} else
				{
					invitationtype = new Integer(EventConstants.TYPE_OHNEPARTNER);
				}

				// Bug 1601
				// Alt: Veraltete Gastgeber zu Gästen machen
				// Neu: gelöschten Gastgeber aus Veranstaltung entfernen.
				if (removeHost)
				{
					Select sel = database.getSelect("Guest").where(Where.and(Expr.equal("fk_event", event.id), Expr.equal("ishost", new Integer(1))));
					Guest hostToRemove = (Guest) database.getBean("Guest", sel);
					if (hostToRemove != null && hostToRemove.id != null)
					{
						WorkerFactory.getGuestListWorker(cntx).removeBean(cntx, hostToRemove);
					}

					// context.execute(SQL.Update().
					// table("veraweb.tguest").
					// update("ishost", Boolean.FALSE).
					// where(Where.and(
					// Expr.equal("fk_event", event.id),
					// Expr.equal("ishost", new Integer(1)))));
				}
				if (createHost)
				{
					Boolean reserve = Boolean.FALSE;
					WorkerFactory.getGuestWorker(cntx).addGuest(cntx, database, context, event, event.host, null, reserve, invitationtype,
						Boolean.TRUE);
				} else if (updateHost)
				{
					context.execute(SQL.Update().table("veraweb.tguest").update("ishost", new Integer(1)).update("invitationtype", invitationtype)
						.where(Where.and(Expr.equal("fk_event", event.id), Expr.equal("fk_person", event.host))));
				}

				if (oldEvent != null && !event.invitationtype.equals(oldEvent.invitationtype))
				{
					context.execute(SQL.Update().table("veraweb.tguest").update("invitationtype", event.invitationtype).where(
						Where.and(Expr.equal("fk_event", event.id), Expr.notEqual("ishost", new Integer(1)))));
				}
			} else
			{
				cntx.setStatus("notsaved");
			}
			cntx.setContent("event", event);
			cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
			cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));

			context.commit();
		} finally
		{
			context.rollBack();
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveTemp(OctopusContext)} */
	public static final String INPUT_saveTemp[] = {};
	/**
	 * Diese Octopus-Aktion holt eine Veranstaltung unter "event" aus dem Octopus-Request und legt sie unter "event" in
	 * den Octopus-Content und unter "eventtemp" in die Session.
	 * 
	 * @param cntx
	 *          Octopus-Kontext
	 */
	public void saveTemp(OctopusContext cntx) throws BeanException {
		Request request = new RequestVeraWeb(cntx);
		Event event = (Event)request.getBean("Event", "event");
		DateHelper.addTimeToDate(event.begin, cntx.requestAsString("event-begintime"), event.getErrors());
		DateHelper.addTimeToDate(event.end, cntx.requestAsString("event-endtime"), event.getErrors());
		event.orgunit = ((PersonalConfigAA)cntx.configImpl()).getOrgUnitId();
		cntx.setSession("eventtemp", event);
		cntx.setContent("event", event);
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #loadTemp(OctopusContext)} */
    public static final String INPUT_loadTemp[] = {};
    /**
     * Diese Octopus-Aktion holt eine Veranstaltung unter "eventtemp" aus der Session und
     * legt sie unter "event" und Hilfsflags unter "event-beginhastime" und "event-endhastime"
     * im Octopus-Content ab.
     * 
     * @param cntx Octopus-Kontext
     */
	public void loadTemp(OctopusContext cntx) {
		Event event = (Event)cntx.sessionAsObject("eventtemp");
		cntx.setContent("event", event);
		cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
		cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #setHost(OctopusContext)} */
    public static final String INPUT_setHost[] = {};
    /**
     * Diese Octopus-Aktion holt eine Veranstaltung unter "eventtemp" aus der Session
     * und die ID eines Gastgebers unter "hostid" aus dem Octopus-Request. Wenn diese
     * ID nicht <code>null</code> ist, wird die zugehörige Person der Veranstaltung
     * (im Speicher, nicht in der DB) als Gastgeber zugeordnet und ein Flag unter
     * "saveevent" im Octopus-Content gesetzt.
     * 
     * @param cntx Octopus-Kontext
     */
	public void setHost(OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		Event event = (Event)cntx.sessionAsObject("eventtemp");
		Integer hostid = null;
		try {
			hostid = new Integer(cntx.requestAsString("hostid"));
		} catch (NumberFormatException e) {
		}
		if (hostid != null) {
			event.host = hostid;
			Person person = (Person)database.getBean("Person", hostid);
			if (person != null) {
				event.hostname = person.getMainLatin().getSaveAs();				
			}
			event.setModified(true);
			cntx.setContent("saveevent", Boolean.TRUE);
		}
	}

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode holt eine Veranstaltung zu der übergebenen ID aus der Datenbank,
     * gibt diese zurück und setzt als Nebeneffekt im Octopus-Content des übergebenen
     * Octopus-Kontexts unter den Schlüsseln "event-beginhastime" und "event-endhastime"
     * Flags, die anzeigen, ob Start- und Ende-Eintrag jeweils einen gültigen Zeitanteil
     * besitzen.
     * 
     * @param cntx Octopus-Kontext, in dem Flags gesetzt werden.
     * @param id Veranstaltungs-ID
     * @return eingelesene Veranstaltung
     */
	static public Event getEvent(OctopusContext cntx, Integer id) throws BeanException, IOException {
		if (id == null) return null;
		
		Database database = new DatabaseVeraWeb(cntx);
		Event event = (Event)database.getBean("Event", id);

        if (event != null) {
            cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
            cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
        }
		return event;
	}
}
