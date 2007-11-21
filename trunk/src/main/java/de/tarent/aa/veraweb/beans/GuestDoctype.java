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

/* $Id: GuestDoctype.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
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
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }
}
