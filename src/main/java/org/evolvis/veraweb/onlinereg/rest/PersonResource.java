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
package org.evolvis.veraweb.onlinereg.rest;

import java.util.List;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by mley on 01.09.14.
 */
@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource extends AbstractResource {

    /**
     * Create person.
     *
     * @param username Username
     * @param firstName First name
     * @param lastname Last name
     *
     * @return The created person.
     */
    @POST
    @Path("/")
    public Person createPerson(@FormParam("username") String username,
    						   @FormParam("firstname") String firstName,
                               @FormParam("lastname") String lastname) {
        final Session session = openSession();
        try {
            final Person person = handleCreatePerson(username, firstName, lastname, session);
            return person;
        } finally {
            session.close();
        }
    }


    /**
     * Create delegate.
     *
     * @param eventId Event id
     * @param username Username
     * @param firstName First name
     * @param lastname Last name
     * @param gender Gender
     * @param company Company
     *
     * @return The newly created person.
     */
    @POST
    @Path("/delegate")
    public Person createDelegate(@FormParam("eventId") Integer eventId,
						    		@FormParam("username") String username,
						    		@FormParam("firstname") String firstName,
						    		@FormParam("lastname") String lastname,
						    		@FormParam("gender") String gender,
					    			@FormParam("company") String company,
                                    @FormParam("category") String category,
                                    @FormParam("function") String function) {
        final Session session = openSession();
        try {
            final Integer mandantId = getOrgUnitId(session, eventId);
            final Person person = handleCreatePersonDelegation(company, mandantId, username, firstName, lastname,
                    gender, session, function);
            return person;
        } finally {
            session.close();
        }
    }

    /**
     * Updating one delegate.
     *
     * @param firstName First name
     * @param lastname Last name
     * @param gender Gender
     * @param function Function
     * @param personId Person ID
     * @return Person updated person
     */
    @POST
    @Path("/delegate/update")
    public Person updateDelegate(   @FormParam("firstname") String firstName,
						    		@FormParam("lastname") String lastname,
						    		@FormParam("gender") String gender,
                                    @FormParam("function") String function,
                                    @FormParam("personId") Integer personId) {
    	
    	final Session session = openSession();
        try {

            final Query query = session.getNamedQuery("Person.findByPersonId");
            query.setInteger("personId", personId);
            
            final Person person = (Person) query.uniqueResult();
            
            person.setFirstname_a_e1(firstName);
            person.setLastname_a_e1(lastname);
            person.setSex_a_e1(gender);
            person.setFunction_a_e1(function);

            updatePerson(person, session);
            
            return person;
            
        } finally {
            session.close();
        }
    }
    /**
     * Create new media representative.
     *
     * @param eventId Event id
     * @param username Username
     * @param firstName First name
     * @param lastname Last name
     * @param gender Gender
     * @param email Email
     * @param address Address
     * @param zipCode Zip code
     * @param city City
     * @param country Country
     *
     * @return The newly created person.
     */
	@POST
    @Path("/press")
    public Person createPersonPress(
    						   @FormParam("eventId") Integer eventId,
    						   @FormParam("username") String username,
                               @FormParam("firstname") String firstName,
                               @FormParam("lastname") String lastname,
                               @FormParam("gender") String gender,
                               @FormParam("email") String email,
                       		   @FormParam("address") String address,
                       		   @FormParam("plz") String zipCode,
                       		   @FormParam("city") String city,
                               @FormParam("country") String country) {


        final Session session = openSession();
        try {
        	final Integer mandantId = getOrgUnitId(session, eventId);
            final Person person = handleCreatePersonPress(mandantId, username, firstName, lastname,gender, email,
                    address, zipCode, city, country, session);
            return person;
        } finally {
            session.close();
        }
    }

	@GET
	@Path("/userinfo/{username}")
	public String getFirstAndLastName(@PathParam("username") String username) {
		final Session session = openSession();
		try {
			final Query query = session.getNamedQuery("Person.getPersonNamesByUsername");
	        query.setString("username", username);

	        return (String) query.uniqueResult();
		} finally {
            session.close();
        }

	}

    /**
     * Get delegates for specific event by delegation uuid.
     *
     * @param uuid Delegation uuid
     *
     * @return List with all delegates
     */
    @GET
    @Path("/{uuid}")
    public List<Person> getDelegatesByUUID(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.getDelegatesByUUID");
            query.setString("uuid", uuid);
            return (List<Person>) query.list();

        } finally {
            session.close();
        }
    }

    /**
     * Get company by UUID.
     *
     * @param uuid UUID
     *
     * @return The company (yes, company is instance of Person)
     */
    @GET
    @Path("/company/{uuid}")
    public Person getCompanyByUUID(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.getCompanyByUUID");
            query.setString("uuid", uuid);

            return (Person)query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * GET user data by usermane
     *
     * @param username String
     * @return Person
     */
    @GET
    @Path("/userdata/{username}")
    public Person getPersonByUsername(@PathParam("username") String username) {
    	final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findByUsername");
            query.setString("username", username);

            return (Person)query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Getting Person pk from the username
     *
     * @param username String
     * @return Integer Person pk
     */
    @Path("/userdata/lite/{username}")
    @GET
    public Integer getUserIdFromUsername(@PathParam("username") String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findPersonIdByUsername");
            query.setString("username", username);
            if (query.list().isEmpty()) {
                // user does not exists
                return null;
            } else {
                return (Integer) query.uniqueResult();
            }

        } finally {
            session.close();
        }

    }

    /**
     * Update the orgunit for a person after registration for event.
     *
     * @param orgunit Integer
     * @param personId Integer
     */
    @POST
    @Path("/update/orgunit")
    public void updatePersonOrgunit(@FormParam("orgunit") Integer orgunit, @FormParam("personId") Integer personId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery("Person.findByPersonId");
            query.setInteger("personId", personId);

            final Person person = (Person) query.uniqueResult();
            person.setFk_orgunit(orgunit);

            session.update(person);
            session.flush();

        } finally {
            session.close();
        }
    }

    /**
     * TODO
     */
    @GET
    @Path("/list/{personId}")
    public Person getUsernameByUserId(@PathParam("personId") Integer personId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery("Person.findByPersonId");
            query.setInteger("personId", personId);

            final Person person = (Person) query.uniqueResult();
            return person;

        } finally {
            session.close();
        }
    }

    private Integer getOrgUnitId(Session session, Integer eventId) {
        final Query query = session.getNamedQuery("Event.getEvent");
        query.setInteger("pk", eventId);
        return ((Event) query.uniqueResult()).getFk_orgunit();
    }

    private Person handleCreatePerson(String username, String firstName, String lastname, Session session) {
        final Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPerson(username, firstName, lastname, session);
        final Person person = (Person) query.uniqueResult();
        return person;
    }


    private Person handleCreatePersonDelegation(String company, Integer orgUnitId, String username, String firstName,
                                                String lastname, String gender,Session session, String function) {
        final Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPersonDelegation(company, orgUnitId, username, firstName, lastname, gender, session, function);
        final Person person = (Person) query.uniqueResult();
        return person;
    }

    private Person handleCreatePersonPress(Integer orgUnit, String username, String firstName, String lastname,
                                           String gender,
            String email, String address, String plz, String city, String country, Session session) {
        final Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPersonPress(orgUnit, username, firstName, lastname, gender, email, address, plz, city, country, session);
        final Person person = (Person) query.uniqueResult();
        return person;
    }

    private Query getSelectPersonByUsernameQuery(String username, Session session) {
        final Query query = session.getNamedQuery("Person.findByUsername");
        query.setString("username", username);
        return query;
    }

    private Person persistPerson(String username, String firstName, String lastname, Session session) {
        final Person person = initPerson(username, firstName, lastname);
        session.persist(person);
        session.flush();
        return person;
    }

    private Person persistPersonDelegation(String company, Integer orgUnitId, String username, String firstName,
                                           String lastname, String gender, Session session, String function) {
        final Person person = initPersonDelegation(company, orgUnitId, username, firstName, lastname, gender, function);
        session.persist(person);
        session.flush();
        return person;
    }
    
    private Person updatePerson(Person person, Session session) {
    	session.update(person);
    	session.flush();
    	return person;
    }

    private Person initPerson(String username, String firstName, String lastname) {
        final Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastname);
        person.setUsername(username);
        person.setFk_orgunit(0);
        person.setSex_a_e1("m");

        return person;
    }

    private Person initPersonDelegation(String company, Integer orgUnitId, String username, String firstName,
                                        String lastname, String gender, String function) {
        final Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastname);
        person.setUsername(username);
        person.setFk_orgunit(orgUnitId);
        person.setSex_a_e1(correctGender(gender));
        person.setCompany_a_e1(company);
        person.setFunction_a_e1(function);

        return person;
    }

    private String correctGender(String complete) {
    	if (complete.equals("Herr")) {
    		return "m";
    	}
    	return "w";
    }

    private Person persistPersonPress(Integer orgUnitId, String username, String firstName, String lastname,
                                      String gender, String email, String address, String plz, String city,
                                      String country, Session session) {
        final Person person = initPersonPress(orgUnitId, username, firstName, lastname, gender, email, address,
                plz, city, country);
        session.persist(person);
        session.flush();
        return person;
    }

    private Person initPersonPress(Integer orgUnitId, String username, String firstName, String lastname, String gender,
                                   String email, String address, String plz, String city, String country) {
        final Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastname);
        person.setUsername(username);
        person.setFk_orgunit(orgUnitId);
        person.setSex_a_e1(gender);
        person.setMail_a_e1(email);
        person.setStreet_a_e1(address);
        person.setZipcode_a_e1(plz);
        person.setCity_a_e1(city);
        person.setCountry_a_e1(country);

        return person;
    }

}
