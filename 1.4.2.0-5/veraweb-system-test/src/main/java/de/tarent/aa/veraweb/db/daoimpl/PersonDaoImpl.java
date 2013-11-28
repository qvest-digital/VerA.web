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
