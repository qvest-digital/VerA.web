/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
 * 
 * Created on 15.12.2005
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
        String origChars = "‰ˆ¸ƒ÷‹ﬂ";
        byte[] isoBytes = origChars.getBytes("UTF-8");
        String mangledChars = new String(isoBytes, "ISO-8859-1");
        String encodedChars = OctopusHelper.encodeString(null, mangledChars, "ISO-8859-1", "UTF-8");
        System.out.println("Orig: <" + origChars + ">, Mangled: <" + mangledChars + ">, Encoded: <" + encodedChars + ">.");
        assertEquals(origChars, encodedChars);
    }
}
