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

import java.io.Serializable;
import java.util.List;

/**
 * Generic interface for accessing Entities.
 *
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 *
 * @param <T>
 *            Type of the entity
 * @param <PK>
 *            Type of the primary key
 */
public interface GenericDao<T extends Serializable, PK extends Serializable> {

    /**
     * Persists the newInstance object into database.
     *
     * @param newInstance
     *            the new instance to persists
     * @throws Exception
     *             thrown if an error occur during persistance
     */
    void persist(T newInstance) throws Exception;

    /**
     * Retrieves an object that was previously persisted to the database using the given pk.
     *
     * @param pk
     *            the primary key
     * @return the retrieved object or null
     * @throws Exception
     *             thrown if an error occur during find
     */
    T find(PK pk) throws Exception;

    /**
     * Updates the state of an object already persisted.
     *
     * @param changedInstance
     *            the object instance to update in database
     * @return the updated entity
     * @throws Exception
     *             thrown if an error occur during update of an entity
     */
    T update(T changedInstance) throws Exception;

    /**
     * Deletes an entity from database.
     *
     * @param entityToDelte
     *            the entity to delete
     * @throws Exception
     *             thrown if an error occur during delete
     */
    void delete(T entityToDelte) throws Exception;

    /**
     * Retrieves all entities from database.
     *
     * @return List of entities
     */
    List<T> findAll();

    /**
     * Count the number of entities in database.
     *
     * @return number of entities in database
     */
    long count();

    /**
     * Delete all entities in database.
     *
     * @return number of deleted entities in database
     */
    int deleteAll();
}
