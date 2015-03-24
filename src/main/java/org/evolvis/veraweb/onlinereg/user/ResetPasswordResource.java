package org.evolvis.veraweb.onlinereg.user;

import com.sun.jersey.api.client.WebResource;
import org.evolvis.veraweb.onlinereg.entities.Event;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

/**
 * This class is used for password reset actions (for example: reset password).
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/reset/password")
public class ResetPasswordResource {

    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";

    /**
     * Show view for password reset.
     *
     * @param personId The ID of the person which will set his/her own password.
     */
    @POST
    @Path("/{personId}")
    public void getEvenByUUId(@PathParam("personId") Integer personId) {
        // TODO
    }
}
