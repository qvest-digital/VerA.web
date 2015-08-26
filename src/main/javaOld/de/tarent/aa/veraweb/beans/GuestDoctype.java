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
package de.tarent.aa.veraweb.beans;

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

	@Override
	public void verify() throws BeanException {
		super.verify();
		if (city != null && city.length() > 100) {
			addError("Der Name der Stadt darf maximal 100 Zeichen lang sein.");
		}
		if (company != null && company.length() > 250) {
			addError("Der Name der Firma darf maximal 250 Zeichen lang sein.");
		}
		if (country != null && country.length() > 100) {
			addError("Der Name des Landes darf maximal 100 Zeichen lang sein.");
		}
		if (fax != null && fax.length() > 100) {
			addError("Die Faxnummer darf maximal 100 Zeichen lang sein.");
		}
		if (firstname != null && firstname.length() > 100) {
			addError("Der Vorname darf maximal 100 Zeichen lang sein.");
		}
		if (lastname != null && lastname.length() > 100) {
			addError("Der Nachname darf maximal 100 Zeichen lang sein.");
		}
		if (firstname_p != null && firstname_p.length() > 100) {
			addError("Der Vorname des Partners darf maximal 100 Zeichen lang sein.");
		}
		if (lastname_p != null && lastname_p.length() > 100) {
			addError("Der Nachname des Partners darf maximal 100 Zeichen lang sein.");
		}
		if (fon != null && fon.length() > 100) {
			addError("Die Telefonnummer darf maximal 100 Zeichen lang sein.");
		}
		if (function != null && function.length() > 250) {
			addError("Die Funktionsbezeichnung darf maximal 250 Zeichen lang sein.");
		}
		if (mail != null && mail.length() > 250) {
			addError("Die E-Mail Addresse darf maximal 250 Zeichen lang sein.");
		}
		if (mobil != null && mobil.length() > 100) {
			addError("Die Mobilfunknummer darf maximal 100 Zeichen lang sein.");
		}
		if (pobox != null && pobox.length() > 50) {
			addError("Die P.O. Box Nummer darf maximal 50 Zeichen lang sein.");
		}
		if (poboxzipcode != null && poboxzipcode.length() > 50) {
			addError("Die Postleitzahl zur P.O. Box darf maximal 50 Zeichen lang sein.");
		}
		if (salutation != null && salutation.length() > 50) {
			addError("Die Anrede darf maximal 50 Zeichen lang sein.");
		}
		if (salutation_p != null && salutation_p.length() > 50) {
			addError("Die Anrede des Partners darf maximal 50 Zeichen lang sein.");
		}
		if (street != null && street.length() > 100) {
			addError("Der Straßenname darf maximal 100 Zeichen lang sein.");
		}
		if (suffix1 != null && suffix1.length() > 100) {
			addError("Das erste Suffix darf maximal 100 Zeichen lang sein.");
		}
		if (suffix2 != null && suffix2.length() > 100) {
			addError("Das zweite Suffix darf maximal 100 Zeichen lang sein.");
		}
		if (textjoin != null && textjoin.length() > 50) {
			addError("Der Verbinder darf maximal 50 Zeichen lang sein.");
		}
		if (titel != null && titel.length() > 250) {
			addError("Die Titelbezeichnung darf maximal 250 Zeichen lang sein.");
		}
		if (titel_p != null && titel_p.length() > 250) {
			addError("Die Titelbezeichnung des Partners darf maximal 250 Zeichen lang sein.");
		}
		if (www != null && www.length() > 250) {
			addError("Die WWW URL darf maximal 250 Zeichen lang sein.");
		}
		if (zipcode != null && zipcode.length() > 250) {
			addError("Die Postleitzahl darf maximal 50 Zeichen lang sein.");
		}
	}
}
