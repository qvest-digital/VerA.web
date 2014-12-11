package org.evolvis.veraweb.onlinereg.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Rest api for delegations and extra fields
 * @author jnunez
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
public class DelegationResource extends AbstractResource {

    @GET
    @Path("/fields/{eventId}")
    public List<OptionalField> getFieldsFromEvent(@PathParam("eventId") int eventId) {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("OptionalField.findByEventId");
            query.setInteger("eventId", eventId);
            
            return (List<OptionalField>) query.list();
        } finally {
            session.close();
        }
    }
	
	
    @GET
    @Path("/values")
    public Guest getDelegationByFieldAndGuest(@QueryParam("fieldId") Integer fieldId, @QueryParam("guestId") Integer guestId) {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("Guest.findByEventAndUser");
            query.setInteger("fk_delegation_field", fieldId);
            query.setInteger("fk_guest", guestId);
            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }
    

	
}
