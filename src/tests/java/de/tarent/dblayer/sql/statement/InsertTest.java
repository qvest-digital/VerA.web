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
