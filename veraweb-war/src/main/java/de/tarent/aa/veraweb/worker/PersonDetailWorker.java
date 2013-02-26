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
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.beans.facade.PersonDoctypeFacade;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
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

/**
 * Octopus-Worker der Aktionen zur Detailansicht von Personen bereitstellt,
 * wie das {@link #showDetail(OctopusContext, Integer, Person) Laden} oder das
 * {@link #saveDetail(OctopusContext, Person) Speichern} von Daten.
 * 
 * @author Christoph
 */
public class PersonDetailWorker implements PersonConstants {
    /** Logger dieser Klasse */
	private final static Logger logger = Logger.getLogger(PersonDetailWorker.class);

    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Person)} */
	public static final String INPUT_showDetail[] = { "id", "person" };
    /** Ausgabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Person)} */
	public static final String OUTPUT_showDetail = "person";
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Person)} */
	public static final boolean MANDATORY_showDetail[] = { false, false };
	/**
	 * Diese Octopus Aktion nimmt die �bergebene Person oder die Person zu der
     * �bergebenen ID oder die Person zu der ID unter "person-id" in der Session
     * und gibt sie zur�ck. Als Seiteneffekt wird (wenn die Person nicht null ist)
     * im Octopus-Content unter "person-diplodatetime" ein Flag, ob das
     * Akkreditierungsdatum einen Zeitanteil enth�lt, und die �bergebene ID (falls
     * die Person durch sie identifiziert wurde) in der Session unter "person-id"
     * abgelegt.
	 * 
	 * @param cntx Octopus-Kontext
	 * @param id ID der Person
	 * @param person Person
	 */
	public Person showDetail(OctopusContext cntx, Integer id, Person person) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		if (person == null) {
			id = getPersonId(cntx, id, false);
			person = (Person)database.getBean("Person", id);
		}
		if (person != null) {
			cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));
		}

		Map map = (Map)cntx.sessionAsObject( "statistikSettings" );
		if ( map == null )
		{
			map = new HashMap();
			cntx.setSession( "statistikSettings", map );
		}

		/*
		 * added for support of direct search result list navigation, see below
		 * cklein 2008-03-12
		 */
		this.restoreNavigation( cntx, person, database );
		
		/*
		 * modified to support a direct statistics access from the detail view as per the change request for version 1.2.0
		 * cklein
		 * 2008-02-21
		 */
		map.put( "statistik", "EventsGroupByGuest" );
		Date d = new Date( System.currentTimeMillis() );
		if ( person != null && person.created != null )
		{
			d = new Date( person.created.getTime() );
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime( d );
		map.put( "begin", "01." + ( cal.get( Calendar.MONTH ) + 1 ) + "." + cal.get( Calendar.YEAR ) );
// previously disabled due to the fact that the stats reach "out to near infinite time" in the future
//		cal.setTime( new Date() );
//		map.put( "end", cal.get( Calendar.DAY_OF_MONTH ) + "." + ( cal.get( Calendar.MONTH ) + 1 ) + "." + cal.get( Calendar.YEAR ) );

		return person;
	}

	protected void restoreNavigation( OctopusContext cntx, Person person, Database database ) throws BeanException, IOException
	{
		String action = cntx.requestAsString( "action" );
		Integer personId = cntx.requestAsInteger( "id" );
		
		// now get the proper select from the workers based on 
		// the optionally defined action parameter
		Person sample = new Person();
		Select select = database.getSelectIds( sample );
		if ( "duplicateSearch".equals( action ) )
		{
			//add the action and personId once again to the context
			cntx.setContent("action", action);
			cntx.setContent("id", personId);
			
			// must navigate through all persons matching duplicate search query filter
			PersonDuplicateSearchWorker w = WorkerFactory.getPersonDuplicateSearchWorker( cntx );
			// replaces the original select as it is very similar
			select = w.getSelect( database );
			w.extendWhere( cntx, select );
			select.clearColumnSelection();
			select.selectAs( "tperson.pk", "id" );
			select.select( "tperson.lastname_a_e1" );
			select.select( "tperson.firstname_a_e1" );
			select.orderBy( Order.asc( "tperson.lastname_a_e1" ).andAsc( "tperson.firstname_a_e1" ) );
		}
		else if ( action == null || action.length() == 0 )
		{
			// standard person list
			// must navigate through all persons matching current search query filter
			PersonListWorker w = WorkerFactory.getPersonListWorker( cntx );
			PersonSearch s = w.getSearch( cntx );
			w.extendWhere( cntx, select );

			// part replication of the worker's behaviour
			select.setDistinct( s.categoriesSelection != null || s.categorie2 != null );
			select.select( "tperson.lastname_a_e1" );
			select.select( "tperson.firstname_a_e1" );
			select.orderBy( Order.asc( "tperson.lastname_a_e1" ).andAsc( "tperson.firstname_a_e1" ) );
		}

		try
		{
			Map< String, Map< String, Object > > navigation = new HashMap< String, Map< String, Object > >();
			Map< String, Object > entry = null;

			// setup current
			entry = new HashMap< String, Object >();
			entry.put( "person", person );
			navigation.put( "current", entry );

			ResultList result = new ResultList( select.executeSelect( database ).resultSet() );

			int size = result.size();
			int i;
			for ( i = 0; i < size; i++ )
			{
				Map cur = ( Map ) result.get( i );
				if ( cur.get( "id" ).equals( personId ) )
				{
					break;
				}
			}
			Map first = null;
			Map previous = null;
			if ( i > 0 )
			{
				first = copyPersonMap( ( Map ) result.get( 0 ) );
				previous = copyPersonMap( ( Map ) result.get( i - 1 ) );
			}

			Map fentry = null;
			if ( first != null )
			{
				fentry = new HashMap< String, Object >();
				fentry.put( "person", first );
			}
			navigation.put( "first", fentry );

			Map pentry = null;
			if ( previous != null )
			{
				pentry = new HashMap< String, Object >();
				pentry.put( "person", previous );
			}
			navigation.put( "previous", pentry );

			Map next = null;
			Map last = null;
			if ( i < size - 1 )
			{
				next = copyPersonMap( ( Map ) result.get( i + 1 ) );
				last = copyPersonMap( ( Map ) result.get( size - 1 ) );
			}

			Map nentry = null;
			if ( next != null )
			{
				nentry = new HashMap< String, Object >();
				nentry.put( "person", next );
			}
			navigation.put( "next", nentry );

			Map lentry = null;
			if ( last != null )
			{
				lentry = new HashMap< String, Object >();
				lentry.put( "person", last );
			}
			navigation.put( "last", lentry );

			Map< String, Object > meta = new HashMap< String, Object >();
			meta.put( "action", action );
			meta.put( "offset", new Integer( i + 1 ) );
			meta.put( "count", new Integer( size ) );
			navigation.put( "meta", meta );
			cntx.setContent( "navigation", navigation );
		}
		catch ( SQLException e )
		{
			throw new BeanException( "Unexpected exception occurred: ", e );
		}
	}

	private Map< String, Object > copyPersonMap( Map< String, Object > personMap )
	{
		Map< String, Object > result = new HashMap< String, Object >();
		for ( String key : personMap.keySet() )
		{
			result.put( key, personMap.get( key ) );
		}
		return result;
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #copyPerson(OctopusContext, Integer)} */
	public static final String INPUT_copyPerson[] = { "id" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #copyPerson(OctopusContext, Integer)} */
	public static final boolean MANDATORY_copyPerson[] = { false };
	/**
	 * Kopiert die Personendaten, die der �bergebenen ID zugeordnet sind oder
     * sich im Octopus-Request unterhalb des Schl�ssels "person" befinden, 
	 * und stellt diese unter dem Schl�ssel "person" und ein Flag, ob das
     * Akkreditierungsdatum einen Zeitanteil enth�lt, unter "person-diplodatetime" 
     * in den Octopus-Content.<br>
     * In der aktuellen Implementierung werden dabei neben den Teilpersonen- und
     * Adressangaben in den Zusatzzeichens�tzen lediglich folgende Felder
     * auf <code>null</code> gesetzt:
	 * {@link Person#id},
	 * {@link Person#expire},
	 * {@link Person#created},
	 * {@link Person#createdby},
	 * {@link Person#changed},
	 * {@link Person#changedby} und
	 * {@link Person#importsource}.
	 * 
	 * @param cntx Octopus-Kontext
	 * @param id Personen-ID
	 */
	public void copyPerson(OctopusContext cntx, Integer id) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		Person person;
		if (id != null) {
			person = showDetail(cntx, id, null);
		} else {
			Request request = new RequestVeraWeb(cntx);
			person = (Person)request.getBean("Person", "person");
			DateHelper.addTimeToDate(person.diplodate_a_e1, cntx.requestAsString("person-diplotime_a_e1"), person.getErrors());
		}
		
		if (person == null) {
			person = new Person();
		} else {
			person.id = null;
			person.expire = null;
			person.deleted = PersonConstants.DELETED_FALSE;
			person.created = null;
			person.createdby = null;
			person.changed = null;
			person.changedby = null;
			person.importsource = null;
			AddressHelper.clearAddressData(person.getMainExtra1());
			AddressHelper.clearAddressData(person.getMainExtra2());
			AddressHelper.clearAddressData(person.getPartnerExtra1());
			AddressHelper.clearAddressData(person.getPartnerExtra2());
			AddressHelper.clearAddressData(person.getBusinessExtra1());
			AddressHelper.clearAddressData(person.getBusinessExtra2());
			AddressHelper.clearAddressData(person.getPrivateExtra1());
			AddressHelper.clearAddressData(person.getPrivateExtra2());
			AddressHelper.clearAddressData(person.getOtherExtra1());
			AddressHelper.clearAddressData(person.getOtherExtra2());
			AddressHelper.checkPersonSalutation(person, database, database.getTransactionContext());
		}

		cntx.setContent("person", person);
		cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));
		cntx.setContent("originalPersonId", cntx.requestAsInteger("originalPersonId"));

		/*
		 * added for support of direct search result list navigation, see below
		 * cklein 2008-03-12
		 */
		this.restoreNavigation( cntx, person, database );
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #showTestPerson(OctopusContext)} */
	public static final String INPUT_showTestPerson[] = {};
	/**
	 * Erstellt eine Test-Person und stellt diese unter dem Schl�ssel "person"
     * und ein Flag, ob das Akkreditierungsdatum einen Zeitanteil enth�lt, unter
     * "person-diplodatetime" in den Octopus-Content.<br>
     * Anhand des Werts des Octopus-Request-Parameters zum Schl�ssel "partner"
     * werden nur Daten zur Partnerperson ("only"), nur Daten zur Hauptperson
     * ("without") oder zu beiden (sonst) erzeugt.
	 * 
	 * @param cntx Octopus-Kontext
	 */
	public void showTestPerson(OctopusContext cntx) throws BeanException {
		String partner = cntx.requestAsString("partner");
		Person person = getTestPerson(partner);
		cntx.setContent("person", person);
		cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #getDoctype(OctopusContext)} */
	public static final String INPUT_getDoctype[] = {};
	/**
	 * Holt den Personen-Dokumenttyp zum eingestellten Label-Dokumenttyp. Die
     * Person wird hierzu im Octopus-Content unter "person" und die ID des
     * Label-Dokumenttyps in der Konfiguration unter "freitextfeld" erwartet,
     * und der Dokumenttyp wird im Octopus-Content unter "doctype" abgelegt.<br>
	 * Wenn zu dieser Person noch kein entsprechender Eintrag existiert
	 * wird die einfache Form eines Dokumenttypens zur�ckgegeben.
	 * 
	 * @param cntx Octopus-Kontext
	 */
	public void getDoctype(OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		Person person = (Person)cntx.contentAsObject("person");
		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		
		PersonDoctype personDoctype = null;
		if (!(person == null || person.id == null || freitextfeld == null)) {
			personDoctype = (PersonDoctype)
					database.getBean("PersonDoctype",
					database.getSelect("PersonDoctype").
					join("veraweb.tdoctype", "fk_doctype", "tdoctype.pk").
					selectAs("tdoctype.pk", "doctype").
					selectAs("tdoctype.pk", "doctypeId").
					selectAs("tdoctype.addresstype", "doctypeAddresstype").
					selectAs("tdoctype.locale", "doctypeLocale").
					selectAs("tdoctype.docname", "name").
					where(Where.and(
							Expr.equal("fk_person", person.id),
							Expr.equal("fk_doctype", freitextfeld))));
		}
		if (personDoctype != null) {
			cntx.setContent("doctype", personDoctype);
		} else if (freitextfeld != null) {
			Doctype doctype = (Doctype)
					database.getBean("Doctype",
					database.getSelect("Doctype").
					where(Expr.equal("pk", freitextfeld)));
			cntx.setContent("doctype", doctype);
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #createExport(OctopusContext)} */
	public static final String INPUT_createExport[] = {};
	/**
	 * Erstellt ein Personen-Etikett-Label, um damit in einer Textverarbeitung
	 * leicht einen Brief zu erstellen. Die Person wird hierzu im Octopus-Content
     * unter "person", ein ({@link PersonDoctype Personen-}){@link Doctype Dokumenttyp}
     * unter "doctype" und die ID des Label-Dokumenttyps (f�r den Fall, dass der
     * Dokumenttyp nicht personalisiert wurde) in der Konfiguration unter "freitextfeld"
     * erwartet, und das Erzeugnis wird im Octopus-Content unter "personExport" abgelegt.
	 * 
	 * @param cntx Octopus-Kontext
	 */
	public void createExport(OctopusContext cntx) throws BeanException, IOException {
		Person person = (Person)cntx.contentAsObject("person");
		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		
		Integer addresstype = null, locale = null;
		Object doctype = cntx.contentAsObject("doctype");
		if (doctype == null) {
			return;
		} else if (doctype instanceof Doctype) {
			addresstype = ((Doctype)doctype).addresstype;
			locale = ((Doctype)doctype).locale;
		} else if (doctype instanceof PersonDoctype) {
			addresstype = ((PersonDoctype)doctype).addresstype;
			locale = ((PersonDoctype)doctype).locale;
		}
		
		if (person != null) {
			String nl = "\n";
			StringBuffer buffer = new StringBuffer();
			PersonDoctypeFacade helper = new PersonDoctypeFacade(cntx, person);
			PersonAddressFacade facade = person.getAddressFacade(addresstype, locale);
			
			// freitextfelder und verbinder
			if (doctype instanceof PersonDoctype) {
				if ( ((PersonDoctype)doctype).textfield != null && ((PersonDoctype)doctype).textfield.length() > 0 )
					buffer.append(((PersonDoctype)doctype).textfield).append(nl);
				if ( ((PersonDoctype)doctype).textfieldJoin != null && ((PersonDoctype)doctype).textfieldJoin.length() > 0 )
					buffer.append(((PersonDoctype)doctype).textfieldJoin).append(nl);
				if ( ((PersonDoctype)doctype).textfieldPartner != null && ((PersonDoctype)doctype).textfieldPartner.length() > 0 )
					buffer.append(((PersonDoctype)doctype).textfieldPartner).append(nl).append(nl);
			} else {
				buffer.append(helper.getFreitext(freitextfeld, addresstype, locale, true)).append(nl);
				buffer.append(helper.getFreitextVerbinder(freitextfeld, addresstype, locale)).append(nl);
				buffer.append(helper.getFreitext(freitextfeld, addresstype, locale, false)).append(nl).append(nl);
			}
			
			// funktion, firma und stra�e
			if (facade.getFunction() != null && facade.getFunction().length() != 0)
				buffer.append(facade.getFunction()).append(nl);
			if (facade.getCompany() != null && facade.getCompany().length() != 0)
				buffer.append(facade.getCompany()).append(nl);
			if (facade.getStreet() != null && facade.getStreet().length() != 0)
				buffer.append(facade.getStreet()).append(nl);
			
			// plz ort
			if (facade.getZipCode() != null)
				buffer.append(facade.getZipCode());
			if (!(
					facade.getZipCode() == null ||
					facade.getZipCode().length() == 0 ||
					facade.getCity() == null ||
					facade.getCity().length() == 0))
				buffer.append(' ');
			if (facade.getCity() != null)
				buffer.append(facade.getCity());
			buffer.append(nl);
			
			// plz postfach
			if (facade.getPOBoxZipCode() != null)
				buffer.append(facade.getPOBoxZipCode());
			if (!(
					facade.getPOBoxZipCode() == null ||
					facade.getPOBoxZipCode().length() == 0 ||
					facade.getPOBox() == null ||
					facade.getPOBox().length() == 0))
				buffer.append(' ');
			if (facade.getPOBox() != null)
				buffer.append(facade.getPOBox());
			buffer.append(nl);
			
			// land
			if (facade.getCountry() != null && facade.getCountry().length() != 0)
				buffer.append(facade.getCountry()).append(nl);
			
			// adresszusatz 1 und 2
			if (facade.getSuffix1() != null && facade.getSuffix1().length() != 0)
				buffer.append(facade.getSuffix1()).append(nl);
			if (facade.getSuffix2() != null && facade.getSuffix2().length() != 0)
				buffer.append(facade.getSuffix2());
			
			cntx.setContent("personExport", buffer.toString());
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #prepareSaveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_prepareSaveDetail[] = { "saveperson" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #prepareSaveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_prepareSaveDetail[] = { false };
	/**
     * Diese Octopus-Aktion testet das �bergebene Flag; falls es gesetzt
     * ist, wird der Status "saveperson" gesetzt.
	 * 
	 * @param cntx Octopus-Kontext
	 */
	public void prepareSaveDetail(OctopusContext cntx, Boolean saveperson) {
		if (saveperson != null && saveperson.booleanValue()) {
			cntx.setStatus("saveperson");
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext, Person)} */
	public static final String INPUT_saveDetail[] = { "person" };
    /** Ausgabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext, Person)} */
	public static final String OUTPUT_saveDetail = "person";
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #saveDetail(OctopusContext, Person)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/**
     * Diese Octopus-Aktions speichert die �bergebenen Person oder die Person
     * aus dem Octopus-Request unterhalb des Schl�ssels "person" in der Datenbank.
     * Hierbei werden gegebenenfalls �nderungen im Latin-Zeichensatz in die anderen
     * Zeichens�tze �bertragen.<br>
     * Die gespeicherte Person wird zur�ckgegeben, "countInsert" oder "countUpdate"
     * im Octopus-Content wird mit 1 und "person-diplodatetime" mit einem Flag, ob
     * das Akkreditierungsdatum einen Zeitanteil hat, belegt, und die Werte unter
     * "personTab", "personMemberTab", "personAddresstypeTab" und "personLocaleTab"
     * im Octopus-Request werden unter den gleichen Schl�sseln in den -Content
     * kopiert.
	 * 
	 * @param cntx Octopus-Kontext
     * @return die abgespeicherte Person
	 */
	public Person saveDetail(OctopusContext cntx, Person person) throws BeanException, IOException {
		cntx.setContent("personTab", cntx.requestAsInteger("personTab"));
		cntx.setContent("personMemberTab", cntx.requestAsInteger("personMemberTab"));
		cntx.setContent("personAddresstypeTab", cntx.requestAsInteger("personAddresstypeTab"));
		cntx.setContent("personLocaleTab", cntx.requestAsInteger("personLocaleTab"));

		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();

		Integer originalPersonId = cntx.requestAsInteger("originalPersonId");

		try {
			if (person == null) {
				Request request = new RequestVeraWeb(cntx); 
				person = (Person)request.getBean("Person", "person");
			}

			/* fix for bug 1013
			 * cklein 2008-03-12
			 */
	        person.verify();
			if ( ! person.isCorrect() )
			{
				cntx.setStatus("notcorrect");

				// is this a new record?
				if ( person.id == null )
				{
					/*
					 * 2009-06-08 cklein
					 * fixing issue with new persons losing all state and data
					 * when entering an invalid date and trying to store the person
					 * part of fix to issue #1529, as it first showed up when testing
					 * the fixes to that issue
					 */
					// we transfer the errors from the
					// person to the template parameter newPersonErrors
					cntx.setContent( "newPersonErrors", person.getErrors() );
				}

				return person;
			}

			if (cntx.requestAsBoolean("forcedupcheck").booleanValue())
			{
				return person;
			}

			/* person was copied
			 * fix for bug 1011
			 * cklein 2008-03-12
			 */
			if ( originalPersonId != null && originalPersonId > 0 )
			{
				person.setModified( true );
			}

			/*
			 * added support for workarea assignment
			 * 
			 * cklein
			 * 2008-02-20
			 */
			person.workarea = cntx.requestAsInteger( "workarea-id" );

			Person personOld = null;
			if (person != null && person.id != null) {
				personOld = (Person)database.getBean("Person", person.id, context);
			}
			if (person == null) {
				return null;
			}

			DateHelper.addTimeToDate(person.diplodate_a_e1, cntx.requestAsString("person-diplotime_a_e1"), person.getErrors());
			person.orgunit = ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId();
			person.updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
			AddressHelper.checkPersonSalutation(person, database, context);

			// Updatet das Gueltigkeitsdatum automatisch auf "in 3 Jahre"
			// wenn dieses nicht ver�ndert wurde.
			if (person.expire != null && personOld != null && personOld.expire != null) {
				Calendar e1 = Calendar.getInstance();
				Calendar e2 = Calendar.getInstance();
				Calendar ty = Calendar.getInstance();
				ty.add(Calendar.YEAR, 3);
				
				e1.setTimeInMillis(person.expire.getTime());
				e2.setTimeInMillis(personOld.expire.getTime());
				
				boolean notModified = 
					e1.get(Calendar.YEAR) == e2.get(Calendar.YEAR) &&
					e1.get(Calendar.MONTH) == e2.get(Calendar.MONTH) &&
					e1.get(Calendar.DAY_OF_MONTH) == e2.get(Calendar.DAY_OF_MONTH);
				
				if (notModified && person.expire.getTime() < ty.getTimeInMillis()) {
					person.expire = new Timestamp(ty.getTimeInMillis());
					person.setModified(true);
				}
			}

			// must reverify due to above changes
	        person.verify();
			if (person.isModified() && person.isCorrect()) {
		        AddressHelper.copyAddressData(cntx, person, personOld);
				
				/*
				 * modified to support change logging
				 * cklein 2008-02-12
				 */
		        BeanChangeLogger clogger = new BeanChangeLogger( database, context );
				if (person.id == null) {
					cntx.setContent("countInsert", new Integer(1));
					database.getNextPk(person, context);
					Insert insert = database.getInsert(person);
					insert.insert("pk", person.id);
					if (!((PersonalConfigAA)cntx.personalConfig()).getGrants().mayReadRemarkFields()) {
						insert.remove("note_a_e1");
						insert.remove("note_b_e1");
						insert.remove("notehost_a_e1");
						insert.remove("notehost_b_e1");
						insert.remove("noteorga_a_e1");
						insert.remove("noteorga_b_e1");
					}
					context.execute(insert);

					//Bug 1592 Wenn die person kopiert wurde, dann die Kategorien der
					//original Person an neue Person kopieren
					if (originalPersonId != null && originalPersonId.intValue() != 0)
					{
						copyCategories(originalPersonId, person.id,  database,  context);
					}

					clogger.logInsert( cntx.personalConfig().getLoginname(), person );	
				} else {
					cntx.setContent("countUpdate", new Integer(1));
					Update update = database.getUpdate(person);
					if (!((PersonalConfigAA)cntx.personalConfig()).getGrants().mayReadRemarkFields()) {
						update.remove("note_a_e1");
						update.remove("note_b_e1");
						update.remove("notehost_a_e1");
						update.remove("notehost_b_e1");
						update.remove("noteorga_a_e1");
						update.remove("noteorga_b_e1");
					}
					context.execute(update);

					clogger.logUpdate( cntx.personalConfig().getLoginname(), personOld, person );	
				}

				getPersonId(cntx, person.id, true);
				
				PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person);
			} else if (person.isModified()) {
				cntx.setStatus("notcorrect");
			}
			context.commit();

			// must reset the originalPersonId here, otherwise restoreNavigation will fail
			cntx.setContent("originalPersonId", ( Integer ) null);

			cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));

			/*
			 * added for support of direct search result list navigation, see below
			 * cklein 2008-03-12
			 */
			this.restoreNavigation( cntx, person, database );
		} 
		catch( BeanException e )
		{
			context.rollBack();
			throw new BeanException("Die Person konnte nicht gespeichert werden.", e);
		}

		return person;
	}

	
	/**
	 * kopiert alle Kategorie-Relationen der original-Person an die new-Person
	 * 
	 * @param originalPersonId
	 *          Id der original-Person
	 * @param newPersonId
	 *          Id der new-Person
	 * @param database
	 *          Datenbank
	 * @param context
	 *          Transaktionskontext der Datenbank
	 * @throws IOException 
	 */
	private void copyCategories(Integer originalPersonId, Integer newPersonId, Database database, TransactionContext context) throws IOException
	{
		assert originalPersonId != null;
		assert newPersonId != null;
		assert database != null;
		assert context != null;
	
		if (originalPersonId.equals(newPersonId)) return;
		
		Select select;
		try
		{
			select = database.getSelect("PersonCategorie").where(Expr.equal("fk_person", originalPersonId));
			//order by geht auf andere Tabelle; ist nur fuer join gedacht
			select.orderBy(null);
			List result = database.getBeanList("PersonCategorie", select);
			//if (result.isEmpty()) return;

			Iterator i = result.iterator();
			while (i.hasNext())
			{
				PersonCategorie bean = (PersonCategorie) i.next();
				bean.person = newPersonId;
				/*obsolete, die vera-DB setzt den pk automatisch
				database.getNextPk(bean, context);*/
				Insert insert = database.getInsert(bean);
				database.execute(insert);
			}
		} catch (BeanException e)
		{
			logger.warn("Beim Kopieren einer Person konnten Kategorien nicht uebernommen werden", e);
		} 
	}

		/** Eingabe-Parameter der Octopus-Aktion {@link #updatePerson(OctopusContext, Person, Integer)} */
	public static final String INPUT_updatePerson[] = { "person", "person-id" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #updatePerson(OctopusContext, Person, Integer)} */
	public static final boolean MANDATORY_updatePerson[] = { false, false };
	/**
     * Diese Octopus-Aktion aktualisiert die Historisierungsdaten der Person mit der
     * �bergebenen ID (es wird die �bergebene genommen oder eine Instanz aus der DB
     * geladen).
	 * 
	 * @param cntx Octopus-Kontext
	 * @param person Person; wird benutzt, falls sie die richtige ID hat
	 * @param personId Personen-ID
	 */
	public void updatePerson(OctopusContext cntx, Person person, Integer personId) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		if (person == null) {
			person = (Person)cntx.contentAsObject("person"); // ???
		}
		if (person == null || !person.id.equals(personId)) {
			person = (Person)database.getBean("Person", personId);
		}
		if (person != null && person.id != null) {
			person.updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
			Update update = database.getUpdate("Person");
			update.update(database.getProperty(person, "created"), person.created);
			update.update(database.getProperty(person, "createdby"), person.createdby);
			update.update(database.getProperty(person, "changed"), person.changed);
			update.update(database.getProperty(person, "changedby"), person.changedby);
			update.where(Expr.equal(database.getProperty(person, "id"), person.id));
			database.execute(update);
			
			// get the original version of the object for logging purposes
			Person personOld = ( Person ) database.getBean( "Person", personId );
			BeanChangeLogger clogger = new BeanChangeLogger( database );
			clogger.logUpdate( cntx.personalConfig().getLoginname(), personOld, person );	
		}
	}

    //
    // �ffentliche Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt eine Test-Person und liefert diese zur�ck.
     * 
     * @param partner bei "only" werden nur Daten zur Partnerperson, bei "without"
     *  nur Daten zur Hauptperson und sonst Daten zu beiden erzeugt.
     */
    public static Person getTestPerson(String partner) throws BeanException {
        Person person = new Person();
        String suffix = " [test-" + new Random().nextInt(10000) + "]";
        if (partner == null || !partner.equals("only")) {
            showTestPerson(person.getMainLatin(), suffix + " (Hauptperson L)");
            showTestPerson(person.getMainExtra1(), suffix + " (Hauptperson ZS1)");
            showTestPerson(person.getMainExtra2(), suffix + " (Hauptperson ZS2)");
        }
        if (partner == null || !partner.equals("without")) {
            showTestPerson(person.getPartnerLatin(), suffix + " (Partner L)");
            showTestPerson(person.getPartnerExtra1(), suffix + " (Partner ZS1)");
            showTestPerson(person.getPartnerExtra2(), suffix + " (Partner ZS2)");
        }
        showTestPerson(person.getBusinessLatin(), suffix + " (Geschäftlich L)");
        showTestPerson(person.getBusinessExtra1(), suffix + " (Geschäftlich ZS1)");
        showTestPerson(person.getBusinessExtra2(), suffix + " (Geschäftlich ZS2)");
        showTestPerson(person.getPrivateLatin(), suffix + " (Privat L)");
        showTestPerson(person.getPrivateExtra1(), suffix + " (Privat ZS1)");
        showTestPerson(person.getPrivateExtra2(), suffix + " (Privat ZS2)");
        showTestPerson(person.getOtherLatin(), suffix + " (Weitere L)");
        showTestPerson(person.getOtherExtra1(), suffix + " (Weitere ZS1)");
        showTestPerson(person.getOtherExtra2(), suffix + " (Weitere ZS2)");
        person.importsource = "Test-Person" + suffix;
        person.verify();
        person.setModified(true);
        return person;
    }
    
    //
    // gesch�tzte Hilfsmethoden
    //
    /**
     * Diese Methode f�llt die Personen-Member-Facade mit Testwerten.
     * 
     * @param suffix Suffix f�r Text-wertige Attribute
     */
    protected static void showTestPerson(PersonMemberFacade facade, String suffix) {
        facade.setBirthday(new Timestamp(System.currentTimeMillis()));
        facade.setDiplodate(new Timestamp(System.currentTimeMillis()));
        facade.setFirstname("Vorname" + suffix);
        facade.setLanguages("Sprachen" + suffix);
        facade.setLastname("Nachname" + suffix);
        facade.setNationality("Nationalität" + suffix);
        facade.setNote("Bemerkung" + suffix);
        facade.setNoteHost("Bemerkung (Gastgeber)" + suffix);
        facade.setNoteOrga("Bemerkung (Organisation)" + suffix);
        facade.setSalutation("Anrede" + suffix);
        facade.setTitle("Titel" + suffix);
    }

    /**
     * Diese Methode f�llt die Personen-Adress-Facade mit Testwerten.
     * 
     * @param suffix Suffix f�r Text-wertige Attribute
     */
    protected static void showTestPerson(PersonAddressFacade facade, String suffix) {
        facade.setCity("Ort" + suffix);
        facade.setCompany("Firma" + suffix);
        facade.setCountry("Land" + suffix);
        facade.setEMail("eMail" + suffix);
        facade.setFax("Fax" + suffix);
        facade.setFunction("Funktion" + suffix);
        facade.setMobile("Mobil" + suffix);
        facade.setPhone("Telefon" + suffix);
        facade.setPOBox("Postfach" + suffix);
        facade.setPOBoxZipCode("Postfach PLZ" + suffix);
        facade.setStreet("Straße" + suffix);
        facade.setSuffix1("Adresszusatz 1" + suffix);
        facade.setSuffix2("Adresszusatz 2" + suffix);
        facade.setUrl("www" + suffix);
        facade.setZipCode("PLZ" + suffix);
        facade.setState("Bundesland" + suffix);
    }


    /**
     * Diese Methode liefert eine aktuelle Personen-ID, wahlweise die �bergebene
     * oder die unter dem Schl�ssel "person-id" in der Session. Falls die �bergebene
     * genommen wird, wird sie unter "person-id" in die Session geschrieben.<br>
     * Die �bergebene ID wird genutzt, wenn sie nicht <code>null</code> ist oder
     * der Parameter <code>forceset</code> <code>true</code> ist.
     * 
     * @param cntx Octopus-Kontext
     * @param id neue aktuelle ID
     * @param forceSet erzwingt das Nutzen der �bergebenen ID, selbst wenn sie
     *  <code>null</code> ist.
     * @return die aktuelle Personen-ID
     */
    private Integer getPersonId(OctopusContext cntx, Integer id, boolean forceSet) {
        if (forceSet || id != null) {
            cntx.setSession("person-id", id);
            return id;
        }
        return (Integer)cntx.sessionAsObject("person-id");
    }

	/**
	 * <p>
	 * L�scht eine Person aus der Tabelle <code>tperson</code>, wenn
	 * auf diese keine Referenzen mehr in <code>tguest</code> existieren.
	 * Dabei werden auch alle abh�ngigen Tabelleneintr�ge gel�scht.
	 * </p>
	 * <p>
	 * Wenn entsprechende Eintr�ge noch existieren, wird lediglich
	 * die Spalte <code>deleted</code> auf @link PersonConstants#DELETED_TRUE
	 * gesetzt, entsprechende Eintr�ge werden bei der Suche, etc. nicht
	 * mehr ber�cksichtigt. 
	 * </p>
	 * 
	 * @param cntx Aktueller Octopus Kontext
	 * @param database Datenbank-Referenz in der gel�scht werden soll.
	 * @param personid PK aus tperson, dessen Eintrag gel�scht werden soll.
	 * @throws BeanException inkl. Datenbank-Fehler
	 * @throws IOException
	 */
	void removePerson(OctopusContext cntx, TransactionContext context, Integer personid) throws BeanException, IOException {
		Database database = context.getDatabase();
		
		// Gibt an ob diese Person noch einer Veranstaltung zugeordnet ist.
		boolean hasEvent =
				database.getCount(
				database.getCount("Guest").
				where(Expr.equal("fk_person", personid)), context).intValue() != 0;

		Person oldPerson = ( Person ) database.getBean( "Person", personid, context );
		if (hasEvent) {
			// Datenbank-Eintrag auf Gel�scht setzten.
			if (logger.isEnabledFor(Priority.DEBUG)) {
				logger.log(Priority.DEBUG, "Person löschen: Person #" + personid + " wird als gelöscht markiert.");
			}
			context.execute(SQL.Update( database ).
					table("veraweb.tperson").
					update("deleted", PersonConstants.DELETED_TRUE).
					where(Expr.equal("pk", personid)));
		} else {
			// Datenbank-Eintr�ge inkl. Abh�nigkeiten l�schen.
			if (logger.isEnabledFor(Priority.DEBUG)) {
				logger.log(Priority.DEBUG, "Person löschen: Person #" + personid + " wird vollständig gelöscht.");
			}
			
			context.execute(SQL.Delete( database ).
					from("veraweb.tperson_categorie").
					where(Expr.equal("fk_person", personid)));
			context.execute(SQL.Delete( database ).
					from("veraweb.tperson_doctype").
					where(Expr.equal("fk_person", personid)));
			context.execute(SQL.Delete( database ).
					from("veraweb.tperson_mailinglist").
					where(Expr.equal("fk_person", personid)));
			context.execute(SQL.Delete( database ).
					from("veraweb.tperson").
					where(Expr.equal("pk", personid)));
			context.execute(SQL.Update( database ).
					table("veraweb.tevent").
					update("fk_host", null).
					update("hostname", null).
					where(Expr.equal("fk_host", personid)));
		}

		/*
		 * modified to support change logging
		 * cklein 2008-02-12
		 */
		BeanChangeLogger clogger = new BeanChangeLogger( database, context );
		if ( hasEvent )
		{
			Person newPerson = ( Person ) database.getBean( "Person", personid, context );
			clogger.logUpdate( cntx.personalConfig().getLoginname(), oldPerson, newPerson );
		}
		else
		{
			clogger.logDelete( cntx.personalConfig().getLoginname(), oldPerson );
		}
	}

	private final static String DELETE_ALL_STALE_PERSON_CATEGORIES = 
		"DELETE FROM tperson_categorie WHERE fk_person IN "
		+ "(SELECT pk FROM tperson WHERE deleted = 't' AND pk NOT IN "
		+ "(SELECT DISTINCT fk_person FROM tguest))";

	private final static String DELETE_ALL_STALE_PERSON_DOCTYPES = 
		"DELETE FROM tperson_doctype WHERE fk_person IN "
		+ "(SELECT pk FROM tperson WHERE deleted = 't' AND pk NOT IN "
		+ "(SELECT DISTINCT fk_person FROM tguest))";

	private final static String DELETE_ALL_STALE_PERSON_MAILINGLISTS = 
		"DELETE FROM tperson_mailinglist WHERE fk_person IN "
		+ "(SELECT pk FROM tperson WHERE deleted = 't' AND pk NOT IN "
		+ "(SELECT DISTINCT fk_person FROM tguest))";

	private final static String DELETE_ALL_STALE_PERSONS = 
		"DELETE FROM tperson WHERE deleted = 't' AND pk NOT IN "
		+ "(SELECT DISTINCT fk_person FROM tguest)";

	private final static String UPDATE_ALL_STALE_EVENT_HOSTS = 
		"UPDATE tevent SET fk_host = NULL, hostname = NULL WHERE "
		+ "fk_host IN (SELECT pk FROM tperson WHERE deleted = 't' "
		+ "AND pk NOT IN (SELECT DISTINCT fk_person FROM tguest))";

	private final static String BULK_INSERT_CHANGELOG_ENTRIES = 
		"INSERT INTO tchangelog (username, objname, objtype, objid, op, attributes, date) "
		+ "SELECT DISTINCT "
		+ "''{0}'' AS username, lastname_a_e1 || CASE WHEN firstname_a_e1 IS NOT NULL "
		+ "THEN '', '' || firstname_a_e1 ELSE '''' END AS objname, "
		+ "''de.tarent.aa.veraweb.beans.Person'' AS objtype, pk AS objid, "
		+ "''delete'' AS op, ''*'' AS attributes, NOW() AS date "
		+ "FROM tperson WHERE deleted = ''t'' AND pk NOT IN ("
		+ "SELECT DISTINCT fk_person FROM tguest)";
	private final static MessageFormat BULK_INSERT_CHANGELOG_ENTRIES_FORMAT = new MessageFormat( BULK_INSERT_CHANGELOG_ENTRIES );

	static void removeAllDeletedPersonsHavingNoEvent(OctopusContext cntx, TransactionContext context) throws BeanException
	{
		try
		{
			DB.insert( context, DELETE_ALL_STALE_PERSON_CATEGORIES );
			DB.insert( context, DELETE_ALL_STALE_PERSON_DOCTYPES );
			DB.insert( context, DELETE_ALL_STALE_PERSON_MAILINGLISTS );
			DB.insert( context, DELETE_ALL_STALE_PERSON_DOCTYPES );
			DB.insert( context, DELETE_ALL_STALE_PERSONS );
			DB.update( context, UPDATE_ALL_STALE_EVENT_HOSTS );
			DB.insert( context, BULK_INSERT_CHANGELOG_ENTRIES_FORMAT.format( new Object[] { cntx.personalConfig().getLoginname() } ) );
			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Das Löschen aller zum löschen markierten Personen ist fehlgeschlagen.", e );
		}
		catch ( SQLException e )
		{
			context.rollBack();
			throw new BeanException( "Das Löschen aller zum löschen markierten Personen ist fehlgeschlagen.", e );
		}
	}
}
