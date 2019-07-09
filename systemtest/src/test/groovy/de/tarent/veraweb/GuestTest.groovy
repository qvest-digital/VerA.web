package de.tarent.veraweb

import de.tarent.veraweb.pages.guest.AddGuestPage
import de.tarent.veraweb.pages.guest.GuestListPage
import de.tarent.veraweb.pages.event.EventEditPage
import de.tarent.veraweb.pages.event.EventOverviewPage
import de.tarent.veraweb.pages.person.PersonSearchPage

class GuestTest extends AbstractUITest {
    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
    }

    def 'add person to event'() {
        given:
        mainPage.navigationBar.toEventOverview(driver)
        EventOverviewPage eventOverviewPage = at EventOverviewPage
        eventOverviewPage.table.clickRowByName("Sommerfest")
        EventEditPage eventEditPage = at EventEditPage

        when:
        eventEditPage.toGuestList()

        then:
        GuestListPage guestListPage = at GuestListPage

        when:
        guestListPage.addGuest()

        then:
        PersonSearchPage personSearchPage = at PersonSearchPage

        when:
        personSearchPage.searchPersonByName("Max", 'Mustermann')

        then:
        AddGuestPage addGuestPage = at AddGuestPage

        when:
        addGuestPage.selectRowByName('Max', 'Mustermann')
        addGuestPage.addSelectedPerson()

        then:
        GuestListPage guestListPageNew = at GuestListPage
        guestListPageNew.selectRowByName('Mustermann', 'Max') == true
    }
}
