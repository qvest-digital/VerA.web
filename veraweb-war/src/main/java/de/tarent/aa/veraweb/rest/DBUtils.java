package de.tarent.aa.veraweb.rest;

import de.tarent.dblayer.engine.DBContextImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by mley on 29.07.14.
 */
public class DBUtils {

    /**
     * Safely close all AutoClosables
     *
     * @param closeable AutoClosables
     */
    public static void close(AutoCloseable... closeable) {
        if (closeable == null) {
            return;
        }

        for (AutoCloseable ac : closeable) {
            if (ac != null) {
                try {
                    ac.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Get a JDBC connection from the veraweb Pool
     *
     * @return Connection object
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        DBContextImpl dbc = new DBContextImpl();
        dbc.setPoolName("veraweb");
        return dbc.getDefaultConnection();
    }

    /**
     * Converts the data of an SQL result set to a JSON string
     *
     * @param resultSet ResultSet
     * @return JSON string
     * @throws SQLException
     */
    public static String resultSetToJson(ResultSet resultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        String[] columnNames = getColumnNames(resultSet);

        while (resultSet.next()) {
            if(sb.length() > 1) {
                sb.append(",");
            }
            sb.append("\n  ");

            singleResultToJSON(resultSet, columnNames, sb);
        }
        sb.append("\n]");

        return sb.toString();
    }

    /**
     * Returns an array of Strings with the column names
     * @param resultSet ResultSet
     * @return array of Strings
     */
    public static String[] getColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // get column names
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = metaData.getColumnLabel(i + 1);
        }

        return columnNames;
    }

    /**
     * Convert one result to a JSON string
     *
     * @param resultSet     ResultSet
     * @param columnNames   array of column names
     * @param stringBuilder optional StringBuilder. If non-null this Stringbuilder will be appended to and method
     *                      returns null. If this parameter is null, a new StringBuilder is created and the return value
     *                      is the resulting string.
     * @return JSON string or null
     */
    public static String singleResultToJSON(ResultSet resultSet, String[] columnNames, StringBuilder stringBuilder) throws SQLException {
        StringBuilder sb;
        if (stringBuilder != null) {
            sb = stringBuilder;
        } else {
            sb = new StringBuilder();
        }

        sb.append("{");

        boolean first = true;

        for (String name : columnNames) {

            if(first) {
                first = false;
            } else {
                sb.append(",");
            }


            sb.append(stringify(name)).append(":");
            sb.append(stringify(resultSet.getString(name)));
        }

        sb.append("}");

        if (stringBuilder != null) {
            return null;
        } else {
            return sb.toString();
        }
    }

    /**
     * converts a String to a JSON string
     *
     * @param in input string
     * @return JSON string in quotes. Quotes in the input are escaped.
     */
    public static String stringify(String in) {
        if (in == null) {
            return "null";
        }

        String out = in.replaceAll("\"", "\\\\\"");

        return "\"" + out + "\"";
    }


}
