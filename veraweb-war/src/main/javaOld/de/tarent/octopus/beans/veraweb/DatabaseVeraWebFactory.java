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
package de.tarent.octopus.beans.veraweb;

import de.tarent.octopus.server.OctopusContext;

/**
 * Factory which creates a new {@link DatabaseVeraWeb}. This was needed to unit test the legacy code.
 *
 * @author Hendrik Helwich
 */
public class DatabaseVeraWebFactory {

	public DatabaseVeraWeb createDatabaseVeraWeb(OctopusContext cntx) {
        return new DatabaseVeraWeb(cntx);
	}

}
