package org.evolvis.veraweb.onlinereg.user;

/*-
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.auth.HmacToken;
import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.evolvis.veraweb.onlinereg.mail.EmailValidator;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.osiam.resources.scim.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

/**
 * Created by mley on 26.08.14. Edited by Max Marche, m.marche@tarent.de
 */
@Path("/idm")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class LoginResource {

    public static final String USERNAME = "USERNAME";

    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    // TYPE REFERENCES
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {
    };
    private static final TypeReference<OsiamUserActivation> OSIAM_USER_ACTIVATION = new TypeReference<OsiamUserActivation>() {
    };

    /**
     * key name for access tokens
     */
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    /**
     * Jackson Object Mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * configuration
     */
    private final Config config;
    private Client client;

    /**
     * Servlet context
     */
    @javax.ws.rs.core.Context
    private HttpServletRequest request;

    private final ResourceReader resourceReader;

    public static final String VWOA_ACCESS_TOKEN = "vwoa-access-token";

    /**
     * Creates a new LoginResource
     *
     * @param config configuration
     */
    public LoginResource(Config config, Client client) {
        this.config = config;
        this.client = client;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    /**
     * Logs a user in.
     *
     * @param userName
     *            user name or user email
     * @param password
     *            password
     * @return true if login was successful, fals if username or password are
     *         not vaild
     */
    @POST
    @Path("/login/{username}")
    public Response login(@PathParam("username") String userName, @FormParam("password") String password, @FormParam("delegation") String delegation)
            throws IOException {
        if (userName == null || password == null) {
            return null;
        }

        // if the given login name looks like a email, lookup the osiam user
        // by email address.
        if (EmailValidator.isValidEmailAddress(userName)) {
            User userByEmail = config.getOsiam().getClient(this.client).getUserByEmail(userName);
            userName = userByEmail.getUserName();
        }

        // have osiam check the credentials and optain an access token
        String accessToken;
        try {
            accessToken = config.getOsiam().getClient(client).getAccessTokenUserPass(userName, password, "GET", "POST");
        } catch (Exception ex) {
            accessToken = null;
        }

        if (accessToken == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        // finally, fetch a displayable user name for the account
        // First- and lastname or a company name.
        // This may actually be empty or null. We don't care. Handle it on the
        // client side.
        String displayName = fetchDisplayName(userName);

        // we have everything. Create a hmac token and store it in a cookie.
        final String encodedToken = new HmacToken(accessToken, System.currentTimeMillis()).toString();
        final NewCookie cookie = cookie(encodedToken,
                600 /* i dunno...ten minutes? */);
        return Response.ok(new State(userName, displayName)).cookie(cookie).build();
    }

    private String fetchDisplayName(String userName) {
        ClientResponse resp = client.resource(path("person", "userinfo", userName)).get(ClientResponse.class);
        // may happen e.g. for delegation users. (generated username, no actual
        // user data)
        if (resp.getStatus() == Status.NO_CONTENT.getStatusCode()) {
            return null;
        }

        return resp.getEntity(String.class);
    }

    private NewCookie cookie(final String encodedToken, int maxAge) {
        // FIXME: we absolutely want the secure flag to be set here, at least in
        // production!
        // but this would break typical dev situation (no https) What to do?
        NewCookie cookie = new NewCookie(VWOA_ACCESS_TOKEN, encodedToken, "/", null, null, maxAge, false);
        return cookie;
    }

    public static class State {
        public final String accountName;
        public final String displayName;
        public final boolean authenticated;

        public State() {
            this.authenticated = false;
            this.accountName = null;
            this.displayName = null;
        }

        public State(String userAccountName, String userDisplayName) {
            this.accountName = userAccountName;
            this.displayName = userDisplayName;
            this.authenticated = true;
        }

        public String getAccountName() {
            return accountName;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Test if user is logged in.
     *
     * @return true if user is logged in, which means username and valid access
     *         token are stored in the session context.
     */
    @GET
    @Path("/login")
    public State queryStatus() throws IOException {
        final String accessToken = (String) request.getAttribute(ACCESS_TOKEN);
        final String username = (String) request.getAttribute(USERNAME);

        if (accessToken == null || username == null) {
            return new State();
        }
        final String displayName = fetchDisplayName(username);

        return new State(username, displayName);
    }

    /**
     * Logs the current logged in user out
     */
    @POST
    @Path("/logout")
    public Response logout() {
        request.removeAttribute(USERNAME);
        request.removeAttribute(ACCESS_TOKEN);
        return Response.ok(new State()).cookie(cookie("egal", 0)).build();
    }

    private String getSuffix(String link, String host, Integer port) {
        final String suffix;
        if (port > 0) {
            suffix = link.substring(link.indexOf(port.toString()) + port.toString().length(), link.length());
        } else {
            suffix = link.substring(link.indexOf(host) + host.toString().length(), link.length());
        }
        return suffix;
    }

    private String buildLink(String endpoint, String activationToken) {
        return endpoint + "user/resend/confirmationmail/" + activationToken;
    }

    private OsiamUserActivation getOsiamUserActivationByUsername(String username) throws IOException {

        final String osiamUserActivationPath = resourceReader.constructPath(BASE_RESOURCE, "osiam", "user", "get", "activation", "byusername",
                username);
        return resourceReader.readStringResource(osiamUserActivationPath, OSIAM_USER_ACTIVATION);
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path
     * fragmensts.
     *
     * @param path
     *            path fragments
     * @return complete path as string
     */
    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T> Type of requested entity
     * @return requested resource
     * @throws IOException FIXME
     */
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        return resourceReader.readStringResource(path, type);
    }

}
