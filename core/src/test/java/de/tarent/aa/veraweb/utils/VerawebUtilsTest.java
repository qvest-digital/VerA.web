package de.tarent.aa.veraweb.utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class VerawebUtilsTest {

    @Test
    public void testClearCommaSeparatedString() throws Exception {
        // WHEN
        final String result = VerawebUtils.clearCommaSeparatedString("große, a, ä, ü,  ö, ß, b,   ,,,,,  ... d");

        // THEN
        assertEquals("große,a,ä,ü,ö,ß,b,d", result);
    }
}
