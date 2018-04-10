package org.evolvis.veraweb.onlinereg.event;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.Getter;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.user.LoginResource;
import org.evolvis.veraweb.onlinereg.utils.EventTransporter;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.evolvis.veraweb.onlinereg.utils.VerawebConstants;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mley on 29.07.14.
 *
 * @author jnunez
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class EventResource {

    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    public static final String USERNAME = "USERNAME";

    /**
     * Event type
     */
    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {
    };

    /**
     * List of Events type
     */
    private static final TypeReference<List<Event>> EVENT_LIST = new TypeReference<List<Event>>() {
    };

    /**
     * Person type
     */
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {
    };

    /**
     * Guest type
     */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {
    };

    /**
     * Guest type
     */
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {
    };

    /**
     * Guest type
     */
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {
    };

    private static final Integer INVITATIONSTATUS_OPEN = 0;
    private static final Integer INVITATIONSTATUS_COMMITMENT = 1;

    /**
     * Jersey client
     */
    private Client client;

    /**
     * Configuration
     */
    private Config config;

    /**
     * Jackson Object Mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Servlet context
     */
    @javax.ws.rs.core.Context
    @Getter
    private HttpServletRequest request;

    private final ResourceReader resourceReader;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public EventResource(Config config, Client client) {
        this.client = client;
        this.config = config;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    /**
     * Returns a list of events
     *
     * @return List of Event objects
     * @throws IOException TODO
     */
    @GET
    @Path("/list")
    public List<EventTransporter> getEvents() throws IOException {
        final String username = (String) request.getAttribute(LoginResource.USERNAME);
        final List<Event> listEvents = readResource(path("event"), EVENT_LIST);
        final List<EventTransporter> listTransporters = new ArrayList<EventTransporter>();

        for (final Iterator<Event> iterator = listEvents.iterator(); iterator.hasNext(); ) {
            final EventTransporter eventTransporter = createEventTransporter(username, iterator);
            listTransporters.add(eventTransporter);
        }

        return listTransporters;
    }

    /**
     * Returns an event with given id.
     *
     * @param eventId event id
     * @return Event object
     * @throws IOException TODO
     */
    @GET
    @Path("/{eventId}")
    public Event getEvent(@PathParam("eventId") int eventId) throws IOException {
        return readResource(path("event", eventId), EVENT);
    }

    /**
     * Gets the current registration to an event of a user
     *
     * @param eventId event id
     * @param userId  user id
     * @return Guest object
     * @throws IOException TODO
     */
    @GET
    @Path("/{eventId}/register/{userId}")
    public Guest getRegistration(
            @PathParam("eventId") int eventId,
            @PathParam("userId") int userId) throws IOException {
        return readResource(path("guest", eventId, userId), GUEST);
    }

    /**
     * Save the registration to an event
     *
     * @param eventId     event id
     * @param notehost    note to host
     * @param guestStatus Guest status
     * @return updated Guest object
     * @throws IOException TODO
     */
    @POST
    @Path("/{eventId}/register")
    public String register(
            @PathParam("eventId") String eventId,
            @FormParam("notehost") String notehost,
            @FormParam("guestStatus") String guestStatus) throws IOException {
        final String username = (String) request.getAttribute(USERNAME);

        // checking if the user is registered on the event
        if (!isUserRegistered(username, eventId)) {
            registerUserAsGuestForEvent(eventId, notehost, guestStatus, username);
            return StatusConverter.convertStatus("OK");
        }

        return StatusConverter.convertStatus("REGISTERED");
    }

    /**
     * Update guest status to "zusage"
     *
     * @param eventId             Event id
     * @param notehost            Note for the hoster
     * @param noLoginRequiredUUID UUID
     * @return updated status string
     * @throws IOException ФИѝМЕ
     */
    @POST
    @Path("/{eventId}/register/nologin")
    public String registerGuestWithoutLogin(
            @PathParam("eventId") String eventId,
            @FormParam("notehost") String notehost,
            @FormParam("noLoginRequiredUUID") String noLoginRequiredUUID) throws IOException {

        // checking if the user is registered on the event
        if (!isUserWithoutLoginRegistered(noLoginRequiredUUID, eventId)) {
            updateGuestStatusWithoutLogin(noLoginRequiredUUID, VerawebConstants.GUEST_STATUS_ACCEPT, notehost);
            return StatusConverter.convertStatus("OK");
        }
        return StatusConverter.convertStatus("REGISTERED");
    }

    /**
     * Get user's subscribed events.
     *
     * @return Users' events list
     * @throws IOException TODO
     */
    @GET
    @Path("/userevents")
    public List<Event> getUsersEvents() throws IOException {
        final String username = (String) request.getAttribute(USERNAME);
        return readResource(path("event", "userevents", username), EVENT_LIST);
    }

    /**
     * Controlling status of guest and reserve lists
     *
     * @param eventId the event ID
     * @return response to send
     * @throws IOException FIXME
     */
    @GET
    @Path("/guestlist/status/{eventId}")
    public String getGuestListStatus(@PathParam("eventId") Integer eventId) throws IOException {

        Boolean isGuestListFull = readResource(path("event", "guestlist", "status", eventId), BOOLEAN);

        if (!isGuestListFull) {
            return StatusConverter.convertStatus(VerawebConstants.GUEST_LIST_OK);
        } else {
            Boolean isReserveListFull = readResource(path("event", "reservelist", "status", eventId), BOOLEAN);
            if (!isReserveListFull) {
                return StatusConverter.convertStatus(VerawebConstants.WAITING_LIST_OK);
            } else {
                return StatusConverter.convertStatus(VerawebConstants.WAITING_LIST_FULL);
            }
        }
    }

    /**
     * Checking if the user is already registered for the event
     *
     * @param eventId the event ID
     * @return FIXME
     * @throws IOException FIXME
     */
    @GET
    @Path("/registered/{eventId}")
    public Boolean isUserRegisteredIntoEvent(
            @PathParam("eventId") final Integer eventId) throws IOException {
        final String username = (String) request.getAttribute(LoginResource.USERNAME);
        return readResource(path("guest", "registered", username, eventId), BOOLEAN);
    }

    /**
     * Get
     *
     * @param uuid The free visitors UUID
     * @return Event id
     * @throws IOException TODO
     */
    @GET
    @Path("/uuid/{uuid}")
    public String getEvenByUUId(@PathParam("uuid") String uuid) throws IOException {
        final WebResource resource = client.resource(path("event", "uuid", uuid));
        try {
            return StatusConverter.convertStatus(String.valueOf(resource.get(Event.class).getPk()));
        } catch (UniformInterfaceException e) {
            return StatusConverter.convertStatus("ERROR");
        }
    }

    private void updateGuestStatusWithoutLogin(final String noLoginRequiredUUID,
            final Integer invitationstatus,
            final String notehost) {
        final Form postBodyForUpdate = new Form();
        postBodyForUpdate.add("invitationstatus", invitationstatus);
        postBodyForUpdate.add("notehost", notehost);

        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/guest/update/nologin/" +
                noLoginRequiredUUID);
        resource.post(postBodyForUpdate);
    }

    /**
     * Get Person instance from one username
     *
     * @param username Username
     * @return Person
     * @throws IOException TODO
     */
    private Person getUserData(String username) throws IOException {
        return readResource(path("person", "userdata", username), PERSON);
    }

    /**
     * Includes a new guest in the database - Table "tguest".
     *
     * @param eventId Event id
     * @param userId  User id
     * @param gender  Gender of the person
     * @throws IOException IOException
     */
    private Guest addGuestToEvent(String eventId,
            String userId,
            String gender,
            String lastName,
            String firstName,
            String username,
            String nodeHost,
            Integer reserve) throws IOException {
        final WebResource resource = client.resource(path("guest", "register"));
        final Form postBody = new Form();

        postBody.add("eventId", eventId);
        postBody.add("userId", userId);
        postBody.add("invitationstatus", INVITATIONSTATUS_COMMITMENT.toString());
        postBody.add("invitationtype", "2");
        postBody.add("gender", gender);
        postBody.add("category", "0");
        postBody.add("username", username);
        postBody.add("hostNode", nodeHost);
        postBody.add("reserve", reserve);

        final Guest guest = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Guest.class, postBody);

        return guest;
    }

    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        return resourceReader.readStringResource(path, type);
    }

    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }

    private EventTransporter createEventTransporter(String username, Iterator<Event> iterator) throws IOException {
        final Event event = (Event) iterator.next();
        final String eventId = String.valueOf(event.getPk());
        return new EventTransporter(
                event.getPk(),
                event.getShortname(),
                event.getDatebegin(),
                event.getDateend(),
                isUserRegistered(username, eventId),
                event.getHash()
        );
    }

    private Boolean isUserRegistered(String username, String eventId) throws IOException {
        if (username == null) {
            return false;
        }
        return readResource(path("guest", "registered", "accept", username, eventId), BOOLEAN);
    }

    private Boolean isUserWithoutLoginRegistered(String noLoginRequiredUUID, String eventId) throws IOException {
        return readResource(path("guest", "registered", "nologin", noLoginRequiredUUID, eventId), BOOLEAN);
    }

    private void updatePersonOrgunit(String eventId, Integer personId) throws IOException {
        final Integer orgunit = getOrgunitForEvent(eventId);

        final Form postBody = createPostFormToChangeOrgunit(personId, orgunit);

        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/update/orgunit");
        resource.post(postBody);
    }

    private Form createPostFormToChangeOrgunit(Integer personId, Integer orgunit) {
        final Form postBody = new Form();
        postBody.add("orgunit", orgunit);
        postBody.add("personId", personId);

        return postBody;
    }

    private Integer getOrgunitForEvent(String eventId) throws IOException {
        final Event event = readResource(path("event", eventId), EVENT);
        return event.getFk_orgunit();
    }

    private void registerUserAsGuestForEvent(String eventId, String notehost, String guestStatus, String username)
            throws IOException {
        final Person person = getUserData(username);
        final Integer userId = person.getPk();

        if (person != null && userId != null) {
            addGuestToEvent(eventId,
                    userId.toString(),
                    person.getSex_a_e1(),
                    person.getFirstname_a_e1(),
                    person.getLastname_a_e1(),
                    username,
                    notehost,
                    getGuestReservationStatus(guestStatus)
            );

            updatePersonOrgunit(eventId, userId);
        }
    }

    private Integer getGuestReservationStatus(final String guestStatus) {
        if (guestStatus.equals(VerawebConstants.GUEST_LIST_OK)) {
            return 0; // not reserve
        }
        return 1; // reserve
    }
}
