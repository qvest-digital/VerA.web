//package de.tarent.dblayer.sql.clause;
//
//import java.sql.SQLException;
//
//import junit.framework.TestCase;
//
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//import de.tarent.dblayer.engine.DBContext;
//import de.tarent.dblayer.sql.SQL;
//import de.tarent.dblayer.sql.statement.Select;
//
//public class ExistsTest extends TestCase {
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
//	public void testExists() {
//		Select subSelect = SQL.Select(dbc).select("test").from("test").where(
//				Expr.equal("test2", "5"));
//		Select select = SQL.Select(dbc).select("test").from("test").where(
//				Expr
//						.exists(new SubSelect(DB
//								.getDefaultContext(SchemaCreator.TEST_POOL),
//								subSelect)));
//		assertEquals(
//				"Statement wurde nicht korrekt generiert!",
//				"SELECT test FROM test WHERE  EXISTS (SELECT test FROM test WHERE test2='5')",
//				select.toString());
//
//	}
//}
