/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

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
        String origChars = "�������";
        byte[] isoBytes = origChars.getBytes("UTF-8");
        String mangledChars = new String(isoBytes, "ISO-8859-1");
        String encodedChars = OctopusHelper.encodeString(null, mangledChars, "ISO-8859-1", "UTF-8");
        System.out.println("Orig: <" + origChars + ">, Mangled: <" + mangledChars + ">, Encoded: <" + encodedChars + ">.");
        assertEquals(origChars, encodedChars);
    }
}
