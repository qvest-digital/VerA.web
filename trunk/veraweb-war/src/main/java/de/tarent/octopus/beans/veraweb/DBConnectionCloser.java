package de.tarent.octopus.beans.veraweb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.Closeable;

public class DBConnectionCloser implements Closeable {
	private static final Logger logger = Logger.getLogger(DBConnectionCloser.class.getName());
	private Connection con;
	
	protected DBConnectionCloser(Connection con){
		this.con = con;
	}
	
	public void close() {
		try {
			if(con!=null&&!con.isClosed()){
				DB.close(con);
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}
}
