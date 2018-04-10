//package de.tarent.dblayer.sql.mssql;
//
//import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//import de.tarent.dblayer.engine.DBContext;
//import de.tarent.dblayer.sql.SQL;
//import de.tarent.dblayer.sql.clause.Expr;
//
//
///**
// * @author kirchner
// *
// */
//public class MSSQLFormatTest extends TestCase {
//	DBContext dbx;
//	static public final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//	/**
//	 * @param arg0
//	 * @throws SQLException
//	 */
//	public MSSQLFormatTest(String arg0) throws SQLException {
//		super(arg0);
//		SchemaCreator.getInstance().setUp(false);
//		dbx = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//	}
//
//	/* (non-Javadoc)
//	 * @see junit.framework.TestCase#setUp()
//	 */
//	protected void setUp() throws Exception {
//		super.setUp();
//	}
//
//	/* (non-Javadoc)
//	 * @see junit.framework.TestCase#tearDown()
//	 */
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	public void testDate(){
//        // only execute if pool is on MS SQL
//        if (SQL.isMSSQL(dbx)) {
//            Date date = new Date();
//            String sql_result = SQL.Select(dbx).select("*").from("test").where(Expr.equal("test", date)).toString();
//            String sql_expected = "SELECT * FROM test WHERE test=convert(datetime, '"+ DF.format(date) + "', 120)";
//            assertEquals("Statement nicht koorekt!", sql_expected, sql_result);
//        }
//	}
//
//}
