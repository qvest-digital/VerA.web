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
//package de.tarent.dblayer.sql;
//
//
//public class EscaperTest
//    extends junit.framework.TestCase {
//
//    public EscaperTest() {
//        super();
//    }
//
//    public EscaperTest(String init) {
//        super(init);
//    }
//
//    public void testSimpleEscaping() {
//        assertEquals("Simple ' escaping", "sebastian''s", Escaper.escape("sebastian's"));
//        assertEquals("Simple \\ escaping", "sebastian\\\\n", Escaper.escape("sebastian\\n"));
//        assertEquals("Combined ' \\ escaping", "\\\\n sebastian''s\\\\n", Escaper.escape("\\n sebastian's\\n"));
//    }
//
//    public void testAppending() {
//        StringBuffer sb = new StringBuffer("\\\\n Test''s");
//        Escaper.escape(sb, "\\n sebastian's\\n");
//        assertEquals("Combined ' \\ escaping", "\\\\n Test''s\\\\n sebastian''s\\\\n", sb.toString());
//    }
//}