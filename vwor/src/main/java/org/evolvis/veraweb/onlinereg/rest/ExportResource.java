package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.export.CsvExporter;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.utils.KeepOpenWriter;
import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
@Path(RestPaths.REST_EXPORT)
@Produces(VworConstants.TEXT_CSV_CONTENT_TYPE)
public class ExportResource extends AbstractResource {
    @javax.ws.rs.core.Context
    ResourceContext resourceContext;

    private InitialContext initContext;
    private static final String CONFIG_FILE_NAME = "config.jsn";
    private static final String CONFIG_PLACEHOLDER = "__event_id_placeholder__";

    private Event getEvent(int eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEvent");
            query.setParameter("pk", eventId);
            return (Event) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @POST
    @Path(RestPaths.REST_EXPORT_GET_GUESTLIST)
    public Response getGuestList(@PathParam("eventId") final int eventId,
      MultivaluedMap<String, String> params,
      @FormParam("selectedFields[]") List<String> selList)
      throws NamingException, UnsupportedEncodingException {
        final Event event = getEvent(eventId);
        final String downloadFilename = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_export.csv";
        if (initContext == null) {
            initContext = new InitialContext();
        }
        final Context namingContext = (Context) initContext.lookup("java:comp/env");
        final DataSource dataSource = (DataSource) namingContext.lookup("jdbc/vwonlinereg");

        final Properties properties = new Properties();
        properties.setProperty("event.shortname", event.getShortname());
        properties.setProperty("event.begin", String.valueOf(event.getDatebegin().getTime()));

        params.keySet().forEach(key -> properties.setProperty(key, params.getFirst(key)));

        Map<String, String> filterSettings = new HashMap<>();
        params.keySet().stream().filter(key -> key.startsWith("filter"))
          .forEach(key -> filterSettings.put(key, params.getFirst(key)));

        final InputStream configFileAsStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        final Reader reader = new InputStreamReader(configFileAsStream, "utf-8");
        final Map<String, String> substitutions = new HashMap<>();
        substitutions.put(CONFIG_PLACEHOLDER, String.valueOf(eventId));

        addOptionalFieldsSubstitutions(eventId, substitutions);

        StreamingOutput stream = os -> {
            final Writer writer = new BufferedWriter(new OutputStreamWriter(os));
            final CsvExporter csvExporter = new CsvExporter(reader, new KeepOpenWriter(writer), dataSource, properties, selList);

            csvExporter.export(substitutions, filterSettings);

            writer.flush();
        };

        return Response.ok(stream).header("Content-Disposition", "attachment;filename=" + downloadFilename + ";charset=Unicode")
          .build();
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
}
