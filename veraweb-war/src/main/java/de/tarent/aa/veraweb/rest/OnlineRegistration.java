package de.tarent.aa.veraweb.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by mley on 24.07.14.
 */
@Path("/onlinereg")
public class OnlineRegistration {

    private static final String GET_EVENT_TEMPLATE = "select tevent.*, tlocation.locationname from tevent left outer join tlocation on tevent.fk_location = tlocation.pk";

    /**
     * Get a list of all events.
     * @return JSON array with all events and their location
     * @throws SQLException
     */
    @GET
    @Path("/event/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEvents() throws SQLException {

        ResultSet resultSet = null;
        try {
            Connection c = DBUtils.getConnection();

            Statement statement = c.createStatement();
            statement.execute(GET_EVENT_TEMPLATE);
            resultSet = statement.getResultSet();

            String json = DBUtils.resultSetToJson(resultSet);
            return json;
        } finally {
            DBUtils.close(resultSet);
        }
    }

    @GET
    @Path("/event/{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEvent(@PathParam("eventId") int eventId) throws SQLException {

        ResultSet resultSet = null;
        try {
            Connection c = DBUtils.getConnection();
            String sql = GET_EVENT_TEMPLATE+ " where tevent.pk = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, eventId);
            resultSet = ps.executeQuery();

            if(resultSet.next()) {
                return DBUtils.singleResultToJSON(resultSet, DBUtils.getColumnNames(resultSet), null);
            }
            throw new VeraWebRestException("no data found");
        } finally {
            DBUtils.close(resultSet);
        }


    }

    @POST
    @Path("/event/{eventId}/register")
    public boolean registerForEvent(@PathParam("eventId") int eventId, @QueryParam("acceptance") String acceptance, @QueryParam("notesToHost") String notesToHost) throws SQLException {
        Statement statement = null;
        try {
            Connection c = DBUtils.getConnection();

            statement = c.createStatement();
            //return statement.execute("select * from tevent left outer join tlocation on tevent.fk_location = tlocation.pk;");
            return true;
        }finally {
            DBUtils.close(statement);
        }
    }

}
