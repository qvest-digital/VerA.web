//package de.tarent.dblayer.sql.clause;
//
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//import de.tarent.dblayer.engine.DBContext;
//import de.tarent.dblayer.sql.SQL;
//import de.tarent.dblayer.sql.statement.Select;
//import junit.framework.TestCase;
//
///**
// * A Simple test for genereted SubSelects...
// *
// * @author kirchner
// */
//public class SubSelectTest extends TestCase {
//
//	DBContext dbc;
//
//	public void setUp() throws Exception {
//		SchemaCreator.getInstance().setUp(false);
//		dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//	}
//
//	public void tearDown() throws Exception {
//		dbc.getDefaultConnection().close();
//	}
//
//	public void testSimpleSubSelect() {
//		Select subSelect = SQL.Select(dbc).select("test").from("test").where(
//				Expr.equal("test2", "5"));
//		Select select = SQL.Select(dbc).select("test").from("test").where(
//				Expr
//						.in("test", new SubSelect(DB
//								.getDefaultContext(SchemaCreator.TEST_POOL),
//								subSelect)));
//		assertEquals(
//				"Statement wurde nicht korrekt generiert!",
//				"SELECT test FROM test WHERE test IN (SELECT test FROM test WHERE test2='5')",
//				select.toString());
//	}
//
//}
