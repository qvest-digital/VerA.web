//package de.tarent.dblayer.sql.statement;
//
//import java.sql.SQLException;
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
//public class InsertTest extends TestCase {
//
//	/**
//	 * @param init
//	 */
//	public InsertTest(String init) {
//		super(init);
//	}
//
//	/* (non-Javadoc)
//	 * @see junit.framework.TestCase#setUp()
//	 */
//	protected void setUp() throws Exception {
//		SchemaCreator.getInstance().setUp(true);
//	}
//
//	public void testRetrievelOfKeys() throws SQLException{
//		DBContext dbx = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//		InsertKeys keys = SQL.Insert(dbx).table("insert_test").insert("data", "test").executeInsertKeys(dbx);
//		assertEquals(new Integer(1), new Integer(keys.getPk()));
//		keys = SQL.Insert(dbx).table("insert_test").insert("data", "test2").executeInsertKeys(dbx);
//		assertEquals(new Integer(2), new Integer(keys.getPk()));
//        dbx.getDefaultConnection().close();
//	}
//
//	public void testRetrievelOfKeys2() throws SQLException{
//		DBContext dbx = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//		InsertKeys keys = SQL.Insert(dbx).table("insert_test").insert("data", "Nur ein Test!").executeInsertKeys(dbx);
//		assertNotNull("Kein Key gefunden!", keys.getPkAsInteger());
//		assertEquals("Key sollte 1 sein...", 1, keys.getPk());
//        dbx.getDefaultConnection().close();
//	}
//
//}
