/**
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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
package de.tarent.aa.veraweb.beans

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class PersonTest extends Specification {

    private Validator validator
    private java.util.Set<javax.validation.ConstraintViolation<org.evolvis.veraweb.onlinereg.entities.Person>> violations

    public void setup() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public void testFirstnameSuccess() {
        given:
            Person person = new Person(firstname_a_e1: "Max_Latin");

        when:
            violations = this.validator.validate(person);

        then:
            assert this.violations.isEmpty() == true
            person.firstname_a_e1 == "Max_Latin"
    }

    public void testValidation() {
        def tooLongString = "maxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxx"
        given:
            Person person = new Person(
                birthplace_a_e1: tooLongString,
                birthplace_a_e2: tooLongString,
                birthplace_a_e3: tooLongString,
                city_a_e1: tooLongString,
                city_a_e2: tooLongString,
                city_a_e3: tooLongString,
                city_b_e1: tooLongString,
                city_b_e2: tooLongString,
                city_b_e3: tooLongString,
                city_c_e1: tooLongString,
                city_c_e2: tooLongString,
                city_c_e3: tooLongString,
                company_a_e1: tooLongString,
                company_a_e2: tooLongString,
                company_a_e3: tooLongString,
                company_b_e1: tooLongString,
                company_b_e2: tooLongString,
                company_b_e3: tooLongString,
                company_c_e1: tooLongString,
                company_c_e2: tooLongString,
                company_c_e3: tooLongString,
                country_a_e1: tooLongString,
                country_a_e2: tooLongString,
                country_a_e3: tooLongString,
                country_b_e1: tooLongString,
                country_b_e2: tooLongString,
                country_b_e3: tooLongString,
                country_c_e1: tooLongString,
                country_c_e2: tooLongString,
                country_c_e3: tooLongString,
                fax_a_e1: tooLongString,
                fax_a_e2: tooLongString,
                fax_a_e3: tooLongString,
                fax_b_e1: tooLongString,
                fax_b_e2: tooLongString,
                fax_b_e3: tooLongString,
                fax_c_e1: tooLongString,
                fax_c_e2: tooLongString,
                fax_c_e3: tooLongString,
                firstname_a_e1: tooLongString,
                firstname_a_e2: tooLongString,
                firstname_a_e3: tooLongString,
                firstname_b_e1: tooLongString,
                firstname_b_e2: tooLongString,
                firstname_b_e3: tooLongString,
                fon_a_e1: tooLongString,
                fon_a_e2: tooLongString,
                fon_a_e3: tooLongString,
                fon_b_e1: tooLongString,
                fon_b_e2: tooLongString,
                fon_b_e3: tooLongString,
                fon_c_e1: tooLongString,
                fon_c_e2: tooLongString,
                fon_c_e3: tooLongString,
                function_a_e1: tooLongString,
                function_a_e2: tooLongString,
                function_a_e3: tooLongString,
                function_b_e1: tooLongString,
                function_b_e2: tooLongString,
                function_b_e3: tooLongString,
                function_c_e1: tooLongString,
                function_c_e2: tooLongString,
                function_c_e3: tooLongString,
                languages_a_e1: tooLongString,
                languages_b_e1: tooLongString,
                lastname_a_e1: tooLongString,
                lastname_a_e2: tooLongString,
                lastname_a_e3: tooLongString,
                lastname_b_e1: tooLongString,
                lastname_b_e2: tooLongString,
                lastname_b_e3: tooLongString,
                mail_a_e1: tooLongString,
                mail_a_e2: tooLongString,
                mail_a_e3: tooLongString,
                mail_b_e1: tooLongString,
                mail_b_e2: tooLongString,
                mail_b_e3: tooLongString,
                mail_c_e1: tooLongString,
                mail_c_e2: tooLongString,
                mail_c_e3: tooLongString,
                mobil_a_e1: tooLongString,
                mobil_a_e2: tooLongString,
                mobil_a_e3: tooLongString,
                mobil_b_e1: tooLongString,
                mobil_b_e2: tooLongString,
                mobil_b_e3: tooLongString,
                mobil_c_e1: tooLongString,
                mobil_c_e2: tooLongString,
                mobil_c_e3: tooLongString,
                nationality_a_e1: tooLongString,
                nationality_b_e1: tooLongString,
                note_a_e1: tooLongString,
                note_b_e1: tooLongString,
                notehost_a_e1: tooLongString,
                notehost_b_e1: tooLongString,
                noteorga_a_e1: tooLongString,
                noteorga_b_e1: tooLongString,
                pobox_a_e1: tooLongString,
                pobox_a_e2: tooLongString,
                pobox_a_e3: tooLongString,
                pobox_b_e1: tooLongString,
                pobox_b_e2: tooLongString,
                pobox_b_e3: tooLongString,
                pobox_c_e1: tooLongString,
                pobox_c_e2: tooLongString,
                pobox_c_e3: tooLongString,
                poboxzipcode_a_e1: tooLongString,
                poboxzipcode_a_e2: tooLongString,
                poboxzipcode_a_e3: tooLongString,
                poboxzipcode_b_e1: tooLongString,
                poboxzipcode_b_e2: tooLongString,
                poboxzipcode_b_e3: tooLongString,
                poboxzipcode_c_e1: tooLongString,
                poboxzipcode_c_e2: tooLongString,
                poboxzipcode_c_e3: tooLongString,
                state_a_e1: tooLongString,
                state_a_e2: tooLongString,
                state_a_e3: tooLongString,
                state_b_e1: tooLongString,
                state_b_e2: tooLongString,
                state_b_e3: tooLongString,
                state_c_e1: tooLongString,
                state_c_e2: tooLongString,
                state_c_e3: tooLongString,
                street_a_e1: tooLongString,
                street_a_e2: tooLongString,
                street_a_e3: tooLongString,
                street_b_e1: tooLongString,
                street_b_e2: tooLongString,
                street_b_e3: tooLongString,
                street_c_e1: tooLongString,
                street_c_e2: tooLongString,
                street_c_e3: tooLongString,
                suffix1_a_e1: tooLongString,
                suffix1_a_e2: tooLongString,
                suffix1_a_e3: tooLongString,
                suffix1_b_e1: tooLongString,
                suffix1_b_e2: tooLongString,
                suffix1_b_e3: tooLongString,
                suffix1_c_e1: tooLongString,
                suffix1_c_e2: tooLongString,
                suffix1_c_e3: tooLongString,
                suffix2_a_e1: tooLongString,
                suffix2_a_e2: tooLongString,
                suffix2_a_e3: tooLongString,
                suffix2_b_e1: tooLongString,
                suffix2_b_e2: tooLongString,
                suffix2_b_e3: tooLongString,
                suffix2_c_e1: tooLongString,
                suffix2_c_e2: tooLongString,
                suffix2_c_e3: tooLongString,
                title_a_e1: tooLongString,
                title_a_e2: tooLongString,
                title_a_e3: tooLongString,
                title_b_e1: tooLongString,
                title_b_e2: tooLongString,
                title_b_e3: tooLongString,
                url_a_e1: tooLongString,
                url_a_e2: tooLongString,
                url_a_e3: tooLongString,
                url_b_e1: tooLongString,
                url_b_e2: tooLongString,
                url_b_e3: tooLongString,
                url_c_e1: tooLongString,
                url_c_e2: tooLongString,
                url_c_e3: tooLongString,
                zipcode_a_e1: tooLongString,
                zipcode_a_e2: tooLongString,
                zipcode_a_e3: tooLongString,
                zipcode_b_e1: tooLongString,
                zipcode_b_e2: tooLongString,
                zipcode_b_e3: tooLongString,
                zipcode_c_e1: tooLongString,
                zipcode_c_e2: tooLongString,
                zipcode_c_e3: tooLongString,
            );

        when:
            violations = this.validator.validate(person);

        then:
            assert violations.isEmpty() == false
            assert violations.size() == 175
    }

}
