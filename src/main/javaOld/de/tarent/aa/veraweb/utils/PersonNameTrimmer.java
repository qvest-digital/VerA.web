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
import de.tarent.aa.veraweb.beans.Person;

public class PersonNameTrimmer {

    public static void trimAllPersonNames(Person person) {
        trimSpacesFromNamesForMainPerson(person);
        trimSpacesFromNamesForPartner(person);
    }

    private static void trimSpacesFromNamesForMainPerson(Person person) {
        trimNamesParnerFirstEncoding(person);
        trimNamesPartnerSecondEncoding(person);
        trimNamesPartnerThirdEncoding(person);
    }

    private static void trimSpacesFromNamesForPartner(Person person) {
        trimNamesMainPersonFirstEncoding(person);
        trimNamesMainPersonSecondEncoding(person);
        trimNamesMainPersonThirdEncoding(person);
    }

    private static void trimNamesMainPersonFirstEncoding(Person person) {
        person.firstname_b_e1 = person.firstname_b_e1.trim();
        person.lastname_b_e1 = person.lastname_b_e1.trim();
    }

    private static void trimNamesMainPersonSecondEncoding(Person person) {
        person.firstname_b_e2 = person.firstname_b_e2.trim();
        person.lastname_b_e2 = person.lastname_b_e2.trim();
    }

    private static void trimNamesMainPersonThirdEncoding(Person person) {
        person.firstname_b_e3 = person.firstname_b_e3.trim();
        person.lastname_b_e3 = person.lastname_b_e3.trim();
    }

    private static void trimNamesParnerFirstEncoding(Person person) {
        person.firstname_a_e1 = person.firstname_a_e1.trim();
        person.lastname_a_e1 = person.lastname_a_e1.trim();
    }

    private static void trimNamesPartnerSecondEncoding(Person person) {
        person.firstname_a_e2 = person.firstname_a_e2.trim();
        person.lastname_a_e2 = person.lastname_a_e2.trim();
    }

    private static void trimNamesPartnerThirdEncoding(Person person) {
        person.firstname_a_e3 = person.firstname_a_e3.trim();
        person.lastname_a_e3 = person.lastname_a_e3.trim();
    }
}