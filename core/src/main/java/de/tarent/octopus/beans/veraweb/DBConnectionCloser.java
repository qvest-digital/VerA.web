package de.tarent.octopus.beans.veraweb;
import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.Closeable;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.SQLException;

@Log4j2
public class DBConnectionCloser implements Closeable {
    private Connection con;

    protected DBConnectionCloser(Connection con) {
        this.con = con;
    }

    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                DB.close(con);
            }
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }
}
