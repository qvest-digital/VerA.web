/**
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.tarent.veraweb.modules.person

import geb.Module
import org.openqa.selenium.By

class PersonForm extends Module {

    static content = {
        mainContent { $('form', name: 'PersonForm') }

        // tabs
        personData { mainContent.find(By.id('tabsMain')).$('ul').$('li')[0] }
        addressData { mainContent.find(By.id('tabsMain')).$('ul').$('li')[1] }
        addressDataCompany { mainContent.$('div.tabsSub')[1].$('ul').$('li')[0].$('div') }
        addressDataPrivate { mainContent.$('div.tabsSub')[1].$('ul').$('li')[1].$('div') }

        // person input fields
        isCompany { mainContent.find(By.id('person-iscompany')) }
        firstName { mainContent.find(By.id('person-firstname_a_e1')) }
        lastName { mainContent.find(By.id('person-lastname_a_e1')) }

        // address input fields
        companyName { mainContent.find(By.id('person-company_a_e1')) }
        companyStreet { mainContent.find(By.id('person-street_a_e1')) }
        companyZipCode { mainContent.find(By.id('person-zipcode_a_e1')) }
        companyCity { mainContent.find(By.id('person-city_a_e1')) }
        companyState { mainContent.find(By.id('person-state_a_e1')) }
        companyCountry { mainContent.find(By.id('person-country_a_e1')) }
        companyPhone { mainContent.find(By.id('person-fon_a_e1')) }
        companyFax { mainContent.find(By.id('person-fax_a_e1')) }
        companyMobile { mainContent.find(By.id('person-mobil_a_e1')) }
        companyEmail { mainContent.find(By.id('person-mail_a_e1')) }
        companyUrl { mainContent.find(By.id('person-url_a_e1')) }

        privateCompanyName  { mainContent.find(By.id('person-company_b_e1')) }
        privateStreet { mainContent.find(By.id('person-street_b_e1')) }
        privateZipCode { mainContent.find(By.id('person-zipcode_b_e1')) }
        privateCity { mainContent.find(By.id('person-city_b_e1')) }
        privateState { mainContent.find(By.id('person-state_b_e1')) }
        privateCountry { mainContent.find(By.id('person-country_b_e1')) }
        privatePhone { mainContent.find(By.id('person-fon_b_e1')) }
        privateFax { mainContent.find(By.id('person-fax_b_e1')) }
        privateMobile { mainContent.find(By.id('person-mobil_b_e1')) }
        privateEmail { mainContent.find(By.id('person-mail_b_e1')) }
        privateUrl { mainContent.find(By.id('person-url_b_e1')) }

        // top buttons
        deleteButton { mainContent.find(By.id('buttonPanel-top')).$('input', name: 'remove')}
        reallyDeleteButton { mainContent.$('div.msg.errormsg.errormsgButton').$('div.floatRight').$('input')[0]}

        // bottom buttons
        submitButton { mainContent.find(By.id('buttonPanel-bottom')).$('input.mainButton')[0] }
    }

    def toPersonData() {
        personData.click()
    }

    def fillPersonData(String firstName, String lastName, boolean isCompany) {
        this.firstName = firstName
        this.lastName = lastName
        if (isCompany) {
            this.isCompany.click()
        }
    }

    def toPrivateAddressData() {
        addressData.click()
        addressDataPrivate.click()
    }

    def fillPrivateAddressData(String company, String street, String zipCode, String city, String state,
                               String country, String phone, String fax, String mobile, String email) {
        toPrivateAddressData()
        privateCompanyName = company
        privateStreet = street
        privateZipCode = zipCode
        privateCity = city
        privateState = state
        privateCountry = country
        privatePhone = phone
        privateFax = fax
        privateMobile = mobile
        privateEmail = email
    }

    def toCompanyAddressData() {
        addressData.click()
        addressDataCompany.click()
    }

    def fillCompanyAddressData(String company, String street, String zipCode, String city, String state,
                               String country, String phone, String fax, String mobile, String email) {
        toCompanyAddressData()
        companyName = company
        companyStreet = street
        companyZipCode = zipCode
        companyCity = city
        companyState = state
        companyCountry = country
        companyPhone = phone
        companyFax = fax
        companyMobile = mobile
        companyEmail = email
    }

    def submit() {
        submitButton.click()
    }
}
