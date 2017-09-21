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