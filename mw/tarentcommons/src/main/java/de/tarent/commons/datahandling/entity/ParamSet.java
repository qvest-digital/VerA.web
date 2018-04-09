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

package de.tarent.commons.datahandling.entity;

import java.util.*;

/**
 * Represents an API to set paraemters in an parameterized structure.
 */
public interface ParamSet extends WritableAttribute {

    /**
     * Clears all parameters of the structure.
     */
    public void clearAttributes();

    /**
     * Sets the attribute <code>attributeName</code> of this structure to the supplied value.
     */
    public void setAttribute(String attributeName, Object attributeValue);

    /**
     * Sets the attributes of this structure to the supplied attributes in the map.
     */
    public void setAttributes(AttributeSource attributeSource);

    /**
     * Returns the String List of the AttributeNames
     */
    public List getAttributeNames();

}
