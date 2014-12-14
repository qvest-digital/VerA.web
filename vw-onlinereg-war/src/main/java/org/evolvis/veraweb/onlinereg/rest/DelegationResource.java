package org.evolvis.veraweb.onlinereg.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.pk.DelegationPK;
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
    @Path("/fields/{eventId}/{guestId}")
    public List<OptionalFieldValue> getFieldsFromEvent(
    		@PathParam("eventId") int eventId,
    		@PathParam("guestId") int guestId) {

        Session session = openSession();
        try {
            Query query = session.getNamedQuery("OptionalField.findByEventId");
            query.setInteger("eventId", eventId);

            List<OptionalField> fields = (List<OptionalField>) query.list();
            List<OptionalFieldValue> result = new ArrayList<>(fields.size());

            for(OptionalField field : fields){
            	query = session.getNamedQuery(Delegation.QUERY_FIND_BY_GUEST);
            	query.setInteger(Delegation.PARAM_GUEST_ID, guestId);
            	query.setInteger(Delegation.PARAM_FIELD_ID, field.getPk());

            	Delegation delegation = (Delegation)query.uniqueResult();
            	OptionalFieldValue newValue = new OptionalFieldValue(field,
            			delegation == null ? null : delegation.getValue());

            	result.add(newValue);
            }

            return result;
        } finally {
            session.close();
        }
    }


    @GET
    @Path("/field/{eventId}")
    public Integer getLabelIdfromEventAndLabel(@PathParam("eventId") int eventId, @QueryParam("label") String label) {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("OptionalField.findByEventIdAndLabel");
            query.setInteger("eventId", eventId);
            query.setString("label", label);

            return (Integer) query.uniqueResult();
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

    @POST
    @Path("/field/save")
    public Delegation saveOptionalField(@QueryParam("guestId") Integer guestId, @QueryParam("fieldId") Integer fieldId, @QueryParam("fieldValue") String fieldValue) {
        Session session = openSession();
        try {
        	Delegation delegation = new Delegation();
        	delegation.setPk(new DelegationPK(guestId,fieldId));
        	delegation.setValue(fieldValue);

        	session.saveOrUpdate(delegation);
        	session.flush();

        	return delegation;

        } finally {
            session.close();
        }
    }

}
