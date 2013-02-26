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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.aa.veraweb.beans.MailinglistAddress;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker stellt Aktionen zur Verwaltung (erstellen
 * und l�schen) von Verteilern in VerA.Web bereit.
 * 
 * @author Hendrik, Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailinglistWorker {
	/** Octopus-Eingabe-Parameter f�r {@link #guessMaillinglist(OctopusContext)} */
	public static final String INPUT_guessMaillinglist[] = {};
	/** Octopus-Ausgabe-Parameter f�r {@link #guessMaillinglist(OctopusContext)} */
	public static final String OUTPUT_guessMaillinglist = "mailinglistParams";

	/**
	 * Sch�tzt wie gro� der neue Verteiler werden wird und
	 * erweitert die Map <code>mailinglistParam</code> im Content
	 * um den Key <code>count</code>.
	 * 
	 * @param cntx Octopus-Context
	 * @return Map mit dem Key <code>count</code>
	 * @throws BeanException
	 * @throws IOException
	 */
	public Map guessMaillinglist(OctopusContext cntx) throws BeanException, IOException {
		Map result = (Map)cntx.contentAsObject("mailinglistParams");
		if (result == null) result = new HashMap();
		Database database = new DatabaseVeraWeb(cntx);
		GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
		
		List selection = (List)cntx.contentAsObject("listselection");
		if (selection != null && selection.size() != 0) {
			result.put("count", new Integer(selection.size()));
		} else {
			WhereList list = new WhereList();
			GuestListWorker.addGuestListFilter(search, list);
			
			result.put("count",
					database.getCount(
					database.getCount("Guest").
					where(list)));
		}
		return result;
	}


	/** Octopus-Eingabe-Parameter f�r {@link #createMailinglist(OctopusContext, Mailinglist)} */
	public static final String INPUT_createMailinglist[] = { "CONTENT:mailinglist" };
	/** Octopus-Ausgabe-Parameter f�r {@link #createMailinglist(OctopusContext, Mailinglist)} */
	public static final String OUTPUT_createMailinglist = "mailinglistParams";

	/**
	 * Erstellt einen neuen Verteiler und speichert die Anzahl der
	 * hinzugef�gten Adressen in der Map <code>mailinglistParam</code>
	 * im Content im Key <code>count</code>.
	 * 
	 * @param cntx
	 * @param mailinglist
	 * @return Map mit dem Key <code>count</code>
	 * @throws BeanException
	 * @throws IOException
	 */
	public Map createMailinglist(OctopusContext cntx, Mailinglist mailinglist) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
		
		// Adresstype und Locale laden
		Integer addresstype = new Integer(PersonConstants.ADDRESSTYPE_BUSINESS);
		Integer locale = new Integer(PersonConstants.LOCALE_LATIN);
		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		Doctype doctype = (freitextfeld == null) ? null : (Doctype)
				database.getBean("Doctype",
				database.getSelect("Doctype").
				where(Expr.equal("pk", freitextfeld)));
		if (doctype != null && doctype.locale != null) {
			locale = doctype.locale;
		}
		if (doctype != null && doctype.addresstype != null) {
			addresstype = doctype.addresstype;
		}
		
		// Bedingung des Verteilers definieren
		Clause clause;
		List selection = (List)cntx.contentAsObject("listselection");
		if (selection != null && selection.size() != 0) {
			clause = Where.and(
					Expr.equal("tguest.fk_event", search.event),
					Expr.in("tguest.pk", selection));
		} else {
			clause = new WhereList();
			GuestListWorker.addGuestListFilter(search, (WhereList)clause);
		}
		
		// Personen hinzuf�gen
		int savedAddresses = addMailinglist(cntx, mailinglist, freitextfeld, addresstype, locale, clause);
		
		// Ergebnis ist Params eintragen
		Map result = (Map)cntx.contentAsObject("mailinglistParams");
		if (result == null) result = new HashMap();
		result.put("count", new Integer(savedAddresses));
		return result;
	}

	/**
	 * F�gt G�ste einem bestehendem Verteiler anhand der �bergebenen Bedingung
	 * <code>clause</code> hinzu. Die Bedingung darf dabei Einschr�nkungen auf
	 * die Tabellen <code>tguest</code> und <code>tperson</code> vornehmen.
	 * <br><br>
	 * Die Adresse die in den entsprechenden Verteiler eingetragen wird, wird
	 * aus dem entsprechenden Personen-Dokumenttyp 'Freitextfeld' gew�hlt.
	 * Sollte dieser nicht existieren wird entsprechend des �bergebenen
	 * Adresstyps und Zeichensatzes in den normalen Personen Datengesucht. (Im
	 * Zweifel wird auf die gesch�ftlichen lateinischen Daten zur�ckgegriffen.)
	 * 
	 * @param cntx Octopus-Context
	 * @param mailinglist Verteiler dem G�ste hinzugef�gt werden sollen.
	 * @param doctype ID des Dokumenttypes der verwendet werden soll.
	 * @param addresstype Adresstyp
	 * @param locale Zeichensatz
	 * @param clause Bedingung
	 * @return Anzahl der hinzugef�gten Adressen.
	 * @throws BeanException 
	 * @throws IOException 
	 */
	protected int addMailinglist(OctopusContext cntx, Mailinglist mailinglist, Integer doctype, Integer addresstype, Integer locale, Clause clause) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		int savedAddresses = 0;
		
		String personMail = getMailColumn(addresstype, locale);
		String personFax = getFaxColumn(addresstype, locale);
		
		Select select = SQL.SelectDistinct( database ).
				from("veraweb.tguest").
				selectAs("tguest.pk", "guest").
				selectAs("tperson.pk", "person").
				selectAs(personMail, "mail2").
				selectAs(personFax, "fax2").
				selectAs("tperson.mail_a_e1", "mail3").
				selectAs("tperson.fax_a_e1", "fax3").
				joinLeftOuter("veraweb.tperson", "tperson.pk", "tguest.fk_person");
		if (doctype != null) {
			select.selectAs("tguest_doctype.pk IS NOT NULL", "hasguestdoctype");
			select.selectAs("tguest_doctype.fk_doctype", "doctype");
			select.selectAs("tguest_doctype.mail", "mail1");
			select.selectAs("tguest_doctype.fax", "fax1");
			select.joinLeftOuter("veraweb.tguest_doctype", "tguest_doctype.fk_guest = tguest.pk AND tguest_doctype.fk_doctype", doctype.toString());
		} else {
			select.selectAs("FALSE", "hasguestdoctype");
			select.selectAs("NULL", "doctype");
			select.selectAs("NULL", "mail1");
			select.selectAs("NULL", "fax1");
		}
		
		select.where(new RawClause("(" + clause.clauseToString() + ") AND (" +
						"length(tguest_doctype.mail) != 0 OR " +
						"length(tguest_doctype.fax) != 0 OR " +
						"length(" + personMail + ") != 0 OR " +
						"length(" + personFax + ") != 0 OR " +
						"length(tperson.mail_a_e1) != 0 OR " +
						"length(tperson.fax_a_e1) != 0)"));
		
		for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			Integer person = (Integer)data.get("person");
			if (((Boolean)data.get("hasguestdoctype")).booleanValue()) {
				String mail1 = (String)data.get("mail1");
				String fax1 = (String)data.get("fax1");
				
				if (mail1 != null && mail1.length() != 0) {
					if (savePerson(database, mailinglist.id, person, getClearMailAddress(cntx, mail1)))
						savedAddresses++;
				} else if (fax1 != null && fax1.length() != 0) {
					if (savePerson(database, mailinglist.id, person, getClearFaxNumber(cntx, fax1)))
						savedAddresses++;
				}
			} else {
				String mail2 = (String)data.get("mail2");
				String fax2 = (String)data.get("fax2");
				String mail3 = (String)data.get("mail3");
				String fax3 = (String)data.get("fax3");
				
				if (mail2 != null && mail2.length() != 0) {
					if (savePerson(database, mailinglist.id, person, getClearMailAddress(cntx, mail2)))
						savedAddresses++;
				} else if (fax2 != null && fax2.length() != 0) {
					if (savePerson(database, mailinglist.id, person, getClearFaxNumber(cntx, fax2)))
						savedAddresses++;
				} else if (mail3 != null && mail3.length() != 0) {
					if (savePerson(database, mailinglist.id, person, getClearMailAddress(cntx, mail3)))
						savedAddresses++;
				} else if (fax3 != null && fax3.length() != 0) {
					if (savePerson(database, mailinglist.id, person, getClearFaxNumber(cntx, fax3)))
						savedAddresses++;
				}
			}
		}
		
		return savedAddresses;
	}

	/**
	 * @param addresstype
	 * @param locale
	 * @return Datenbank-Spalte in der die eMail-Adresse steht.
	 */
	private String getMailColumn(Integer addresstype, Integer locale) {
		int a = addresstype != null ? addresstype.intValue() : PersonConstants.ADDRESSTYPE_BUSINESS;
		int l = locale != null ? locale.intValue() : PersonConstants.LOCALE_LATIN;
		
		switch (a * 3 + l) {
			case 4:
				return "tperson.mail_a_e1";
			case 5:
				return "tperson.mail_a_e2";
			case 6:
				return "tperson.mail_a_e3";
			case 7:
				return "tperson.mail_b_e1";
			case 8:
				return "tperson.mail_b_e2";
			case 9:
				return "tperson.mail_b_e3";
			case 10:
				return "tperson.mail_c_e1";
			case 11:
				return "tperson.mail_c_e2";
			case 12:
				return "tperson.mail_c_e3";
		}
		return "tperson.mail_a_e1";
	}

	/**
	 * @param addresstype
	 * @param locale
	 * @return Datenbank-Spalte in der die Faxnummer steht.
	 */
	private String getFaxColumn(Integer addresstype, Integer locale) {
		int a = addresstype != null ? addresstype.intValue() : PersonConstants.ADDRESSTYPE_BUSINESS;
		int l = locale != null ? locale.intValue() : PersonConstants.LOCALE_LATIN;
		
		switch (a * 3 + l) {
			case 4:
				return "tperson.fax_a_e1";
			case 5:
				return "tperson.fax_a_e2";
			case 6:
				return "tperson.fax_a_e3";
			case 7:
				return "tperson.fax_b_e1";
			case 8:
				return "tperson.fax_b_e2";
			case 9:
				return "tperson.fax_b_e3";
			case 10:
				return "tperson.fax_c_e1";
			case 11:
				return "tperson.fax_c_e2";
			case 12:
				return "tperson.fax_c_e3";
		}
		return "tperson.fax_a_e1";
	}

	/**
	 * Speichert eine Person zu einer Mailingliste, geht dabei wie folgt vor:<br><br>
	 * 
	 * 1. Schaut ob ein entsprechender GuestDoctype existiert und kopiert
	 *    ggf. aus diesem eMail-Adresse oder Fax-Nimmer.<br>
	 * 2. Falls kein entsprechender Eintrag gefunden wurde wird die allgemeine
	 *    Person zu dem �bergebenem Gast gesucht und die eMail-Adresse
	 *    oder Fax-Nummer entsprechend des Dokumenttypens "Etikett" �bernommen.
	 * 
	 * @param database
	 * @param mailinglist
	 * @param person
	 * @param address
	 * @return True wenn ein entsprechender Eintrag gespeichert wurde.
	 */
	protected boolean savePerson(Database database, Integer mailinglist, Integer person, String address) throws BeanException, IOException {
		MailinglistAddress mla = new MailinglistAddress();
		mla.mailinglist = mailinglist;
		mla.person = person;
		mla.address = address;
		mla.verify();
		if (mla.isCorrect()) {
			database.execute(database.getInsert(mla));
			return true;
		}
		return false;
	}

	/**
	 * Gibt eine 'gesauberte' Faxnummer mit dem Zusatz '@fax' zur�ck.
	 * 
	 * @param cntx Octopus-Context
	 * @param number Faxnummer
	 * @return Faxnummer
	 */
	public static String getClearFaxNumber(OctopusContext cntx, String number) {
		String faxdomain = ConfigWorker.getString(cntx, "faxdomain");
		if (faxdomain == null || "".equals(faxdomain)) faxdomain = "@fax.aa";
		
		return getOnlyNumbers(cntx, number) + faxdomain;
	}

	/**
	 * Gibt eine 'gesauberte' eMail-Adresse zur�ck.
	 * 
	 * @param cntx Octopus-Context
	 * @param mail eMail-Adresse
	 * @return eMail-Adresse
	 */
	public String getClearMailAddress(OctopusContext cntx, String mail) {
		return mail;
	}

	/**
	 * @param number Nummer mit Buchstaben
	 * @return Nur noch Nummern
	 */
	public static String getOnlyNumbers(OctopusContext cntx, String number) {
		if (number == null) return "";
		
		if (!areacodeLoaded) {
			areacodeString = cntx.moduleConfig().getParam("phoneAreacode");
			areacodeLoaded = true;
		}
		
		StringBuffer buffer = new StringBuffer(number.length());
		for (int i = 0; i < number.length(); i++) {
			char c = number.charAt(i);
			if (c >= '0' && c <= '9') {
				buffer.append(c);
			} else if (c == '+') {
				buffer.append(areacodeString);
			}
		}
		return buffer.toString();
	}

	private static boolean areacodeLoaded = false;
	private static String areacodeString = null;
}
