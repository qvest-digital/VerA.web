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
//import java.sql.SQLException;
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.*;
//import de.tarent.dblayer.sql.*;
//import de.tarent.dblayer.sql.clause.*;
//
///**
// * @author Sebastian Mancke, tarent GmbH
// *
// */
//public class SelectCloneTest extends TestCase {
//
//    DBContext dbc;
//
//	/**
//	 * @param init
//	 */
//	public SelectCloneTest(String init) {
//		super(init);
//	}
//
//	/* (non-Javadoc)
//	 * @see junit.framework.TestCase#setUp()
//	 */
//	protected void setUp() throws Exception {
//		SchemaCreator.getInstance().setUp(false);
//        dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//	}
//	
//	public void testSimpleClone() throws SQLException{
//        
//        Select s1 = SQL.Select(dbc)
//            .from("xx")
//            .selectAs("ee")
//            .whereAndEq("yyy", new ParamValue("pv"))
//            .whereAndEq("aaa", new ParamValue("pv2"))
//            .Limit(new Limit(10, 10))
//            .orderBy(Order.asc("ccc"));
//
//        String sqlOriginal = s1.statementToString();
//        
//        Select s2 = (Select)s1.clone();
//        s2.whereAndEq("bbb", new ParamValue("pv3"))
//            .from("yy")            
//            .join("sdcsd", "sdcsdc", "sdcsdc")
//            .Limit(new Limit(42, 42))
//            .orderBy(Order.desc("qq"))
//            .selectAs("aa");
//
//
//        ParamValueList pvl = new ParamValueList();
//        s2.getParams(pvl);
//        pvl.setAttribute("pv", "AAAAAAA");
//        pvl.setAttribute("pv2", "BBBBBBB");
//        pvl.setAttribute("pv3", "CCCCCCCCC");
//        
//        String sqlOriginalLater = s1.statementToString();
//        assertEquals("Statement of s1 should not have changed", sqlOriginal, sqlOriginalLater);
//    }
//}
