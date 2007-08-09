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
 * Map based implementation for the attribute source interface.
 *
 * @author Sebastian Mancke, tarent GmbH
 *
 */
public class MapAttributeSource implements AttributeSource {

    Map delegate;
    
    public MapAttributeSource(Map delegate) {
        setDelegate(delegate);
    }

    public Object getAttribute(String attributeName) {
        return delegate.get(attributeName);
    }

    public Class getAttributeType(String attributeName) {
    	Object param = delegate.get(attributeName);
    	return param == null ? null : param.getClass();
    }

    /**
     * Returns a list the attribute names
     * @return list of Strings
     */
    public List getAttributeNames() {
        List names = new ArrayList(delegate.size());
        for (Iterator iter = delegate.keySet().iterator(); iter.hasNext();) {
            names.add(iter.next());
        }
        return names;
    }

    public Map getDelegate() {
        return delegate;
    }

    public void setDelegate(Map newDelegate) {
        this.delegate = newDelegate;
    }

}
