package de.tarent.aa.veraweb.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class VerawebUtilsTest {

    @Test
    public void testClearCommaSeparatedString() throws Exception {
        // WHEN
        final String result = VerawebUtils.clearCommaSeparatedString("a,   b,   ,,,,,   d");

        // THEN
        assertEquals("a,b,d", result);
    }
}