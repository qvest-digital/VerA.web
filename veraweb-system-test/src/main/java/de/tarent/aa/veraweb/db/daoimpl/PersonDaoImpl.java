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
package de.tarent.aa.veraweb.db.daoimpl;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.PersonDao;
import de.tarent.aa.veraweb.db.entity.Person;

@Component(value = "PersonDao")
public class PersonDaoImpl extends GenericDaoImpl<Person, Long> implements PersonDao {

    /**
     * Logger used wihtin this class.
     */
    private static final Logger LOG = Logger.getLogger(PersonDaoImpl.class);

    @Override
    public Person getPersonByFirstname(String firstName) throws Exception {
        if (firstName == null) {
            LOG.error("id is null");
            throw new NullPointerException();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing " + Person.GET_PERSON_BY_FIRSTNAME + " for entity type: "
                    + Person.class.getSimpleName() + " with name: " + firstName);
        }
        Query q = em.createNamedQuery(Person.GET_PERSON_BY_FIRSTNAME);
        q.setParameter("firstname", firstName);
        return (Person) q.getSingleResult();
    }

}
