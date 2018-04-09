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

package de.tarent.commons.datahandling;

/**
 * Is a wrapper class for attribute names (of any business object).<p>
 * It will be used by {@link de.tarent.commons.datahandling.ListFilterProvider}
 * in order to distinguish and handle the filter elements inside the list, that represents a filter.
 * <p>
 * @see de.tarent.commons.datahandling.ListFilterProvider#getFilterList()
 * <p>
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public class ListFilterPropertyName {

    private String propertyName;

    /**
     * Creates an instance.<p>
     * @param newPropertyName of an instance
     */
    public ListFilterPropertyName(String newPropertyName) {
        this.propertyName = newPropertyName;
    }

    /** Returns an encapsulated property name. */
    public String toString() {
        return propertyName;
    }

}
