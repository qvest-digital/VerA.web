package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path(RestPaths.REST_OPTIONALFIELDS)
@Produces(MediaType.APPLICATION_JSON)
public class OptionalFieldResource extends AbstractResource {
    @GET
    @Path(RestPaths.REST_OPTIONALFIELDS_GET_ALL)
    public List<OptionalField> getOptionalFields(@PathParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID);
            query.setParameter("eventId", eventId);
            return (List<OptionalField>) query.list();
        } finally {
            session.close();
        }
    }
}
