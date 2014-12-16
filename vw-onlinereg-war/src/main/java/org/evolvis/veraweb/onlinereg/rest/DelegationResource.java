package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.pk.DelegationPK;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Rest api for delegations and extra fields.
 *
 * @author jnunez
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
public class DelegationResource extends AbstractResource {

    /**
     * Get optional fields content for a guest.
     *
     * @param eventId Event id
     * @param guestId Guest id
     *
     * @return Fields content
     */
    @GET
    @Path("/fields/list/{eventId}/{guestId}")
    public List<OptionalFieldValue> getFieldsFromEvent(
    		@PathParam("eventId") int eventId,
    		@PathParam("guestId") int guestId) {

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OptionalField.findByEventId");
            query.setInteger("eventId", eventId);
            final List<OptionalField> fields = (List<OptionalField>) query.list();

            return convertOptionalFieldsResultSetToList(guestId, fields);
        } finally {
            session.close();
        }
    }

    /**
     * Get label id by event id and label.
     *
     * @param eventId The event id
     * @param label The label (for example: firstname, lastname, email etc), not the content for this label
     *
     * @return The label id
     */
    @GET
    @Path("/field/{eventId}")
    public Integer getLabelIdfromEventAndLabel(@PathParam("eventId") int eventId, @QueryParam("label") String label) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OptionalField.findByEventIdAndLabel");
            query.setInteger("eventId", eventId);
            query.setString("label", label);

            return (Integer) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Save the field content.
     *
     * @param guestId Guest id
     * @param fieldId Field id
     * @param fieldContent Field content
     *
     * @return TODO
     */
    @POST
    @Path("/field/save")
    public Delegation saveOptionalField(
            @QueryParam("guestId") Integer guestId,
            @QueryParam("fieldId") Integer fieldId,
            @QueryParam("fieldContent") String fieldContent) {
        final Session session = openSession();
        try {
            final Delegation delegation = new Delegation();
        	delegation.setPk(new DelegationPK(guestId,fieldId));
        	delegation.setValue(fieldContent);

        	session.saveOrUpdate(delegation);
        	session.flush();

        	return delegation;

        } finally {
            session.close();
        }
    }

    private List<OptionalFieldValue> convertOptionalFieldsResultSetToList(int guestId, List<OptionalField> fields) {
        final List<OptionalFieldValue> fieldsList = new ArrayList<>(fields.size());
        final Session session = openSession();
        for(OptionalField field : fields){
            final Query query = session.getNamedQuery(Delegation.QUERY_FIND_BY_GUEST);
            query.setInteger(Delegation.PARAM_GUEST_ID, guestId);
            query.setInteger(Delegation.PARAM_FIELD_ID, field.getPk());

            final Delegation delegation = (Delegation)query.uniqueResult();
            final OptionalFieldValue newValue = new OptionalFieldValue(field,
                    delegation == null ? null : delegation.getValue());

            fieldsList.add(newValue);
        }

        return fieldsList;
    }
}
