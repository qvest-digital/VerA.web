package de.tarent.veraweb

import de.tarent.veraweb.pages.EventEditPage
import de.tarent.veraweb.pages.EventListPage
import de.tarent.veraweb.pages.EventsSearchPage
import de.tarent.veraweb.pages.GuestListPage
import de.tarent.veraweb.pages.PersonOverviewPage
import de.tarent.veraweb.pages.PersonSearchPage

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

        when: 'navigate to person search'
        mainPage.navigationBar.toPersonSearch(driver)

        then:
        at PersonSearchPage
    }

    def 'navigate to guest list and select guest'() {

        when: 'navigate to event search'
        mainPage.navigationBar.toEventSearch(driver)

        then:
        def eventSearchPage = at EventsSearchPage

        when: 'search event'
        eventSearchPage.searchEvent('Sommerfest')

        then:
        def eventListPage = at EventListPage

        when: 'click event'
        eventListPage.clickRowByName('Sommerfest')

        then:
        def eventEditPage = at EventEditPage

        when: 'move to guest list'
        eventEditPage.toGuestList()

        then:
        def guestListPage = at GuestListPage

        when: 'select guest'
        def selected = guestListPage.selectRowByName('Mustermann', 'Lotte')

        then:
        selected == true
    }
}
