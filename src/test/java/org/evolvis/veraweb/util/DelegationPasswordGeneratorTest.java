package org.evolvis.veraweb.util;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DelegationPasswordGeneratorTest {

    @Test
    public void test() {
        final String companyName = "tarent solutions GmbH";
        final String eventName = "3. Treffen der annynomen Scrum-Leugner";
        final Calendar cal = Calendar.getInstance();
        cal.set(1978, 0, 9);
        final Date eventBegin = cal.getTime();
        final String password = new DelegationPasswordGenerator().generatePassword(eventName,eventBegin,companyName);
        assertEquals("3. tar1978-01-09", password);
    }

}
