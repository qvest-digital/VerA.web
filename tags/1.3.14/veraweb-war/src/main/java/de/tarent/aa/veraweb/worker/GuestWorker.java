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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.EventDoctype;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestDoctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.GuestSerialNumber;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen f�r G�ste von
 * Veranstaltungen zur Verf�gung. 
 * 
 * @author christoph
 * @author mikel
 */
public class GuestWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabe-Parameter f�r {@link #addGuestList(OctopusContext)} */
	public static final String INPUT_addGuestList[] = {};
	/**
	 * Diese Octopus-Aktion f�gt eine Reihe von G�sten einer
	 * Veranstaltung hinzu.<br>
	 * 
	 * @param cntx OctopusContext
	 * @throws BeanException
	 * @throws IOException
	 */
	protected static final String COUNT_INVITED_NOT_INVITED_2_PATTERN = 
		"select (select count(*) from tperson p where p.pk not in (select fk_person from tguest "
		+ "where fk_event = {0}) and p.deleted = ''f'' and p.pk in ({1})) as invited, "
		+ "(select count(*) from tguest g left join tperson p on g.fk_person = p.pk "
		+ "where g.fk_event = {0} and (g.fk_person in ({1}) or (g.fk_person in ({1})"
		+ "and p.deleted=''f''))) as notinvited;";
	protected static final MessageFormat COUNT_INVITED_NOT_INVITED_2_FORMAT = new MessageFormat(COUNT_INVITED_NOT_INVITED_2_PATTERN);

	protected static final String ADD_PERSONS_TO_GUESTLIST_PATTERN =
		"insert into tguest ( fk_person, fk_event, fk_category, fk_color, invitationtype, invitationstatus, "
		+ "ishost, diplodate, rank, reserve, notehost, noteorga, \"language\", "
		+ "gender, nationality, domestic_a, invitationstatus_p, notehost_p, "
		+ "noteorga_p, language_p, gender_p, nationality_p, domestic_b, fk_color_p, createdby, created ) "
		+ "select p.pk as fk_person, {0} as fk_event, 0 as fk_category, "
		+ "(CASE WHEN p.domestic_a_e1 = ''f'' THEN CASE WHEN p.sex_a_e1 = ''f'' THEN 3 ELSE 4 END ELSE CASE WHEN p.sex_a_e1 =''f'' THEN 1 ELSE 2 END END) as fk_color, "
		+ "0 as invitationtype, 0 as invitationstatus, 0 as ishost, p.diplodate_a_e1 as diplodate, 0 as rank, 0 as reserve, "
		+ "p.notehost_a_e1 as notehost, p.noteorga_a_e1 as noteorga, p.languages_a_e1 as \"language\", p.sex_a_e1 as gender, "
		+ "p.nationality_a_e1 as nationality, p.domestic_a_e1 as domestic_a, 0 as "
		+ "invitationstatus_p, p.notehost_b_e1 as notehost_p, p.noteorga_b_e1 as noteorga_p, p.languages_b_e1 as language_p, "
		+ "p.sex_b_e1 as gender_p, p.nationality_b_e1 as nationality_p, p.domestic_b_e1 as domestic_b, "
		+ "(CASE WHEN p.domestic_b_e1 = ''f'' THEN CASE WHEN p.sex_b_e1 = ''f'' THEN 3 ELSE 4 END ELSE CASE WHEN p.sex_b_e1 =''f'' THEN 1 ELSE 2 END END) as fk_color_p, "
		+ "''{1}'' as createdby, current_timestamp as created from tperson p "
		+ "where p.pk in ({2}) and p.deleted=''f'' and p.pk not in (select g.fk_person from tguest g "
		+ "where g.fk_event = {0});";
	protected static final MessageFormat ADD_PERSONS_TO_GUESTLIST_FORMAT = new MessageFormat(ADD_PERSONS_TO_GUESTLIST_PATTERN);

	protected static final String UPDATE_PERSON_TO_GUEST_LIST_PATTERN =
		"update tguest set fk_category={0}, invitationtype={1}, "
		+ "rank=(select rank from tperson_categorie where pk={0}), reserve={2} "
		+ "where fk_person={3} and fk_event={4};";
	protected static final MessageFormat UPDATE_PERSON_TO_GUEST_LIST_FORMAT = new MessageFormat(UPDATE_PERSON_TO_GUEST_LIST_PATTERN);

	public void addGuestList(OctopusContext cntx) throws BeanException, IOException
	{
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();

		try
		{
			Event event = ( Event ) cntx.contentAsObject( "event" );
			List invitemain = ( List ) cntx.sessionAsObject( "selectionPerson" );
			List invitepartner = ( List )cntx.sessionAsObject( "addguest-invitepartner" );
			List selectreserve = ( List ) cntx.sessionAsObject( "addguest-selectreserve" );
			Map invitecategory = ( Map ) cntx.sessionAsObject( "addguest-invitecategory" );
			if ( invitecategory == null )
			{
				invitecategory = new HashMap();
			}

			String personIds = DatabaseHelper.listsToIdListString( new List[] { invitemain, invitepartner, selectreserve } );
			String sql = COUNT_INVITED_NOT_INVITED_2_FORMAT.format( new Object[] { event.id.toString(), personIds } );
			Result res = DB.result( context, sql );

			ResultSet rs = res.resultSet();
			rs.first();
			cntx.setContent( "invited", new Integer( rs.getInt( "invited" ) ) );
			cntx.setContent( "notInvited", new Integer( rs.getInt( "notinvited" ) ) );
			rs.close();

			// prepare third step, fill in missing data into guest tupels
			StringBuffer sql3 = new StringBuffer();
			try
			{
				// not optimized due to dynamic creation of doctype content from configuration
				// must still instantiate person beans from database, which may lead to destabilization
				// of the system once more
				List<Person> persons = database.getBeanList( "Person", database.getSelect( "Person" ).
					where( new RawClause( "tperson.pk in (" + personIds + ") and tperson.deleted='f'"
						+ " and tperson.pk not in (select fk_person from tguest where fk_event = " + event.id + ")"
				)));
	
				for ( int i = 0; i < persons.size(); i++ )
				{
					Person person = persons.get( i );
					PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person );
	
					Integer fk_category = ( Integer ) invitecategory.get( person.id );
					if ( fk_category != null && fk_category.intValue() == 0 )
					{
						fk_category = null;
					}

					sql3.append( UPDATE_PERSON_TO_GUEST_LIST_FORMAT.format( new Object[] {
						fk_category != null ? fk_category.toString() : null, new Integer( invitepartner.indexOf( person.id ) != -1 ? EventConstants.TYPE_MITPARTNER : EventConstants.TYPE_OHNEPARTNER ),
						( selectreserve.indexOf( person.id ) != -1 ) ? 1 : 0, person.id.toString(), event.id.toString()
					} ) );
					sql3.append( ';' );
				}
				context.commit();
			}
			catch( BeanException e )
			{
				// will silently fail here as the following transaction
				// must be run under all cases, even if individual
				// person document types have not been updated
				logger.warn( "Transaktion fehlgeschlagen. Die Dokumenttypen der Personen wurden nicht aktualisiert.", e );
			}
			catch( OutOfMemoryError e )
			{
				// will silently fail here as the following transaction
				// must be run under all cases, even if individual
				// person document types have not been updated

				// enforce garbage collection so that the following code
				// may continue
				logger.fatal( "Nicht genügend Speicher. Forciere Garbage-Collection.", e );
				System.gc();
			}

			// second step, create guest tupels
			sql = ADD_PERSONS_TO_GUESTLIST_FORMAT.format( new Object[] { event.id.toString(), ( ( PersonalConfigAA ) cntx.personalConfig() ).getRoleWithProxy(), personIds } );
			DB.insert( context, sql );
			context.commit();

			if ( sql3.length() > 0 )
			{
				DB.insert( context, sql3.toString() );
				context.commit();
			}

			sql = UPDATE_GUEST_DOCUMENT_TYPES_FORMAT.format( new Object[] { event.id.toString() } );
			context.commit();
			DB.insert( context, sql );

			// TODO bulk log guest create event

			// prevent alert message in case of invited == 0 and notinvited == 0
			cntx.setContent("doNotAlert", true);
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die Gäste konnten nicht auf die Gästeliste gesetzt werden.", e );
		}
		catch ( SQLException e )
		{
			context.rollBack();
			throw new BeanException( "Die Gäste konnten nicht auf die Gästeliste gesetzt werden.", e );
		}
		catch ( OutOfMemoryError e )
		{
			context.rollBack();
			// just rethrow
			throw e;
		}
	}

	/** Octopus-Eingabe-Parameter f�r {@link #addEvent(OctopusContext, Integer)} */
	public static final String INPUT_addEvent[] = { "id" };
	/**
	 * F�gt die G�ste einer Veranstaltung einer anderen Veranstaltung hinzu.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	protected static final String COUNT_INVITED_NOT_INVITED_PATTERN =
		"select (select count(*) from tguest g left join tperson p on g.fk_person = p.pk "
		+ "where g.fk_event = {1} and g.fk_person not in (select fk_person from tguest "
		+ "where fk_event = {0}) and p.deleted = ''f'') as invited, "
		+ "(select count(*) from tguest g left join tperson p on g.fk_person = p.pk "
		+ "where g.fk_event = {1} and (g.fk_person in (select fk_person from tguest "
		+ "where fk_event = {0}) or (g.fk_person in (select fk_person from tguest "
		+ "where fk_event = {0} and p.deleted=''f'')))) as notinvited;";
	protected static final MessageFormat COUNT_INVITED_NOT_INVITED_FORMAT = new MessageFormat(COUNT_INVITED_NOT_INVITED_PATTERN);

	protected static final String ADD_FROM_EVENT_PATTERN = 
		"insert into tguest ( fk_person, fk_event, fk_category, fk_color, invitationtype, invitationstatus, "
		+ "ishost, diplodate, rank, reserve, tableno, seatno, orderno, notehost, noteorga, \"language\", "
		+ "gender, nationality, domestic_a, invitationstatus_p, tableno_p, seatno_p, orderno_p, notehost_p, "
		+ "noteorga_p, language_p, gender_p, nationality_p, domestic_b, fk_color_p, createdby, created ) "
		+ "select p.pk as fk_person, {0} as fk_event, g.fk_category as fk_category, g.fk_color "
		+ "as fk_color, CASE WHEN {1} <> g.invitationtype AND {1} <> {2} THEN g.invitationtype ELSE {1} END as invitationtype, 0 as invitationstatus, "
		+ "g.ishost as ishost, p.diplodate_a_e1 as diplodate, g.rank as rank, g.reserve as reserve, "
		+ "g.tableno as tableno, g.seatno as seatno, g.orderno as orderno, p.notehost_a_e1 as notehost, "
		+ "p.noteorga_a_e1 as noteorga, p.languages_a_e1 as \"language\", p.sex_a_e1 as gender, "
		+ "p.nationality_a_e1 as nationality, p.domestic_a_e1 as domestic_a, 0 as "
		+ "invitationstatus_p, g.tableno_p as tableno_p, g.seatno_p as seatno_p, g.orderno_p as orderno_p, "
		+ "p.notehost_b_e1 as notehost_p, p.noteorga_b_e1 as noteorga_p, p.languages_b_e1 as language_p, "
		+ "p.sex_b_e1 as gender_p, p.nationality_b_e1 as nationality_p, p.domestic_b_e1 as domestic_b, "
		+ "g.fk_color_p as fk_color_p, ''{3}'' as createdby, current_timestamp as created from tperson p "
		+ "left join tguest g on p.pk = g.fk_person and g.fk_event = {4} "
		+ "where p.pk in (select g.fk_person from tguest g "
		+ "where g.fk_event = {4}) and p.deleted=''f'' and p.pk not in (select g.fk_person from tguest g "
		+ "where g.fk_event = {0});";
	protected static final MessageFormat ADD_FROM_EVENT_FORMAT = new MessageFormat(ADD_FROM_EVENT_PATTERN);

	protected static final String UPDATE_GUEST_DOCUMENT_TYPES_PATTERN = 
		"delete from tguest_doctype where fk_guest in ( select g.pk from tguest g "
		+ "where g.fk_event = {0} ); "
		+ "insert into tguest_doctype ( fk_guest, fk_doctype, addresstype, locale, "
		+ "textfield, textfield_p, textjoin, salutation, titel, \"function\", firstname, "
		+ "lastname, zipcode, city, street, country, suffix1, suffix2, salutation_p, "
		+ "titel_p, firstname_p, lastname_p, fon, fax, mail, www, mobil, company, "
		+ "pobox, poboxzipcode, state) "
		+ "select distinct on (d.fk_doctype,g.pk) "
		+ "g.pk as fk_guest, d.fk_doctype as fk_doctype, d.addresstype as addresstype, "
		+ "d.locale as locale, d.textfield as textfield, d.textfield_p as textfield_p, "
		+ "d.textjoin as textjoin, (CASE WHEN d.locale = 1 THEN sa1.salutation "
		+ "WHEN d.locale = 2 THEN sa2.salutation WHEN d.locale = 3 THEN sa3.salutation "
		+ "ELSE '''' END) as salutation, (CASE WHEN d.locale = 1 THEN p.function_a_e1 "
		+ "WHEN d.locale = 2 THEN p.function_a_e2 WHEN d.locale = 3 THEN p.function_a_e3 "
		+ "ELSE '''' END) as \"function\", (CASE WHEN d.locale = 1 THEN p.title_a_e1 "
		+ "WHEN d.locale = 2 THEN p.title_a_e2 WHEN d.locale = 3 THEN p.title_a_e3 "
		+ "ELSE '''' END) as titel, (CASE WHEN d.locale = 1 THEN p.firstname_a_e1 "
		+ "WHEN d.locale = 2 THEN p.firstname_a_e2 WHEN d.locale = 3 THEN p.firstname_a_e3 "
		+ "ELSE '''' END) as firstname, (CASE WHEN d.locale = 1 THEN p.lastname_a_e1 "
		+ "WHEN d.locale = 2 THEN p.lastname_a_e2 WHEN d.locale = 3 THEN p.lastname_a_e3 "
		+ "ELSE '''' END) as lastname, (CASE WHEN d.locale = 1 THEN p.zipcode_a_e1 "
		+ "WHEN d.locale = 2 THEN p.zipcode_a_e2 WHEN d.locale = 3 THEN p.zipcode_a_e3 "
		+ "ELSE '''' END) as zipcode, (CASE WHEN d.locale = 1 THEN p.city_a_e1 "
		+ "WHEN d.locale = 2 THEN p.city_a_e2 WHEN d.locale = 3 THEN p.city_a_e3 "
		+ "ELSE '''' END) as city, (CASE WHEN d.locale = 1 THEN p.street_a_e1 "
		+ "WHEN d.locale = 2 THEN p.street_a_e2 WHEN d.locale = 3 THEN p.street_a_e3 "
		+ "ELSE '''' END) as street, (CASE WHEN d.locale = 1 THEN p.country_a_e1 "
		+ "WHEN d.locale = 2 THEN p.country_a_e2 WHEN d.locale = 3 THEN p.country_a_e3 "
		+ "ELSE '''' END) as country, (CASE WHEN d.locale = 1 THEN p.suffix1_a_e1 "
		+ "WHEN d.locale = 2 THEN p.suffix1_a_e2 WHEN d.locale = 3 THEN p.suffix1_a_e3 "
		+ "ELSE '''' END) as suffix1, (CASE WHEN d.locale = 1 THEN p.suffix2_a_e1 "
		+ "WHEN d.locale = 2 THEN p.suffix2_a_e2 WHEN d.locale = 3 THEN p.suffix2_a_e3 "
		+ "ELSE '''' END) as suffix2, (CASE WHEN d.locale = 1 THEN sb1.salutation "
		+ "WHEN d.locale = 2 THEN sb2.salutation WHEN d.locale = 3 THEN sb3.salutation "
		+ "ELSE '''' END) as salutation_p, (CASE WHEN d.locale = 1 THEN p.title_b_e1 "
		+ "WHEN d.locale = 2 THEN p.title_b_e2 WHEN d.locale = 3 THEN p.title_b_e3 "
		+ "ELSE '''' END) as titel_p, (CASE WHEN d.locale = 1 THEN p.firstname_b_e1 "
		+ "WHEN d.locale = 2 THEN p.firstname_b_e2 WHEN d.locale = 3 THEN p.firstname_b_e3 "
		+ "ELSE '''' END) as firstname_p, (CASE WHEN d.locale = 1 THEN p.lastname_b_e1 "
		+ "WHEN d.locale = 2 THEN p.lastname_b_e2 WHEN d.locale = 3 THEN p.lastname_b_e3 "
		+ "ELSE '''' END) as lastname_p, (CASE WHEN d.locale = 1 THEN p.fon_a_e1 "
		+ "WHEN d.locale = 2 THEN p.fon_a_e2 WHEN d.locale = 3 THEN p.fon_a_e3 ELSE '''' "
		+ "END) as fon, (CASE WHEN d.locale = 1 THEN p.fax_a_e1 WHEN d.locale = 2 THEN p.fax_a_e2 "
		+ "WHEN d.locale = 3 THEN p.fax_a_e3 ELSE '''' END) as fax, (CASE WHEN d.locale = 1 "
		+ "THEN p.mail_a_e1 WHEN d.locale = 2 THEN p.mail_a_e2 WHEN d.locale = 3 THEN p.mail_a_e3 "
		+ "ELSE '''' END) as mail, (CASE WHEN d.locale = 1 THEN p.url_a_e1 WHEN d.locale = 2 "
		+ "THEN p.url_a_e2 WHEN d.locale = 3 THEN p.url_a_e3 ELSE '''' END) as www, "
		+ "(CASE WHEN d.locale = 1 THEN p.mobil_a_e1 WHEN d.locale = 2 THEN p.mobil_a_e2 "
		+ "WHEN d.locale = 3 THEN p.mobil_a_e3 ELSE '''' END) as mobil, (CASE WHEN d.locale = 1 "
		+ "THEN p.company_a_e1 WHEN d.locale = 2 THEN p.company_a_e2 WHEN d.locale = 3 THEN "
		+ "p.company_a_e3 ELSE '''' END) as company, (CASE WHEN d.locale = 1 THEN p.pobox_a_e1 "
		+ "WHEN d.locale = 2 THEN p.pobox_a_e2 WHEN d.locale = 3 THEN p.pobox_a_e3 ELSE '''' "
		+ "END) as pobox, (CASE WHEN d.locale = 1 THEN p.poboxzipcode_a_e1 WHEN d.locale = 2 "
		+ "THEN p.poboxzipcode_a_e2 WHEN d.locale = 3 THEN p.poboxzipcode_a_e3 ELSE '''' "
		+ "END) as poboxzipcode, (CASE WHEN d.locale = 1 THEN p.state_a_e1 WHEN d.locale = 2 "
		+ "THEN p.state_a_e2 WHEN d.locale = 3 THEN p.state_a_e3 ELSE '''' END) as state "
		+ "from tguest g left join tevent_doctype ed on ed.fk_event = g.fk_event "
		+ "left join tperson_doctype d on d.fk_person = g.fk_person left join tperson p on p.pk "
		+ "= g.fk_person left join tsalutation sa1 on p.fk_salutation_a_e1 = sa1.pk "
		+ "left join tsalutation sa2 on p.fk_salutation_a_e2 = sa2.pk left join tsalutation sa3 "
		+ "on p.fk_salutation_a_e3 = sa3.pk left join tsalutation sb1 on p.fk_salutation_b_e1 = sb1.pk "
		+ "left join tsalutation sb2 on p.fk_salutation_b_e2 = sb2.pk left join tsalutation sb3 on "
		+ "p.fk_salutation_b_e3 = sb3.pk where g.fk_event = {0};";
	protected static final MessageFormat UPDATE_GUEST_DOCUMENT_TYPES_FORMAT = new MessageFormat(UPDATE_GUEST_DOCUMENT_TYPES_PATTERN);

	public void addEvent(OctopusContext cntx, Integer eventId) throws BeanException, IOException
	{
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();
		try
		{
			Event event = ( Event ) cntx.contentAsObject( "event" );
			logger.debug( "Füge Gäste der Veranstaltung #" + eventId + " der Verstanstaltung #" + event.id + " hinzu." );
			String sql = COUNT_INVITED_NOT_INVITED_FORMAT.format( new Object[] { event.id.toString(), eventId.toString() } );
			Result res = DB.result( context, sql );

			ResultSet rs = res.resultSet();
			rs.first();
			cntx.setContent( "invited", new Integer( rs.getInt( "invited" ) ) );
			cntx.setContent( "notInvited", new Integer( rs.getInt( "notinvited" ) ) );
			rs.close();

			try
			{
				// not optimized due to dynamic creation of doctype content from configuration
				// must still instantiate person beans from database, which may lead to destabilization
				// of the system once more
				List<Person> persons = database.getBeanList( "Person", database.getSelect( "Person" ).
					join( new Join( Join.LEFT_OUTER, "veraweb.tguest", new RawClause( "tguest.fk_person = tperson.pk" ) ) ).
					where( new RawClause( "tguest.fk_event = " + eventId
						+ " and tguest.fk_person not in (select fk_person from tguest where fk_event = " + event.id
						+ ") and tperson.deleted = 'f'"
				)));
	
				for ( Person person : persons )
				{
					PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person );
				}
				context.commit();
			}
			catch( BeanException e )
			{
				// will silently fail here as the following transaction
				// must be run under all cases, even if individual
				// person document types have not been updated
				logger.warn( "Transaktion fehlgeschlagen. Die Dokumenttypen der Personen wurden nicht aktualisiert.", e );
			}
			catch( OutOfMemoryError e )
			{
				// will silently fail here as the following transaction
				// must be run under all cases, even if individual
				// person document types have not been updated

				// enforce garbage collection so that the following code
				// may continue
				logger.fatal( "Nicht genügend Speicher. Forciere Garbage-Collection.", e );
				System.gc();
			}

			sql = ADD_FROM_EVENT_FORMAT.format( new Object[] { event.id.toString(), event.invitationtype, EventConstants.TYPE_OHNEPARTNER, ( ( PersonalConfigAA ) cntx.personalConfig() ).getRoleWithProxy(), eventId.toString() } );
			DB.insert( context, sql );
			context.commit();

			sql = UPDATE_GUEST_DOCUMENT_TYPES_FORMAT.format( new Object[] { event.id.toString() } );
			context.commit();
			DB.insert( context, sql );

			// TODO bulk log guest create event
		}
		catch ( SQLException e )
		{
			context.rollBack();
			throw new BeanException( "Die Gäste aus der Gästeliste konnten nicht übernommen werden.", e );
		}
	}

	/** Octopus-Eingabe-Parameter f�r {@link #addPerson(OctopusContext, Integer)} */
	public static final String INPUT_addPerson[] = { "event-id" };
	/**
	 * F�gt eine Person aus dem Content zu einer Veranstaltung hinzu.
	 * 
	 * Wird offenbar nur verwendet wenn aus der Detail-Sicht einer Person diese Person einer Veranstaltung zugewiesen
	 * wird. Falls der Person nur eine einzige Kategorie zugeordnet ist, wird diese mit in die Veranstaltung uebernommen
	 * (Bug 1593)
	 */
	public void addPerson(OctopusContext cntx, Integer eventId) throws BeanException, IOException
	{
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();
		Event event = (Event) database.getBean("Event", eventId, context);
		Person person = (Person) cntx.contentAsObject("person");

		try
		{
			int invited = 0;
			int notInvited = 0;
			boolean invite = false;

			if (event != null && person != null)
			{
				// Falls der Person nur eine einzige Kategorie zugeordnet ist, wird diese mit in die Veranstaltung uebernommen (Bug 1593)
				Integer catId = null;
				try
				{
					Select select = database.getSelect("PersonCategorie")
					.where(Expr.equal("fk_person", person.id))
					.orderBy(null); //TODO beans.property von PersonCategorie ist nicht korrekt!
					List list = database.getBeanList("PersonCategorie",select, context);
					if (list.size() == 1)
					{
						catId = ((PersonCategorie)list.get(0)).id;
					}
				} catch (Exception e)
				{
					logger.warn("addPerson: Konnte fuer Person: " + person + " beim Hinzuf�gen zur Veranstaltung: " + event
						+ " keine PersonCategorie ermitteln", e);
				}
				invite = addGuest(cntx, database, context, event, person.id, catId, Boolean.FALSE, event.invitationtype, Boolean.FALSE);
				if (invite)
					invited++;
				else
					notInvited++;
			} else
			{
				logger.error("addPerson: Konnte Person: " + person + " der Veranstaltung: " + event + " nicht hinzuf�gen.");
			}

			cntx.setContent("event", event);
			cntx.setContent("invited", new Integer(invited));
			cntx.setContent("notInvited", new Integer(notInvited));

			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die Person konnte nicht auf die Gästeliste gesetzt werden.", e );
		}
	}

	/** Octopus-Eingabe-Parameter f�r {@link #reloadData(OctopusContext, Integer)} */
	public static final String INPUT_reloadData[] = { "guest-id" };
	/**
	 * Diese Octopus-Aktion aktualisiert die Daten eines Gastes
	 * aus den Stammdaten und erzeugt die Dokumenttypen neu.
	 */
	public void reloadData(OctopusContext cntx, Integer guestId) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();
		Event event = (Event)cntx.contentAsObject("event");
		
		try {
			updateGuest(cntx, database, context, event, guestId);
			
			String show = cntx.requestAsString("show");
			if (show != null && show.equals("doctype")) {
				cntx.setStatus("showDoctype");
			}
			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die Daten der Person konnten nicht aktualisiert werden.", e );
		}
	}

	/**
	 * Octopus-Eingabe-Parameter f�r {@link #reloadAllData(OctopusContext)}
	 */
	public static final String INPUT_reloadAllData[] = {};
	/**
	 * Diese Octopus-Aktion aktuallisiert die Dokumenttypen
	 * des aktuellen Gastes zu einer Veranstaltung.
	 */
	public void reloadAllData(OctopusContext cntx) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
		Event event = (Event)cntx.contentAsObject("event");
		
		try {
			List selection = (List)cntx.contentAsObject("listselection");
			if (selection != null && selection.size() != 0) {
				for (Iterator it = selection.iterator(); it.hasNext(); ) {
					Integer guestId = (Integer)it.next();
					updateGuest(cntx, database, context, event, guestId);
				}
			} else {
				List list =
						database.getList(
						database.getSelectIds(new Guest()).
						where(Expr.equal("fk_event", event.id)), database);
				for (Iterator it = list.iterator(); it.hasNext(); ) {
					Integer guestId = (Integer)((Map)it.next()).get("id");
					updateGuest(cntx, database, context, event, guestId);
				}
			}
			context.commit();
		}
		catch ( BeanException e )
		{
			context.rollBack();
			throw new BeanException( "Die Personendaten konnten aktualisiert werden.", e );
		}
	}

    /** Octopus-Eingabe-Parameter f�r {@link #calcSerialNumber(OctopusContext)} */
    public static final String INPUT_calcSerialNumber[] = {};
    /**
     * Diese Octopus-Aktion berechnet f�r eine Veranstaltung die 'Laufende Nummer'.
     * 
     * @param cntx
     * @throws BeanException
     * @throws IOException
     */
    public void calcSerialNumber(OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		Event event = (Event)cntx.contentAsObject("event");
		logger.debug("calc order number for event #" + event.id);
		
		Map questions = new HashMap();
		if (event.begin.before(new Date()) && !cntx.requestAsBoolean("calc-serialno").booleanValue()) {
			questions.put("calc-serialno", "Diese Veranstaltung liegt bereits in der Vergangenheit, möchten Sie trotzdem die Laufende-Nummer neu berechnen?");
		} else {
			TransactionContext context = database.getTransactionContext();
			try
			{
				( new GuestSerialNumber.CalcSerialNumberImpl3(context, event) ).calcSerialNumber();
				context.commit();
			}
			catch ( BeanException e )
			{
				context.rollBack();
				throw new BeanException( "Die laufende Nummer konnte nicht berechnet werden.", e );
			}
		}
		if (!questions.isEmpty()) {
			cntx.setContent("listquestions", questions);
		}
	}

    //
    // Gesch�tzte Methoden
    //
	/**
	 * Neuen Gast hinzuf�gen.
	 * 
	 * @see #saveGuest(OctopusContext, Database, ExecutionContext, Event, Integer, Integer, Integer, Boolean, Integer, Boolean)
	 */
	boolean addGuest(OctopusContext cntx, Database database, ExecutionContext context, Event event, Integer personId, Integer categoryId, Boolean reserve, Integer invitationtype, Boolean ishost) throws BeanException, IOException {
		return saveGuest(cntx, database, context, event, null, personId, categoryId, reserve, invitationtype, ishost);
	}

	/**
	 * Bestehnden Gast aus Stammdaten neuladen.
	 * 
	 * @see #saveGuest(OctopusContext, Database, ExecutionContext, Event, Integer, Integer, Integer, Boolean, Integer, Boolean)
	 */
	boolean updateGuest(OctopusContext cntx, Database database, ExecutionContext context, Event event, Integer guestId) throws BeanException, IOException {
		if (guestId == null) return false;
		return saveGuest(cntx, database, context, event, guestId, null, null, null, null, null);
	}

	void refreshDoctypes(OctopusContext cntx, Database database, ExecutionContext context, Integer guestId) throws BeanException, IOException {
		Guest guest = (Guest)
				database.getBean("Guest",
				database.getSelect("Guest").
				where(Expr.equal("pk", guestId)), context);
		if (guest == null) return;
		
		Person person = (Person)
				database.getBean("Person",
				database.getSelect("Person").
				where(Expr.equal("pk", guest.person)), context);
		
		refreshDoctypes(cntx, database, context, guest, person);
	}

    /**
     * Diese Methode f�gt eine Person einer Veranstaltung hinzu.<br><br>
     * 
     * <strong>Achtung:</strong> Wenn die Gast-ID null ist wird ein neuer
     * Gast angelegt wenn dieser noch nicht dieser Veranstaltung zugeordnet
     * war. Wenn die Gast-ID �bergeben wird, wird dieser Gast aktuallisiert!
     * 
     * @param cntx Octopus-Context
     * @param database Datenbank
     * @param event Veranstaltung
     * @param guestId Gast der bearbeitet werden soll, null zum hinzuf�gen.
     * @param personId Person mit dessen Daten der Gast gef�llt werden soll.
     * @param categoryId Kategorie nach der gefiltert wurde.
     * @param reserve Gibt an ob dieser Gast auf Reserve gesetzt werden soll.
     * @param invitationtype Gibt an ob dieser Gast mit/ohne Partner eingeladen werden soll.
     * @param ishost Gibt an ob dieser Gast gleichzeitig Gastgeber ist.
     */
	/*
	 * 2009-05-12 cklein
	 * 
	 * fixed as part of issue #1531 - personCategorie was always null due to malformed query
	 */
	protected boolean saveGuest(OctopusContext cntx, Database database, ExecutionContext context, Event event, Integer guestId, Integer personId, Integer categoryId, Boolean reserve, Integer invitationtype, Boolean ishost) throws BeanException, IOException {
		if (event == null) return false;
		
		if (guestId == null) {
			logger.debug("Füge Person #" + personId + " der Veranstaltung #" + event.id + " hinzu.");
		}
		
		// Keinen neuen Gast hinzuf�gen wenn diese Person bereits zugeordnet war.
		if (guestId == null) {
			if (database.getCount(database.getCount("Guest").where(Where.and(
					Expr.equal("fk_event", event.id),
					Expr.equal("fk_person", personId))), context).intValue() > 0)
				return false;
		}
		
		Guest guest = null;
		// Gast laden
		if (guestId != null) {
			guest = (Guest)
					database.getBean("Guest",
					database.getSelect("Guest").
					where(Expr.equal("pk", guestId)), context);
			if (guest == null) {
				logger.warn("Gast #" + guestId + " konnte nicht gefunden werden.");
				return false;
			}
			personId = guest.person;
		}
		
		// Vollst�ndige Personendaten laden.
		Person person = (Person)
				database.getBean("Person",
				database.getSelect("Person").
				where(Expr.equal("pk", personId)), context);
		if (person == null) {
			logger.warn("Person #" + personId + " konnte nicht gefunden und daher der Veranstaltung #" + event.id + " nicht hinzugef�gt werden.");
			return false;
		}
		
		// Neuen Gast anlegen.
		if (guest == null) {
			guest = new Guest();
			guest.event = event.id;
	        guest.person = person.id;
			guest.ishost = new Integer(ishost.booleanValue() ? 1 : 0);
			guest.reserve = reserve;
			guest.invitationtype = invitationtype;
			
			if (personId != null && categoryId != null) {
				PersonCategorie personCategorie = new PersonCategorie();
				personCategorie.person = personId;
				personCategorie.categorie = categoryId;
				personCategorie = (PersonCategorie)
						database.getBean("PersonCategorie",
						database.getSelect(personCategorie).where(
						database.getWhere(personCategorie)).
						select("tcategorie.rank").
						select("tcategorie.catname").
						joinLeftOuter("veraweb.tcategorie",
								"tperson_categorie.fk_categorie", "tcategorie.pk"), context );
				
				if (personCategorie != null) {
					guest.category = personCategorie.categorie;
					guest.rank = personCategorie.rank;
				} else {
					guest.category = categoryId;
					guest.rank = null;
				}
			} else {
				guest.category = null;
				guest.rank = null;
			}
		}
		guest.domestic_a = person.domestic_a_e1;
		guest.domestic_b = person.domestic_b_e1;
		guest.sex_a = person.sex_a_e1;
		guest.sex_b = person.sex_b_e1;
		guest.nationality_a = person.nationality_a_e1;
		guest.nationality_b = person.nationality_b_e1;
		guest.language_a = person.languages_a_e1;
		guest.language_b = person.languages_b_e1;
		guest.notehost_a = person.notehost_a_e1;
		guest.notehost_b = person.notehost_b_e1;
		guest.noteorga_a = person.noteorga_a_e1;
		guest.noteorga_b = person.noteorga_b_e1;
		
		guest.verify();
		if (guest.isCorrect()) {

			/*
			 * modified for change logging support
			 * cklein
			 * 2008-02-12
			 */
			BeanChangeLogger clogger = new BeanChangeLogger( database );
			if (guest.id == null) {
				database.getNextPk(guest, context);
				Insert insert = database.getInsert(guest);
				insert.insert("pk", guest.id);
				if (!((PersonalConfigAA)cntx.personalConfig()).getGrants().mayReadRemarkFields()) {
					insert.remove("notehost_a");
					insert.remove("notehost_b");
					insert.remove("noteorga_a");
					insert.remove("noteorga_b");
				}
				context.execute(insert);
				
				clogger.logInsert( cntx.personalConfig().getLoginname(), guest );
			} else {
				Update update = database.getUpdate(guest);
				if (!((PersonalConfigAA)cntx.personalConfig()).getGrants().mayReadRemarkFields()) {
					update.remove("notehost_a");
					update.remove("notehost_b");
					update.remove("noteorga_a");
					update.remove("noteorga_b");
				}
				context.execute(update);

				Guest guestOld = ( Guest ) database.getBean( "Guest", guest.id );
				clogger.logUpdate( cntx.personalConfig().getLoginname(), guestOld, guest );
			}

			refreshDoctypes(cntx, database, context, guest, person);
			return true;
		}
		return false;
	}

    /**
     * Diese Methode aktualisiert die Dokumenttypen eines �bergebenen Gasts.
     * 
     * @param cntx OctopusContext
     * @param database Datenbank
     * @param guest Veranstaltungsgast
     * @param person Person-Eintrag
     * @throws IOException 
     * @throws BeanException 
     */
    protected void refreshDoctypes(OctopusContext cntx, Database database, ExecutionContext context, Guest guest, Person person) throws BeanException, IOException {
        if (guest == null || person == null) {
			logger.warn("Aktuallisieren der Dokumententypen von Gast #" +
					(guest == null || guest.id == null ? "null" : guest.id.toString()) +
					" nicht möglich. (Person #" +
					(person == null || person.id == null ? "null" : person.id.toString()) + ")");
			return;
        }
		logger.debug("Aktualisiere Dokumententypen der Person #" + person.id + ".");
		PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person);
		
		logger.debug("Aktualisiere Dokumententypen des Gast #" + guest.id + ".");
		
        Select select = database.getSelect("PersonDoctype").where(Expr.equal("fk_person", guest.person));
		select.selectAs("tperson_doctype.fk_doctype", "doctype");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.pk", "doctypeId");
		select.selectAs("tdoctype.addresstype", "doctypeAddresstype");
		select.selectAs("tdoctype.locale", "doctypeLocale");
		select.joinLeftOuter("veraweb.tdoctype", "fk_doctype", "tdoctype.pk");
        Map personDocTypes = personDocTypesToMap(database.getBeanList("PersonDoctype", select, context));
        select = database.getSelect("GuestDoctype").where(Expr.equal("fk_guest", guest.id));
        Map guestDocTypes = guestDocTypesToMap(database.getBeanList("GuestDoctype", select, context));
        select = database.getSelect("EventDoctype").where(Expr.equal("fk_event", guest.event));
		select.join("veraweb.tdoctype", "tevent_doctype.fk_doctype", "tdoctype.pk");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
        List eventDocTypes = database.getBeanList("EventDoctype", select, context);
		
        for (Iterator itDocTypes = eventDocTypes.iterator(); itDocTypes.hasNext(); ) {
            EventDoctype eventDt = (EventDoctype) itDocTypes.next();
            if (eventDt == null || eventDt.doctype == null)
                continue;
            PersonDoctype personDt = (PersonDoctype) personDocTypes.get(eventDt.doctype);
            if (personDt == null)
                continue;
            GuestDoctype guestDt = (GuestDoctype) guestDocTypes.get(eventDt.doctype);
			
            // vom Doctype nehmen
			Integer addresstype = personDt.doctypeAddresstype;
			Integer locale = personDt.doctypeLocale;
			
			// vom Person-Doctype nehmen
			if (personDt.addresstype != null && personDt.addresstype.intValue() != 0)
				addresstype = personDt.addresstype;
			if (personDt.locale != null && personDt.locale.intValue() != 0)
				locale = personDt.locale;
			// vom Guest-Doctype nehmen
			if (guestDt != null && guestDt.addresstype != null && guestDt.addresstype.intValue() != 0)
				addresstype = guestDt.addresstype;
			if (guestDt != null && guestDt.locale != null && guestDt.locale.intValue() != 0)
				locale = guestDt.locale;
			
            if (guestDt == null) {
                guestDt = new GuestDoctype();
                guestDt.doctype = eventDt.doctype;
                guestDt.guest = guest.id;
    			guestDt.addresstype = addresstype;
    			guestDt.locale = locale;
                guestDt.setModified(true);
            } else {
        		if (!equals(guestDt.addresstype, addresstype)) {
        			guestDt.addresstype = addresstype;
        			guestDt.setModified(true);
        		}
        		if (!equals(guestDt.locale, locale)) {
        			guestDt.locale = locale;
        			guestDt.setModified(true);
        		}
            }
            
            fillDoctype(guestDt, person, personDt);
            
            if (guestDt.isModified()) {
            	database.saveBean(guestDt, context, false);
            }
        }
    }

	static void fillDoctype(GuestDoctype guestDt, Person person, PersonDoctype personDt) {
		String newValue;
		PersonMemberFacade memberFacade;
		PersonAddressFacade addressFacade;
		
		// Freitextfelder
		if (!equals(guestDt.textfield, personDt.textfield)) {
			guestDt.textfield = personDt.textfield;
			guestDt.setModified(true);
		}
		if (!equals(guestDt.textfield_p, personDt.textfieldPartner)) {
			guestDt.textfield_p = personDt.textfieldPartner;
			guestDt.setModified(true);
		}
		if (!equals(guestDt.textjoin, personDt.textfieldJoin)) {
			guestDt.textjoin = personDt.textfieldJoin;
			guestDt.setModified(true);
		}
		
		// Hauptperson
		memberFacade = person.getMemberFacade(true, guestDt.locale);
		newValue = memberFacade.getSalutation();
		if (!equals(guestDt.salutation, newValue)) {
			guestDt.salutation = newValue;
			guestDt.setModified(true);
		}
		newValue = memberFacade.getTitle();
		if (!equals(guestDt.titel, newValue)) {
			guestDt.titel = newValue;
			guestDt.setModified(true);
		}
		newValue = memberFacade.getFirstname();
		if (!equals(guestDt.firstname, newValue)) {
			guestDt.firstname = newValue;
			guestDt.setModified(true);
		}
		newValue = memberFacade.getLastname();
		if (!equals(guestDt.lastname, newValue)) {
			guestDt.lastname = newValue;
			guestDt.setModified(true);
		}
		
		// Partner
		memberFacade = person.getMemberFacade(false, guestDt.locale);
		newValue = memberFacade.getSalutation();
		if (!equals(guestDt.salutation_p, newValue)) {
			guestDt.salutation_p = newValue;
			guestDt.setModified(true);
		}
		newValue = memberFacade.getTitle();
		if (!equals(guestDt.titel_p, newValue)) {
			guestDt.titel_p = newValue;
			guestDt.setModified(true);
		}
		newValue = memberFacade.getFirstname();
		if (!equals(guestDt.firstname_p, newValue)) {
			guestDt.firstname_p = newValue;
			guestDt.setModified(true);
		}
		newValue = memberFacade.getLastname();
		if (!equals(guestDt.lastname_p, newValue)) {
			guestDt.lastname_p = newValue;
			guestDt.setModified(true);
		}
		
		// Adressdaten
		addressFacade = person.getAddressFacade(guestDt.addresstype, guestDt.locale);
		newValue = addressFacade.getFunction();
		if (!equals(guestDt.function, newValue)) {
			guestDt.function = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getZipCode();
		if (!equals(guestDt.zipcode, newValue)) {
			guestDt.zipcode = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getCity();
		if (!equals(guestDt.city, newValue)) {
			guestDt.city = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getStreet();
		if (!equals(guestDt.street, newValue)) {
			guestDt.street = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getCountry();
		if (!equals(guestDt.country, newValue)) {
			guestDt.country = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getSuffix1();
		if (!equals(guestDt.suffix1, newValue)) {
			guestDt.suffix1 = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getSuffix2();
		if (!equals(guestDt.suffix2, newValue)) {
			guestDt.suffix2 = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getPhone();
		if (!equals(guestDt.fon, newValue)) {
			guestDt.fon = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getFax();
		if (!equals(guestDt.fax, newValue)) {
			guestDt.fax = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getEMail();
		if (!equals(guestDt.mail, newValue)) {
			guestDt.mail = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getUrl();
		if (!equals(guestDt.www, newValue)) {
			guestDt.www = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getMobile();
		if (!equals(guestDt.mobil, newValue)) {
			guestDt.mobil = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getCompany();
		if (!equals(guestDt.company, newValue)) {
			guestDt.company = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getPOBox();
		if (!equals(guestDt.pobox, newValue)) {
			guestDt.pobox = newValue;
			guestDt.setModified(true);
		}
		newValue = addressFacade.getPOBoxZipCode();
		if (!equals(guestDt.poboxzipcode, newValue)) {
			guestDt.poboxzipcode = newValue;
			guestDt.setModified(true);
		}
	}

    private static Map guestDocTypesToMap(List guestDocTypes) {
		Map result = new HashMap();
		for (Iterator it = guestDocTypes.iterator(); it.hasNext();) {
			GuestDoctype dt = (GuestDoctype) it.next();
			result.put(dt.doctype, dt);
		}
		return result;
	}

	private static Map personDocTypesToMap(List personDocTypes) {
		Map result = new HashMap();
		for (Iterator it = personDocTypes.iterator(); it.hasNext();) {
			PersonDoctype dt = (PersonDoctype) it.next();
			result.put(dt.doctype, dt);
		}
		return result;
	}

	private static boolean equals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

    //
    // Variablen
    //
    /** Logger f�r diese Klasse */
    private final static Logger logger = Logger.getLogger(GuestWorker.class);
}
