/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess.backend.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.StoreProcessor;
import de.tarent.commons.dataaccess.backend.AbstractDataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.QueryingWithQueryBuilder;
import de.tarent.commons.dataaccess.backend.StoringAttributeSets;
import de.tarent.commons.dataaccess.backend.StoringStrategy;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.query.QueryBuilder;
import de.tarent.commons.dataaccess.query.impl.SqlQueryBuilder;

public class SqlDataAccessBackend extends AbstractDataAccessBackend implements QueryingWithQueryBuilder, StoringAttributeSets {
	private Connection connection;

	private String driverClass;
	private String jdbcURL;
	private String username;
	private String password;
	
	private Class requestedType;

	public void init() {
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public QueryingStrategy getQueryingStrategy() {
		return this;
	}

	public StoringStrategy getStoringStrategy() {
		return this;
	}

	public QueryBuilder getQueryBuilder() {
		return new SqlQueryBuilder();
	}

	public Object executeQuery(QueryProcessor queryProcessor, Object query) {
		if (!(query instanceof String))
			throw new IllegalArgumentException("Only pur strings are (currently) allowed for storing, please use pure SQL.");
		
		String sql = (String) query;
		
		Statement s = null;
		try {
			ensureOpenConnection();
			s = connection.createStatement();
			
			List result = new LinkedList();
			ResultSet resultSet = s.executeQuery(sql);
			ResultSetMetaData metaData = resultSet.getMetaData();
			
			while (resultSet.next()) {
				Map line = new LinkedHashMap();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					line.put(metaData.getColumnName(i), resultSet.getObject(i));
				}
				result.add(line);
			}
			
			return result;
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			close(s);
		}
	}

	/** {@inheritDoc} */
	public void store(StoreProcessor storeProcessor, Object o) {
		if (!(o instanceof String))
			throw new IllegalArgumentException("Only pur strings are (currently) allowed for storing, please use pure SQL.");
		
		Statement s = null;
		try {
			ensureOpenConnection();
			s = connection.createStatement();
			s.executeUpdate((String) o);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			close(s);
		}
	}

	public void store(StoreProcessor storeProcessor, AttributeSet attributeSet) {
		throw new DataAccessException("LdapDataAccessBackend currently do not support storing data.");
	}
	
	public void delete(StoreProcessor storeProcessor, AttributeSet o) {
		throw new DataAccessException("LdapDataAccessBackend currently do not support deleting data.");
	}

	/** {@inheritDoc} */
	public void begin() {
		try {
			ensureOpenConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	public void commit() {
		try {
			ensureOpenConnection();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	public void rollback() {
		try {
			ensureOpenConnection();
			connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void ensureOpenConnection() {
		if (connection == null) {
			try {
				if (driverClass != null) {
					DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
				}
			} catch (Exception e) {
				connection = null;
				throw new DataAccessException(e);
			}
			try {
				connection = DriverManager.getConnection(jdbcURL, username, password);
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				connection = null;
				throw new DataAccessException(e);
			}
		}
	}

	protected void finalize() throws Throwable {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}
	}

	private void close(Statement statement) {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
	public Class getRequestedType() {
		return requestedType;
	}

	public void setRequestedType(Class requestedType) {
		this.requestedType = requestedType;
	}
}
