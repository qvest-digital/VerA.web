package org.evolvis.veraweb.onlinereg.rest;

/*-
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
import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/press/activation")
@Produces(MediaType.APPLICATION_JSON)
public class MediaRepresentativeActivationResource extends AbstractResource {

    @POST
    @Path("/create")
    public MediaRepresentativeActivation addMediaRepresentativeActivationEntry(
        @FormParam("activationToken") String activationToken,
        @FormParam("email") String email,
        @FormParam("eventId") Integer eventId,
        @FormParam("gender") String gender,
        @FormParam("address") String address,
        @FormParam("city") String city,
        @FormParam("country") String country,
        @FormParam("firstname") String firstname,
        @FormParam("lastname") String lastname,
        @FormParam("zip") Integer zip
    ) {

        final Session session = openSession();
        try {

            final MediaRepresentativeActivation mediaRepresentativeActivation = initMediaRepresentativeActivation(
                    activationToken, email, eventId, gender, address, city, country, firstname, lastname, zip
            );
            session.persist(mediaRepresentativeActivation);
            session.flush();
            return mediaRepresentativeActivation;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/exists/{encodedAddress}/{eventId}")
    public Boolean existEventIdByDelegation(@PathParam("encodedAddress") String encodedAddress, @PathParam("eventId") String eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId");
            query.setInteger("fk_event", Integer.parseInt(eventId));
            query.setString("email", encodedAddress);
            final BigInteger activations = (BigInteger) query.uniqueResult();
            if (activations != null && activations.intValue() >= 1) {
                return true;
            }
            return false;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/exists/{mediaRepresentativeActivationToken}")
    public MediaRepresentativeActivation getMediaRepresentativeActivationByToken(
            @PathParam("mediaRepresentativeActivationToken") String mediaRepresentativeActivationToken) {

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.getActivationByActivationToken");
            query.setString("activation_token", mediaRepresentativeActivationToken);
            return (MediaRepresentativeActivation) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @POST
    @Path("/update")
    public void activatePressUser(@FormParam("email") String email, @FormParam("eventId") Integer eventId) {

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.activate");
            query.setString("email", email);
            query.setInteger("fk_event", eventId);
            query.executeUpdate();
        } finally {
            session.close();
        }
    }

    private MediaRepresentativeActivation initMediaRepresentativeActivation(
            String activationToken,
            String email,
            Integer eventId,
            String gender,
            String address,
            String city,
            String country,
            String firstname,
            String lastname,
            Integer zip) {
        final MediaRepresentativeActivation mediaRepresentativeActivation = new MediaRepresentativeActivation();
        mediaRepresentativeActivation.setActivation_token(activationToken);
        mediaRepresentativeActivation.setEmail(email);
        mediaRepresentativeActivation.setFk_event(eventId);
        mediaRepresentativeActivation.setGender(gender);
        mediaRepresentativeActivation.setAddress(address);
        mediaRepresentativeActivation.setCity(city);
        mediaRepresentativeActivation.setCountry(country);
        mediaRepresentativeActivation.setFirstname(firstname);
        mediaRepresentativeActivation.setLastname(lastname);
        mediaRepresentativeActivation.setZip(zip);
        mediaRepresentativeActivation.setActivated(0);
        
        return mediaRepresentativeActivation;
    }
}
