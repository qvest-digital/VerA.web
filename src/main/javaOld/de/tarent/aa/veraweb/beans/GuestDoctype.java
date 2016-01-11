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
package de.tarent.aa.veraweb.beans;

import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;


/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tguest_doctype dar,
 * eine Auflistung von Gästen und Dokumenttypen mit Daten für die Dokumente.
 *
 * @author christoph
 * @author mikel
 */
public class GuestDoctype extends AbstractBean {
    /** Primärschlüssel */
	public Integer id;
    /** Fremdschlüssel {@link Guest Gast} */
	public Integer guest;
    /** Fremdschlüssel {@link Doctype Dokumenttyp} */
	public Integer doctype;
    /** 1=Privat 2=Geschäftlich 3=Weitere */
	public Integer addresstype;
    /** 1=Latein 2=Z1 3=Z2 */
	public Integer locale;
    /** Freitext */
	public String textfield;
    /** Freitext Partner */
	public String textfield_p;
    /** Freitext Verbindung */
	public String textjoin;
    /** Anrede */
	public String salutation;
    /** Funktionsbezeichnung */
	public String function;
    /** Titel */
	public String titel;
    /** Vorname */
	public String firstname;
    /** Nachname */
	public String lastname;
    /** Adressfeld PLZ */
	public String zipcode;
    /** Adressfeld Stadt */
	public String city;
    /** Adressfeld Straße */
	public String street;
    /** Adressfeld Land */
	public String country;
    /** Adresszusatz 1 */
	public String suffix1;
    /** Adresszusatz 2 */
	public String suffix2;
    /** Anrede Partner */
	public String salutation_p;
    /** Titel Partner */
	public String titel_p;
    /** Vorname Partner */
	public String firstname_p;
    /** Nachname Partner */
	public String lastname_p;
    /** Telefonnummer */
	public String fon;
    /** Faxnummer */
	public String fax;
    /** E-Mail-Adresse */
	public String mail;
    /** Web-Seite */
	public String www;
    /** Mobiltelefonnummer */
	public String mobil;
    /** Firma */
	public String company;
    /** Adressfeld Postfach */
	public String pobox;
    /** Adressfeld PLZ Postfach */
	public String poboxzipcode;

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     *
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Writer ist.
     *
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }

	public void verify(final OctopusContext octopusContext) throws BeanException {
		super.verify();
		final VerawebMessages messages = new VerawebMessages(octopusContext);
		if (city != null && city.length() > 100) {
			addError(messages.getMessageGuestdoctypeCitynameMaxLength());
		}
		if (company != null && company.length() > 250) {
			addError(messages.getMessageGuestdoctypeCompanynameMaxLength());
		}
		if (country != null && country.length() > 100) {
			addError(messages.getMessageGuestdoctypeCountrynameMaxLength());
		}
		if (fax != null && fax.length() > 100) {
			addError(messages.getMessageGuestdoctypeFaxMaxLength());
		}
		if (firstname != null && firstname.length() > 100) {
			addError(messages.getMessageGuestdoctypeFirstnameMaxLength());
		}
		if (lastname != null && lastname.length() > 100) {
			addError(messages.getMessageGuestdoctypeLastnameMaxLength());
		}
		if (firstname_p != null && firstname_p.length() > 100) {
			addError(messages.getMessageGuestdoctypeFirstnamePartnerMaxLength());
		}
		if (lastname_p != null && lastname_p.length() > 100) {
			addError(messages.getMessageGuestdoctypeLastnamePartnerMaxLength());
		}
		if (fon != null && fon.length() > 100) {
			addError(messages.getMessageGuestdoctypePhoneMaxLength());
		}
		if (function != null && function.length() > 250) {
			addError(messages.getMessageGuestdoctypeFunctionMaxLength());
		}
		if (mail != null && mail.length() > 250) {
			addError(messages.getMessageGuestdoctypeEmailMaxLength());
		}
		if (mobil != null && mobil.length() > 100) {
			addError(messages.getMessageGuestdoctypeMobilePhoneMaxLength());
		}
		if (pobox != null && pobox.length() > 50) {
			addError(messages.getMessageGuestdoctypePOBoxMaxLength());
		}
		if (poboxzipcode != null && poboxzipcode.length() > 50) {
			addError(messages.getMessageGuestdoctypePOBoxZipMaxLength());
		}
		if (salutation != null && salutation.length() > 50) {
			addError(messages.getMessageGuestdoctypeSalutationMaxLength());
		}
		if (salutation_p != null && salutation_p.length() > 50) {
			addError(messages.getMessageGuestdoctypeSalutationPartnerMaxLength());
		}
		if (street != null && street.length() > 100) {
			addError(messages.getMessageGuestdoctypeStreetMaxLength());
		}
		if (suffix1 != null && suffix1.length() > 100) {
			addError(messages.getMessageGuestdoctypeSuffix1MaxLength());
		}
		if (suffix2 != null && suffix2.length() > 100) {
			addError(messages.getMessageGuestdoctypeSuffix2MaxLength());
		}
		if (textjoin != null && textjoin.length() > 50) {
			addError(messages.getMessageGuestdoctypeTextjoinMaxLength());
		}
		if (titel != null && titel.length() > 250) {
			addError(messages.getMessageGuestdoctypeTitleMaxLength());
		}
		if (titel_p != null && titel_p.length() > 250) {
			addError(messages.getMessageGuestdoctypeTitlePartnerMaxLength());
		}
		if (www != null && www.length() > 250) {
			addError(messages.getMessageGuestdoctypeUrlMaxLength());
		}
		if (zipcode != null && zipcode.length() > 250) {
			addError(messages.getMessageGuestdoctypeZipMaxLength());
		}
	}
}
