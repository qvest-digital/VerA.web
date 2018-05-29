package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContentFacade;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.query.Query;
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
        session.beginTransaction();
        try {
            final Query query = session.getNamedQuery("Delegation.deleteOptionalFieldsByGuestId");
            query.setParameter("guestId", guestId);
            query.setParameter("fieldId", fieldId);
            query.executeUpdate();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    /**
     * Get label id by event id and label.
     *
     * @param eventId The event id
     * @param label   The label (for example: firstname, lastname, email etc), not
     *                the content for this label
     * @return The label id
     */
    @GET
    @Path("/field/{eventId}")
    public Integer getLabelIdfromEventAndLabel(@PathParam("eventId") int eventId, @QueryParam("label") String label) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL);
            query.setParameter("eventId", eventId);
            query.setParameter("label", label);

            return (Integer) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Save the field content.
     *
     * @param guestId      Guest id
     * @param fieldId      Field id
     * @param fieldContent Field content
     * @return {@link Delegation}
     */
    @POST
    @Path("/field/save")
    public Delegation saveOptionalField(@FormParam("guestId") Integer guestId, @FormParam("fieldId") Integer fieldId,
      @FormParam("fieldContent") String fieldContent) {
        final Session session = openSession();
        session.beginTransaction();
        try {
            final Delegation delegation = new Delegation();
            delegation.setFk_guest(guestId);
            delegation.setFk_delegation_field(fieldId);
            delegation.setValue(fieldContent);

            session.saveOrUpdate(delegation);
            session.flush();
            session.getTransaction().commit();

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

    private void markOptionsAsSelected(final List<Delegation> delegationContents,
      final List<OptionalFieldTypeContentFacade> typeContentsFacade) {
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
        getTypeContentsQuery.setParameter("optionalFieldId", field.getPk());
        return (List<OptionalFieldTypeContent>) getTypeContentsQuery.list();
    }

    private List<Delegation> getDelegationContentsByGuest(int guestId, Session session, OptionalField field) {
        final Query query = session.getNamedQuery(Delegation.QUERY_FIND_BY_GUEST);
        query.setParameter(Delegation.PARAM_GUEST_ID, guestId);
        query.setParameter(Delegation.PARAM_FIELD_ID, field.getPk());

        return (List<Delegation>) query.list();
    }
}
