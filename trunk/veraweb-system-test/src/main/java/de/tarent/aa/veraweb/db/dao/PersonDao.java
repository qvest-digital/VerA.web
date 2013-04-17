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
