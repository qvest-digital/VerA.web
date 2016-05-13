package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.export.CsvExporter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by mweier on 23.03.16.
 */
@Path("/export")
@Produces(MediaType.TEXT_PLAIN)
public class ExportResource extends AbstractResource{

    @GET
    @Path("/guestList/{eventId}")
    public DataSource getGuestList() throws NamingException {

        InitialContext initContext = new InitialContext();
        Context envContext  = (Context)initContext.lookup("java:comp/env");
        DataSource dataSource = (DataSource)envContext.lookup("jdbc/vwonlinereg");

//        CsvExporter csvExporter = new CsvExporter(writer, dataSource);

        return dataSource;
    }
}
