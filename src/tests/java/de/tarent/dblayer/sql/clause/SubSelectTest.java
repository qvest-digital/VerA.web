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
