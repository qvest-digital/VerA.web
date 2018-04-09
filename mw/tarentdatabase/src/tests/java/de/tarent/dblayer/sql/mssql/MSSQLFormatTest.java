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
