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
import java.util.HashMap;
import java.util.Map;


/**
 * Created by mley on 24.07.14.
 */
@Path("/onlinereg")
public class OnlineRegistration {

    private static final String GET_EVENT_TEMPLATE = "select tevent.*, tlocation.locationname from tevent left outer join tlocation on tevent.fk_location = tlocation.pk";

    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public String isAvailable() {
        try {
            DBUtils.getConnection().close();
            return "OK";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    /**
     * Get a list of all events.
     *
     * @return JSON array with all events and their location
     * @throws SQLException
     */
    @GET
    @Path("/event/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEvents() throws SQLException {
        Connection c = null;
        try {
            c = DBUtils.getConnection();

            Statement statement = c.createStatement();
            statement.execute(GET_EVENT_TEMPLATE);
            ResultSet resultSet = statement.getResultSet();

            return DBUtils.resultSetToJson(resultSet);
        } finally {
            DBUtils.close(c);
        }
    }

    /**
     * Get a single event
     *
     * @param eventId id of the event
     * @return returns event as json or throws an exception is event is not found
     * @throws SQLException
     */
    @GET
    @Path("/event/{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEvent(@PathParam("eventId") int eventId) throws SQLException {
        Connection c = null;
        try {
            c = DBUtils.getConnection();
            String sql = GET_EVENT_TEMPLATE + " where tevent.pk = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return DBUtils.singleResultToJSON(resultSet, DBUtils.getColumnNames(resultSet), null);
            }
            throw new VeraWebRestException("no data found");
        } finally {
            DBUtils.close(c);
        }


    }

    /**
     * Get the registration status of a user for an event
     *
     * @param eventId event id
     * @param userId  user id
     * @return registration status and note to host
     * @throws SQLException
     */
    @GET
    @Path("/event/{eventId}/register/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getRegistration(@PathParam("eventId") int eventId, @PathParam("userId") int userId) throws SQLException {
        Connection c = null;
        try {
            c = DBUtils.getConnection();


            PreparedStatement ps = c.prepareStatement("select invitationstatus, notehost from tguest where fk_person = ? and fk_event = ?");
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {

                Map<String, String> result = new HashMap<String, String>();
                result.put("acceptance", Integer.toString(resultSet.getInt(1)));
                result.put("noteToHost", resultSet.getString(2));

                return result;
            }
        } finally {
            DBUtils.close(c);
        }
        return null;
    }

    /**
     * Save registration status
     *
     * @param eventId    event id
     * @param userId     user id
     * @param acceptance acceptance status.
     * @param noteToHost note to host
     * @return true if changes where saved
     * @throws SQLException
     */
    @POST
    @Path("/event/{eventId}/register/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean registerForEvent(@PathParam("eventId") int eventId, @PathParam("userId") int userId, @QueryParam("acceptance") int acceptance, @QueryParam("noteToHost") String noteToHost) throws SQLException {
        Connection c = null;

        try {
            c = DBUtils.getConnection();


            PreparedStatement ps = c.prepareStatement("select 1 from tguest where fk_person = ? and fk_event = ?");
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            if (!ps.executeQuery().next()) {
                // user is not invited to this event.
                return false;
            }

            ps = c.prepareStatement("UPDATE tguest SET invitationstatus=?, notehost=? WHERE fk_person = ? and fk_event = ?");
            ps.setInt(1, acceptance);
            ps.setString(2, noteToHost);
            ps.setInt(3, userId);
            ps.setInt(4, eventId);

            int updated = ps.executeUpdate();
            c.commit();
            return updated == 1;
        } finally {
            DBUtils.close(c);
        }
    }

}
