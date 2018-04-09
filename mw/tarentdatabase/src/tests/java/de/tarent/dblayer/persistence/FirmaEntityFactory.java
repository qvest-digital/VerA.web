/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
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
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.dblayer.persistence;

import de.tarent.commons.datahandling.entity.EntityFactory;
import de.tarent.commons.datahandling.entity.DefaultEntityFactory;

public class FirmaEntityFactory extends DefaultEntityFactory {

    private static FirmaEntityFactory instance = null;

    protected FirmaEntityFactory() {
        super(Firma.class);
    }

    public static synchronized FirmaEntityFactory getInstance() {
        if (instance == null) {
            instance = new FirmaEntityFactory();
        }
        return instance;
    }

    protected EntityFactory getFactoryFor(String attribute) {
        if ("firma".equals(attribute))
            return PersonEntityFactory.getInstance();
        return null;
    }

}
