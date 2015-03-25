/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
		if (name == null || name.trim().length() == 0)
			addError("Sie m\u00fcssen der E-Mail-Vorlage einen Namen geben.");
		if (subject == null || subject.trim().length() == 0)
			addError("Sie haben der E-Mail-Vorlage keinen Betreff gegeben.");
		if (text == null || text.trim().length() == 0)
			addError("Sie haben in dieser E-Mail-Vorlage keinen Text eingegeben.");
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
