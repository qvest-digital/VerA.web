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
package de.tarent.aa.veraweb.db.dao;

import de.tarent.aa.veraweb.db.entity.Person;

public interface PersonDao extends GenericDao<Person, Long> {

    /**
     * Retrieves an object that was previously persisted to the database using the given {@code firstName}.
     *
     * @param firstName
     *            the firstName
     * @return the retrieved object or null
     * @throws Exception
     *             thrown if an error occur during find
     */
    Person getPersonByFirstname(String firstName) throws Exception;

}
