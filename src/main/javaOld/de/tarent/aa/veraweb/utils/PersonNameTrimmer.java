package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.beans.Person;

public class PersonNameTrimmer {

    public static void trimAllPersonNames(Person person) {
        trimSpacesFromNamesForMainPerson(person);
        trimSpacesFromNamesForPartner(person);
    }

    public static void trimSpacesFromNamesForMainPerson(Person person) {
        trimNamesParnerFirstEncoding(person);
        trimNamesPartnerSecondEncoding(person);
        trimNamesPartnerThirdEncoding(person);
    }

    public static void trimSpacesFromNamesForPartner(Person person) {
        trimNamesMainPersonFirstEncoding(person);
        trimNamesMainPersonSecondEncoding(person);
        trimNamesMainPersonThirdEncoding(person);
    }

    public static void trimNamesMainPersonFirstEncoding(Person person) {
        person.firstname_b_e1 = person.firstname_b_e1.trim();
        person.lastname_b_e1 = person.lastname_b_e1.trim();
    }

    public static void trimNamesMainPersonSecondEncoding(Person person) {
        person.firstname_b_e2 = person.firstname_b_e2.trim();
        person.lastname_b_e2 = person.lastname_b_e2.trim();
    }

    public static void trimNamesMainPersonThirdEncoding(Person person) {
        person.firstname_b_e3 = person.firstname_b_e3.trim();
        person.lastname_b_e3 = person.lastname_b_e3.trim();
    }

    public static void trimNamesParnerFirstEncoding(Person person) {
        person.firstname_a_e1 = person.firstname_a_e1.trim();
        person.lastname_a_e1 = person.lastname_a_e1.trim();
    }

    public static void trimNamesPartnerSecondEncoding(Person person) {
        person.firstname_a_e2 = person.firstname_a_e2.trim();
        person.lastname_a_e2 = person.lastname_a_e2.trim();
    }

    public static void trimNamesPartnerThirdEncoding(Person person) {
        person.firstname_a_e3 = person.firstname_a_e3.trim();
        person.lastname_a_e3 = person.lastname_a_e3.trim();
    }
}