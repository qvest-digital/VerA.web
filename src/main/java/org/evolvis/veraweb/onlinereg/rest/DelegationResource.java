/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
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

    @javax.ws.rs.core.Context
    ResourceContext resourceContext;

    /**
     * Get optional fields content for a guest.
     *
     * @param eventId Event id
     * @param guestId Guest id
     * @return Fields content
     */
    @GET
    @Path("/fields/list/{eventId}/{guestId}")
    public List<OptionalFieldValue> getFieldsFromEvent(@PathParam("eventId") int eventId, @PathParam("guestId") int guestId) {

        final Session session = openSession();
        try {
            final OptionalFieldResource optionalFieldResource = resourceContext.getResource(OptionalFieldResource.class);
            final List<OptionalField> fields = optionalFieldResource.getOptionalFields(eventId);

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
     * @param eventId
     *            The event id
     * @param label
     *            The label (for example: firstname, lastname, email etc), not
     *            the content for this label
     *
     * @return The label id
     */
    @GET
    @Path("/field/{eventId}")
    public Integer getLabelIdfromEventAndLabel(@PathParam("eventId") int eventId, @QueryParam("label") String label) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL);
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
     * @param guestId
     *            Guest id
     * @param fieldId
     *            Field id
     * @param fieldContent
     *            Field content
     *
     * @return {@link Delegation}
     */
    @POST
    @Path("/field/save")
    public Delegation saveOptionalField(@FormParam("guestId") Integer guestId, @FormParam("fieldId") Integer fieldId,
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

    public void setResourceContext(ResourceContext resourceContext) {
        this.resourceContext = resourceContext;
    }

    private List<OptionalFieldValue> convertOptionalFieldsResultSetToList(final Integer guestId, final List<OptionalField> fields,
            final Session session) {

        final List<OptionalFieldValue> fieldsList = new ArrayList<OptionalFieldValue>(fields.size());
        for (OptionalField field : fields) {
            final List<Delegation> delegationContents = getDelegationContentsByGuest(guestId, session, field);

            if (field.getFk_type() != null) {
                if (!delegationContents.isEmpty() || field.getFk_type() != 1) {
                    final OptionalFieldValue newOptionalFieldValue = initOptionalField(session, field, delegationContents);
                    fieldsList.add(newOptionalFieldValue);
                } else {
                    final OptionalFieldValue newOptionalFieldValue = new OptionalFieldValue(field, null);
                    fieldsList.add(newOptionalFieldValue);
                }
            }
        }
        return fieldsList;
    }

    private OptionalFieldValue initOptionalField(Session session, OptionalField field, List<Delegation> delegationContents) {
        final List<OptionalFieldTypeContentFacade> typeContentsFacade = convertTypeContentsToDisplay(session, field);
        OptionalFieldValue optionalFieldValue = null;
        if (field.getFk_type() == 1) {
            optionalFieldValue = initInputField(field, delegationContents);
        } else {
            optionalFieldValue = initDropdown(field, delegationContents, typeContentsFacade);
        }
        return optionalFieldValue;
    }

    private OptionalFieldValue initDropdown(OptionalField field, List<Delegation> delegationContents,
            List<OptionalFieldTypeContentFacade> typeContentsFacade) {
        OptionalFieldValue newOptionalFieldValue = new OptionalFieldValue(field, null);
        markOptionsAsSelected(delegationContents, typeContentsFacade);
        newOptionalFieldValue.setOptionalFieldTypeContentsFacade(typeContentsFacade);

        return newOptionalFieldValue;
    }

    private OptionalFieldValue initInputField(OptionalField field, List<Delegation> delegationContents) {
        OptionalFieldValue newOptionalFieldValue = new OptionalFieldValue(field, null);
        newOptionalFieldValue.setValue(delegationContents.get(0).getValue());
        return newOptionalFieldValue;
    }

    private void markOptionsAsSelected(final List<Delegation> delegationContents, final List<OptionalFieldTypeContentFacade> typeContentsFacade) {
        for (Delegation delegationContent : delegationContents) {
            for (int i = 0; i < typeContentsFacade.size(); i++) {
                if (delegationContent.getValue().equals(typeContentsFacade.get(i).getContent())) {
                    typeContentsFacade.get(i).setIsSelected(true);
                }
            }
        }
    }

    private List<OptionalFieldTypeContentFacade> convertTypeContentsToDisplay(final Session session, final OptionalField field) {
        final List<OptionalFieldTypeContent> typeContents = getTypeContentsByField(session, field);
        final List<OptionalFieldTypeContentFacade> typeContentsFacade = new ArrayList<OptionalFieldTypeContentFacade>();
        for (OptionalFieldTypeContent typeContent : typeContents) {
            final OptionalFieldTypeContentFacade typeContentFacade = new OptionalFieldTypeContentFacade(typeContent);
            typeContentsFacade.add(typeContentFacade);
        }
        return typeContentsFacade;
    }

    private List<OptionalFieldTypeContent> getTypeContentsByField(final Session session, final OptionalField field) {
        final Query getTypeContentsQuery = session.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField");
        getTypeContentsQuery.setInteger("optionalFieldId", field.getPk());
        return (List<OptionalFieldTypeContent>) getTypeContentsQuery.list();
    }

    private List<Delegation> getDelegationContentsByGuest(int guestId, Session session, OptionalField field) {
        final Query query = session.getNamedQuery(Delegation.QUERY_FIND_BY_GUEST);
        query.setInteger(Delegation.PARAM_GUEST_ID, guestId);
        query.setInteger(Delegation.PARAM_FIELD_ID, field.getPk());

        return (List<Delegation>) query.list();
    }
}
