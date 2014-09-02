package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Config;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by mley on 02.09.14.
 */
@Path("/available")
public class HealthResource extends AbstractResource {

    @GET
    @Path("/")
    public  String health() {
        Session session = openSession();
        try {

            Query q = session.getNamedQuery("Config.find");
            q.setString("key", "SCHEMA_VERSION");
            Config c = (Config)q.uniqueResult();
            if( c != null) {
                return "OK";
            } else {
                return "NO SCHEMA_VERSION";
            }
        } finally {
            session.close();
        }

    }
}
