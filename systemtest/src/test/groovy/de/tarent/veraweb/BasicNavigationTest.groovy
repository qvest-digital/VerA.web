/**
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
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
package de.tarent.veraweb

import de.tarent.veraweb.pages.person.DoubletSearchPage
import de.tarent.veraweb.pages.event.EventsSearchPage
import de.tarent.veraweb.pages.person.PersonCreatePage
import de.tarent.veraweb.pages.person.PersonOverviewPage
import de.tarent.veraweb.pages.person.PersonSearchSimplePage
import de.tarent.veraweb.pages.person.PersonSearchReplacePage
import de.tarent.veraweb.pages.event.EventCreatePage
import de.tarent.veraweb.pages.event.EventOverviewPage
import de.tarent.veraweb.pages.person.PersonExportPage
import de.tarent.veraweb.pages.person.PersonImportPage

class BasicNavigationTest extends AbstractUITest {

    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
    }

    def 'navigate to person overview'() {

        when: 'navigate to person overview'
        mainPage.navigationBar.toPersonOverview(driver)

        then:
        at PersonOverviewPage
    }

    def 'navigate to person search'() {

        when: 'navigate to person search simple'
        mainPage.navigationBar.toPersonSearch(driver)

        then:
        at PersonSearchSimplePage
    }

    def 'navigate to new person'() {
        when: 'navigate to new person'
        mainPage.navigationBar.toPersonCreation(driver)

        then:
        at PersonCreatePage
    }

    def 'navigate to person search and replace'() {
        when: 'navigate to person search and replace'
        mainPage.navigationBar.toPersonSearchReplace(driver)

        then:
        at PersonSearchReplacePage
    }

    def 'navigate to person doublets'() {
        when: 'navigate to person doublets'
        mainPage.navigationBar.toPersonDoublet(driver)

        then:
        at DoubletSearchPage
    }

    def 'navigate to person export'() {
        when: 'navigate to person export'
        mainPage.navigationBar.toPersonExport(driver)

        then:
        at PersonExportPage
    }

    def 'navigate to person import'() {
        when: 'navigate to person import'
        mainPage.navigationBar.toPersonImport(driver)

        then:
        at PersonImportPage
    }

    def 'navigate to event overview'() {
        when: 'navigate to event overview'
        mainPage.navigationBar.toEventOverview(driver)

        then:
        at EventOverviewPage
    }

    def 'navigate to event search'() {
        when: 'navigate to event search'
        mainPage.navigationBar.toEventSearch(driver)

        then:
        at EventsSearchPage
    }

    def 'navigate to event creation'() {
        when: 'navigate to event creation'
        mainPage.navigationBar.toEventCreation(driver)

        then:
        at EventCreatePage
    }
}
