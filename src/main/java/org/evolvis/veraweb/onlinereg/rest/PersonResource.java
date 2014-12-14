package org.evolvis.veraweb.onlinereg.rest;

import java.util.List;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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


    @POST
    @Path("/delegate")
    public Person createPersonWithGender(@QueryParam("eventId") Integer eventId,
    							@QueryParam("username") String username,
                               @QueryParam("firstname") String firstName,
                               @QueryParam("lastname") String lastname,
                               @QueryParam("gender") String gender) {
        Session session = openSession();
        try {
        	Integer fk_orgUnitId = getOrgUnitId(session, eventId);
            Person person = handleCreatePersonDelegation(fk_orgUnitId, username, firstName, lastname, gender, session);
            return person;
        } finally {
            session.close();
        }
    }

    private Integer getOrgUnitId(Session session, Integer eventId) {
        Query query = session.getNamedQuery("Event.getEvent");
        query.setInteger("pk", eventId);
        return ((Event) query.uniqueResult()).getFk_orgunit();
	}


	@POST
    @Path("/press")
    public Person createPersonPress(@QueryParam("username") String username,
                               @QueryParam("firstname") String firstName,
                               @QueryParam("lastname") String lastname,
                               @QueryParam("gender") String gender,
                               @QueryParam("email") String email,
                       		   @QueryParam("address") String address,
                       		   @QueryParam("plz") String plz,
                       		   @QueryParam("city") String city,
                               @QueryParam("country") String country) {


        Session session = openSession();
        try {
            Person person = handleCreatePersonPress(username, firstName, lastname,gender, email, address, plz, city, country, session);
            return person;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/{uuid}")
    public List<Person> getDelegatesByUUID(@PathParam("uuid") String uuid) {
    	Session session = openSession();
        try {
            Query query = session.getNamedQuery("Person.getDelegatesByUUID");
            query.setString("uuid", uuid);
            return (List<Person>) query.list();

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


    private Person handleCreatePersonDelegation(Integer orgUnitId, String username, String firstName, String lastname, String gender,Session session) {
        Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPersonDelegation(orgUnitId, username, firstName, lastname, gender, session);
        Person person = (Person) query.uniqueResult();
        return person;
    }

    private Person handleCreatePersonPress(String username, String firstName, String lastname, String gender,
            String email, String address, String plz, String city, String country, Session session) {
        Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPersonPress(username, firstName, lastname, gender, email, address, plz, city, country, session);
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

    private Person persistPersonDelegation(Integer orgUnitId, String username, String firstName, String lastname, String gender, Session session) {
        Person p = initPersonDelegation(orgUnitId, username, firstName, lastname, gender);
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

    private Person initPersonDelegation(Integer orgUnitId, String username, String firstName, String lastname, String gender) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastname);
        p.setUsername(username);
        p.setFk_orgunit(orgUnitId);
        p.setSex_a_e1(correctGender(gender));

        return p;
    }

    private String correctGender(String complete) {
    	if (complete.equals("Herr")) {
    		return "m";
    	}
    	return "w";
    }

    private Person persistPersonPress(String username, String firstName, String lastname, String gender,
            String email, String address, String plz, String city, String country, Session session) {
        Person p = initPersonPress(username, firstName, lastname, gender, email, address, plz, city, country);
        session.persist(p);
        session.flush();
        return p;
    }

    private Person initPersonPress(String username, String firstName, String lastname,  String gender,
            String email, String address, String plz, String city, String country) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastname);
        p.setUsername(username);
        p.setFk_orgunit(0);
        p.setSex_a_e1(gender);
        p.setMail_a_e1(email);
        p.setStreet_a_e1(address);
        p.setZipcode_a_e1(plz);
        p.setCity_a_e1(city);
        p.setCountry_a_e1(country);

        return p;
    }

}
