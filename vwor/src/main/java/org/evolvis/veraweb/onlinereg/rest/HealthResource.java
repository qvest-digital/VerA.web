package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.onlinereg.entities.Config;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by mley on 02.09.14.
 */
@Path(RestPaths.REST_HEALTH_AVAILABLE)
public class HealthResource extends AbstractResource {
    @GET
    public String health() {
        try (Session session = openSession()) {
            Query q = session.getNamedQuery("Config.find");
            q.setParameter("key", "SCHEMA_VERSION");
            Config c = (Config) q.uniqueResult();
            if (c != null) {
                return "OK";
            } else {
                return "NO SCHEMA_VERSION";
            }
        }
    }
}
