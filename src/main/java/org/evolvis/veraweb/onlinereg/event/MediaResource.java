/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package org.evolvis.veraweb.onlinereg.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailDispatcher;
import org.evolvis.veraweb.onlinereg.mail.EmailValidator;
import org.evolvis.veraweb.onlinereg.utils.PressTransporter;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.evolvis.veraweb.onlinereg.utils.VerawebConstants;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

/**
 * @author jnunez
 */
@Path("/media")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class MediaResource {
    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";

    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {};
    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {};
    private static final TypeReference<MediaRepresentativeActivation> MEDIA_REPRESENTATIVE_ACTIVATION = new TypeReference<MediaRepresentativeActivation>() {};
    private static final String INVITATION_TYPE = "2";

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();
    private Config config;
    private Client client;

    /**
     * Default constructor.
     */
    public MediaResource() {}

    /**
     * Alternative constructor.
     *
     * @param config The configuration
     * @param client Client
     */
	public MediaResource(Config config, Client client) {
		super();
		this.config = config;
		this.client = client;
	}

    /**
     * Adds delegate to event
     *
     * @param uuid     Event uuid
     * @param nachname family name
     * @param vorname  first name
     * @param gender   gender
     * @param email    E-Mail Address
     * @param address  address
     * @param plz      zip code
     * @param city     city
     * @param country  country
     *
     * @throws java.io.IOException If creating person and saving it as guest fails.
     *
     * @return result of delegation. Values can be "WRONG_EVENT"
     */
    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(
            @PathParam("uuid") String uuid,
            @FormParam("nachname") String nachname,
            @FormParam("vorname") String vorname,
            @FormParam("gender") String gender,
            @FormParam("email") String email,
            @FormParam("address") String address,
            @FormParam("plz") String plz,
            @FormParam("city") String city,
            @FormParam("country") String country,
            @FormParam("current_language") String currentLanguageKey) throws IOException {

        final Boolean emailValid = EmailValidator.isValidEmailAddress(email);

        if(emailValid) {
            final Boolean mediaRepresentationIsFound = checkForExistingPressEvent(uuid);

            if (mediaRepresentationIsFound) {
                final String activationToken = UUID.randomUUID().toString();
                final Integer eventId = getEventIdFromUuid(uuid);
                final Boolean existinPressUser = checkForExistingPressUserActivation(email, eventId);
                if (existinPressUser) {
                    return StatusConverter.convertStatus("PRESS_USER_EXISTS_ALREADY");
                }

                addMediaRepresentativeActivationEntry(
                        nachname, vorname, gender, email, address, plz, city, country, activationToken, eventId
                );
                sendEmailVerification(email, activationToken, currentLanguageKey);
                return StatusConverter.convertStatus("OK");

            }

            return StatusConverter.convertStatus("WRONG_EVENT");
        }

        return StatusConverter.convertStatus("WRONG_EMAIL");
    }

    // http://localhost:8181/#/media/activation/confirm/40a576c8-bac0-419f-b8a8-d68826e59613
    @GET
    @Path("/activation/confirm/{pressUserActivationToken}")
    public String activateMediaUser(@PathParam("pressUserActivationToken") String expectedActivationToken) throws IOException {
        final MediaRepresentativeActivation mediaRepresentativeActivation = getPressUserByActivationToken(expectedActivationToken);
        if (mediaRepresentativeActivation != null && mediaRepresentativeActivation.getActivated().equals(VerawebConstants.MEDIA_REPRESENTATIVE_INACTIVE)) {
            setMediaUserAsActive(mediaRepresentativeActivation.getEmail(), mediaRepresentativeActivation.getFk_event());
            final Event event = getEvent(mediaRepresentativeActivation.getFk_event());
            final PressTransporter transporter = initPressTransporter(mediaRepresentativeActivation, event);
            final String resultStatus = createAndAssignMediaRepresentativeGuest(transporter);
            return StatusConverter.convertStatus(resultStatus);
        }
        return StatusConverter.convertStatus("PRESS_USER_ALREADY_ACTIVATED");
    }

    private void setMediaUserAsActive(String email, Integer eventId) {
        final Form postBody = new Form();
        postBody.add("email", email);
        postBody.add("eventId", eventId);

        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/press/activation/update");
        resource.post(postBody);
    }

    private PressTransporter initPressTransporter(MediaRepresentativeActivation mediaRepresentativeActivation, Event event) {
        return new PressTransporter(
            event.getMediarepresentatives(),
            mediaRepresentativeActivation.getLastname(),
            mediaRepresentativeActivation.getFirstname(),
            mediaRepresentativeActivation.getGender(),
            mediaRepresentativeActivation.getEmail(),
            mediaRepresentativeActivation.getAddress(),
            mediaRepresentativeActivation.getZip().toString(),
            mediaRepresentativeActivation.getCity(),
            mediaRepresentativeActivation.getCountry(),
            usernameGenerator()
        );
    }

    private Event getEvent(Integer eventId) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "event", eventId);
        return resourceReader.readStringResource(path, EVENT);
    }

    private MediaRepresentativeActivation getPressUserByActivationToken(String activationToken) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "press", "activation", "exists", activationToken);
        return resourceReader.readStringResource(path, MEDIA_REPRESENTATIVE_ACTIVATION);
    }

    private Boolean checkForExistingPressUserActivation(String email, Integer eventId) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String encodedAddress = URLEncoder.encode(email, "utf8");
        final String path = resourceReader.constructPath(BASE_RESOURCE, "press", "activation", "exists", encodedAddress, eventId);

        return resourceReader.readStringResource(path, BOOLEAN);
    }

    private void addMediaRepresentativeActivationEntry(
            final String lastname,
            final String firstname,
            final String gender,
            final String email,
            final String address,
            final String zip,
            final String city,
            final String country,
            final String activationToken,
            final Integer eventId) {

        final Form postBody = new Form();
        postBody.add("activationToken", activationToken);
        postBody.add("address", address);
        postBody.add("city", city);
        postBody.add("country", country);
        postBody.add("email",email);
        postBody.add("eventId", eventId);
        postBody.add("firstname", firstname);
        postBody.add("gender", gender);
        postBody.add("lastname", lastname);
        postBody.add("zip", zip);

        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/press/activation/create");
        resource.post(postBody);
    }

    private void sendEmailVerification(String email, String activationToken, String currentLanguageKey) {
        final EmailDispatcher emailDispatcher = new EmailDispatcher(config, client);
        emailDispatcher.sendEmailVerification(email, activationToken, currentLanguageKey, true);
    }

    private String createAndAssignMediaRepresentativeGuest(PressTransporter transporter) throws IOException {
        // Assing person to event as guest
        final Integer eventId = getEventIdFromUuid(transporter.getUuid());
        // Store in tperson
        final Integer personId = createPerson(eventId, transporter);

        if (eventId == null) {
            return "NO_EVENT_DATA";
        }
        addGuestToEvent(transporter.getUuid(), String.valueOf(eventId), String.valueOf(personId),
                transporter.getGender(), transporter.getUsername());

        return "OK";
    }

    /**
     * Includes a new guest in the database - Table "tguest"
     *
     * @param uuid FIXME
     * @param eventId Event id
     * @param userId User id
     * @param gender gender
     * @throws IOException
     */
    private void addGuestToEvent(String uuid, String eventId, String userId, String gender, String username)
            throws IOException {
        final Integer categoryID = getCategoryIdFromCatname(uuid);

        final WebResource resource = getAddGuestResource(uuid);
        final Form postBody = new Form();

        postBody.add("eventId", eventId);
        postBody.add("userId", userId);
        postBody.add("invitationstatus", "0");
        postBody.add("invitationtype", INVITATION_TYPE);
        postBody.add("gender", gender);
        postBody.add("category", String.valueOf(categoryID));
        postBody.add("username", username);

        resource.post(Guest.class, postBody);
    }

    private WebResource getAddGuestResource(String uuid) {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "guest", uuid, "register");
        return client.resource(path);
    }

    /**
     * Searching an event ID using the UUID
     */
    private Integer getEventIdFromUuid(String uuid) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "event", "require", uuid);
        return resourceReader.readStringResource(path, INTEGER);
    }

    /**
     * Searching the ID of one category using the catname
     */
    private Integer getCategoryIdFromCatname(String uuid) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "category", "Pressevertreter", uuid);
        return resourceReader.readStringResource(path, INTEGER);
    }

    /**
     * Includes a new person in the database - Table "tperson"
     *
     * @param eventId   event ID
     * @param transporter transporter
     *
     * return primary key for a person
     */
    private Integer createPerson(Integer eventId, PressTransporter transporter) {
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/press/");
        final Form postBody = new Form();

        postBody.add("eventId", String.valueOf(eventId));
        postBody.add("username", transporter.getUsername());
        postBody.add("firstname", transporter.getVorname());
        postBody.add("lastname", transporter.getNachname());
        postBody.add("gender", correctGender(transporter.getGender()));
        postBody.add("email", transporter.getEmail());
        postBody.add("address", transporter.getAddress());
        postBody.add("plz", transporter.getPlz());
        postBody.add("city", transporter.getCity());
        postBody.add("country", transporter.getCountry());

        final Person person = resource.post(Person.class, postBody);

        return person.getPk();
    }

    private Boolean checkForExistingPressEvent(String uuid) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "event", "exist", uuid);
        return resourceReader.readStringResource(path, BOOLEAN);
    }

    private String usernameGenerator() {
        final Date current = new Date();

        return "press" + current.getTime();
    }

    private String correctGender(String gender) {
        String dbGender;
        if (gender.equals("Herr")) {
            dbGender = "m";
        } else {
            dbGender = "f";
        }

        return dbGender;
    }

}
