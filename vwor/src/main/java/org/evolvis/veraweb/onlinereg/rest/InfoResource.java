package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.RestPaths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by mweier on 23.03.16.
 */
@Path(RestPaths.REST_INFO)
@Produces(MediaType.TEXT_PLAIN)
public class InfoResource extends AbstractResource {
    @GET
    public String getInfo() {
        return getClass().getPackage().getImplementationVersion();
    }
}
