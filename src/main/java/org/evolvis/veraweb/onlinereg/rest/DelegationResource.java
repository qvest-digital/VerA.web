/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContentFacade;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metamodel.relational.Database;
import org.hibernate.sql.Select;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Iterator;
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

            return convertOptionalFieldsResultSetToList(guestId, fields, session);
        } finally {
            session.close();
        }
    }

    @POST
    @Path("/remove/fields")
    public void removeFieldsForGuest(@FormParam("guestId") Integer guestId, @FormParam("fieldId") Integer fieldId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Delegation.deleteOptionalFieldsByGuestId");
            query.setInteger("guestId", guestId);
            query.setInteger("fieldId", fieldId);
            query.executeUpdate();
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
            @FormParam("guestId") Integer guestId,
            @FormParam("fieldId") Integer fieldId,
            @FormParam("fieldContent") String fieldContent) {
        final Session session = openSession();
        try {
            final Delegation delegation = new Delegation();
            delegation.setFk_guest(guestId);
            delegation.setFk_delegation_field(fieldId);
            delegation.setValue(fieldContent);

            session.saveOrUpdate(delegation);
            session.flush();

            return delegation;

        } finally {
            session.close();
        }
    }

    private List<OptionalFieldValue> convertOptionalFieldsResultSetToList(
            int guestId,
            List<OptionalField> fields,
            Session session) {
        // TODO Refactor
        // wenn type 1 -> 1 objekt mit einer Value (in der Maske Value anzeigen)
        // type 2 -> 1 Objekt mit einer Value (in der Maske isSelected [1 max] anzeigen als ausgewählt und Liste anzeigen als auswählbare Werte )
        // type 3 -> 1 Objekt mit einer Value (in der Maske isSelected anzeigen als ausgewählte und Liste anzeigen als auswählbare Werte )

        final List<OptionalFieldValue> fieldsList = new ArrayList<OptionalFieldValue>(fields.size());
        for (OptionalField field : fields) {
            OptionalFieldValue newValue;

            final Query query = session.getNamedQuery(Delegation.QUERY_FIND_BY_GUEST);
            query.setInteger(Delegation.PARAM_GUEST_ID, guestId);
            query.setInteger(Delegation.PARAM_FIELD_ID, field.getPk());

            // ausgewählte
            final List<Delegation> delegationList = (List<Delegation>) query.list();

            // Keine Ausgewählte
            if (delegationList.isEmpty()) {
                newValue = new OptionalFieldValue(field, null);
            } else {
                newValue = new OptionalFieldValue(field, null);
                final Query query2 = session.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField");
                query2.setInteger("optionalFieldId", field.getPk());

                // auswählbare
                final List<OptionalFieldTypeContent> typeContents = (List<OptionalFieldTypeContent>) query2.list();
                final List<OptionalFieldTypeContentFacade> typeContentsFacade = new ArrayList<OptionalFieldTypeContentFacade>();
                for (Iterator<OptionalFieldTypeContent> iterator = typeContents.iterator(); iterator.hasNext(); ) {
                    OptionalFieldTypeContentFacade oftcf = new OptionalFieldTypeContentFacade(iterator.next());
                    typeContentsFacade.add(oftcf);
                }

                if (field.getFk_type() == 1) {
                    newValue = new OptionalFieldValue(field, delegationList.get(0).getValue());
                } else {


                    for (Delegation delegation : delegationList) {
                        String insertedValue = delegation.getValue();
                        for (int i = 0; i < typeContentsFacade.size(); i++) {
                            if (insertedValue.equals(typeContentsFacade.get(i).getContent())) {
                                typeContentsFacade.get(i).setIsSelected(true);
                            }
                        }
                    }
                    newValue.setOptionalFieldTypeContentsFacade(typeContentsFacade);
                    ;

                }
            }
            fieldsList.add(newValue);
        }
        return fieldsList;
    }
}
