//package de.tarent.dblayer.sql;
//
//import java.sql.SQLException;
//import java.util.Date;
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//import de.tarent.dblayer.engine.DBContext;
//import de.tarent.dblayer.engine.Result;
//import de.tarent.dblayer.sql.statement.Procedure;
//
///**
// * @author kirchner
// *
// */
//public class ProcedureTest extends TestCase {
//	DBContext dbc;
//
//	public ProcedureTest(String name) throws SQLException{
//		super(name);
//	}
//
//    public void setUp() throws Exception {
//    	SchemaCreator.getInstance().setUp(false);
//        dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//    }
//
//    public void tearDown() throws Exception {
//        dbc.getDefaultConnection().close();
//    }
//
//	public void testFormat() throws SyntaxErrorException{
//
//         if (SQL.isMSSQL(dbc)) {
//             Procedure proc_ms = SQL.Procedure(dbc, "dbo.unit_test").addParam("hallo");
//             assertEquals("Formatierung für MSSQL nicht korrekt", "dbo.unit_test 'hallo'", proc_ms.statementToString());
//         }
//         if (SQL.isPostgres(dbc)) {
//             Procedure proc_pg = SQL.Procedure(dbc, "public.unit_test").addParam("hallo");
//             assertEquals("Formatierung für Postgres nicht korrekt", "SELECT * FROM public.unit_test('hallo')", proc_pg.statementToString());
//         }
//	}
//
//	public void testEcho() throws SQLException {
//
// 		String param = (new Date()).toString();
// 		Procedure proc = null;
//        if (SQL.isMSSQL(dbc)) {
//             proc = SQL.Procedure(dbc, "dbo.unit_test").addParam(param);
//         } else {
//             proc = SQL.Procedure(dbc, "public.unit_test").addParam(param);
//         }
// 		Result r = proc.executeProcedure(dbc);
// 		if(r.resultSet().next()){
// 			assertEquals("Rückgabe entsprach nicht dem Parameter!", param, r.resultSet().getString("param1"));
// 		}else{
// 			fail("Prozedur hat nichts zurückgegeben!");
// 		}
// 		r.close();
//	}
//
// 	public void testvoidProcedure() throws SQLException {
//
//  		Procedure proc = null;
//         if (SQL.isMSSQL(dbc)) {
//              proc = SQL.Procedure(dbc, "dbo.unit_test2");
//          } else {
//              proc = SQL.Procedure(dbc, "public.unit_test2");
//          }
//  		proc.executeVoidProcedure(dbc);
// 	}
//}
