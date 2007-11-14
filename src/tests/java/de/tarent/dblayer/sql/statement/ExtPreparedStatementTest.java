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
//package de.tarent.dblayer.sql.statement;
//
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.*;
//import de.tarent.dblayer.sql.*;
//import de.tarent.dblayer.sql.statement.*;
//import de.tarent.dblayer.sql.clause.*;
//import de.tarent.dblayer.engine.*;
//import java.sql.*;
//import de.tarent.dblayer.engine.Result;
//import de.tarent.dblayer.engine.ResultProcessor;
//import java.util.Date;
//import java.util.Calendar;
//
///**
// *
// * @author Sebastian Mancke, tarent GmbH
// */
//public class ExtPreparedStatementTest extends TestCase {
//
//    DBContext dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//
//	/**
//	 * @param init
//	 */
//	public ExtPreparedStatementTest(String init) {
//		super(init);
//	}
//
//	/* (non-Javadoc)
//	 * @see junit.framework.TestCase#setUp()
//	 */
//	protected void setUp() throws Exception {
//		SchemaCreator.getInstance().setUp(false);
//	}
//	
//	public void testPreparedSelect() throws Exception {
//        Select select = SQL.Select(dbc);
//        select.from("person")
//            .select("pk_person")
//            .whereAnd(Expr.equal("pk_person", new ParamValue("id")))
//            .whereAnd(Expr.equal("nachname", new ParamValue("nname")));
//        
//        ExtPreparedStatement extstmt = select.prepare(dbc);
//        
//        try {
//            extstmt.setAttribute("id", new Integer(1));
//            extstmt.setAttribute("nname", "Duck");
//            ResultSet rs = extstmt.getPreparedStatement().executeQuery();
//            assertTrue("Expect a record to be found", rs.next());
//            assertEquals("Expect the right record", 1, rs.getInt(1));
//            rs.close();
//
//            extstmt.clearAttributes();
//            extstmt.setAttribute("id", new Integer(2));
//            extstmt.setAttribute("nname", "Duck");
//            rs = extstmt.getPreparedStatement().executeQuery();
//            assertTrue("Expect a record to be found", rs.next());
//            assertEquals("Expect the right record", 2, rs.getInt(1));
//        } finally {
//            DB.close(extstmt.getPreparedStatement());
//        }
//	}
//
//	public void testPreparedInsert() throws Exception {
//        Insert stmt = SQL.Insert(dbc);
//        stmt.table("person");
//        stmt.insert("fk_firma", new ParamValue("firma"));
//        stmt.insert("vorname", new ParamValue("vname"));
//        stmt.insert("nachname", new ParamValue("nname"));
//        stmt.insert("geburtstag", new ParamValue("geb"));
//
//        ExtPreparedStatement extstmt = stmt.prepare(dbc);
//        
//        try {
//            Calendar c = Calendar.getInstance();
//            c.clear();
//            c.set(2006, 9, 4);
//            final Date date = c.getTime();
//            extstmt.setAttribute("geb", date);
//            extstmt.setAttribute("firma", new Integer(2));
//            extstmt.setAttribute("vname", "Frank");
//            extstmt.setAttribute("nname", "Mueller");
//            extstmt.getPreparedStatement().executeUpdate();            
//            Integer id = extstmt.returnGeneratedKeys(dbc).getPkAsInteger();
//            
//            int rows = SQL.Select().from("person")
//                .select("fk_firma")
//                .select("vorname")
//                .select("nachname")
//                .select("geburtstag")
//                .whereAndEq("pk_person", id)
//                .iterate(dbc, new ResultProcessor() {
//                        public void process(ResultSet rs) throws SQLException {
//                            assertEquals("Inserted fiels are correct", 2, rs.getInt(1));
//                            assertEquals("Inserted fiels are correct", "Frank", rs.getString(2));
//                            assertEquals("Inserted fiels are correct", "Mueller", rs.getString(3));
//                            assertEquals("Inserted fiels are correct", date, rs.getDate(4));
//                        }
//                    });
//            assertEquals("One row retrieved", 1, rows);
//            
//
//            // leaving the date
//            //extstmt.setAttribute(geb, date);
//            extstmt.setAttribute("firma", new Integer(1));
//            extstmt.setAttribute("vname", "Willhelm");
//            extstmt.setAttribute("nname", "Schneider");
//            extstmt.getPreparedStatement().executeUpdate();
//            id = extstmt.returnGeneratedKeys(dbc).getPkAsInteger();
//            
//            rows = SQL.Select().from("person")
//                .select("fk_firma")
//                .select("vorname")
//                .select("nachname")
//                .select("geburtstag")
//                .whereAndEq("pk_person", id)
//                .iterate(dbc, new ResultProcessor() {
//                        public void process(ResultSet rs) throws SQLException {
//                            assertEquals("Inserted fiels are correct", 1, rs.getInt(1));
//                            assertEquals("Inserted fiels are correct", "Willhelm", rs.getString(2));
//                            assertEquals("Inserted fiels are correct", "Schneider", rs.getString(3));
//                            assertEquals("Inserted fiels are correct", date, rs.getDate(4));
//                        }
//                    });
//            assertEquals("One row retrieved", 1, rows);
//            
//        } finally {
//            DB.close(extstmt.getPreparedStatement());
//        }
//	}
//}
