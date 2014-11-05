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

            Query query = session.getNamedQuery("Person.findByUsername");
            query.setString("username", "username:" + username);

            if (!query.list().isEmpty()) {
                // user already exists
                return null;
            }

            Person p = new Person();
            p.setFirstName(firstName);
            p.setLastName(lastname);
            p.setUsername(username);
            p.setFk_orgunit(0);

            session.persist(p);
            session.flush();

            query.setString("username", "username:" + username);
            p = (Person) query.uniqueResult();
            return p;

        } finally {
            session.close();
        }

    }
}
