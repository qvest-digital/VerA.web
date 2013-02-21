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
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieses Bean repr�sentiert eine eMail-Vorlage und wird als Tupel
 * in der Tabelle <code>veraweb.tmaildraft</code> gespeichert.
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailDraft extends AbstractHistoryBean {
	/** PK der Tabelle tmaildraft */
	public Integer id;
	/** Name der Vorlage */
	public String name;
	/** Betreff der eMail */
	public String subject;
	/** Text der eMail */
	public String text;
	/** Erstellt von */
	public String createdby;
	/** Erstellt am */
	public Timestamp created;
	/** Ge�nder von */
	public String changedby;
	/** Ge�ndert am */
	public Timestamp changed;

	@Override
    public void verify() throws BeanException {
		if (name == null || name.length() == 0)
			addError("Sie müssen der E-Mail-Vorlage einen Namen geben.");
		if (subject == null || subject.length() == 0)
			addError("Sie haben der E-Mail-Vorlage keinen Betreff gegeben.");
		if (text == null || text.length() == 0)
			addError("Sie haben in dieser E-Mail-Vorlage keinen Text eingegeben..");
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
	 * Test ist, ob der Benutzer Standard-Reader ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 * @see AbstractBean#checkRead(OctopusContext)
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
	 * @see AbstractBean#checkWrite(OctopusContext)
	 */
	@Override
    public void checkWrite(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
	}
}
