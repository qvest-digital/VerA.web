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
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class PdfTemplate extends AbstractHistoryBean {
    public Integer id;
    public String name;
    public String subject;
    public String text;
    public String createdby;
    public Timestamp created;
    public String changedby;
    public Timestamp changed;

    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

        if (name == null || name.trim().length() == 0) {
            addError(messages.getMessageEMailDraftNameMissing());
        }
    }

    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
    }
}
