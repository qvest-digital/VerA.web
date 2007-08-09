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

package de.tarent.commons.dataaccess.query;

import de.tarent.commons.dataaccess.query.impl.TraversingVisitor;

/**
 * QueryVisitor listener will be called when visitor handle the query tree.
 * 
 * See {@link QueryVisitor#empty()}, {@link QueryVisitor#term(Object)},
 * {@link QueryVisitor#and(Object[])}, {@link QueryVisitor#or(Object[])}
 * and {@link QueryVisitor#not(Object[])}.
 * 
 * See also {@link TraversingVisitor}.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public interface QueryVisitorListener {
	/**
	 * Will be called before all entries will be added.
	 * 
	 * @param entryCount The number of entries that will be filled.
	 */
	public void handleBeforeAll(int entryCount);

	/**
	 * Will be called before an entriy will be added.
	 * 
	 * @param first True if the next entry is the first entry.
	 * @param last True if the next entry is the last entry.
	 */
	public void handleBeforeEntry(boolean first, boolean last);

	/**
	 * Will be called after an entriy will be added.
	 * 
	 * @param first True if the last entry was the first entry.
	 * @param last True if the last entry was the last entry.
	 */
	public void handleAfterEntry(boolean first, boolean last);

	/**
	 * Will be called after all entries will be added.
	 * 
	 * @param entryCount The number of entries that will be filled.
	 */
	public void handleAfterAll(int entryCount);
}
