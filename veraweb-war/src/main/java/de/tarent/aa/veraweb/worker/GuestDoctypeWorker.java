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
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import de.tarent.aa.veraweb.beans.EventDoctype;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.GuestDoctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Aktionen zum Bearbeiten von {@link GuestDoctype}-Beans
 * bereit. 
 * 
 * @author mikel
 */
public class GuestDoctypeWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabeparameter der Aktion {@link #showDetail(OctopusContext)} */
	public static final String INPUT_showDetail[] = {};
    /**
     * Diese Octopus-Aktion liefert zu dem Gast "guest" ({@link Guest} aus dem Content)
     * in "allDoctype" eine Liste aller Dokumenttypen der Veranstaltung und --- falls
     * "doctype-id" in Request oder Session angegeben denjenigen, sonst den ersten
     * aus der Liste aller --- in "guestdoctype" den entsprechenden
     * Gast-Dokumenttyp-Detaileintrag.<br>
     * 
     * @param cntx
     * @throws BeanException
     * @throws IOException
     */
	public void showDetail(OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		Guest guest = (Guest)cntx.contentAsObject("guest");
		List eventDoctype = getEventDoctypeList(database, guest.event);
		cntx.setContent("allDoctype", eventDoctype);
		
		Integer doctype = getDoctype(cntx, eventDoctype);
		if (doctype != null) {
			GuestDoctype guestDoctype = getGuestDoctype(database, guest, doctype);
			cntx.setContent("guestdoctype", guestDoctype);
		}
	}

    /** Octopus-Eingabeparameter der Aktion {@link #saveDetail(OctopusContext)} */
    public static final String INPUT_saveDetail[] = {};
    /**
     * Diese Octopus-Aktion liest aus dem Request eine Gästedokumenttyp-Bean mit
     * Präfix "guestdoctype" und speichert sie in der Datenbank.
     * 
     * @param cntx
     * @throws BeanException
     * @throws IOException
     */
	public void saveDetail(OctopusContext cntx) throws BeanException, IOException {
		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
		
		GuestDoctype guestDt = (GuestDoctype)request.getBean("GuestDoctype", "guestdoctype");
		// Dokumenttyp mit speziellen Adresstype und Zeichensatz erzeugen.
		if (cntx.requestContains("create")) {
			assert guestDt != null && guestDt.guest != null && guestDt.doctype != null;
			Person person = (Person)
					database.getBean("Person",
					database.getSelect("Person").
					join("veraweb.tguest", "tperson.pk = tguest.fk_person AND tguest.pk", guestDt.guest.toString()));
			assert person != null;
			
			PersonDoctype personDt = (PersonDoctype)
					database.getBean("PersonDoctype",
					database.getSelect("PersonDoctype").where(Where.and(
							Expr.equal("fk_person", person.id),
							Expr.equal("fk_doctype", guestDt.doctype))));
			assert personDt != null;
			
			GuestWorker.fillDoctype(guestDt, person, personDt);
		}
		
		database.saveBean(guestDt);
	}

    //
    // geschützte Hilfsmethoden
    //
    /**
     * Diese Methode ermittelt die Veranstaltungsdokumenttypen zu der angegebenen
     * Veranstaltung.
     * 
     * @param database Datenbank
     * @param event Veranstaltung
     * @return Liste zugehöriger {@link EventDoctype}-Beans 
     */
	protected static List getEventDoctypeList(Database database, Integer event) throws BeanException, IOException {
		Select select = database.getSelect("EventDoctype");
		select.join("veraweb.tdoctype", "tevent_doctype.fk_doctype", "tdoctype.pk");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
		select.where(Expr.equal("fk_event", event));
		return database.getBeanList("EventDoctype", select);
	}

    /**
     * Diese Methode holt zu einem Gast und einem Dokumenttyp den zugehörigen
     * Gastdokumenttyp.
     * 
     * @param database Datenbank
     * @param guest Gast
     * @param doctype Dokumenttyp-ID
     * @return zugehörige {@link GuestDoctype}-Bohne.
     */
	protected static GuestDoctype getGuestDoctype(Database database, Guest guest, Integer doctype) throws BeanException, IOException {
		Select select = database.getSelect("GuestDoctype");
		select.where(Where.and(
				Expr.equal("fk_guest", guest.id),
				Expr.equal("fk_doctype", doctype)));
		GuestDoctype guestDoctype = (GuestDoctype)database.getBean("GuestDoctype", select);
		
		if (guestDoctype == null) {
			guestDoctype = new GuestDoctype();
			guestDoctype.guest = guest.id;
			guestDoctype.doctype = doctype;
		}
		return guestDoctype;
	}

    /**
     * Diese Methode ermittelt aus Request oder alternativ Session den aktuellen
     * Dokumenttyp "doctype-id", prüft anhand der übergebenen Liste der IDs der
     * verfügbaren Dokumenttypen, ob er im aktuellen Kontext erlaubt ist; falls
     * keiner angegeben war, wird der erste der Liste genommen.<br>
     * Der ermittelte Dokumenttyp wird in die Session eingetragen.<br>
     * TODO: ggfs auch im Content nachschauen<br>
     * TODO: wenn "doctype-id" nicht in der Liste, soll dann wirklich <code>null</code> oder besser der erste der Liste geliefert werden?
     * 
     * @param cntx
     * @param availableDoctypes Liste von Integern, IDs verfügbarer Dokumenttypen
     * @return ID des ausgewählten "aktuellen" Dokumenttyps
     */
	protected static Integer getDoctype(OctopusContext cntx, List availableDoctypes) {
		// doctype aus request bzw. session laden.
		Integer doctype = cntx.requestAsInteger("doctype-id");
		if (doctype == null || doctype.intValue() == 0) {
			doctype = (Integer)cntx.sessionAsObject("doctype-id");
		}
		
		// prüfen ob doctype verfügbar ist.
		if (doctype != null) {
			boolean available = false;
			for (Iterator it = availableDoctypes.iterator(); it.hasNext(); ) {
				if (doctype.equals(((EventDoctype)it.next()).doctype))
					available = true;
			}
			if (!available)
				doctype = null;
		} else {
			Iterator it = availableDoctypes.iterator();
			if (it.hasNext())
				doctype = ((EventDoctype)it.next()).doctype;
		}
		
		cntx.setSession("doctype-id", doctype);
		return doctype;
	}
}
