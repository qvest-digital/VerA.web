/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
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
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // Öffentliche Konstanten
    //
    /** Parameter: Wessen Ereignisse? */
    private final static String PARAM_DOMAIN = "domain";

    /** Parameterwert: beliebige Ereignisse */
	private final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Ereignisse des gleichen Mandanten */
	private final static String PARAM_DOMAIN_VALUE_OU = "ou";

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
        // um, um erst die "ältesten" Veranstaltungen zu sehen,
        // da diese am wahrscheinlichsten für einen Export
        // in Frage kommen.
        String invertOrder = cntx.contentAsString("invertOrder");
        if ("true".equals(invertOrder)) {
        	select.orderBy(Order.desc("datebegin").andAsc("shortname"));
        }
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
	@Override
    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException {
		final Event search = getSearch(octopusContext);

		// WHERE - Filtert das Datenbank Ergebnis anhand der Benutzereingaben.
		WhereList where = new WhereList();

        TcPersonalConfig pConfig = octopusContext.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = octopusContext.contentAsString(PARAM_DOMAIN);
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
		if (search.location != null) {
			where.addAnd(Expr.equal("fk_location", search.location));
		}
		if (search.begin != null) {
			Timestamp nextDay = new Timestamp(search.begin.getTime() + 86400000); // nächster tag
			where.addAnd(Where.and(
					Expr.greaterOrEqual("datebegin", search.begin),
					Expr.less("datebegin", nextDay)));
		}
		if (search.end != null) {
			final String dateClause = "((datebegin IS NOT NULL AND datebegin>=now()::date) OR (dateend IS NOT NULL AND dateend>=now()::date))";
			where.addAnd(new RawClause(dateClause));
		}

        if (where.size() > 0) {
			select.where(where);
		}
	}

	/**
	 * Überprüft ob es noch laufende oder zukünftige Veranstaltungen und fragt ggf. ob diese trotzdem gelöscht werden sollen.
	 */
	@Override
    protected int removeSelection(OctopusContext cntx, List errors, List selectionList, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		if (selectionList == null || selectionList.size() == 0) return count;
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
                            Expr.in("pk", selectionList),
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

			LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
			LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);

    		if (countOfNotExpiredEvents != null && countOfNotExpiredEvents > 0) {
    			questions.put("force-remove-events", languageProvider.getProperty("EVENT_LIST_WARNING_EVENTS_IN_FUTURE"));
    			questions2.put("force-remove-events", languageProvider.getProperty("EVENT_LIST_WARNING_SELECTION_CHANGING"));
    		} else {
    		    questions.put("force-remove-events", languageProvider.getProperty("EVENT_LIST_DELETE_CONFIRMATION_MESSAGE"));
    		}
    		cntx.setContent("listquestions", questions);
				cntx.setContent("listquestions2", questions2);
            return -1;
		}

		/*
		 * remove events
		 */
		Event event = (Event)database.createBean("Event");

		for (Object selection : selectionList) {
			event.id = (Integer) selection;
			if (removeBean(cntx, event, context)) {
				count++;
			}
		}
		selectionList.clear();

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
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext transactionContext) throws BeanException, IOException {
		Database database = transactionContext.getDatabase();
		OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(cntx);

		Event event = (Event)bean;

		List<OptionalField> optionalFields;
		try {
			optionalFields = optionalFieldsWorker.getOptionalFieldsByEvent(event.id);
			for (OptionalField optionalField : optionalFields) {
				transactionContext.execute(
					SQL.Delete(database)
					.from("veraweb.toptional_fields_delegation_content")
					.where(new Where("fk_delegation_field", optionalField.getId(), "="))
				);

				transactionContext.execute(
						SQL.Delete(database)
						.from("veraweb.toptional_fields")
						.where(new Where("pk", optionalField.getId(), "="))
					);

				transactionContext.commit();
			}


		} catch (SQLException e) {
			throw new BeanException("SQL Exception while deleting OptionalFields from Event", e);
		}
		transactionContext.execute(
		        SQL.Delete(database)
		        .from("veraweb.ttask")
		        .where(Expr.equal("fk_event", event.id))
		        );
		transactionContext.execute(
				SQL.Delete( database ).
				from("veraweb.tguest").
				where(Expr.equal("fk_event", event.id)));
		transactionContext.commit();
		boolean result = super.removeBean(cntx, bean, transactionContext);
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
	 * @param octopusContext Octopus-Context
	 * @return Event-Instanz die die aktuelle Suche repräsentiert.
	 * @throws BeanException FIXME
	 */
	public Event getSearch(OctopusContext octopusContext) throws BeanException {
		Event search = null;
		if ("reset".equals(octopusContext.requestAsString("search"))) {
			search = (Event)new RequestVeraWeb(octopusContext).getBean("Event");
			if (octopusContext.requestAsBoolean("current")) {
				long now = System.currentTimeMillis();
				search.end = new Timestamp(now - (now % 86400000) - 86400000);
			}
		} else if ("current".equals(octopusContext.requestAsString("search"))) {
			long now = System.currentTimeMillis();
			search = new Event();
			search.end = new Timestamp(now - (now % 86400000) - 86400000);
		}
		if (search == null)
			search = (Event)octopusContext.sessionAsObject("search" + BEANNAME);
		if (search == null)
			search = new Event();
		octopusContext.setSession("search" + BEANNAME, search);
		return search;
	}
}
