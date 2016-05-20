package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.export.CsvExporter;
import org.evolvis.veraweb.onlinereg.utils.VworConstants;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by mweier on 23.03.16.
 */
@Path("/export")
@Produces(VworConstants.TEXT_CSV_CONTENT_TYPE)
public class ExportResource extends AbstractResource{

    private final String downloadFilename = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_export.csv";
    private InitialContext initContext;

    @GET
    @Path("/guestList/{eventId}")
    public Response getGuestList(@PathParam("eventId") final int eventId) throws NamingException, UnsupportedEncodingException {

        if (initContext == null) {
            initContext = new InitialContext();
        }
        final Context namingContext  = (Context) initContext.lookup("java:comp/env");
        final DataSource dataSource = (DataSource) namingContext.lookup("jdbc/vwonlinereg");

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                CsvExporter csvExporter = new CsvExporter(writer, dataSource, eventId);
                csvExporter.export();

                writer.flush();
            }
        };

        return Response.ok(stream).header("Content-Disposition", "attachment;filename=" + downloadFilename + ";charset=Unicode").build();
    }

}
