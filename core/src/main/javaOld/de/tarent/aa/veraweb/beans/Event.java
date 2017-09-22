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
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.sql.Timestamp;

public class Event extends AbstractHistoryBean implements OrgUnitDependent {
    /** PK */
    public Integer id;
    /** Orgunit */
    public Integer orgunit;
    /** Einladungsart, siehe TYPE_* */
    public Integer invitationtype;
    /** FK auf veraweb.tperson.pk */
    public Integer host;
    public String shortname;
    public String eventname;
    public Timestamp begin;
    public Timestamp end;
    public Boolean invitepartner;
    public String eventtype;
    public String hostname;
    public Integer maxguest;
    public Integer maxreserve;
    public Integer location;
    public String note;
    public String createdby;
    public String changedby;
    public Timestamp created;
    public Timestamp changed;
    public String mediarepresentatives;
    public String hash;
    public Boolean login_required;

    public void verify(OctopusContext octopusContext) throws BeanException {
        addErrors(octopusContext);
        /*
		 * 2009-05-17 cklein
		 * temporarily fixes issue #1529 until i gain access to the old octopus repository
		 */
        DateHelper.temporary_fix_translateErrormessageEN2DE(this.getErrors(), octopusContext);
    }

    private void addErrors(OctopusContext octopusContext) {
        final VerawebMessages messages = new VerawebMessages(octopusContext);
        if (begin != null && end != null && begin.after(end)) {
            addError(messages.getMessageEndAndStartDate());
        }
        if (shortname == null || shortname.trim().length() == 0) {
            addError(messages.getMessageEventMissingShortDescription());
        }
        if (begin == null) {
            addError(messages.getMessageEventWrongDateFormat());
        }
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
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
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_WRITE);
    }

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Hier sind es die Bemerkungsfelder, wenn der Benutzer nicht in der Gruppe
     * {@link PersonalConfigAA#GROUP_READ_REMARKS} der hierzu freigeschalteten ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#clearRestrictedFields(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void clearRestrictedFields(OctopusContext octopusContext) throws BeanException {
        PersonalConfig personalConfig = octopusContext != null ? octopusContext.personalConfig() : null;
        if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
            note = null;
        }
        super.clearRestrictedFields(octopusContext);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
