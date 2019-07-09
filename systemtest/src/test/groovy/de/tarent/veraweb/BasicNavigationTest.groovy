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
