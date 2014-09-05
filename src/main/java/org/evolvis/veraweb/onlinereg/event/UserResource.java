package org.evolvis.veraweb.onlinereg.event;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;

import lombok.Getter;

/**
 * Resource to register new users in OSIAM backend
 */
@Getter
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final String VERAWEB_SCHEME = "urn:scim:schemas:veraweb:1.5:Person";
	private Config config;
    private Client client;

    /**
     * Creates new UserResource
     *
     * @param config configuration
     * @param client jersey client
     */
    public UserResource(Config config, Client client) {
        this.config = config;
        this.client = client;
    }

    /**
     * Creates a new user
     *
     * @param osiam_username   user name
     * @param osiam_firstname  first name
     * @param osiam_secondname family name
     * @param osiam_password1  password
     * @return result of creation. Values can be "OK", "INVALID_USERNAME" or "USER_EXISTS"
     * @throws IOException
     */
    @POST
    @Path("/register/{osiam_username}")
    public String registerUser(@PathParam("osiam_username") String osiam_username,
                               @QueryParam("osiam_firstname") String osiam_firstname,
                               @QueryParam("osiam_secondname") String osiam_secondname,
                               @QueryParam("osiam_password1") String osiam_password1) throws IOException {

        if (!osiam_username.matches("\\w+")) {
            return "INVALID_USERNAME";
        }

        OsiamClient osiamClient = config.getOsiam().getClient(client);
        String accessToken = osiamClient.getAccessToken("GET POST");

        User user = osiamClient.getUser(accessToken, osiam_username);
        if (user != null) {
            return "USER_EXISTS";
        }
        
        WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", osiam_username).queryParam("firstname", osiam_firstname).queryParam("lastname", osiam_secondname);
        Person person = r.post(Person.class);

		user = new User.Builder(osiam_username)
                .setName(new Name.Builder().setGivenName(osiam_firstname).setFamilyName(osiam_secondname).build())
                .setPassword(osiam_password1)
                .setActive(true)
                .addExtension(new Extension.Builder(VERAWEB_SCHEME).setField("tpersonid", BigInteger.valueOf(person.getPk())).build())
                .build();              
        

        osiamClient.createUser(accessToken, user);      
       

        return "OK";
    }
}