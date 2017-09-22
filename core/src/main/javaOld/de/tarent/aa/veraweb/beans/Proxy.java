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
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.sql.Timestamp;

/**
 * Diese Bohne stellt Stellvertretungen dar.
 *
 * @author mikel
 */
public class Proxy extends AbstractHistoryBean {
    //
    // tproxy
    //
    /** tproxy.pk serial NOT NULL */
    public Integer id;

    /** tproxy.fk_user int4 NOT NULL */
    public Integer user;

    /** tproxy.proxy varchar(100) NOT NULL */
    public String proxy;

    /** tproxy.validfrom timestamptz */
    public Timestamp validFrom;

    /** tproxy.validtill timestamptz */
    public Timestamp validTill;

    //
    // tuser
    //
    /** tuser.username varchar(100) NOT NULL */
    public String userRole;

    /** tuser.fk_orgunit: int4 DEFAULT 0 */
    public Integer orgunit;

    //
    // Klasse AbstractBean
    //
    /**
     * Der Benutzer und Stellvertreterrolle müssen angegeben sein.
     * @param octopusContext The {@link OctopusContext}
     */
    public void verify(OctopusContext octopusContext) {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

        if (proxy == null || proxy.trim().length() == 0) {
            addError(messages.getMessageProxyNoRepresentative());
        }

        if (user == null || user.intValue() == 0) {
            addError(messages.getMessageProxyNoRole());
        }

        if (validFrom != null && validTill != null && validFrom.after(validTill)) {
        	addError(messages.getMessageProxyRepresentativeBeginBeforeEnd());
        }
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist leer.
     *
     * @param octopusContext
     *            Octopus-Kontext
     * @throws BeanException
     *             Wenn im angegebenen Kontext diese Bohne nicht gelesen werden
     *             darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer User ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfig.GROUP_USER);
    }
}
