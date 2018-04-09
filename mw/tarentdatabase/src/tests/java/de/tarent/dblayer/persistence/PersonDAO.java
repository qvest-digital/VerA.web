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

import java.sql.SQLException;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.ParamValueList;
import java.sql.PreparedStatement;
import de.tarent.dblayer.sql.statement.ExtPreparedStatement;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.InsertKeys;
import java.util.*;
import de.tarent.dblayer.SchemaCreator;

public class PersonDAO extends AbstractDAO {

    PersonDBMapping mapping = new PersonDBMapping(DB.getDefaultContext(SchemaCreator.TEST_POOL));
    PersonEntityFactory entityFactory = new PersonEntityFactory();

    public PersonDAO() {
        super();
        setDbMapping(mapping);
        setEntityFactory(entityFactory);
    }

    public void setEntityKeys(InsertKeys keys, Object entity) {
        ((Person)entity).setId(keys.getPk());
    }

    public Person getPersonByID(DBContext dbc, Integer id) throws SQLException {
        return (Person)getEntityByIdFilter(dbc, mapping.STMT_SELECT_ONE, "id", id);
    }

    public Person getPersonAndFirmaByID(DBContext dbc, Integer personID) throws SQLException {
        return (Person)getEntityByIdFilter(dbc, mapping.STMT_PERSON_FIRMA, "id", personID);
    }
}
