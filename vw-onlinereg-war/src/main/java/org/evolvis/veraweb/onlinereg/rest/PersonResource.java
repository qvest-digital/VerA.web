package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by mley on 01.09.14.
 */
@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource extends AbstractResource {

    @POST
    @Path("/")
    public Person createPerson(@QueryParam("username") String username,
                               @QueryParam("firstname") String firstName,
                               @QueryParam("lastname") String lastname) {
        Session session = openSession();
        try {
            Person person = handleCreatePerson(username, firstName, lastname, session);
            return person;
        } finally {
            session.close();
        }
    }

    private Person handleCreatePerson(String username, String firstName, String lastname, Session session) {
        Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }

        persistPerson(username, firstName, lastname, session);
        Person person = (Person) query.uniqueResult();
        return person;
    }

    private Query getSelectPersonByUsernameQuery(String username, Session session) {
        Query query = session.getNamedQuery("Person.findByUsername");
        query.setString("username", "username:" + username);
        return query;
    }

    private Person persistPerson(String username, String firstName, String lastname, Session session) {
        Person p = initPerson(username, firstName, lastname);
        session.persist(p);
        session.flush();
        return p;
    }

    private Person initPerson(String username, String firstName, String lastname) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastname);
        p.setUsername(username);
        p.setFk_orgunit(0);
        return p;
    }

}
