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

/* $Id$ */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestDoctype;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.beans.facade.GuestMemberFacade;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
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
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker dient der Anzeige und Bearbeitung von Details von G�sten.
 */
public class GuestDetailWorker extends GuestListWorker {
    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Integer)} */
	public static final String INPUT_showDetail[] = { "id", "offset" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #showDetail(OctopusContext, Integer, Integer)} */
	public static final boolean MANDATORY_showDetail[] = { false, false };
	/**
	 * Diese Octopus-Aktion l�dt Details zu einem Gast, der �ber ID oder �ber Position in der Ergebnisliste zu einer
	 * aktuellen G�stesuche identifiziert wird.<br>
	 * Wird der Gast oder die dazugeh�rende Person nicht gefunden, so wird der Status "notfound" gesetzt. Ansonsten werden
	 * folgende Eintr�ge im Octopus-Content gesetzt:
	 * <ul>
	 * <li> "guest" mit den Gastdetails
	 * <li> "person" mit den Details der zugeh�rigen Person
	 * <li> "main" mit der Facade zur Hauptperson
	 * <li> "partner" mit der Facade zur Partnerperson
	 * <li> "address" mit der Facade zur Adresse zum Standard-Freitextfeld
	 * </ul>
	 * 
	 * @param cntx
	 *          Octopus-Kontext
	 * @param guestid
	 *          ID des Gasts
	 * @param offset
	 *          alternativ: Offset des Gasts in der aktuellen G�stesuche
	 */
	@SuppressWarnings("unchecked")
	public void showDetail(OctopusContext cntx, Integer guestid, Integer offset) throws BeanException, IOException
	{
		Database database = getDatabase(cntx);

		GuestSearch search = getSearch(cntx);
		Guest guest = getGuest(cntx, search.event, guestid, offset);
		if (guest == null)
		{
			logger.error("showDetail konnte Gast #" + guestid + " unerwartet nicht laden.");
			cntx.setStatus("notfound");
			return;
		}

		Person person = (Person) database.getBean("Person", guest.person);
		if (person == null)
		{
			logger.error("showDetail konnte Person #" + guest.person + " unerwartet nicht laden.");
			cntx.setStatus("notfound");
			return;
		}

		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		Doctype doctype = (Doctype) database.getBean("Doctype", freitextfeld);

		Integer addresstype = doctype != null ? doctype.addresstype : null;
		Integer locale = doctype != null ? doctype.locale : null;

		cntx.setContent("guest", guest);
		cntx.setContent("person", person);
		cntx.setContent("main", person.getMemberFacade(true, locale));
		cntx.setContent("partner", person.getMemberFacade(false, locale));
		cntx.setContent("address", person.getAddressFacade(addresstype, locale));

		// Bug 1591 Im Kopf der Gaesteliste sollen nicht die Stammdaten, sondern die
		// Daten der Gaesteliste angezeigt werden
		try
		{
			if (freitextfeld == null)
			{
				//Kopfdaten der Gaesteliste: Anzeige der Stammdaten oder Kopien fuer Gaesteliste
				cntx.setContent("showGuestListData", new Boolean(false));
			} else
			{
				GuestDoctype guestDoctype = new GuestDoctype();

				Select select = database.getSelect(guestDoctype);
				guestDoctype.doctype = freitextfeld;
				guestDoctype.guest = guest.id;
				select.where(database.getWhere(guestDoctype));

				guestDoctype = (GuestDoctype) database.getBean("GuestDoctype", select);
				
				cntx.setContent("showGuestListData", new Boolean(guestDoctype != null));
				
				cntx.setContent("guestListData", guestDoctype);
			}
		} catch (Exception e)
		{
			logger.warn("zum Gast: " + guestid + " und Doctyp: " + freitextfeld + " kann Bean 'GuestDoctype' nicht geladen werden", e);
			cntx.setContent("showGuestListData", new Boolean(false));
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)} */
    public static final String INPUT_saveDetail[] = {};
    /**
		 * Diese Methode speichert Details zu einem Gast.<br>
		 * Der Gast wird aus dem Octopus-Request gelesen. Je nach Einladungsart und -status werden dann Korrekturen an den
		 * laufenden Nummern ausgef�hrt und die Bean wird gepr�ft ({@link BeanException} falls sie unvollst�ndig ist oder
		 * ung�ltige Eintr�ge enth�lt). Schlie�lich wird sie gespeichert und passend wird im Octopus-Content unter
		 * "countInsert" oder "countUpdate" 1 eingetragen. 
		 * Wenn der Nutzer dies im GUI bestaetigt hat, wird der Rang der Kategorie aus den Stammdaten der Person uebernommen.
		 * 
		 * @param cntx
		 *          Octopus-Content
		 * @throws BeanException
		 *           bei ung�ltigen oder unvollst�ndigen Eintr�gen
		 */
	public void saveDetail(OctopusContext cntx) throws BeanException, IOException, Exception
	{
		Request request = getRequest(cntx);
		Database database = getDatabase(cntx);
		TransactionContext context = database.getTransactionContext();

		try
		{
			Guest guest = (Guest) request.getBean("Guest", "guest");
			if (guest.reserve != null && guest.reserve.booleanValue())
			{
				guest.orderno_a = null;
				guest.orderno_b = null;
			}

			try
			{
				// Der Rang der Kategorie wird aus den Stammdaten der Person gezogen,
				// wenn Nutzer dies will und wenn kein Rang vorbelegt ist.
				if (cntx.requestAsBoolean("fetchRankFromMasterData").booleanValue() && guest.rank == null)
				{
					if (guest.person != null && guest.category != null)
					{
						Select sel = database.getSelect("PersonCategorie").where(
							Where.and(Expr.equal("fk_person", guest.person), Expr.equal("fk_categorie", guest.category)));
						sel.orderBy(null); //im Bean.property steht ein Verweis auf andere Tabelle!

						PersonCategorie perCat = (PersonCategorie) database.getBean("PersonCategorie", sel);
						if (perCat != null)
						{
							guest.rank = perCat.rank;
						}
					}
				}
			} catch (Exception ex)
			{
				logger.warn("Kann den Rang der Gast-Kategorie nicht aus dem Personenstamm laden", ex);
			}

			if (guest.invitationtype.intValue() == EventConstants.TYPE_MITPARTNER)
			{
				if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2)
					guest.orderno_a = null;
				if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2)
					guest.orderno_b = null;
			} else if (guest.invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER)
			{
				if (guest.invitationstatus_a != null && guest.invitationstatus_a.intValue() == 2)
					guest.orderno_a = null;
				guest.orderno_b = null;
			} else if (guest.invitationtype.intValue() == EventConstants.TYPE_NURPARTNER)
			{
				guest.orderno_a = null;
				if (guest.invitationstatus_b != null && guest.invitationstatus_b.intValue() == 2)
					guest.orderno_b = null;
			}

			guest.verify();

			/*
			 * modified to support change logging
			 * cklein 2008-02-12
			 */
			BeanChangeLogger clogger = new BeanChangeLogger( database, context );
			if (guest.id == null)
			{
				cntx.setContent("countInsert", new Integer(1));
				database.getNextPk(guest, context);
				Insert insert = database.getInsert(guest);
				insert.insert("pk", guest.id);
				if (!((PersonalConfigAA) cntx.personalConfig()).getGrants().mayReadRemarkFields())
				{
					insert.remove("notehost_a");
					insert.remove("notehost_b");
					insert.remove("noteorga_a");
					insert.remove("noteorga_b");
				}
				context.execute(insert);

				clogger.logInsert( cntx.personalConfig().getLoginname(), guest );
			} else
			{
				Guest guestOld = ( Guest ) database.getBean( "Guest", guest.id, context);

				cntx.setContent("countUpdate", new Integer(1));
				Update update = database.getUpdate(guest);
				if (!((PersonalConfigAA) cntx.personalConfig()).getGrants().mayReadRemarkFields())
				{
					update.remove("notehost_a");
					update.remove("notehost_b");
					update.remove("noteorga_a");
					update.remove("noteorga_b");
				}
				context.execute(update);

				// retrieve old instance of guest for update logging
				// we will quietly ignore non existing old entities and simply omit logging
				if ( guestOld != null )
				{
					clogger.logUpdate( cntx.personalConfig().getLoginname(), guestOld, guest );
				}
			}

			context.commit();
		}
		// cklein
		// 2008-02-13
		// prior to the change, there was a finally here
		// which caused the transaction to be always rolled back
		catch( BeanException e )
		{
			context.rollBack();
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #showTestGuest(OctopusContext)} */
	public static final String INPUT_showTestGuest[] = {};
	/**
	 * Diese Octopus-Aktion liefert Details zu einem Test-Gast. Dieser wird unter
     * "guest" im Octopus-Content eingetragen.
	 * 
	 * @param cntx Octopus-Kontext
	 */
	public void showTestGuest(OctopusContext cntx) throws BeanException {
		Guest guest = new Guest();
		int random = new Random().nextInt(10000);
		String suffix = " [test-" + random + "]";
		showTestGuest(guest.getMain(), suffix + " (Hauptperson)", random);
		showTestGuest(guest.getPartner(), suffix + " (Partner)", random);
		guest.verify();
		cntx.setContent("guest", guest);
	}

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt Test-Gast-Daten in einer Gast-Member-Facade.
     * 
     * @param facade zu f�llende Gast-Member-Facade
     * @param suffix Feldinhaltanhang f�r Textfelder
     * @param random Wert f�r laufende Nummer, Sitznummer und Tischnummer
     */
	protected void showTestGuest(GuestMemberFacade facade, String suffix, int random) {
		facade.setColor("Farbe" + suffix);
		facade.setColorFK(new Integer(new Random().nextInt(4)));
		facade.setInvitationStatus(new Integer(new Random().nextInt(3) + 1));
		facade.setInvitationType(new Integer(new Random().nextInt(3)));
		facade.setLanguages("Sprachen" + suffix);
		facade.setNationality("Nationalit�t" + suffix);
		facade.setNoteHost("Bemerkung (Gastgeber)" + suffix);
		facade.setNoteOrga("Bemerkung (Organisation)" + suffix);
		facade.setOrderNo(new Integer(random));
		facade.setSeatNo(new Integer(random));
		facade.setTableNo(new Integer(random));
	}

    /**
     * Diese Methode liefert eine {@link Guest}-Instanz passend zu den �bergebenen
     * Kriterien. Falls eine Gast- und Veranstaltungs-ID vorliegt, wird der Gast
     * anhand dieser selektiert, sonst wird er �ber den �bergebenen Offset in der
     * Ergebnisliste zur aktuellen Gastsuche ausgew�hlt.<br>
     * 
     * @param cntx Octopus-Kontext
     * @param eventid Veranstaltungs-ID f�r Selektion �ber ID
     * @param guestid Gast-ID f�r Selektion �ber ID
     * @param offset Gast-Offset f�r Selektion �ber Offset in Suchergebnisliste
     * @return der selektierte Gast oder <code>null</code>
     */
	protected Guest getGuest(OctopusContext cntx, Integer eventid, Integer guestid, Integer offset) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		// Offset aus der GuestListSearch lesen
		GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
		
		// Offset in den aktuellen Suchfilter-Bereich legen.
		if (offset == null || offset.intValue() < 1) {
			offset = search.offset;
		}
		
		Select select = database.getCount(BEANNAME);
		extendWhere(cntx, select);
		Integer count = database.getCount(select);
		if (offset == null || offset.intValue() < 1)
			offset = new Integer(1);
		else if (offset.intValue() > count.intValue())
			offset = count;
		
		// Offset und Count in die GuestListSearch schreiben
		search.offset = offset;
		search.count = count;
		
		// Select bauen das entweder ID oder das Offset verwenden um einen Gast zu laden
		select = database.getSelect(BEANNAME);
		select.joinLeftOuter("veraweb.tcolor c1", "tguest.fk_color", "c1.pk");
		select.joinLeftOuter("veraweb.tcolor c2", "tguest.fk_color_p", "c2.pk");
		select.selectAs("c1.color", "color_a");
		select.selectAs("c2.color", "color_b");
		extendColumns(cntx, select);
		if (guestid != null && guestid.intValue() != 0) {
			if (logger.isEnabledFor(Priority.DEBUG))
				logger.log(Priority.DEBUG, "GuestDetail show for id " + guestid);
			select.where(Where.and(
					Expr.equal("tguest.pk", guestid),
					Expr.equal("tguest.fk_event", eventid)));
			
			Guest guest = (Guest)database.getBean(BEANNAME, select);
			if (guest != null) {
				// Gast wurde gefunden. Durch diese Suche (per ID) konnte sich aber ggf. die
				// Position innerhalb der Liste ver�ndert werden, daher wird diese nun neu
				// Kalkuliert.
				Select selectForPosition = database.getSelect("Guest");
				extendColumns(cntx, selectForPosition);
				extendWhere(cntx, selectForPosition);
				
				int newOffset = 1;
				for (Iterator it = database.getList(selectForPosition, database).iterator(); it.hasNext(); ) {
					Integer id = (Integer)((Map)it.next()).get("id");
					if (id.intValue() == guestid.intValue()) {
						search.offset = new Integer(newOffset);
						break;
					}
					newOffset++;
				}
				
				return guest;
			}
		}
		
		if (logger.isEnabledFor(Priority.DEBUG))
			logger.log(Priority.DEBUG, "GuestDetail show for offset " + offset);
		WhereList list = new WhereList();
		addGuestListFilter(search, list);
		select.where(list);
		select.Limit(new Limit(new Integer(1), new Integer(offset.intValue() - 1)));
		return (Guest)database.getBean(BEANNAME, select);
	}

    /** Logger dieser Klasse */
    public static final Logger logger = Logger.getLogger(GuestDetailWorker.class);
}
