package de.tarent.commons.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class VariableDateFormatTest extends TestCase {
    public void testAnalyzeString() throws ParseException {
        VariableDateFormat format = new VariableDateFormat();
        assertEquals(new SimpleDateFormat("d.M.y").parse("04.04.06"), format.analyzeString("04.04.06"));
        assertEquals(new SimpleDateFormat("d.M.y").parse("04.05.06"), format.analyzeString("2006-05-04 00:00:00.0"));
    }
}
