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

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.LinkType;
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
import de.tarent.aa.veraweb.utils.OnlineRegistrationHelper;
import de.tarent.aa.veraweb.utils.OsiamLoginCreator;
import de.tarent.aa.veraweb.utils.OsiamLoginRemover;
import de.tarent.aa.veraweb.utils.PropertiesReader;
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
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * Octopus-Worker der Aktionen zur Detailansicht von Personen bereitstellt,
 * wie das {@link #showDetail(OctopusContext, Integer, Person) Laden} oder das
 * {@link #saveDetail(OctopusContext, Person) Speichern} von Daten.
 *
 * @author Christoph
 * @author Stefan Weiz, tarent solutions GmbH
 */
public class PersonDetailWorker implements PersonConstants {
    /** Logger dieser Klasse */
	private static final Logger LOGGER = Logger.getLogger(PersonDetailWorker.class);

	/**
	 * Example Property file: client.id=example-client client.secret=secret
	 * client
	 * .redirect_uri=http://osiam-test.lan.tarent.de:8080/addon-administration/
	 *
	 * osiam.server.resource=http://osiam-test.lan.tarent.de:8080/osiam-resource
	 * -server/
	 * osiam.server.auth=http://osiam-test.lan.tarent.de:8080/osiam-auth-server/
	 */
	private static final String OSIAM_RESOURCE_SERVER_ENDPOINT = "osiam.server.resource";
	private static final String OSIAM_AUTH_SERVER_ENDPOINT = "osiam.server.auth";
	private static final String OSIAM_CLIENT_REDIRECT_URI = "osiam.client.redirect_uri";
	private static final String OSIAM_CLIENT_SECRET = "osiam.client.secret";
	private static final String OSIAM_CLIENT_ID = "osiam.client.id";

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
	 * Diese Octopus Aktion nimmt die übergebene Person oder die Person zu der
     * übergebenen ID oder die Person zu der ID unter "person-id" in der Session
     * und gibt sie zurück. Als Seiteneffekt wird (wenn die Person nicht null ist)
     * im Octopus-Content unter "person-diplodatetime" ein Flag, ob das
     * Akkreditierungsdatum einen Zeitanteil enthält, und die übergebene ID (falls
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

		/**
		 * BUGFIX: 18738
		 *
		 */
		Person originalPerson = (Person)database.getBean("Person", id);
		if(originalPerson != null) {
			person.setField("birthday_a_e1", originalPerson.birthday_a_e1);
			person.setField("birthday_b_e1", originalPerson.birthday_b_e1);
			person.setField("diplodate_a_e1", originalPerson.diplodate_a_e1);
			person.setField("diplodate_b_e1", originalPerson.diplodate_b_e1);
		}

		/**
		 * BUGFIX: 18738
		 *
		 */
		if (person != null) {
			cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));
		}

		Map map = (Map)cntx.sessionAsObject( "statistikSettings" );
		if (map == null) {
			map = new HashMap();
			cntx.setSession("statistikSettings", map);
		}

		/*
		 * added for support of direct search result list navigation, see below
		 * cklein 2008-03-12
		 */
		this.restoreNavigation( cntx, person, database );

		setCurrentTimeForPersonInMap(person, map);

		cntx.setContent("personTab", cntx.requestAsString("personTab"));
        cntx.setContent("personMemberTab", cntx.requestAsString("personMemberTab"));
        cntx.setContent("personAddresstypeTab", cntx.requestAsString("personAddresstypeTab"));
        cntx.setContent("personLocaleTab", cntx.requestAsString("personLocaleTab"));

		return person;
	}

    private void setCurrentTimeForPersonInMap(Person person, Map map) {
        /*
		 * modified to support a direct statistics access from the detail view as per the change request for version 1.2.0
		 * cklein
		 * 2008-02-21
		 */
		map.put( "statistik", "EventsGroupByGuest" );
		Date d = new Date( System.currentTimeMillis() );

		if (person != null && person.created != null) {
			d = new Date(person.created.getTime());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime( d );
		map.put( "begin", "01." + ( cal.get( Calendar.MONTH ) + 1 ) + "." + cal.get( Calendar.YEAR ) );
    }

	protected void restoreNavigation( OctopusContext cntx, Person person, Database database ) throws BeanException, IOException
	{
		final String action = cntx.requestAsString( "action" );
		final Integer personId = cntx.requestAsInteger( "id" );

		// now get the proper select from the workers based on
		// the optionally defined action parameter
		Person sample = new Person();
		Select select = database.getSelectIds( sample );
		if ( "duplicateSearch".equals( action ) )
		{
			select = getPersonIdAndName(cntx, database, action, personId);
		}
		else if ( action == null || action.length() == 0 || "person".equals(action))
		{
			setPersonsForNavigation(cntx, select);
		}

		try
		{
			fillNavigationWithPersonData(cntx, person, database, action, personId, select);
		}
		catch ( SQLException e )
		{
			throw new BeanException( "Unexpected exception occurred: ", e );
		}
	}

    private void fillNavigationWithPersonData(OctopusContext cntx, Person person, Database database,
            final String action, final Integer personId, Select select) throws SQLException {
        Map< String, Map< String, Object > > navigation = new HashMap< String, Map< String, Object > >();
        Map< String, Object > entry = null;

        // setup current
        entry = new HashMap< String, Object >();
        entry.put( "person", person );
        navigation.put( "current", entry );

        ResultList result = new ResultList( select.executeSelect( database ).resultSet() );

        int size = result.size();
        int i;
        i = setPersonMapSize(personId, navigation, result, size);

        Map< String, Object > meta = new HashMap< String, Object >();
        meta.put( "action", action );
        meta.put( "offset", new Integer( i + 1 ) );
        meta.put( "count", new Integer( size ) );
        navigation.put( "meta", meta );
        cntx.setContent( "navigation", navigation );
    }

    private int setPersonMapSize(final Integer personId, Map<String, Map<String, Object>> navigation,
            ResultList result, int size) {
        int i;
        for ( i = 0; i < size; i++ )
        {
        	Map cur = ( Map ) result.get( i );
        	if ( cur.get( "id" ).equals( personId ) )
        	{
        		break;
        	}
        }
        if (i >= size) {
        	i = size - 1;
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
        return i;
    }

    private void setPersonsForNavigation(OctopusContext cntx, Select select) throws BeanException {
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

    private Select getPersonIdAndName(OctopusContext cntx, Database database, final String action,
            final Integer personId) throws BeanException, IOException {
        Select select;
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
        return select;
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
	 * Kopiert die Personendaten, die der übergebenen ID zugeordnet sind oder
     * sich im Octopus-Request unterhalb des Schlüssels "person" befinden,
	 * und stellt diese unter dem Schlüssel "person" und ein Flag, ob das
     * Akkreditierungsdatum einen Zeitanteil enthält, unter "person-diplodatetime"
     * in den Octopus-Content.<br>
     * In der aktuellen Implementierung werden dabei neben den Teilpersonen- und
     * Adressangaben in den Zusatzzeichensätzen lediglich folgende Felder
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

		person = createNewPersonOrClearData(database, person);

		cntx.setContent("person", person);
		cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));
		cntx.setContent("originalPersonId", cntx.requestAsInteger("originalPersonId"));

		/*
		 * added for support of direct search result list navigation, see below
		 * cklein 2008-03-12
		 */
		this.restoreNavigation( cntx, person, database );
	}

    private Person createNewPersonOrClearData(Database database, Person person) throws BeanException, IOException {
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
			person.username = null;

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
        return person;
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #showTestPerson(OctopusContext)} */
	public static final String INPUT_showTestPerson[] = {};
	/**
	 * Erstellt eine Test-Person und stellt diese unter dem Schlüssel "person"
     * und ein Flag, ob das Akkreditierungsdatum einen Zeitanteil enthält, unter
     * "person-diplodatetime" in den Octopus-Content.<br>
     * Anhand des Werts des Octopus-Request-Parameters zum Schlüssel "partner"
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
	 * wird die einfache Form eines Dokumenttypens zurückgegeben.
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
     * unter "doctype" und die ID des Label-Dokumenttyps (für den Fall, dass der
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
			setContentToFacades(cntx, person, freitextfeld, addresstype, locale, doctype);
		}
	}

    private void setContentToFacades(OctopusContext cntx, Person person, Integer freitextfeld, Integer addresstype,
            Integer locale, Object doctype) throws BeanException, IOException {
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

        // funktion, firma und straße
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

    /** Eingabe-Parameter der Octopus-Aktion {@link #prepareSaveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_prepareSaveDetail[] = { "saveperson" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #prepareSaveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_prepareSaveDetail[] = { false };
	/**
     * Diese Octopus-Aktion testet das übergebene Flag; falls es gesetzt
     * ist, wird der Status "saveperson" gesetzt.
	 *
	 * @param cntx Octopus-Kontext
	 * @throws BeanException
	 */
	public void prepareSaveDetail(OctopusContext cntx, Boolean saveperson) throws BeanException {
        if (saveperson != null && saveperson.booleanValue()) {
            cntx.setStatus("saveperson");
        }
    }

	public static final String INPUT_verify[] = {"person-nodupcheck"};
    public static final boolean MANDATORY_verify[] = {false};

	public void verify(final OctopusContext octopusContext, Boolean nodupcheck) throws BeanException {
	    if (nodupcheck == null || (nodupcheck != null && !nodupcheck.booleanValue())) {

            Request request = new RequestVeraWeb(octopusContext);
            Person person = (Person)request.getBean("Person", "person");

            if (person != null) {
                person.verify(octopusContext);
                if (!person.isCorrect()) {
                    if (person.id == null) {
                        octopusContext.setStatus("notcorrect");
                        octopusContext.setContent("newPersonErrors", person.getErrors());
                        octopusContext.setContent("person-iscompany", person.iscompany);
                    }
                }
            }
        }
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext, Person)} */
	public static final String INPUT_saveDetail[] = { "person" };
    /** Ausgabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext, Person)} */
	public static final String OUTPUT_saveDetail = "person";
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #saveDetail(OctopusContext, Person)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/**
     * Diese Octopus-Aktions speichert die übergebenen Person oder die Person
     * aus dem Octopus-Request unterhalb des Schlüssels "person" in der Datenbank.
     * Hierbei werden gegebenenfalls änderungen im Latin-Zeichensatz in die anderen
     * Zeichensätze übertragen.<br>
     * Die gespeicherte Person wird zurückgegeben, "countInsert" oder "countUpdate"
     * im Octopus-Content wird mit 1 und "person-diplodatetime" mit einem Flag, ob
     * das Akkreditierungsdatum einen Zeitanteil hat, belegt, und die Werte unter
     * "personTab", "personMemberTab", "personAddresstypeTab" und "personLocaleTab"
     * im Octopus-Request werden unter den gleichen Schlüsseln in den -Content
     * kopiert.
	 *
	 * @param octopusContext Octopus-Kontext
     * @return die abgespeicherte Person
	 */
	public Person saveDetail(final OctopusContext octopusContext, Person person) throws BeanException, IOException {
	    octopusContext.setContent("personTab", octopusContext.requestAsString("personTab"));
		octopusContext.setContent("personMemberTab", octopusContext.requestAsString("personMemberTab"));
		octopusContext.setContent("personAddresstypeTab", octopusContext.requestAsString("personAddresstypeTab"));
		octopusContext.setContent("personLocaleTab", octopusContext.requestAsString("personLocaleTab"));

		Database database = new DatabaseVeraWeb(octopusContext);
		TransactionContext context = database.getTransactionContext();

		Integer originalPersonId = octopusContext.requestAsInteger("originalPersonId");

		try {
			if (person == null) {
				Request request = new RequestVeraWeb(octopusContext);
				person = (Person)request.getBean("Person", "person");
			}

			/* fix for bug 1013
			 * cklein 2008-03-12
			 */
	        person.verify(octopusContext);
            if (!person.isCorrect()) {
                octopusContext.setStatus("notcorrect");

                // is this a new record?
                if (person.id == null) {
                    /*
                     * 2009-06-08 cklein fixing issue with new persons losing
                     * all state and data when entering an invalid date and
                     * trying to store the person part of fix to issue #1529, as
                     * it first showed up when testing the fixes to that issue
                     */
                    // we transfer the errors from the
                    // person to the template parameter newPersonErrors
                    octopusContext.setContent("newPersonErrors", person.getErrors());
                }

                return person;
            }

            if (octopusContext.requestAsBoolean("forcedupcheck").booleanValue()) {
                return person;
            }

			/* person was copied
			 * fix for bug 1011
			 * cklein 2008-03-12
			 */
            if (originalPersonId != null && originalPersonId > 0) {
                person.setModified(true);
            }

			/*
			 * added support for workarea assignment
			 *
			 * cklein
			 * 2008-02-20
			 */
			person.workarea = octopusContext.requestAsInteger( "workarea-id" );

			savePersonDetail(octopusContext, person, database, context, originalPersonId);
		}
		catch( BeanException e )
		{
			context.rollBack();
			throw new BeanException("Die Person konnte nicht gespeichert werden.", e);
		}

		return person;
	}

    private void savePersonDetail(final OctopusContext octopusContext, Person person, Database database, TransactionContext context,
            Integer originalPersonId) throws BeanException, IOException {
        Person personOld = null;
        if (person != null && person.id != null) {
        	personOld = (Person)database.getBean("Person", person.id, context);
        }

        DateHelper.addTimeToDate(person.diplodate_a_e1, octopusContext.requestAsString("person-diplotime_a_e1"), person.getErrors());
        person.orgunit = ((PersonalConfigAA)octopusContext.personalConfig()).getOrgUnitId();
        person.updateHistoryFields(null, ((PersonalConfigAA)octopusContext.personalConfig()).getRoleWithProxy());
        AddressHelper.checkPersonSalutation(person, database, context);

        updateExpireDate(person, personOld);

        // must reverify due to above changes
        person.verify(octopusContext);
        if (person.isModified() && person.isCorrect()) {
            createOrUpdatePerson(octopusContext, person, database, context, originalPersonId, personOld);
        } else if (person.isModified()) {
        	octopusContext.setStatus("notcorrect");
        }
        context.commit();

        // must reset the originalPersonId here, otherwise restoreNavigation will fail
        octopusContext.setContent("originalPersonId", (Integer) null);

        octopusContext.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));

        /*
         * added for support of direct search result list navigation, see below
         * cklein 2008-03-12
         */
        this.restoreNavigation( octopusContext, person, database );
    }

    private void updateExpireDate(Person person, Person personOld) {
        // Updatet das Gueltigkeitsdatum automatisch auf "in 3 Jahre"
        // wenn dieses nicht verändert wurde.
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
    }

    private void createOrUpdatePerson(OctopusContext cntx, Person person, Database database,
            TransactionContext context, Integer originalPersonId, Person personOld) throws BeanException, IOException {
        //Commented for Bugfix #19336
        //checkConversionFromFirmaToPerson(person);
        AddressHelper.copyAddressData(cntx, person, personOld);

        /*
         * modified to support change logging
         * cklein 2008-02-12
         */
        BeanChangeLogger clogger = new BeanChangeLogger( database, context );
        if (person.id == null) {
        	createNewPerson(cntx, person, database, context, originalPersonId, clogger);
        } else {
        	updateExistingPerson(cntx, person, database, context, personOld, clogger);
        }

        getPersonId(cntx, person.id, true);

        PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person);
    }

    private void updateExistingPerson(OctopusContext cntx, Person person, Database database,
            TransactionContext context, Person personOld, BeanChangeLogger clogger) throws BeanException, IOException {
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

    private void createNewPerson(OctopusContext cntx, Person person, Database database, TransactionContext context,
            Integer originalPersonId, BeanChangeLogger clogger) throws BeanException, IOException {
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
        if (originalPersonId != null && originalPersonId.intValue() != 0) {
        	copyCategories(originalPersonId, person.id,  database,  context);
        }

        clogger.logInsert( cntx.personalConfig().getLoginname(), person );
    }

//	private void checkConversionFromFirmaToPerson(Person person) {
//		if ((!person.company_a_e1.equals(null) || person.company_a_e1.equals(""))
//				&& person.iscompany.equals("f")) {
//			person.company_a_e1 = null;
//		}
//	}


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
			LOGGER.warn("Beim Kopieren einer Person konnten Kategorien nicht uebernommen werden", e);
		}
	}

		/** Eingabe-Parameter der Octopus-Aktion {@link #updatePerson(OctopusContext, Person, Integer)} */
	public static final String INPUT_updatePerson[] = { "person", "person-id" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #updatePerson(OctopusContext, Person, Integer)} */
	public static final boolean MANDATORY_updatePerson[] = { false, false };
	/**
     * Diese Octopus-Aktion aktualisiert die Historisierungsdaten der Person mit der
     * übergebenen ID (es wird die übergebene genommen oder eine Instanz aus der DB
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
    // Öffentliche Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt eine Test-Person und liefert diese zurück.
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
    // geschützte Hilfsmethoden
    //
    /**
     * Diese Methode füllt die Personen-Member-Facade mit Testwerten.
     *
     * @param suffix Suffix für Text-wertige Attribute
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
     * Diese Methode füllt die Personen-Adress-Facade mit Testwerten.
     *
     * @param suffix Suffix für Text-wertige Attribute
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
     * Diese Methode liefert eine aktuelle Personen-ID, wahlweise die übergebene
     * oder die unter dem Schlüssel "person-id" in der Session. Falls die übergebene
     * genommen wird, wird sie unter "person-id" in die Session geschrieben.<br>
     * Die übergebene ID wird genutzt, wenn sie nicht <code>null</code> ist oder
     * der Parameter <code>forceset</code> <code>true</code> ist.
     *
     * @param cntx Octopus-Kontext
     * @param id neue aktuelle ID
     * @param forceSet erzwingt das Nutzen der übergebenen ID, selbst wenn sie
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
	 * Löscht eine Person aus der Tabelle <code>tperson</code>, wenn
	 * auf diese keine Referenzen mehr in <code>tguest</code> existieren.
	 * Dabei werden auch alle abhängigen Tabelleneinträge gelöscht.
	 * </p>
	 * <p>
	 * Wenn entsprechende Einträge noch existieren, wird lediglich
	 * die Spalte <code>deleted</code> auf @link PersonConstants#DELETED_TRUE
	 * gesetzt, entsprechende Einträge werden bei der Suche, etc. nicht
	 * mehr berücksichtigt.
	 * </p>
	 *
	 * @param cntx Aktueller Octopus Kontext
	 * @param personid PK aus tperson, dessen Eintrag gelöscht werden soll.
	 * @throws BeanException inkl. Datenbank-Fehler
	 * @throws IOException
	 */
	void removePerson(OctopusContext cntx, TransactionContext context, Integer personid, String username) throws BeanException, IOException {
		Database database = context.getDatabase();

		Person oldPerson = ( Person ) database.getBean( "Person", personid, context );
		// Datenbank-Einträge inkl. Abhängigkeiten löschen.
		if (LOGGER.isEnabledFor(Priority.DEBUG)) {
			LOGGER.log(Priority.DEBUG, "Person l\u00f6schen: Person #" + personid + " wird vollst\u00e4ndig gel\u00f6scht.");
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
				from("veraweb.ttask").
				where(Expr.equal("fk_person", personid)));

		deleteOsiamUser(cntx, username);

		context.execute(SQL.Delete( database ).
				from("veraweb.tguest").
				where(Expr.equal("fk_person", personid)));
		context.execute(SQL.Update( database ).
				table("veraweb.tperson").
				update("deleted", PersonConstants.DELETED_TRUE).
				where(Expr.equal("pk", personid)));
		context.execute(SQL.Update( database ).
				table("veraweb.tevent").
				update("fk_host", null).
				update("hostname", null).
				where(Expr.equal("fk_host", personid)));

		/*
		 * modified to support change logging
		 * cklein 2008-02-12
		 */
		BeanChangeLogger clogger = new BeanChangeLogger( database, context );
		clogger.logDelete( cntx.personalConfig().getLoginname(), oldPerson );
	}


    /** Eingabe-Parameter der Octopus-Aktion {@link #createOsiamUser(OctopusContext, ExecutionContext, Person)} */
	public static final String INPUT_createOsiamUser[] = { "personId" };
    /** Ausgabe-Parameter der Octopus-Aktion {@link #createOsiamUser(OctopusContext, ExecutionContext, Person)} */
	public static final String OUTPUT_createOsiamUser = "person";
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #createOsiamUser(OctopusContext, ExecutionContext, Person)} */
	public static final boolean MANDATORY_createOsiamUser[] = { false };

	/**
	 * Creates an OSIAM user with random username and password.
	 *
	 * @param cntx The {@link de.tarent.octopus.server.OctopusContext}
	 */
	public Person createOsiamUser(OctopusContext cntx, Integer personId) throws BeanException, IOException {

		Person person = getPersonById(cntx, personId);
		if (!hasUsername(cntx, personId)) {
			Database database = new DatabaseVeraWeb(cntx);
			final OsiamLoginCreator osiamLoginCreator = new OsiamLoginCreator(database);
			final OsiamConnector connector = getConnector();

			final String firstname = person.firstname_a_e1;
			final String lastname = person.lastname_a_e1;
			final String username = osiamLoginCreator.generateUsername(firstname, lastname, connector);
			final String password = osiamLoginCreator.generatePassword();

			person.username = username;

			// Update in tperson
			this.updateUsernameInVeraweb(cntx, person);

			// Create in OSIAM
			createUser(username, password, connector, database);

			// Saving uuid to generate the reset-password url
			saveLinkUUID(personId, database);
			cntx.setContent("osiam-user-created", true);
		} else {
			cntx.setContent("osiam-user-exists", true);
		}

		return person;
	}

	/**
	 * Save new instance LinkUUID to allow having a reset password url
	 *
	 * @param personId
	 * @throws BeanException
	 * @throws IOException
	 */
	private void saveLinkUUID(Integer personId, Database database)
			throws BeanException, IOException {
		database.execute(SQL.Insert(database).
				table("veraweb.link_uuid").
				insert("uuid", getNewPersonUUID()).
				insert("linktype", LinkType.PASSWORDRESET.getText()).
				insert("personid", personId));
	}

	 /**
	 * Deletes an OSIAM user with the given username.
	 *
	 * @param cntx The {@link de.tarent.octopus.server.OctopusContext}
	 * @param username The username
	 */
	public void deleteOsiamUser(OctopusContext cntx, String username) {
		if(OnlineRegistrationHelper.isOnlineregActive(cntx)) {
			final OsiamConnector connector = getConnector();
			final AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
			final OsiamLoginRemover osiamLoginRemover = new OsiamLoginRemover(connector);
			osiamLoginRemover.deleteOsiamUser(accessToken, username);
		}
	}

	private void updateUsernameInVeraweb(OctopusContext cntx, Person person)
			throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);

		database.execute(SQL.Update( database ).
				table("veraweb.tperson").
				update("username", person.username).
				where(Expr.equal("pk", person.id)));
	}

	private Boolean hasUsername(OctopusContext cntx, Integer personId) throws BeanException, IOException {

		final Database database = new DatabaseVeraWeb(cntx);
		Integer counter = database.getCount(database.getCount("Person").where(Where.and(Expr.equal("pk", personId),Expr.isNotNull("username"))));

		return (counter == 1);
	}

	private Person getPersonById(OctopusContext cntx, Integer personId)
			throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		Person person = (Person)database.getBean("Person", personId);
		return person;
	}

	private Properties getProperties() {
		final PropertiesReader propertiesReader = new PropertiesReader();
		return propertiesReader.getProperties();
	}

	private void createUser(String username, String password, OsiamConnector connector, Database database) {
		final AccessToken accessToken = connector.retrieveAccessToken(Scope.ALL);
		final OsiamLoginCreator osiamLoginCreator = new OsiamLoginCreator(database);

		osiamLoginCreator.createOsiamUser(accessToken, username, password, connector);
	}

	/**
	 * Getting OSIAM Connector to execute updates over OSIAM's Database
	 *
	 * @return OsiamConnector the connector
	 */
	private OsiamConnector getConnector() {
		final Properties properties = getProperties();

		final OsiamConnector connector = new OsiamConnector.Builder()
				.setClientRedirectUri(
						properties.getProperty(OSIAM_CLIENT_REDIRECT_URI))
				.setClientSecret(
						properties.getProperty(OSIAM_CLIENT_SECRET))
				.setClientId(properties.getProperty(OSIAM_CLIENT_ID))
				.setAuthServerEndpoint(
						properties.getProperty(OSIAM_AUTH_SERVER_ENDPOINT))
				.setResourceServerEndpoint(
						properties
								.getProperty(OSIAM_RESOURCE_SERVER_ENDPOINT))
				.build();

		return connector;
	}


    /**
     * New hash for persons
     *
     * @param event
     * @param oldEvent
     */
    private String getNewPersonUUID() {
		UUID uuid = UUID.randomUUID();

		return uuid.toString();
    }
}
