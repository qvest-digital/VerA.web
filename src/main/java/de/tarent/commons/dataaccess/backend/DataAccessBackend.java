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

package de.tarent.commons.dataaccess.backend;

import de.tarent.commons.dataaccess.TransactionSupport;

/**
 * <p>
 * The DataAccessBackend combines the interfaces
 * {@link QueryBackend}, {@link StorageBackend} and {@link TransactionBackend}.
 * These defines data access methods for loading, saving and deleting of
 * objects. Also transactions will be supported.
 * </p>
 * <p>
 * Implementations should query a RDBMS with SQL, an spreadsheat,
 * an ODBMS, an index like lucene, different webservices, or anything else.
 * For example guess the the object content for testing an application or
 * develop it agile.
 * </p>
 * <p>
 * The build in mechanism will save all the content in an in-memory-object
 * cache, an SQL database (not yet) or in the tarent databroker (soon).
 * </p>
 * <p>
 * See {@link QueryBackend}, {@link StorageBackend} and {@link TransactionBackend}
 * for more information.
 * </p>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public interface DataAccessBackend extends TransactionSupport {
	public void init();
	
	public QueryingStrategy getQueryingStrategy();

	public StoringStrategy getStoringStrategy();
}
