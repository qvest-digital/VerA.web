package org.evolvis.veraweb.onlinereg.utils;

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
import org.apache.commons.lang3.StringUtils;
import org.evolvis.veraweb.onlinereg.entities.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to get correct salutation and/or address for the guest list export as PDF.
 *
 * - salutationCompleteOne = salutation + " " + title + " " + first name + " " + last name
 * - salutationCompleteTwo = salutationCompleteOne + ", " + suffix1_a_e1
 * - envelopeOne =
 *      function + "\n" +
 *      salutation_complete + "\n" +
 *      suffix1_a_e1 + "\n" +
 *      street + "\n" +
 *      plz + " " + city + "\n" +
 *      country
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class PdfTemplateUtilities {

    private final Person person;

    public PdfTemplateUtilities(Person person) {
        this.person = person;
    }

    public String getSalutationCompleteOne() {
        final List<String> salutationCompleteComponents = new ArrayList<>();

        if (person.getSalutation_a_e1() != null && !person.getSalutation_a_e1().equals("")) {
            salutationCompleteComponents.add(person.getSalutation_a_e1());
        }
        if (person.getTitle_a_e1() != null && !person.getTitle_a_e1().equals("")) {
            salutationCompleteComponents.add(person.getTitle_a_e1());
        }
        if (person.getFirstname_a_e1() != null && !person.getFirstname_a_e1().equals("")) {
            salutationCompleteComponents.add(person.getFirstname_a_e1());
        }
        if (person.getLastname_a_e1() != null && !person.getLastname_a_e1().equals("")) {
            salutationCompleteComponents.add(person.getLastname_a_e1());
        }

        return StringUtils.join(salutationCompleteComponents, " ");
    }

    public String getSalutationCompleteTwo() {
        final List<String> salutationCompleteTwo = new ArrayList<>();
        final String salutationCompleteOne = getSalutationCompleteOne();
        if (salutationCompleteOne != null && !salutationCompleteOne.equals("")) {
            salutationCompleteTwo.add(salutationCompleteOne);
        }
        if (person.getSuffix1_a_e1() != null && !person.getSuffix1_a_e1().equals("")) {
            salutationCompleteTwo.add(person.getSuffix1_a_e1());
        }

        return StringUtils.join(salutationCompleteTwo, ", ");
    }

    public String getEnvelopeOne() {

        final List<String> envelopeOne = new ArrayList<>();
        if (person.getFunction_a_e1() != null && !person.getFunction_a_e1().equals("")) {
            envelopeOne.add(person.getFunction_a_e1());
        }

        final String salutationComplete = getSalutationCompleteOne();
        if (salutationComplete != null && !salutationComplete.equals("")) {
            envelopeOne.add(salutationComplete);
        }

        if (person.getSuffix1_a_e1() != null && !person.getSuffix1_a_e1().equals("")) {
            envelopeOne.add(person.getSuffix1_a_e1());
        }

        if (person.getStreet_a_e1() != null && !person.getStreet_a_e1().equals("")) {
            envelopeOne.add(person.getStreet_a_e1());
        }

        String zipCodeAndCity = getZipCodeAndCity(person);
        if (zipCodeAndCity != null && !zipCodeAndCity.equals("")) {
            envelopeOne.add(zipCodeAndCity);
        }
        if (person.getCountry_a_e1() != null && !person.getCountry_a_e1().equals("")) {
            envelopeOne.add(person.getCountry_a_e1());
        }

        return StringUtils.join(envelopeOne, "\n");
    }

    private String getZipCodeAndCity(Person person) {
        final List<String> zipCodeAndCity = new ArrayList<>();
        if (person.getZipcode_a_e1() != null && !person.getZipcode_a_e1().equals("")) {
            zipCodeAndCity.add(person.getZipcode_a_e1());
        }
        if (person.getCity_a_e1() != null && !person.getCity_a_e1().equals("")) {
            zipCodeAndCity.add(person.getCity_a_e1());
        }
        return StringUtils.join(zipCodeAndCity, " ");
    }
}
