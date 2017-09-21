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
import org.evolvis.veraweb.export.CsvExporter;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.utils.KeepOpenWriter;
import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Created by mweier on 23.03.16.
 */
@Path("/export")
@Produces(VworConstants.TEXT_CSV_CONTENT_TYPE)
public class ExportResource extends AbstractResource{

    @javax.ws.rs.core.Context
    ResourceContext resourceContext;

    private InitialContext initContext;
    private static final String CONFIG_FILE_NAME = "config.yaml";
    private static final String CONFIG_FILE_NAME_GUEST_LIST_SHORT = "configGuestListShort.yaml";
    private static final String CONFIG_PLACEHOLDER = "__event_id_placeholder__";
    
    
    private Event getEvent(int eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEvent");
            query.setInteger("pk", eventId);
            return (Event) query.uniqueResult();
        } finally {
            session.close();
        }

    }
    
    @GET
    @Path("/guestList/{eventId}")
    public Response getGuestList(@PathParam("eventId") final int eventId, @javax.ws.rs.core.Context UriInfo ui) throws NamingException, UnsupportedEncodingException {
        final Event event = getEvent(eventId);
        final String downloadFilename = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_export.csv";
        if (initContext == null) {
            initContext = new InitialContext();
        }
        final Context namingContext  = (Context) initContext.lookup("java:comp/env");
        final DataSource dataSource = (DataSource) namingContext.lookup("jdbc/vwonlinereg");

        final Properties properties = new Properties();
        properties.setProperty("event.shortname", event.getShortname());
        properties.setProperty("event.begin",  String.valueOf(event.getDatebegin().getTime()));

        final InputStream configFileAsStream = getConfigFileAsStream(ui);
        final Reader reader = new InputStreamReader(configFileAsStream, "utf-8");
        final Map<String, String> substitutions=new HashMap<String,String>();
        substitutions.put(CONFIG_PLACEHOLDER, String.valueOf(eventId));

        addOptionalFieldsSubstitutions(eventId, substitutions);

        final MultivaluedMap<String, String> params = ui.getQueryParameters();
        for(String key:params.keySet()){
            properties.setProperty(key, params.getFirst(key));
        }

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                final Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                final CsvExporter csvExporter = new CsvExporter(reader,new KeepOpenWriter(writer), dataSource, properties);
                
                csvExporter.export(substitutions);

                writer.flush();
            }
        };


        return Response.ok(stream).header("Content-Disposition", "attachment;filename=" + downloadFilename + ";charset=Unicode").build();
    }

    private void addOptionalFieldsSubstitutions(@PathParam("eventId") int eventId, Map<String, String> substitutions) {
        final OptionalFieldResource optionalFieldResource = resourceContext.getResource(OptionalFieldResource.class);
        final List<OptionalField> optionalFields = optionalFieldResource.getOptionalFields(eventId);

        for (int i = 0; i < optionalFields.size(); i++) {
            final OptionalField currentField = optionalFields.get(i);
            if (i < 10) {
                substitutions.put("__OPTIONAL_FIELD_LABEL_0" + i + "__", currentField.getLabel());
                substitutions.put("__optional_field_0" + i + "_id_placeholder__", currentField.getPk().toString());
            } else {
                substitutions.put("__OPTIONAL_FIELD_LABEL_" + i + "__", currentField.getLabel());
                substitutions.put("__optional_field_" + i + "_id_placeholder__", currentField.getPk().toString());
            }
        }
    }

    private InputStream getConfigFileAsStream(@javax.ws.rs.core.Context UriInfo ui) {
        final List<String> guestListShortExportQueryParameter = ui.getQueryParameters().get("guestListShortExport");
        if (guestListShortExportQueryParameter != null && guestListShortExportQueryParameter.get(0).equals("true")) {
            return getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME_GUEST_LIST_SHORT);
        }
        return getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
    }


}
