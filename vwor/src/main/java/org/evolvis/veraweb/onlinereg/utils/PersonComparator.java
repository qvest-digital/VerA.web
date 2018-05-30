package org.evolvis.veraweb.onlinereg.utils;

import org.evolvis.veraweb.onlinereg.entities.Person;

import java.util.Comparator;

public class PersonComparator implements Comparator<Person> {
    public int compare(Person p1, Person p2) {
        return p1.getLastname_a_e1().compareTo(p2.getLastname_a_e1());
    }
}
