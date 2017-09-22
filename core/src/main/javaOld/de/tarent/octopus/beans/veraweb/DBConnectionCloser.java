package de.tarent.octopus.beans.veraweb;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.Closeable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
