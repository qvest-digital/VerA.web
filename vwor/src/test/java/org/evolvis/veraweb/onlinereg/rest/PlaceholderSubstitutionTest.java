package org.evolvis.veraweb.onlinereg.rest;

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
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlaceholderSubstitutionTest {

    @Test
    public void test() throws IllegalArgumentException, IllegalAccessException {
        //GIVEN
        Person person = createPerson();
        PlaceholderSubstitution subst = new PlaceholderSubstitution(person);
        final List<String> words = Arrays.asList("firstname", "lastname", "salutation", "title", "function", "company", "street", "zipcode", "city",
                "country", "poboxzipcode", "pobox", "suffix1", "suffix2", "phone", "fax", "mobile", "email", "url");

        final String template = createTemplate(words);
        final String expectedOutput = createExpectedOutput(words);

        // WHEN
        String actualOutput = subst.apply(template);

        // THEN
        assertEquals(expectedOutput, actualOutput);
    }

    private String createExpectedOutput(final List<String> words) {
        final StringBuilder sb2 = new StringBuilder();
        for (String word : words) {
            String expectedValue = word;
            if(word.equals("phone") ){
                expectedValue = "fon";
            }
            if(word.equals("email") ){
                expectedValue = "mail";
            }
            if(word.equals("mobile") ){
                expectedValue = "mobil";
            }
            sb2.append(word+": '"+expectedValue+"_a_e1'\n");
        }

        final String expectedOutput = sb2.toString();
        return expectedOutput;
    }

    private String createTemplate(final List<String> words) {
        final StringBuilder sb1 = new StringBuilder();
        for (String word : words) {
            sb1.append(word+": '&lt;"+word+"&gt;'\n");
        }
        final String template = sb1.toString();
        return template;
    }

    private Person createPerson() throws IllegalAccessException {

        final Person person = new Person();
        final Field[] fields = Person.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.getType().isAssignableFrom(String.class)){
                //for now, all the fields that we care about *are* strings.
                //We put the field name in there, just to have some values we can
                //examine.
                field.set(person,field.getName());
            }
        }
        return person;
    }
}
