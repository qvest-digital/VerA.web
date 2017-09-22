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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailValidator {

    /**
     * checks if an String represents a valid e-mail address
     * return true if e-mail address is valid, false if not
     *
     * @param email String with possible e-mail address
     * @return      boolean if e-mail address is valid or not
     */
    public static boolean isValidEmailAddress(String email){

        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}