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

/* $Id$
 * 
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;


/**
 * <p>
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tperson_doctype dar,
 * eine Auflistung von Personen und Dokumenttypen mit Daten f�r die Dokumente.
 * </p>
 * 
 * <p>
 * <strong>Achtung - Verwendung in Datenbank-Worker:</strong>
 * Die Verkn�pfung mit entsprechendem {@link Doctype Dokumenttyp}
 * muss in dem entsprechendem Worker per Join auf <em>veraweb.tdoctype</em>
 * erfolgen. Siehe {@link de.tarent.aa.veraweb.worker.PersonDetailWorker}
 * </p>
 * 
 * @see de.tarent.aa.veraweb.worker.PersonDoctypeWorker
 * 
 * @author christoph
 * @author mikel
 */
public class PersonDoctype extends AbstractBean {
    /** Prim�rschl�ssel */
	public Integer id;
    /** Dokumentname */
	public String name;
	
    /** Fremdschl�ssel {@link Person} */
	public Integer person;
	
    /** Fremdschl�ssel {@link Doctype Dokumenttyp} */
	public Integer doctype;
	/** Doctype ID */
	public Integer doctypeId;
    /** {@link Doctype Dokumenttyp}: 1=Privat 2=Gesch�ftlich 3=Weitere */
	public Integer doctypeAddresstype;
    /** {@link Doctype Dokumenttyp}: 1=Latein 2=Z1 3=Z2 */
	public Integer doctypeLocale;
	
    /** 1=Privat 2=Gesch�ftlich 3=Weitere */
	public Integer addresstype;
    /** 1=Latein 2=Z1 3=Z2 */
	public Integer locale;
    /** Freitext */
	public String textfield;
    /** Freitext Partner */
	public String textfieldPartner;
    /** Freitext Verbinder */
	public String textfieldJoin;

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

    /**
     * �berpr�ft das Bean auf innere Vollst�ndigkeit und stellt sie gegebenenfalls her.
     * 
     * @throws BeanException bei Unvollst�ndigkeit
     */
    @Override
    public void verify() throws BeanException {
        if (addresstype == null)
            addresstype = new Integer(PersonConstants.ADDRESSTYPE_BUSINESS);
        if (locale == null)
            locale = new Integer(PersonConstants.LOCALE_LATIN);
		if ( textfieldJoin != null && textfieldJoin.length() > 50 )
		{
			addError( "Der Verbinder darf maximal 50 Zeichen lang sein." );
		}
    }
}
