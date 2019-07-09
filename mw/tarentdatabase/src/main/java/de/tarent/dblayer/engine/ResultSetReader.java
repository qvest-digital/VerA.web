package de.tarent.dblayer.engine;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author kleinw
 *
 * Hier wird ein ResultSet ausgelesen
 * und in List oder Mapobjekte umgewandelt.
 *
 * HINWEIS : Date-Objekte werden hier nach Long
 * gewandelt, da sich dieser Datentyp für den
 * SOAPTransfer besser eignet.
 */
public class ResultSetReader {

    final static public List list(Collection listColumns, Result result) throws SQLException {
        List resultList = new ArrayList();
        try {
            if (listColumns.size() == 1) {
                while (result.resultSet().next()) {
                    getRow(result, listColumns, resultList);
                }
            } else {
                int size = listColumns.size();
                while (result.resultSet().next()) {
                    List row = new ArrayList(size);
                    getRow(result, listColumns, row);
                    resultList.add(row);
                }
            }
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (result != null) {
                result.close();
            }
        }
        return resultList;
    }

    private static void getRow(Result result, Collection getColumns, List row) throws SQLException {
        for (Iterator it = getColumns.iterator(); it.hasNext(); ) {
            Object[] get = (Object[]) it.next();
            String column = (String) get[0];
            Class type = (Class) get[2];

            if (type.equals(String.class)) {
                row.add(result.resultSet().getString(column));
            } else if (type.equals(Integer.class)) {
                row.add(new Integer(result.resultSet().getInt(column)));
            } else if (type.equals(Timestamp.class)) {
                Timestamp tstmp = result.resultSet().getTimestamp(column);
                if (tstmp != null) {
                    row.add(new Long(tstmp.getTime()));
                } else {
                    row.add(null);
                }
            } else {
                row.add(result.resultSet().getObject(column));
            }
        }
    }
}
