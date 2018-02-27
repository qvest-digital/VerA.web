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
import de.tarent.aa.veraweb.beans.Task;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.EventURLHandler;
import de.tarent.aa.veraweb.utils.MediaRepresentativesUtilities;
import de.tarent.aa.veraweb.utils.OnlineRegistrationHelper;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dieser Octopus-Worker dient der Anzeige und Bearbeitung von Details von
 * Veranstaltungen.
 */
public class EventDetailWorker {

	private static final Integer NUMBER_OPTIONAL_FIELDS = 15;

    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext, Integer)} */
	public static final String INPUT_showDetail[] = { "id", "task", "eventId" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #showDetail(OctopusContext, Integer)} */
	public static final boolean MANDATORY_showDetail[] = { false, false, false };
    public static final String INPUT_downloadImage[] = {};

	public static final String VWOR_ACTIVE = "online-registration.activated";
	/**
	 * Diese Octopus-Aktion lädt eine Veranstaltung und legt sie unter dem Schlüssel "event"
     * in den Octopus-Content. Begleitend werden dort zwei Flags unter den Schlüsseln
     * "event-beginhastime" und "event-endhastime" abgelegt, die kennzeichnen, ob
     * Anfang bzw. Ende neben dem eigentlichen Datum einen Zeitanteil haben.
	 *
	 * @param octopusContext Octopus-Kontext
	 * @param id ID der zu ladenden Veranstaltung; falls <code>null</code> oder ungültig,
     *  so wird nichts geliefert
	 */
	public void showDetail(OctopusContext octopusContext, Integer id, Task task, Integer eventId) throws BeanException, IOException {
        if (eventId == null && !octopusContext.getRequestObject().get("id").toString().equals("")) {
            eventId = Integer.valueOf(octopusContext.getRequestObject().get("id").toString());
        }

		Event event = getEvent(octopusContext, eventId);

        if (event != null) {

			octopusContext.setContent("event", event);
			// OR Control
			if (OnlineRegistrationHelper.isOnlineregActive(octopusContext)) {
                final MediaRepresentativesUtilities mediaRepresentativesUtilities = new MediaRepresentativesUtilities(octopusContext, event);
				mediaRepresentativesUtilities.setUrlForMediaRepresentatives();
				final EventURLHandler eventURLHandler = new EventURLHandler();
                eventURLHandler.setEventUrl(octopusContext, event.hash);
			}
			//
		}
        octopusContext.setContent("isEntityModified", true);
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_saveDetail[] = { "saveevent" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #saveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/**
	 * Diese Octopus-Aktion speichert eine Veranstaltung, sofern ein ebenfalls zu übergebenes Flag gesetzt ist.<br>
	 * Die zu speichernde Veranstaltung wird dem Octopus-Content unter dem Schlüssel "event" entnommen oder dem
	 * Octopus-Request aus den Parametern unterhalb des Schlüssels "event".<br>
	 * Zunächst wird Überprüft, ob es unter dem gleichen Kurznamen bereits eine andere Veranstaltung gibt; falls ja, wird
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
	 * @param octopusContext
	 *          Octopus-Kontext
	 * @param saveevent
	 *          Flag; nur wenn dieses gesetzt ist, passiert tatsächlich etwas
	 */
	public void saveDetail(OctopusContext octopusContext, Boolean saveevent) throws BeanException, IOException {
		if (saveevent == null || !saveevent.booleanValue()) {
            return;
        }

		Request request = new RequestVeraWeb(octopusContext);
		Database database = new DatabaseVeraWeb(octopusContext);
		TransactionContext transactionContext = database.getTransactionContext();

		try
		{
			Event event = (Event) octopusContext.contentAsObject("event");
			if (event == null)
			{
				event = (Event) request.getBean("Event", "event");
				DateHelper.addTimeToDate(event.begin, octopusContext.requestAsString("event-begintime"), event.getErrors());
				DateHelper.addTimeToDate(event.end, octopusContext.requestAsString("event-endtime"), event.getErrors());
			}
			event.orgunit = ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId();

			Event oldEvent = (Event) database.getBean("Event", event.id, transactionContext);

			Map questions = new HashMap();
            checkForDuplicateEvents(octopusContext, database, event, questions);

            /** Gibt an ob der übergebene Ort in die Stammdaten übernommen werden soll. */
			boolean saveLocation = octopusContext.requestAsBoolean("addcity-masterdata").booleanValue();
			octopusContext.setContent("addcity-masterdata", Boolean.valueOf(saveLocation));

			/** Wenn ein Gastgeber angegeben worden ist zu diesem die Personendaten laden. */
			if (event.host != null) {
                getHostPersonDetails(database, transactionContext, event);
			} else {
				event.hostname = null;
			}

			/** Gibt an ob es sich um eine neue Veranstaltung handelt. */
			boolean newEvent = event.id == null;

            /** Gibt an ob es sich um einen neuen oder alten Gastgeber handelt. */
			boolean createHost;
			boolean updateHost;
			boolean removeHost;
			if (newEvent) {
				// Neue Veranstaltung -> Gastgeber anlegen
				removeHost = false;
				updateHost = false;
				createHost = event.host != null;
			} else {
                if (event.host == null) {
                    // Alte Veranstaltung -> Gastgeber entfernen
                    removeHost = database.getCount(
                            database.getCount("Guest").where(Where.and(Expr.equal("fk_event", event.id), Expr.equal("ishost", new Integer(1))))).intValue() != 0;
                    updateHost = false;
                    createHost = false;
                } else {
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

            if (!questions.isEmpty()) {
                octopusContext.setContent("listquestions", questions);
            }
            if (OnlineRegistrationHelper.isOnlineregActive(octopusContext)) {
            	setEventHash(event,oldEvent);
            }

            /** Veranstaltung speichern */
            event.verify(octopusContext);
            if (event.isModified() && event.isCorrect() && questions.isEmpty()) {
                octopusContext.setContent("isEntityModified", true);
            /*
             * modified to support change logging
             * cklein 2008-02-12
             */
            	// Opened Event or not
                setEventType(event, octopusContext);
                // Allowing Press in the Event or not
                setMediaRepresentatives(event, oldEvent);

                // Allow event configrmation via online registration with/without login
                setLoginRequired(octopusContext, event);
                BeanChangeLogger clogger = new BeanChangeLogger( database, transactionContext );
                if (event.id == null) {

                    octopusContext.setContent("countInsert", new Integer(1));
                    database.getNextPk(event, transactionContext);
                    Insert insert = database.getInsert(event);
                    insert.insert("pk", event.id);
                    if (!((PersonalConfigAA) octopusContext.personalConfig()).getGrants().mayReadRemarkFields()) {
                        insert.remove("note");
                    }
                    transactionContext.execute(insert);
                    transactionContext.commit();

                    clogger.logInsert( octopusContext.personalConfig().getLoginname(), event );
                } else {
                    octopusContext.setContent("countUpdate", new Integer(1));
                    Update update = database.getUpdate(event);
                    if (!((PersonalConfigAA) octopusContext.personalConfig()).getGrants().mayReadRemarkFields()) {
                        update.remove("note");
                    }
                    transactionContext.execute(update);
                    transactionContext.commit();

                    clogger.logUpdate( octopusContext.personalConfig().getLoginname(), oldEvent, event );
                }
                if (newEvent) {
                    initOptionalFields(database, transactionContext, event);
                }
                Integer invitationtype = getInvitationType(event);

                // Bug 1601
                // Alt: Veraltete Gastgeber zu Gästen machen
                // Neu: gelöschten Gastgeber aus Veranstaltung entfernen.
                if (removeHost) {
                    handleRemoveHost(octopusContext, database, transactionContext, event);
                }

                if (createHost) {
                    Boolean reserve = Boolean.FALSE;
                    WorkerFactory.getGuestWorker(octopusContext).addGuest(octopusContext, database, transactionContext, event, event.host, null, reserve, invitationtype,
                            Boolean.TRUE);
                } else if (updateHost) {
                    transactionContext.execute(SQL.Update(database).table("veraweb.tguest").update("ishost", new Integer(1)).update("invitationtype", invitationtype)
                            .where(Where.and(Expr.equal("fk_event", event.id), Expr.equal("fk_person", event.host))));
                    transactionContext.commit();

                    // TODO also modifies tguest, full change logging requires
                    // TODO refactor and centralize in GuestDetailWorker
                }

                if (oldEvent != null && !event.invitationtype.equals(oldEvent.invitationtype)) {
                    transactionContext.execute(SQL.Update(database).table("veraweb.tguest").update("invitationtype", event.invitationtype).where(
                            Where.and(Expr.equal("fk_event", event.id), Expr.notEqual("ishost", new Integer(1)))));
                    transactionContext.commit();

                    // TODO also modifies tevent, full change logging requires
                    // TODO refactor and centralize in EventDetailWorker
                }
            } else {
                octopusContext.setContent("isEntityModified", false);
                octopusContext.setStatus("notsaved");
                if (oldEvent != null && oldEvent.mediarepresentatives != null && event.mediarepresentatives != null) {
                	event.mediarepresentatives = oldEvent.mediarepresentatives;
                }
            }
            Boolean isOnlineregActive = Boolean.valueOf(octopusContext.getContextField(VWOR_ACTIVE).toString());
            // OR Control
            if (isOnlineregActive) {
                final EventURLHandler eventURLHandler = new EventURLHandler();
                eventURLHandler.setEventUrl(octopusContext, event.hash);
                final MediaRepresentativesUtilities mediaRepresentativesUtilities = new MediaRepresentativesUtilities(octopusContext, event);
            	mediaRepresentativesUtilities.setUrlForMediaRepresentatives();
            }
            octopusContext.setContent("event", event);
			octopusContext.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
			octopusContext.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));

			transactionContext.commit();
        } catch (BeanException e) {
            transactionContext.rollBack();
            // must report error to user
            throw new BeanException("Die Eventdetails konnten nicht gespeichert werden.", e);
        }
    }

    private void setLoginRequired(OctopusContext cntx, Event event) {
        String requestParameter = (String) cntx.getRequestObject().getRequestParameters().get("event-login_required");
        event.login_required = requestParameter != null && requestParameter.equals("on");
    }

    private Integer getInvitationType(Event event) {
        Integer invitationtype;
        if (event.invitepartner.booleanValue() || event.invitationtype == null) {
            invitationtype = new Integer(EventConstants.TYPE_MITPARTNER);
        } else if (event.invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
            invitationtype = new Integer(EventConstants.TYPE_NURPARTNER);
        } else {
            invitationtype = new Integer(EventConstants.TYPE_OHNEPARTNER);
        }
        return invitationtype;
    }

    private void handleRemoveHost(OctopusContext cntx, Database database, TransactionContext context, Event event)
            throws BeanException, IOException {
        Select select = database.getSelect("Guest").where(
                Where.and(Expr.equal("fk_event", event.id), Expr.equal("ishost", new Integer(1)))
        );
        Guest hostToRemove = (Guest) database.getBean("Guest", select);
        if (hostToRemove != null && hostToRemove.id != null) {
            WorkerFactory.getGuestListWorker(cntx).removeBean(cntx, hostToRemove, context);
        }
    }

    private void initOptionalFields(Database database, TransactionContext transactionContext, Event event) throws BeanException {
        for(int i = 0; i < NUMBER_OPTIONAL_FIELDS; i++) {
            transactionContext.execute(SQL.Insert(database).table("veraweb.toptional_fields").insert("fk_event", event.id).insert("label", ""));
            transactionContext.commit();
        }
    }

    /**
     * Set/unset the event type (aka public event or private event). Status of the checkbox is saved in the DB as
     * string. Later will be possible to handle more than two types of events.
     *
     * @param event The event
     */
    private void setEventType(Event event, OctopusContext cntx) {
        if(event.eventtype != null && event.eventtype.equals("on") && OnlineRegistrationHelper.isOnlineregActive(cntx)) {
            event.eventtype = "Offene Veranstaltung";
        } else {
            event.eventtype = "";
        }
    }

    /**
     * Set/Unset the event flag for Press. Currently (03.12.2014) we store the uuid into that new column
     * @param event The event
     */
    private void setMediaRepresentatives(Event event, Event oldEvent) {
    	if ((oldEvent == null || oldEvent.mediarepresentatives == null) && event.mediarepresentatives != null) {
    		// We generate an UUID and we store it into tevent - column "mediarepresentatives"
    		if (event.mediarepresentatives.equals("on")) {
	    		UUID uuid = UUID.randomUUID();
	    		event.mediarepresentatives = uuid.toString();
    		}
    		else event.mediarepresentatives = null;
    	} else if (oldEvent != null && event!=null) {
    		if (event.mediarepresentatives!=null && event.mediarepresentatives.equals("on")) {
    		 event.mediarepresentatives = oldEvent.mediarepresentatives;
    		}
    		else event.mediarepresentatives = null;
    	} else {
    		event.mediarepresentatives = null;
    	}
    }

    /**
     * New hash for new and edited events
     *
     * @param event New {@link Event}
     * @param oldEvent Old {@link Event}
     */
    private void setEventHash(Event event, Event oldEvent) {
    	if ((oldEvent == null || oldEvent.hash == null) && event.isModified()) {
    		UUID uuid = UUID.randomUUID();
    		event.hash = uuid.toString();
    	} else if (oldEvent != null){
    		event.hash = oldEvent.hash;
    	}
    }

    private void getHostPersonDetails(Database database, TransactionContext context, Event event) throws BeanException, IOException {
        Person person = (Person) database.getBean("Person", database.getSelect("Person").where(Expr.equal("pk", event.host)), context);
        if (person != null) {
            event.hostname = person.getMainLatin().getSaveAs();
            event.setModified(true);
        }
    }

    private void checkForDuplicateEvents(OctopusContext cntx, Database database, Event event, Map questions) throws BeanException, IOException {
        // Test ob bereits eine Veranstaltung mit diesem Namen existiert.
        if (event.shortname != null && event.shortname.length() != 0) {
            WhereList where = new WhereList();
            where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
            where.addAnd(Expr.equal("shortname", event.shortname));
            if (event.id != null) {
                where.addAnd(Expr.notEqual("pk", event.id));
            }

            if (database.getCount(database.getCount("Event").where(where)).intValue() != 0) {
                if (!cntx.requestAsBoolean("event-samename").booleanValue()) {

                    LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
                    LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);


                    questions.put("event-samename", languageProvider.getProperty("EVENT_DETAIL_ALREADY_EXISTS_ONE")
                                + event.shortname + languageProvider.getProperty("EVENT_DETAIL_ALREADY_EXISTS_TWO"));
                } else {
                    //QUICKFIX wenn die Frage samename schon gestellt wurde und user trotzdem speichern will, ist der
                    //event zwar neu, aber nicht mehr modified. Dann wird weiter unten nicht gespeichert.
                    //modified sagt nur aus, ob die letzte site was geaendert hat. Wird eine Rueckfrage gestellt,
                    //ist modified false, obwohl das bean noch neu (nicht persistent) sein koennte.
                    event.setModified(true);
                }
            }
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
		event.orgunit = ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId();
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
