/* $Id: ConsistentMap.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.util;

import java.util.*;


/**
 * Map Implementierung, deren KeySet().iterator() die Keys
 * in der Reihenfolge zurück gibt, in der sie eingefügt wurden.
 */
public class ConsistentMap extends HashMap {
    /**
	 * serialVersionUID = -7508966936670839920L
	 */
	private static final long serialVersionUID = -7508966936670839920L;

	LinkedHashSet orderedKeySet = new LinkedHashSet();
    
    public ConsistentMap() {
        super();
    }
    
    public void clear() {
        super.clear();
        orderedKeySet.clear();
    }
    
    public Set keySet() {
        return orderedKeySet;
    }
    
    public Object put(Object key, Object value) {
        orderedKeySet.add(key);
        return super.put(key, value);
    }
    
    public void putAll(Map m) {
        orderedKeySet.addAll(m.keySet());
        super.putAll(m);
    }
    
    public Object remove(Object key) {
        orderedKeySet.remove(key);
        return super.remove(key);
    }
}
