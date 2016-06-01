package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/forgotPassword")
@Produces(MediaType.APPLICATION_JSON)
public class ForgotPasswordResource extends AbstractResource {

    @POST
    @Path("/request/reset-password-link")
    public String requestResetPasswordLink(@FormParam("username") String username) {
        return username;
    }

}
