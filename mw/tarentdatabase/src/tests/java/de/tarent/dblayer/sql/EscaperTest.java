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
