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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Collection;

/**
 * Abstraction for a list of Integer Primary Keys.
 * This List has a simple String representation, which is provided
 * by the String constructor and the toString() method.
 * The String representation ist a comma "," separated list.
 */
public class PrimaryKeyList extends LinkedList {

    public static final String DELIM = ",";

    public PrimaryKeyList() {
        super();
    }

    /**
     * Lnitializes the list with a String list.
     */
    public PrimaryKeyList(String listRepresentation) {
        super();
        StringTokenizer st = new StringTokenizer(listRepresentation, DELIM);
        while (st.hasMoreTokens()) {
            add(new Integer(st.nextToken()));
        }
    }

    /**
     * Returns a PrimaryKeyList representations for the supplied collection.
     * If the collection is already a PrimaryKeyList instance, this is simply a cast.
     */
    public static PrimaryKeyList getPrimaryKeysListFor(Collection c) {
        if (c instanceof PrimaryKeyList)
            return (PrimaryKeyList)c;

        PrimaryKeyList pkList = new PrimaryKeyList();
        pkList.addAll(c);
        return pkList;
    }

    public String toString() {
        return toString(DELIM, this);
    }

    public static String toString(String delim, Collection collection) {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            sb.append(iter.next());
            if (iter.hasNext())
                sb.append(delim);
        }
        return sb.toString();
    }
}
