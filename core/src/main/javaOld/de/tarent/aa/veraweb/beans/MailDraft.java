package de.tarent.aa.veraweb.beans;

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
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

import java.sql.Timestamp;

/**
 * Dieses Bean repräsentiert eine eMail-Vorlage und wird als Tupel
 * in der Tabelle <code>veraweb.tmaildraft</code> gespeichert.
 *
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailDraft extends AbstractHistoryBean implements OrgUnitDependent {
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
	/** Geändert von */
	public String changedby;
	/** Geändert am */
	public Timestamp changed;
	/** ID der Mandanten-Einheit */
	public Integer orgunit;

    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

		if (name == null || name.trim().length() == 0) {
            addError(messages.getMessageEMailDraftNameMissing());
        }

		if (subject == null || subject.trim().length() == 0) {
            addError(messages.getMessageEMailDraftSubjectMissing());
        }

		if (text == null || text.trim().length() == 0) {
            addError(messages.getMessageEMailDraftTextMissing());
        }
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
	 * Test ist, ob der Benutzer Standard-Reader ist.
	 *
	 * @param octopusContext Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 * @see AbstractBean#checkRead(OctopusContext)
	 */
	@Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
		checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
	 * Test ist, ob der Benutzer Writer ist.
	 *
	 * @param octopusContext Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 * @see AbstractBean#checkWrite(OctopusContext)
	 */
	@Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
		checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
	}
}
