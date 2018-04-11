package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.query.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * Created by mley on 01.09.14.
 *
 * @author sweiz - tarent solutions GmbH
 */
@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource extends AbstractResource {

    private static final String PARAM_PERSON_ID = "personId";
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_EMAIL = "email";

    /**
     * Create person.
     *
     * @param username  Username
     * @param firstName First name
     * @param lastname  Last name
     * @param email     Email
     * @return The created person.
     */
    @POST
    public Person createPerson(@FormParam(PARAM_USERNAME) String username,
            @FormParam("firstname") String firstName,
            @FormParam("lastname") String lastname,
            @FormParam("email") String email) {
        final Session session = openSession();
        session.beginTransaction();
        try {
            return handleCreatePerson(username, firstName, lastname, session, email);
        } finally {
            session.getTransaction().commit();//XXX?
            session.close();
        }
    }

    /**
     * Create delegate.
     *
     * @param eventId   Event id
     * @param username  Username
     * @param firstName First name
     * @param lastname  Last name
     * @param gender    Gender
     * @param company   Company
     * @param function  Function
     * @return The newly created person.
     */
    @POST
    @Path("/delegate")
    public Person createDelegate(@FormParam("eventId") Integer eventId,
            @FormParam(PARAM_USERNAME) String username,
            @FormParam("firstname") String firstName,
            @FormParam("lastname") String lastname,
            @FormParam("gender") String gender,
            @FormParam("company") String company,
            @FormParam("function") String function) {
        final Session session = openSession();
        session.beginTransaction();
        try {
            final Integer mandantId = getOrgUnitId(session, eventId);
            final Person person = initPersonDelegation(company, mandantId, username, firstName, lastname, gender, function);
            return handleCreatePersonDelegation(person, session);
        } finally {
            session.getTransaction().commit();//XXX?
            session.close();
        }
    }

    /**
     * Updating one delegate.
     *
     * @param firstName First name
     * @param lastname  Last name
     * @param gender    Gender
     * @param function  Function
     * @param personId  Person ID
     * @return Person updated person
     */
    @POST
    @Path("/delegate/update")
    public Person updateDelegate(@FormParam("firstname") String firstName,
            @FormParam("lastname") String lastname,
            @FormParam("gender") String gender,
            @FormParam("function") String function,
            @FormParam(PARAM_PERSON_ID) Integer personId) {

        final Session session = openSession();
        session.beginTransaction();
        try {

            final Query query = session.getNamedQuery("Person.findByPersonId");
            query.setInteger(PARAM_PERSON_ID, personId);

            final Person person = (Person) query.uniqueResult();

            person.setFirstname_a_e1(firstName);
            person.setLastname_a_e1(lastname);
            person.setSex_a_e1(gender);
            person.setFunction_a_e1(function);

            updatePerson(person, session);
            session.getTransaction().commit();

            return person;

        } finally {
            session.close();
        }
    }

    /**
     * Create new media representative.
     *
     * @param eventId   Event id
     * @param username  Username
     * @param firstName First name
     * @param lastname  Last name
     * @param gender    Gender
     * @param email     Email
     * @param address   Address
     * @param zipCode   Zip code
     * @param city      City
     * @param country   Country
     * @return The newly created person.
     */
    @POST
    @Path("/press")
    public Person createPersonPress(@FormParam("eventId") Integer eventId,
            @FormParam(PARAM_USERNAME) String username,
            @FormParam("firstname") String firstName,
            @FormParam("lastname") String lastname,
            @FormParam("gender") String gender,
            @FormParam("email") String email,
            @FormParam("address") String address,
            @FormParam("plz") String zipCode,
            @FormParam("city") String city,
            @FormParam("country") String country) {

        final Session session = openSession();
        session.beginTransaction();
        try {
            final Integer mandantId = getOrgUnitId(session, eventId);
            final Person person =
                    initPersonPress(mandantId, username, firstName, lastname, gender, email, address, zipCode, city, country);
            return handleCreatePersonPress(person, session);
        } finally {
            session.getTransaction().commit();//XXX?
            session.close();
        }
    }

    /**
     * Updates the core data of a person
     *
     * @param username     username of a person
     * @param salutation   salutation of a person
     * @param fkSalutation foreign key of salutation of a person
     * @param title        title of a person
     * @param firstName    firstName of a person
     * @param lastName     lastName of a person
     * @param birthday     birthday of a person as long timestamp
     * @param nationality  nationality of a person
     * @param languages    languages of a person
     * @param gender       gender of a person
     */
    @POST
    @Path("/usercoredata/update/")
    public void updatePersonCoreData(@FormParam(PARAM_USERNAME) String username,
            @FormParam("salutation") String salutation,
            @FormParam("fk_salutation") Integer fkSalutation,
            @FormParam("title") String title,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("birthday") Long birthday,
            @FormParam("nationality") String nationality,
            @FormParam("languages") String languages,
            @FormParam("gender") String gender) {
        final Session session = openSession();
        session.beginTransaction();

        try {
            prepareAndUpdatePersonCoreData(username, salutation, fkSalutation, title, firstName, lastName, birthday,
                    nationality, languages, gender, session);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    /**
     * Gets the first and the last name of a person, by his username
     *
     * @param username username of person
     * @return first name and last name in one string separated by a whitespace
     */
    @GET
    @Path("/userinfo/{username}")
    public String getFirstAndLastName(@PathParam(PARAM_USERNAME) String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.getPersonNamesByUsername");
            query.setString(PARAM_USERNAME, username);
            return (String) query.uniqueResult();
        } finally {
            session.close();
        }

    }

    /**
     * Get delegates for specific event by delegation uuid.
     *
     * @param uuid Delegation uuid
     * @return List with all delegates
     */
    @GET
    @Path("/{uuid}")
    public List<Person> getDelegatesByUUID(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.getDelegatesByUUID");
            query.setString(PARAM_UUID, uuid);
            return (List<Person>) query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get company by UUID.
     *
     * @param uuid UUID
     * @return The company (yes, company is instance of Person)
     */
    @GET
    @Path("/company/{uuid}")
    public Person getCompanyByUUID(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.getCompanyByUUID");
            query.setString(PARAM_UUID, uuid);

            return (Person) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * GET user data by username
     *
     * @param username String
     * @return Person
     */
    @GET
    @Path("/userdata/{username}")
    public Person getPersonByUsername(@PathParam(PARAM_USERNAME) String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findByUsername");
            query.setString(PARAM_USERNAME, username);

            return (Person) query.uniqueResult();
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
    public Integer getUserIdFromUsername(@PathParam(PARAM_USERNAME) String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findPersonIdByUsername");
            query.setString(PARAM_USERNAME, username);
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
     * @param orgunit  Integer
     * @param personId Integer
     */
    @POST
    @Path("/update/orgunit")
    public void updatePersonOrgunit(@FormParam("orgunit") Integer orgunit, @FormParam(PARAM_PERSON_ID) Integer personId) {
        final Session session = openSession();
        session.beginTransaction();

        try {
            final Query query = session.getNamedQuery("Person.findByPersonId");
            query.setInteger(PARAM_PERSON_ID, personId);

            final Person person = (Person) query.uniqueResult();
            person.setFk_orgunit(orgunit);

            session.update(person);
            session.flush();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    /**
     * Getting the username from a person ID
     *
     * @param personId Person id
     * @return The {@link Person}
     */
    @GET
    @Path("/list/{personId}")
    public Person getUsernameByUserId(@PathParam(PARAM_PERSON_ID) Integer personId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery("Person.findByPersonId");
            query.setInteger(PARAM_PERSON_ID, personId);

            return (Person) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    private Integer getOrgUnitId(Session session, Integer eventId) {
        final Query query = session.getNamedQuery("Event.getEvent");
        query.setInteger("pk", eventId);

        return ((Event) query.uniqueResult()).getFk_orgunit();
    }

    private Person handleCreatePerson(String username, String firstName, String lastname, Session session, String email) {
        final Query query = getSelectPersonByUsernameQuery(username, session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPerson(username, firstName, lastname, session, email);
        return (Person) query.uniqueResult();
    }

    private Person handleCreatePersonDelegation(Person person, Session session) {
        final Query query = getSelectPersonByUsernameQuery(person.getUsername(), session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }
        persistPersonDelegation(person, session);
        return (Person) query.uniqueResult();
    }

    private Person handleCreatePersonPress(Person person, Session session) {
        final Query query = getSelectPersonByUsernameQuery(person.getUsername(), session);
        if (!query.list().isEmpty()) {
            // user already exists
            return null;
        }

        persistPersonPress(person, session);
        return (Person) query.uniqueResult();
    }

    private Query getSelectPersonByUsernameQuery(String username, Session session) {
        final Query query = session.getNamedQuery("Person.findByUsername");
        query.setString(PARAM_USERNAME, username);

        return query;
    }

    private void persistPerson(String username, String firstName, String lastname, Session session, String email) {
        final Person person = initPerson(username, firstName, lastname, email);
        session.persist(person);
        session.flush();
    }

    private void persistPersonDelegation(Person person, Session session) {
        session.persist(person);
        session.flush();
    }

    private void updatePerson(Person person, Session session) {
        session.update(person);
        session.flush();
    }

    private Person initPerson(String username, String firstName, String lastname, String email) {
        final Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastname);
        person.setUsername(username);
        person.setMail_a_e1(email);
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
        person.setSex_a_e1(gender);
        person.setCompany_a_e1(company);
        person.setFunction_a_e1(function);

        return person;
    }

    private void persistPersonPress(Person person, Session session) {
        session.persist(person);
        session.flush();
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

    private void prepareAndUpdatePersonCoreData(String username,
            String salutation,
            Integer fk_salutation,
            String title,
            String firstName,
            String lastName,
            Long birthday,
            String nationality,
            String languages,
            String gender,
            Session session) {
        final Query query = getSelectPersonByUsernameQuery(username, session);
        final Person person = (Person) query.uniqueResult();

        if (birthday != null) {
            final Date birthdayDate = new Date(birthday);
            person.setBirthday_a_e1(birthdayDate);
        } else {
            person.setBirthday_a_e1(null);
        }

        if (fk_salutation != 0) {
            person.setFk_salutation_a_e1(fk_salutation);
            person.setSalutation_a_e1(salutation);
        }

        person.setTitle_a_e1(title);
        person.setFirstname_a_e1(firstName);
        person.setLastname_a_e1(lastName);
        person.setNationality_a_e1(nationality);
        person.setLanguages_a_e1(languages);
        person.setSex_a_e1(gender);

        session.update(person);
        session.flush();
    }
}
