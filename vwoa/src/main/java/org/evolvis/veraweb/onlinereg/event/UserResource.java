package org.evolvis.veraweb.onlinereg.event;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
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
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
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
import org.evolvis.veraweb.onlinereg.entities.LinkUUID;
import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailDispatcher;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.evolvis.veraweb.onlinereg.user.LoginResource;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Resource to register new users in OSIAM backend
 */
@Log
@Getter
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    /** Person type */
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {
    };

    /** Servlet context */
    @javax.ws.rs.core.Context
    private HttpServletRequest request;

    private static final String VERAWEB_SCHEME = "urn:scim:schemas:veraweb:1.5:Person";
    private Config config;
    private Client client;
    private ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_RESOURCE = "/rest";
    private static final TypeReference<OsiamUserActivation> OSIAM_USER_ACTIVATION = new TypeReference<OsiamUserActivation>() {};
    private static final TypeReference<List<LinkUUID>> LINK_UUID_TYPE_REFERENCE = new TypeReference<List<LinkUUID>>() {};
    private OsiamClient osiamClient;

    /**
     * Creates new UserResource
     *
     * @param config
     *            configuration
     * @param client
     *            jersey client
     */
    public UserResource(Config config, Client client) {
        this.config = config;
        this.client = client;
        osiamClient = config.getOsiam().getClient(client);
    }

    /**
     * FIXME
     *
     * @param email Email
     * @param currentLanguageKey Current language in frontend
     *
     * @throws IOException FIXME
     * @return FIXME
     */
    @POST
    @Path("/request/resend-login")
    public String resendLogin(@FormParam("email") String email, @FormParam("current_language") String currentLanguageKey) throws IOException {
        Form postBody = new Form();
        postBody.add("mail", email);
        postBody.add("currentLanguageKey", currentLanguageKey);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/forgotLogin/resend/login");
        resource.post(postBody);

        return StatusConverter.convertStatus("OK");
    }
    /**
     * FIXME
     *
     * @param username Username
     * @param currentLanguageKey Current language in frontend
     *
     * @throws IOException FIXME
     *
     * @return FIXME
     */
    @POST
    @Path("/request/reset-password-link")
    public String resetPassword(@FormParam("username") String username, @FormParam("current_language") String currentLanguageKey) throws IOException {
        final Person person = getUserData(username);
        if (person == null) {
            //send OK to not show if username was correct
            return StatusConverter.convertStatus("OK");
        } else {
            return sendEmailWithResetPasswordLink(person, currentLanguageKey, person.getPk(), config.getOnlineRegistrationEndpoint());
        }

    }

    private String sendEmailWithResetPasswordLink(Person person, String currentLanguageKey, Integer personId, String oaEndpoint) throws IOException {
        Form postBody = new Form();
        postBody.add("username", person.getUsername());
        postBody.add("currentLanguageKey", currentLanguageKey);
        postBody.add("oaEndpoint", oaEndpoint);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/forgotPassword/request/reset-password-link");
        resource.post(postBody);

        return StatusConverter.convertStatus("OK");
    }

    /**
     * Get Person object by username
     *
     * @return Person, if person exists
     * @throws IOException
     *             TODO
     */
    @GET
    @Path("/userdata")
    public Person getUserData() throws IOException {
        final String username = (String) request.getAttribute(LoginResource.USERNAME);
        final Person person = getUserData(username);

        if (person != null) {
            return person;
        }

        return null;
    }

    /**
     * Creates a new user
     *
     * @param osiam_username user name
     * @param osiam_firstname first name
     * @param osiam_secondname family name
     * @param osiam_password1 password
     * @param currentLanguageKey current language in frontend
     * @param email email
     * @return result of creation. Values can be "OK", "INVALID_USERNAME" or
     *         "USER_EXISTS"
     * @throws IOException FIXME
     */
    @POST
    @Path("/register/{osiam_username}")
    public String registerUser(@PathParam("osiam_username") String osiam_username, @FormParam("osiam_firstname") String osiam_firstname,
            @FormParam("osiam_secondname") String osiam_secondname, @FormParam("osiam_password1") String osiam_password1,
            @FormParam("osiam_email") String email, @FormParam("current_language") String currentLanguageKey) throws IOException {

        if (!osiam_username.matches("\\w+")) {
            return StatusConverter.convertStatus("INVALID_USERNAME");
        }

        final String accessToken = osiamClient.getAccessTokenClientCred("GET", "POST");

        User user = osiamClient.getUser(accessToken, osiam_username);
        if (user != null) {
            return StatusConverter.convertStatus("USER_EXISTS");
        }

        final Form postBody = createPersonPostBody(osiam_username, osiam_firstname, osiam_secondname, email);

        final Person person;
        try {
            final WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
            person = r.post(Person.class, postBody);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return StatusConverter.convertStatus("USER_EXISTS");
            }

            return StatusConverter.convertStatus("USER_NOT_CREATED");
        }

        user = initUser(osiam_username, osiam_firstname, osiam_secondname, osiam_password1, email, person.getPk());
        osiamClient.createUser(accessToken, user);
        final String activationToken = UUID.randomUUID().toString();
        sendEmailVerification(email, currentLanguageKey, activationToken);
        addOsiamUserActivationEntry(osiam_username, activationToken);

        return StatusConverter.convertStatus("OK");
    }

    private void sendEmailVerification(String email, String currentLanguageKey, String activationToken) {
        final EmailDispatcher emailDispatcher = new EmailDispatcher(config, client);
        emailDispatcher.sendEmailVerification(email, activationToken, currentLanguageKey, false);
    }

    /**
     * Activates a user of the given activation token, if the link isn't expired
     *
     * @param activationToken Activation token
     * @return result of activation. Values can be "OK", "LINK_INVALID" or
     *         "LINK_EXPIRED"
     * @throws IOException FIXME
     */
    @GET
    @Path("/activate/{activationToken}")
    public String activateUser(@PathParam("activationToken") String activationToken) throws IOException {
        final OsiamUserActivation osiamUserActivation = getOsiamUserActivationByToken(activationToken);
        if (osiamUserActivation.getUsername() == null) {
            return StatusConverter.convertStatus("LINK_INVALID");
        } else if (osiamUserActivation.getExpiration_date().before(new Date())) {
            return StatusConverter.convertStatus("LINK_EXPIRED");
        } else {
            removeOsiamUserActivationEntry(activationToken);
            setOsiamUserAsActive(osiamUserActivation.getUsername());
            return StatusConverter.convertStatus("OK");
        }
    }

    /**
     * Sends a new E-Mail to the address of the user of the given
     * activation_token
     *
     * @param oldActivationToken The old activation token
     * @param currentLanguageKey
     *            For the language, the E-Mail will be send
     * @return result of post. Value can be "OK"
     * @throws IOException FIXME
     */
    @POST
    @Path("/update/activation/data")
    public String refreshActivationToken(@FormParam("activation_token") String oldActivationToken, @FormParam("language") String currentLanguageKey)
            throws IOException {

        final Form postBody = new Form();
        final String activationToken = UUID.randomUUID().toString();

        postBody.add("activation_token", activationToken);

        final OsiamUserActivation osiamUserActivation = getOsiamUserActivationByToken(oldActivationToken);

        final String osiamUsername = osiamUserActivation.getUsername();
        final User user = osiamClient.getUser(osiamClient.getAccessTokenClientCred("GET", "POST", "DELETE", "PATCH", "PUT"), osiamUsername);
        final String email = user.getEmails().get(0).getValue().toString();
        postBody.add("username", osiamUsername);
        postBody.add("email", email);
        postBody.add("endpoint", config.getOnlineRegistrationEndpoint());
        postBody.add("language", currentLanguageKey);

        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/osiam/user/refresh/activation/data");
        resource.post(postBody);
        return StatusConverter.convertStatus("OK");
    }

    /**
     * Checks if the user is registered to any events
     *
     * @return Status of getUserRegisteredToEvents
     */
    @GET
    @Path("/userdata/existing/event")
    public String getExistingEvents() {
        if (isAssociatedWithEvent()) {
            return StatusConverter.convertStatus("OK");
        } else {
            return StatusConverter.convertStatus("ERROR");
        }
        
    }

    private boolean isAssociatedWithEvent() {
        final String username = (String) request.getAttribute(LoginResource.USERNAME);
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "event", "userevents", "existing", username);
        final WebResource resource = client.resource(path);
        Boolean b = resource.get(Boolean.class) == true;
        return b;
    }

    /**
     * Updates the core data of a user
     *
     * @param fk_salutation Salutation id
     * @param salutation
     *            (salutation and fk_salutation are in the tperson table
     *            present)
     * @param title Title
     * @param firstName First name
     * @param lastName Last name
     * @param birthday Birthday
     * @param nationality Nationality
     * @param languages Languages
     * @param gender Gender
     * @return result of update. Values can be "OK" and
     *         "USER_ACCOUNT_CORE_DATA_COULD_NOT_UPDATE"
     * @throws IOException FIXME
     */
    @POST
    @Path("/userdata/update")
    public String updateUserCoreData(@FormParam("person_fk_salutation") Integer fk_salutation, @FormParam("person_salutation") String salutation,
            @FormParam("person_title") String title, @FormParam("person_firstName") String firstName, @FormParam("person_lastName") String lastName,
            @FormParam("person_birthday") Date birthday, @FormParam("person_nationality") String nationality,
            @FormParam("person_languages") String languages, @FormParam("person_gender") Integer gender) throws IOException {
        if(! isAssociatedWithEvent()){
            return StatusConverter.convertStatus("USER_ACCOUNT_CORE_DATA_COULD_NOT_UPDATE");
        }
        final String username = (String) request.getAttribute(LoginResource.USERNAME);
        final Form postBody = createUserCoreDataPostBody(username, fk_salutation, salutation, title, firstName, lastName, birthday, nationality,
                languages, gender);

        return updateUserCoreDataWithGivenValues(postBody);
    }

    private List<LinkUUID> getUuidListByPersonId(Integer personId) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String path = resourceReader.constructPath(BASE_RESOURCE, "links", "byPersonId", personId);
        return resourceReader.readStringResource(path, LINK_UUID_TYPE_REFERENCE);
    }

    /**
     * Get Person instance from one username
     *
     * @param username
     *            Username
     * @return Person
     * @throws IOException
     *             TODO
     */
    private Person getUserData(String username) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String osiamUserActivationPath = resourceReader.constructPath(BASE_RESOURCE, "person", "userdata", username);
        return resourceReader.readStringResource(osiamUserActivationPath, PERSON);
    }

    private void setOsiamUserAsActive(String username) throws IOException {
        final String accessTokenAsString = osiamClient.getAccessTokenClientCred("GET", "POST", "DELETE", "PATCH", "PUT");
        final AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
        final User user = osiamClient.getUser(accessTokenAsString, username);
        final UpdateUser updatedUser = new UpdateUser.Builder().updateActive(true).build();
        osiamClient.updateUser(user.getId(), updatedUser, accessToken);
    }

    private void removeOsiamUserActivationEntry(String activationToken) throws IOException {
        final Form postBody = new Form();
        postBody.add("osiam_user_activation", activationToken);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/osiam/user/activate");
        resource.post(postBody);
    }

    private OsiamUserActivation getOsiamUserActivationByToken(String activationToken) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String osiamUserActivationPath = resourceReader.constructPath(BASE_RESOURCE, "osiam", "user", "get", "activation", activationToken);
        return resourceReader.readStringResource(osiamUserActivationPath, OSIAM_USER_ACTIVATION);
    }

    private void addOsiamUserActivationEntry(String osiamUsername, String activationToken) {
        final Form postBody = new Form();
        postBody.add("activation_token", activationToken);
        postBody.add("username", osiamUsername);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/osiam/user/create");
        resource.post(postBody);
    }

    private void passUsernameToOsiamUserActivation(String username, UUID activation_token) {
        final Form postBody = new Form();
        postBody.add("username", username);
        postBody.add("activation_token", activation_token.toString());
        final OsiamUserActivation oua;
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/osiamUserActivation/user/new");
        oua = resource.post(OsiamUserActivation.class, postBody);
    }

    private Form createPersonPostBody(String osiam_username, String osiam_firstname, String osiam_secondname, String email) {
        final Form postBody = new Form();
        postBody.add("username", osiam_username);
        postBody.add("firstname", osiam_firstname);
        postBody.add("lastname", osiam_secondname);
        postBody.add("email", email);
        return postBody;
    }

    private User initUser(@PathParam("osiam_username") String osiam_username, @FormParam("osiam_firstname") String osiam_firstname,
            @FormParam("osiam_secondname") String osiam_secondname, @FormParam("osiam_password1") String osiam_password1,
            @FormParam("email") String email, Integer personId) {
        User user;
        final Email userEmail = buildUserEmail(email);
        user = new User.Builder(osiam_username).setName(new Name.Builder()
                .setGivenName(osiam_firstname).setFamilyName(osiam_secondname).build())
                .setPassword(osiam_password1).setActive(false).addEmail(userEmail)
                .addExtension(new Extension.Builder(VERAWEB_SCHEME).setField("tpersonid", BigInteger.valueOf(personId)).build()).build();
        return user;
    }

    private Email buildUserEmail(@FormParam("email") String email) {
        return new Email.Builder().setType(Email.Type.HOME).setValue(email).build();
    }

    private Form createUserCoreDataPostBody(String username, Integer fk_salutation, String salutation, String title, String firstName,
            String lastName, Date birthday, String nationality, String languages, Integer gender) {
        final Form postBody = new Form();

        postBody.add("username", username);
        postBody.add("fk_salutation", fk_salutation);
        postBody.add("salutation", salutation);
        postBody.add("title", title);
        postBody.add("firstName", firstName);
        postBody.add("lastName", lastName);
        // Can't post a date, but an epoch
        if (birthday != null) {
            postBody.add("birthday", birthday.getTime());
        }
        postBody.add("nationality", nationality);
        postBody.add("languages", languages);
        postBody.add("gender", resolveGenderValueFromOptionIds(gender));

        return postBody;
    }

    private String updateUserCoreDataWithGivenValues(Form postBody) {
        try {
            final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/usercoredata/update/");
            resource.post(postBody);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return StatusConverter.convertStatus("USER_ACCOUNT_CORE_DATA_COULD_NOT_UPDATE");
            }

            return StatusConverter.convertStatus("USER_ACCOUNT_CORE_DATA_COULD_NOT_UPDATE");
        }

        return StatusConverter.convertStatus("OK");
    }

    private String resolveGenderValueFromOptionIds(@FormParam("person_gender") Integer gender) {
        String genderResolved = "";

        // Handle gender ids of option select
        if (gender == 1) {
            genderResolved = "m";
        } else if (gender == 2) {
            genderResolved = "f";
        }
        return genderResolved;
    }
}