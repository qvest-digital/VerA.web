package de.tarent.aa.veraweb.worker;

/*-
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
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.aa.veraweb.beans.MailinglistAddress;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dieser Octopus-Worker stellt Aktionen zur Verwaltung (erstellen
 * und löschen) von Verteilern in VerA.web bereit.
 *
 * @author Hendrik, Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailinglistWorker {

	/** Octopus-Eingabe-Parameter für {@link #createMailinglist(OctopusContext, Mailinglist)} */
	public static final String INPUT_createMailinglist[] = { "CONTENT:mailinglist" };
	/** Octopus-Ausgabe-Parameter für {@link #createMailinglist(OctopusContext, Mailinglist)} */
	public static final String OUTPUT_createMailinglist = "mailinglistParams";

	/**
	 * Erstellt einen neuen Verteiler und speichert die Anzahl der
	 * hinzugefügten Adressen in der Map <code>mailinglistParam</code>
	 * im Content im Key <code>count</code>.
	 *
	 * @param octopusContext The {@link OctopusContext}
	 * @param mailinglist FIXME
	 * @return Map mit dem Key <code>count</code>
	 * @throws BeanException
	 * @throws IOException
	 */
	public Map createMailinglist(OctopusContext octopusContext, Mailinglist mailinglist) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(octopusContext);

		// Adresstype und Locale laden
		Integer addresstype = PersonConstants.ADDRESSTYPE_BUSINESS;
		Integer locale = PersonConstants.LOCALE_LATIN;
		Integer freitextfeld = ConfigWorker.getInteger(octopusContext, "freitextfeld");

		// Bedingung des Verteilers definieren
		int savedAddresses = 0;

		List selection = (List)octopusContext.contentAsObject("listselection");
		if (selection != null && selection.size() != 0) {
			savedAddresses = countSavedAddresses(octopusContext, mailinglist, addresstype, locale, freitextfeld, selection);
		} else {
			savedAddresses = createMailinglistBySelectAllOption(octopusContext, mailinglist);
		}

		if (savedAddresses == 0) {
			LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
			LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);
            octopusContext.setStatus("error");
            mailinglist.addError(languageProvider.getProperty("MAILING_LIST_NO_ADDRESSES"));
    		if (mailinglist.id != null) {
				final TransactionContext transactionContext = database.getTransactionContext();
				transactionContext.execute(database.getDelete(mailinglist));
				transactionContext.commit();
    		}
		}

		// Ergebnis ist Params eintragen
		Map result = (Map)octopusContext.contentAsObject("mailinglistParams");
		if (result == null) result = new HashMap();
		result.put("count", savedAddresses);
		return result;
	}

	private int countSavedAddresses(OctopusContext octopusContext, Mailinglist mailinglist, Integer addresstype, Integer locale, Integer freitextfeld, List selection) throws BeanException, IOException {
        if (octopusContext.contentAsObject("search") instanceof PersonSearch){
            return countSavedAddressesOnPersonSearch(octopusContext, mailinglist, addresstype, locale, selection);
        } else if (octopusContext.contentAsObject("search") instanceof GuestSearch){
            return countSavedAddressesOnGuestSearch(octopusContext, mailinglist, selection);
        }
        return 0;
	}

	private int countSavedAddressesOnPersonSearch(OctopusContext octopusContext, Mailinglist mailinglist, Integer addresstype, Integer locale, List selection) throws BeanException, IOException {
        final Clause clause = Expr.in("tperson.pk", selection);
		return addMailinglistFromPerson(octopusContext, mailinglist, addresstype, locale, clause);
	}

	private int countSavedAddressesOnGuestSearch(OctopusContext octopusContext, Mailinglist mailinglist, List selection) throws BeanException, IOException {
		final GuestSearch search = (GuestSearch)octopusContext.contentAsObject("search");
        final Clause clause = Where.and(Expr.equal("tguest.fk_event", search.event), Expr.in("tguest.pk", selection));
		return addMailinglistFromGuest(octopusContext, mailinglist, clause);
	}

	/**
	 * Create mailing list with "Select all" checkbox checked.
	 *
	 * @param octopusContext The {@link OctopusContext}
	 * @param mailinglist FIXME
	 * @return FIXME
	 * @throws BeanException
     * @throws IOException
     */

	private int createMailinglistBySelectAllOption(OctopusContext octopusContext, Mailinglist mailinglist) throws BeanException, IOException {

		if (octopusContext.contentAsObject("search") instanceof GuestSearch){
            final GuestSearch search = (GuestSearch)octopusContext.contentAsObject("search");
            final WhereList whereList = new WhereList();
			search.addGuestListFilter(whereList);
            final Clause clause = whereList;
            return addMailinglistFromGuest(octopusContext, mailinglist, clause);
        }
		return 0;
	}

	/**
	 * Fügt Gäste einem bestehendem Verteiler anhand der übergebenen Bedingung
	 * <code>clause</code> hinzu. Die Bedingung darf dabei Einschränkungen auf
	 * die Tabellen <code>tguest</code> und <code>tperson</code> vornehmen.
	 * <br><br>
	 * Die Adresse die in den entsprechenden Verteiler eingetragen wird, wird
	 * aus dem entsprechenden Personen-Dokumenttyp 'Freitextfeld' gewählt.
	 * Sollte dieser nicht existieren wird entsprechend des übergebenen
	 * Adresstyps und Zeichensatzes in den normalen Personen Datengesucht. (Im
	 * Zweifel wird auf die geschäftlichen lateinischen Daten zurückgegriffen.)
	 *
	 * @param cntx Octopus-Context
	 * @param mailinglist Verteiler dem Gäste hinzugefügt werden sollen.
	 * @param addresstype Adresstyp
	 * @param locale Zeichensatz
	 * @param clause Bedingung
	 * @return Anzahl der hinzugefügten Adressen.
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int addMailinglistFromPerson(OctopusContext cntx, Mailinglist mailinglist, Integer addresstype, Integer locale, Clause clause) throws BeanException, IOException {
		final Database database = new DatabaseVeraWeb(cntx);
		final String personMail = getMailColumn(addresstype, locale);
		final String personFax = getFaxColumn(addresstype, locale);
		final Select select = SQL.Select(database).setDistinct(true).
			from("veraweb.tperson").
			selectAs("tperson.pk", "person").
			selectAs(personMail, "mail2").
			selectAs(personFax, "fax2").
			selectAs("tperson.mail_a_e1", "mail3").
			selectAs("tperson.fax_a_e1", "fax3");
			select.selectAs("NULL", "mail1");
			select.selectAs("NULL", "fax1");

		select.where(new RawClause("(" + clause.clauseToString(database) + ") AND (" +
			"length(" + personMail + ") != 0 OR " +
			"length(" + personFax + ") != 0 OR " +
			"length(tperson.mail_a_e1) != 0 OR " +
			"length(tperson.fax_a_e1) != 0)"));

		return addMailinglist(cntx, mailinglist, select);
	}

	/**
	 * Fügt Gäste einem bestehendem Verteiler anhand der übergebenen Bedingung
	 * <code>clause</code> hinzu. Die Bedingung darf dabei Einschränkungen auf
	 * die Tabellen <code>tguest</code> und <code>tperson</code> vornehmen.
	 * <br><br>
	 * Die Adresse die in den entsprechenden Verteiler eingetragen wird, wird
	 * aus dem entsprechenden Personen-Dokumenttyp 'Freitextfeld' gewählt.
	 * Sollte dieser nicht existieren wird entsprechend des übergebenen
	 * Adresstyps und Zeichensatzes in den normalen Personen Datengesucht. (Im
	 * Zweifel wird auf die geschäftlichen lateinischen Daten zurückgegriffen.)
	 *
	 * @param octopusContext Octopus-Context
	 * @param mailinglist Verteiler dem Gäste hinzugefügt werden sollen.
	 * @param clause Bedingung
	 * @return Anzahl der hinzugefügten Adressen.
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int addMailinglistFromGuest(OctopusContext octopusContext, Mailinglist mailinglist, Clause clause) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(octopusContext);
		Select select = SQL.Select(database).setDistinct(true).
			from("veraweb.tguest").
			selectAs("tguest.pk", "guest").
			selectAs("tperson.pk", "person").
			selectAs("tperson.mail_a_e1", "mail3").
			selectAs("tperson.fax_a_e1", "fax3").
			selectAs("NULL", "mail1").
			selectAs("NULL", "fax1").
			joinLeftOuter("veraweb.tperson", "tperson.pk", "tguest.fk_person").
			where(new RawClause("(" + clause.clauseToString(database) + ") AND (" +
				"length(tperson.mail_a_e1) != 0 OR " +
				"length(tperson.fax_a_e1) != 0)"));

		return addMailinglist(octopusContext, mailinglist, select);
	}

	/**
	 * Hilfsfunktion für
	 * addMailinglistFromPerson
	 * und
	 * addMailinglistFromGuest
	 */
	protected int addMailinglist(OctopusContext octopusContext, Mailinglist mailinglist, Select select) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(octopusContext);
		int savedAddresses = 0;

		for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {

			Map data = (Map) it.next();
			Integer person = (Integer) data.get("person");
			String mail2 = (String) data.get("mail2");
			String fax2 = (String) data.get("fax2");
			String mail3 = (String) data.get("mail3");
			String fax3 = (String) data.get("fax3");

			if (mail2 != null && mail2.length() != 0) {
				if (savePerson(database, mailinglist.id, person, getClearMailAddress(mail2)))
					savedAddresses++;
			} else if (fax2 != null && fax2.length() != 0) {
				if (savePerson(database, mailinglist.id, person, getClearFaxNumber(octopusContext, fax2)))
					savedAddresses++;
			} else if (mail3 != null && mail3.length() != 0) {
				if (savePerson(database, mailinglist.id, person, getClearMailAddress(mail3)))
					savedAddresses++;
			} else if (fax3 != null && fax3.length() != 0) {
				if (savePerson(database, mailinglist.id, person, getClearFaxNumber(octopusContext, fax3)))
					savedAddresses++;
			}
		}

		return savedAddresses;
	}

	/**
	 * @param addresstype FIXME
	 * @param locale FIXME
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
	 * @param addresstype FIXME
	 * @param locale FIXME
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
	 *    Person zu dem übergebenem Gast gesucht und die eMail-Adresse
	 *    oder Fax-Nummer entsprechend des Dokumenttypens "Etikett" übernommen.
	 *
	 * @param database The {@link Database}
	 * @param mailinglist FIXME
	 * @param person FIXME
	 * @param address FIXME
	 * @return True wenn ein entsprechender Eintrag gespeichert wurde.
	 */
	protected boolean savePerson(Database database, Integer mailinglist, Integer person, String address) throws BeanException, IOException {
		final MailinglistAddress mailinglistAddress = new MailinglistAddress();
		mailinglistAddress.mailinglist = mailinglist;
		mailinglistAddress.person = person;
		mailinglistAddress.address = address;
		mailinglistAddress.verify();
		if (mailinglistAddress.isCorrect()) {
			final TransactionContext transactionContext = database.getTransactionContext();
			transactionContext.execute(database.getInsert(mailinglistAddress));
			transactionContext.commit();
			return true;
		}
		return false;
	}

	/**
	 * Gibt eine 'gesauberte' Faxnummer mit dem Zusatz '@fax' zurück.
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
	 * Gibt eine 'gesauberte' eMail-Adresse zurück.
	 *
	 * @param mail eMail-Adresse
	 * @return eMail-Adresse
	 */
	public String getClearMailAddress(String mail) {
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
