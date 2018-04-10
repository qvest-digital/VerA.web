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
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.user.LoginResource;
import org.evolvis.veraweb.onlinereg.utils.EventTransporter;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * New functions according to the page where the user can change his status and
 * message to an event
 *
 * @author jnunez
 */
@Path("/update")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class UpdateResource {

    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    /* Context data */
    public static final String USERNAME = "USERNAME";
    /* ************ */

    /* RETURN TYPES */
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {
    };
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {
    };
    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {
    };
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {
    };
    /* ************ */

    /* RESPONSE MESSAGES */
    private static final String RESPONSE_SUCCESS = "OK";
    private static final String RESPONSE_ERROR_NOT_REGISTERED = "NOT_REGISTERED";
    /* ***************** */

    private final ObjectMapper mapper = new ObjectMapper();
    private final Config config;
    private final Client client;
    private final ResourceReader resourceReader;

    /**
     * Servlet context
     */
    @javax.ws.rs.core.Context
    private HttpServletRequest request;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public UpdateResource(Config config, Client client) {
        this.client = client;
        this.config = config;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    /**
     * Returns an event with given id.
     *
     * @param eventId event id
     * @return Event object
     * @throws IOException FIXME
     */
    @GET
    @Path("/{eventId}")
    public EventTransporter getEvent(@PathParam("eventId") int eventId) throws IOException {
        String username = (String) request.getAttribute(USERNAME);

        EventTransporter transporter = getEventData(eventId, username);

        return transporter;
    }

    /**
     * Save the registration to an event
     *
     * @param eventId          event id
     * @param notehost         note to host
     * @param invitationstatus status of user
     * @return updated Guest object
     * @throws IOException FIXME
     */
    @POST
    @Path("/{eventId}/update")
    public String update(@PathParam("eventId") String eventId, @FormParam("notehost") String notehost,
            @FormParam("invitationstatus") String invitationstatus) throws IOException {
        String username = (String) request.getAttribute(USERNAME);

        // checking if the user is registered on the event
        if (isUserRegistered(username, eventId)) {

            Integer userId = getUserData(username);

            if (userId != null) {
                updateGuest(eventId, userId, invitationstatus, notehost);
            }

            return StatusConverter.convertStatus(RESPONSE_SUCCESS);
        }

        return StatusConverter.convertStatus(RESPONSE_ERROR_NOT_REGISTERED);
    }

    /**
     * Checking if the guest is a reserve or not
     *
     * @param eventId the event ID
     * @return FIXME
     * @throws IOException FIXME
     */
    @GET
    @Path("/isreserve/{eventId}")
    public Boolean isReserve(@PathParam("eventId") final Integer eventId) throws IOException {
        final String username = (String) request.getAttribute(LoginResource.USERNAME);
        return readResource(path("guest", "isreserve", eventId, username), BOOLEAN);
    }

    /**
     * Update guest
     *
     * @param eventId          int
     * @param userId           int
     * @param invitationstatus String
     * @param notehost         String
     */
    private void updateGuest(String eventId, int userId, String invitationstatus, String notehost) {
        WebResource resource = client.resource(path("guest", "update", eventId, userId));
        Form postBody = new Form();

        postBody.add("invitationstatus", invitationstatus);
        postBody.add("notehost", notehost);

        resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(postBody);
    }

    /**
     * Checking if the user is registered
     *
     * @param username String
     * @param eventId  String
     * @return Boolean
     * @throws IOException the exception
     */
    private Boolean isUserRegistered(String username, String eventId) throws IOException {
        return readResource(path("guest", "registered", username, eventId), BOOLEAN);
    }

    private Integer getUserData(String username) throws IOException {
        return readResource(path("person", "userdata", "lite", username), INTEGER);
    }

    /**
     * Getting the transporter to show data according to the layout
     *
     * @param eventId  int
     * @param username String
     * @return EventTransporter
     * @throws IOException IOException
     */
    private EventTransporter getEventData(int eventId, String username) throws IOException {

        Integer personId = getUserData(username);
        Guest guest = readResource(path("guest", eventId, personId), GUEST);
        Event event = readResource(path("event", eventId), EVENT);
        EventTransporter transporter = createEventTransporter(guest, event);

        return transporter;
    }

    /**
     * Converting Event-Guest to EventTransporter to show data into the layout
     *
     * @param guest Guest
     * @param event Event
     * @return EventTransporter
     */
    private EventTransporter createEventTransporter(Guest guest, Event event) {
        EventTransporter transporter = new EventTransporter();

        transporter.setDatebegin(event.getDatebegin());
        transporter.setMessage(guest.getNotehost());
        transporter.setShortname(event.getShortname());
        transporter.setStatus(guest.getInvitationstatus());

        return transporter;
    }

    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }

    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        return resourceReader.readStringResource(path, type);
    }
}
