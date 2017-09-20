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
import org.evolvis.veraweb.onlinereg.entities.MailTemplate;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/mailtemplate")
@Produces(MediaType.APPLICATION_JSON)
public class MailTemplateResource extends AbstractResource {

    @GET
    @Path("/")
    public Response getMailTemplate(@QueryParam("templateId") Integer templateId, @QueryParam("mandantId") Integer mandantId) {
        if(templateId == null || mandantId == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(MailTemplate.GET_MAILTEMPLATE_BY_ID);
            query.setInteger("templateId", templateId);
            query.setInteger("mandantId", mandantId);
            final MailTemplate template = (MailTemplate) query.uniqueResult();
            if (template != null) {
                return Response.ok(template).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } finally {
            session.close();
        }
    }
}
