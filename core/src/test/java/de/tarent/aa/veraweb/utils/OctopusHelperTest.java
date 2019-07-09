package de.tarent.aa.veraweb.utils;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;

/**
 * Diese Testklasse testet {@link OctopusHelper}.
 *
 * @author mikel
 */
public class OctopusHelperTest extends TestCase {
    //
    // Tests
    //

    /**
     * Diese Testmethode testet das Re-Encoding des OctopusHelpers.
     *
     * @throws UnsupportedEncodingException
     */
    public void testEncodeString() throws UnsupportedEncodingException {
        String origChars = "äöüÄÖÜß";
        byte[] isoBytes = origChars.getBytes("UTF-8");
        String mangledChars = new String(isoBytes, "ISO-8859-1");
        String encodedChars = OctopusHelper.encodeString(null, mangledChars, "ISO-8859-1", "UTF-8");
        System.out.println("Orig: <" + origChars + ">, Mangled: <" + mangledChars + ">, Encoded: <" + encodedChars + ">.");
        assertEquals(origChars, encodedChars);
    }
}
