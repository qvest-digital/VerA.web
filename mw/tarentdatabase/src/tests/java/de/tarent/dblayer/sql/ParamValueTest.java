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
//
//import junit.framework.TestCase;
//import de.tarent.dblayer.*;
//import de.tarent.dblayer.sql.*;
//import de.tarent.dblayer.sql.statement.*;
//import de.tarent.dblayer.sql.clause.*;
//import de.tarent.dblayer.engine.*;
//
///**
// *
// * @author Sebastian Mancke, tarent GmbH
// */
//public class ParamValueTest extends TestCase {
//
//    DBContext dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//
//	/**
//	 * @param init
//	 */
//	public ParamValueTest(String init) {
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
//	public void testSelectParam() throws Exception {
//        Select select = SQL.Select(dbc);
//        select.from("person")
//            .select("pk_person")
//            .whereAnd(Expr.equal("pk_person", new ParamValue("id")));
//
//        assertEquals("Param Select", "SELECT pk_person FROM person WHERE pk_person=?", select.statementToString());
//
//        ParamValueList paramList = new ParamValueList();
//        select.getParams(paramList);
//        paramList.setAttribute("id", new Integer(1));
//
//        assertEquals("Param Select", "SELECT pk_person FROM person WHERE pk_person=1", select.statementToString());
//	}
//
//	public void testSelectParamWhereAndEq() throws Exception {
//        Select select = SQL.Select(dbc);
//        select.from("person")
//            .select("pk_person")
//            .whereAndEq("pk_person", new ParamValue("id"));
//
//        assertEquals("Param Select", "SELECT pk_person FROM person WHERE pk_person=?", select.statementToString());
//
//        ParamValueList paramList = new ParamValueList();
//        select.getParams(paramList);
//        paramList.setAttribute("id", new Integer(1));
//
//        assertEquals("Param Select", "SELECT pk_person FROM person WHERE pk_person=1", select.statementToString());
//	}
//
//	public void testParamsInNestedWhere() throws Exception {
//        Select select = SQL.Select(dbc);
//        select.from("person")
//            .select("pk_person")
//            .whereAnd(Expr.equal("pk_person", new ParamValue("id")))
//            .whereAnd(Where.or(Expr.equal("vorname", new ParamValue("vname1")),
//                               Expr.equal("vorname", new ParamValue("vname2"))));
//
//
//        ParamValueList paramList = new ParamValueList();
//        select.getParams(paramList);
//        paramList.setAttribute("id", new Integer(1));
//        paramList.setAttribute("vname1", "Dagobert");
//        paramList.setAttribute("vname2", "Donald");
//        assertEquals("Attribute on right position", "id", ((ParamValue)paramList.get(0)).getName());
//        assertEquals("Attribute on right position", "vname1", ((ParamValue)paramList.get(1)).getName());
//        assertEquals("Attribute on right position", "vname2", ((ParamValue)paramList.get(2)).getName());
//
//        assertEquals("Param Select", "SELECT pk_person FROM person WHERE pk_person=1 AND (vorname='Dagobert' OR vorname='Donald')", select.statementToString());
//	}
//
//    public void testParamsInInsert() throws Exception {
//        Insert stmt = SQL.Insert(dbc);
//        stmt.table("person");
//        stmt.insert("fk_firma", new ParamValue("firma"));
//        stmt.insert("vorname", new ParamValue("vname"));
//        stmt.insert("nachname", new ParamValue("nname"));
//        stmt.insert("geburtstag", new ParamValue("geb"));
//
//        ParamValueList paramList = new ParamValueList();
//        stmt.getParams(paramList);
//        assertEquals("Attribute on right position", "firma", ((ParamValue)paramList.get(0)).getName());
//        assertEquals("Attribute on right position", "vname", ((ParamValue)paramList.get(1)).getName());
//        assertEquals("Attribute on right position", "nname", ((ParamValue)paramList.get(2)).getName());
//        assertEquals("Attribute on right position", "geb", ((ParamValue)paramList.get(3)).getName());
//
//        assertEquals("Simple param insert", "INSERT INTO person (fk_firma,vorname,nachname,geburtstag) VALUES (?,?,?,?)", stmt.statementToString());
//    }
//}
