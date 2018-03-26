package org.evolvis.veraweb.onlinereg.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
