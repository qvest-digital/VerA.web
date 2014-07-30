package de.tarent.aa.veraweb.rest;

import junit.framework.TestCase;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.mockito.Matchers;


/**
 * Created by mley on 29.07.14.
 */
public class OnlineRegistrationTest extends TestCase {

    public void testStringify() {
        assertEquals("null", DBUtils.stringify(null));
        assertEquals("\"test\"", DBUtils.stringify("test"));
        assertEquals("\"test mit \\\"Anführungszeichen\\\"\"", DBUtils.stringify("test mit \"Anführungszeichen\""));
    }

    public void testResultSetToJson() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsmd = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(rs.getMetaData()).thenReturn(rsmd);

        Mockito.when(rsmd.getColumnCount()).thenReturn(2);
        Mockito.when(rsmd.getColumnLabel(Matchers.eq(1))).thenReturn("eins");
        Mockito.when(rsmd.getColumnLabel(Matchers.eq(2))).thenReturn("zwei");

        Mockito.when(rs.next()).thenReturn(true, true, false);
        Mockito.when(rs.getString(Matchers.eq("eins"))).thenReturn(null, "foo");
        Mockito.when(rs.getString(Matchers.eq("zwei"))).thenReturn("wert \"zwei\"", "bar");

        String expected = "[\n  {\"eins\":null,\"zwei\":\"wert \\\"zwei\\\"\"},\n  {\"eins\":\"foo\",\"zwei\":\"bar\"}\n]";
        String actual = DBUtils.resultSetToJson(rs);

        assertEquals(expected, actual);

    }

}
