package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.export.CsvExporter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.*;


/**
 * Created by mweier on 23.03.16.
 */
@Path("/export")
@Produces("text/csv")
public class ExportResource extends AbstractResource{

    @GET
    @Path("/guestList/{eventId}")
    public Response getGuestList(@PathParam("eventId") final int eventId) throws NamingException, UnsupportedEncodingException {

        final InitialContext initContext = new InitialContext();
        final Context envContext  = (Context)initContext.lookup("java:comp/env");
        final DataSource dataSource = (DataSource)envContext.lookup("jdbc/vwonlinereg");

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                CsvExporter csvExporter = new CsvExporter(writer, dataSource, eventId);
                csvExporter.export();

                writer.flush();
            }
        };

        return Response.ok(stream).build();
    }


}
