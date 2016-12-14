package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.export.CsvExporter;
import org.evolvis.veraweb.onlinereg.entities.Event;
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

        Map labels = new HashMap();
        labels.put("1", "hotel");
        labels.put("2", "auto benötigt");
        labels.put("3", "essen");
        labels.put("4", "04");
        labels.put("5", "05");
        labels.put("6", "06");
        labels.put("7", "07");
        labels.put("8", "08");
        labels.put("9", "09");
        labels.put("10", "10");
        labels.put("11", "11");
        labels.put("12", "12");
        labels.put("13", "13");
        labels.put("14", "14");
        labels.put("15", "15");

        final Properties properties = new Properties();
        properties.setProperty("event.shortname", event.getShortname());
        properties.setProperty("event.begin",  String.valueOf(event.getDatebegin().getTime()));

        final InputStream configFileAsStream = getConfigFileAsStream(ui);
        final Reader reader = new InputStreamReader(configFileAsStream, "utf-8");
        final Map<String, String> substitutions=new HashMap<String,String>();
        substitutions.put(CONFIG_PLACEHOLDER, String.valueOf(eventId));
        int counter = 1;
        for (Object key : labels.entrySet()) {
            if (counter < 10) {
                substitutions.put("__LABEL_OPTIONAL_FIELD_0" + counter + "__", key.toString());
            } else {
                substitutions.put("__LABEL_OPTIONAL_FIELD_" + counter + "__", key.toString());
            }
        }
        final MultivaluedMap<String, String> params = ui.getQueryParameters();
        for(String key:params.keySet()){
            properties.setProperty(key, params.getFirst(key));
        }

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                final Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                final CsvExporter csvExporter = new CsvExporter(reader,new KeepOpenWriter(writer), dataSource, properties);
                
                csvExporter.export(substitutions);

                writer.flush();
            }
        };


        return Response.ok(stream).header("Content-Disposition", "attachment;filename=" + downloadFilename + ";charset=Unicode").build();
    }

    private InputStream getConfigFileAsStream(@javax.ws.rs.core.Context UriInfo ui) {
        final List<String> guestListShortExportQueryParameter = ui.getQueryParameters().get("guestListShortExport");
        if (guestListShortExportQueryParameter != null && guestListShortExportQueryParameter.equals("true")) {
            return getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME_GUEST_LIST_SHORT);
        }
        return getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
    }


}
