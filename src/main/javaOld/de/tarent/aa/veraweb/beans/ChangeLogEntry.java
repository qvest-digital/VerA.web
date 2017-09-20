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
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.util.Date;

/**
 * The bean class ChangeLogEntry represents a single entry
 * in the newly introduced change logging feature.
 *
 * Each changelog entry stores information on the change
 * action, which is one of insert, delete or update and
 * the attributes of the object that were changed by the
 * user who committed the action.
 *
 * For now, changelogging is enabled for the following
 * entities {see Person}, {see Guest}, and {see Event}.
 *
 * @see de.tarent.octopus.beans.veraweb.BeanChangeLogger
 *
 * @author cklein
 * @since 1.2.0
 */
public class ChangeLogEntry extends AbstractBean
{
	public Integer	id;
	public String	username;
	public String	objectname;
	public String	objecttype;
	public Integer	objectid;
	public String	op;
	public String	attributes;
	public Date		created;

	/**
	 * Creates a new instance of this.
	 */
	public ChangeLogEntry()
	{
		super();
	}

	/**
	 * Only admins may read the entity beans from the table.
	 */
	@Override
    public void checkRead( OctopusContext octopusContext) throws BeanException
	{
		checkGroup(octopusContext, PersonalConfigAA.GROUP_ADMIN );
	}

	/**
	 * Anonymous user group requires write access to the
	 * entity bean, since a background service will be responsible
	 * for purging old entries from change log. The service runs
	 * with the priviledge of the anonymous user group.
	 */
	@Override
    public void checkWrite( OctopusContext octopusContext) throws BeanException
	{
		try
		{
			checkGroup(octopusContext, PersonalConfig.GROUP_ANONYMOUS );
		}
		catch( BeanException e )
		{
			checkGroup(octopusContext, PersonalConfig.GROUP_USER );
		}
	}
}
