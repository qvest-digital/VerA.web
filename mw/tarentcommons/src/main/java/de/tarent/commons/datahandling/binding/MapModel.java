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

package de.tarent.commons.datahandling.binding;

import de.tarent.commons.datahandling.entity.*;
import java.util.*;

/**
 * Simple Model implementation storing in a Map.
 */
public class MapModel extends AbstractModel {

    Map map;

    public MapModel() {
	this.map = new HashMap();
    }

    public MapModel(Map storageMap) {
	this.map = storageMap;
    }

    public Map getStorageMap() {
	return map;
    }

    public void setAttribute(String key, Object value) throws EntityException {
	map.put(key, value);
	fireDataChanged(new DataChangedEvent(this, key));
    }

    public Object getAttribute(String key) throws EntityException {
	if (key == null || "".equals(key))
	    return map;
	return map.get(key);
    }

    public String toString() {
	return map.toString();
    }

    public Map getMap(){
	return map;
    }
}
