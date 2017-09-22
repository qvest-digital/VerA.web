package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
