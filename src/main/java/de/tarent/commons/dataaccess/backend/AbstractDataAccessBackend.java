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

public abstract class AbstractDataAccessBackend implements DataAccessBackend {
	/**
	 * The order instance.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	private Object order;

	/**
	 * The first entry index.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	private Integer firstEntryIndex;

	/**
	 * The last entry index.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	private Integer lastEntryIndex;

	/**
	 * Returns the order instance.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	public Object getOrder() {
		return order;
	}

	/**
	 * Sets the order instance.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	public void setOrder(Object order) {
		this.order = order;
	}

	/**
	 * Returns the first entry index.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	public Integer getFirstEntryIndex() {
		return firstEntryIndex;
	}

	/**
	 * Sets the first entry index.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	public void setFirstEntryIndex(Integer firstEntryIndex) {
		this.firstEntryIndex = firstEntryIndex;
	}

	/**
	 * Returns the last entry index.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	public Integer getLastEntryIndex() {
		return lastEntryIndex;
	}

	/**
	 * Sets the last entry index.
	 * 
	 * Only for the query strategy {@link QueryingWithQueryBuilder}.
	 */
	public void setLastEntryIndex(Integer lastEntryIndex) {
		this.lastEntryIndex = lastEntryIndex;
	}
}
