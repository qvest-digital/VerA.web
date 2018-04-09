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

import java.util.HashMap;

/**
 * Simple implementation for a LookupContext based on a Map
 */
public class LookupContextImpl implements LookupContext {

    HashMap map = new HashMap();

    public boolean doesEntityExist(Object entityID, String entityType) {
        return map.containsKey(concat(entityID, entityType));
    }

    public Object getEntity(Object entityID, String entityType) {
        return map.get(concat(entityID, entityType));
    }

    public void registerEntity(Object entityID, String entityType, Object entity) {
        map.put(concat(entityID, entityType), entity);
    }

    protected String concat(Object entityID, String entityType) {
        return entityType +":"+ entityID;
    }
}
