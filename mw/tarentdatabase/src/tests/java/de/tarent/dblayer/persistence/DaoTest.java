//package de.tarent.dblayer.persistence;
//
//import java.sql.SQLException;
//import java.util.*;
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//import de.tarent.dblayer.engine.DBContext;
//import de.tarent.dblayer.engine.InsertKeys;
//import de.tarent.dblayer.sql.SQL;
//
///**
// * @author kirchner
// *
// */
//public class DaoTest extends TestCase {
//
//    PersonDAO pdao = new PersonDAO();
//    DBContext dbc;
//
//	/**
//	 * @param init
//	 */
//	public DaoTest(String init) {
//		super(init);
//	}
//
//	/* (non-Javadoc)
//	 * @see junit.framework.TestCase#setUp()
//	 */
//	protected void setUp() throws Exception {
//		SchemaCreator.getInstance().setUp(true);
//        dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//    }
//
//    public void tearDown() throws Exception {
//        dbc.getDefaultConnection().close();
//    }
//
//	public void testRetrievelOfPersonByID() throws SQLException{
//        Person p = pdao.getPersonByID(dbc, new Integer(1));
//        assertEquals("Rigth record", "Duck", p.getLastName());
//        assertEquals("Rigth record", "Dagobert", p.getGivenName());
//        assertNull("No Firma is loaded", p.getFirma());
//	}
//
//	public void testRetrievelOfAList() throws SQLException {
//        List persons = pdao.getAll(dbc);
//        // they are ordered by firstname
//        assertEquals("Right count of Records", 4, persons.size());
//        assertEquals("Right Person", "Dagobert", ((Person)persons.get(0)).getGivenName());
//        assertEquals("Right Person", "Daisy", ((Person)persons.get(1)).getGivenName());
//        assertEquals("Right Person", "Donald", ((Person)persons.get(2)).getGivenName());
//        assertEquals("Right Person", "Gustav", ((Person)persons.get(3)).getGivenName());
//    }
//
//	public void testDeleteOneEntity() throws SQLException{
//        // load
//        Person p = pdao.getPersonByID(dbc, new Integer(1));
//        assertEquals("Rigth record", "Dagobert", p.getGivenName());
//
//        // delete
//        pdao.delete(dbc, p);
//
//        // try to load again
//        p = pdao.getPersonByID(dbc, new Integer(1));
//        assertNull("Record deleted", p);
//
//        // assert that no other records got deleted
//        List persons = pdao.getAll(dbc);
//        assertEquals("All other records still exist", 3, persons.size());
//    }
//
//	public void testUpdateOneEntity() throws SQLException{
//        // load
//        Person p = pdao.getPersonByID(dbc, new Integer(1));
//        assertEquals("Rigth record", "Dagobert", p.getGivenName());
//        p.setGivenName("Frank");
//        pdao.update(dbc, p);
//
//        // try to load again
//        p = pdao.getPersonByID(dbc, new Integer(1));
//        assertEquals("Updated", "Frank", p.getGivenName());
//
//        // assert that no other records got updated
//        p = pdao.getPersonByID(dbc, new Integer(2));
//        assertEquals("Rigth record", "Daisy", p.getGivenName());
//    }
//
//	public void testRetrievelOfPersonAndFirmaByID() throws SQLException{
//        Person p = pdao.getPersonAndFirmaByID(dbc, new Integer(1));
//        assertEquals("Rigth record", "Duck", p.getLastName());
//        assertEquals("Rigth record", "Dagobert", p.getGivenName());
//
//        assertNotNull("Firma is loaded", p.getFirma());
//        assertEquals("Rigth related entity", "Dagoberts Geldspeicher", p.getFirma().getName());
//	}
//
//	public void testInsertEntity() throws SQLException{
//        Person p = (Person)pdao.getEntityFactory().getEntity();
//        p.setLastName("Mueller");
//        p.setGivenName("Fred");
//        // birthday is left null
//
//        pdao.insert(dbc, p);
//        assertTrue("id was set for new record", p.getId() != 0);
//
//        Person p2 = pdao.getPersonByID(dbc, new Integer(p.getId()));
//        assertEquals("record correct saved", p2.getId(), p.getId());
//        assertEquals("record correct saved", p2.getLastName(), p.getLastName());
//        assertEquals("record correct saved", p2.getGivenName(), p.getGivenName());
//        assertEquals("record correct saved", null, p2.getBirthday());
//	}
//}
