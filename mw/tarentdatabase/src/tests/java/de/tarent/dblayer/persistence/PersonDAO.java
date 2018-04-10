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
