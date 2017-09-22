package org.evolvis.veraweb.onlinereg.mail;

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
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mweier on 21.01.16.
 */
public class EmailValidatorTest {

    @Test
    public void testIsValidEmailAddress() {
        assertTrue(EmailValidator.isValidEmailAddress("valid@email.de"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@e.mail.de"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@127.0.0.1"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@[127.0.0.1]"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@[IPv6:2001:db8::1]"));
        assertTrue(EmailValidator.isValidEmailAddress("valid!#$%&'*+-/=?^_`{|}~@email.de"));


        assertFalse(EmailValidator.isValidEmailAddress("not-valid<>@email.de"));
        assertFalse(EmailValidator.isValidEmailAddress("not-valid@e@mail.de"));
        assertFalse(EmailValidator.isValidEmailAddress("not-valid@email:de"));
    }
}