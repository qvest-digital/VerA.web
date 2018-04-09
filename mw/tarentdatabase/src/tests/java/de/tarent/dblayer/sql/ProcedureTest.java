///*
// * tarent-database,
// * jdbc database library
// * Copyright (c) 2005-2006 tarent GmbH
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License,version 2
// * as published by the Free Software Foundation.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
// * 02110-1301, USA.
// *
// * tarent GmbH., hereby disclaims all copyright
// * interest in the program 'tarent-database'
// * Signature of Elmar Geese, 14 June 2007
// * Elmar Geese, CEO tarent GmbH.
// */
//
///**
// *
// */
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
