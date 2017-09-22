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
import java.text.SimpleDateFormat;
import java.util.Date;

public class DelegationPasswordGenerator {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public String generatePassword(String eventName, Date eventBegin, String companyName) {
        return generateDelegationPassword(eventName, eventBegin, companyName);
    }
    private static String generateDelegationPassword(final String shortName, final Date begin, String companyName) {
        final StringBuilder passwordBuilder = new StringBuilder();
        final SimpleDateFormat dateFormat = DATE_FORMAT;
        passwordBuilder.append(extractFirstXChars(shortName, 3));
        passwordBuilder.append(extractFirstXChars(companyName, 3));
        passwordBuilder.append(dateFormat.format(begin));
        return passwordBuilder.toString();
    }

    private static String extractFirstXChars(String value, int x) {
        return value.substring(0, Math.min(value.length(), x));
    }

}
