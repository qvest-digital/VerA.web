package de.tarent.veraweb

import de.tarent.veraweb.pages.event.EventEditPage

import de.tarent.veraweb.pages.event.EventsSearchPage
import de.tarent.veraweb.pages.event.EventCreatePage
import de.tarent.veraweb.pages.event.EventOverviewPage
import de.tarent.veraweb.pages.event.EventSearchResultPage

class EventTest extends AbstractUITest {
    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
    }

    def 'search for event'() {
        given:
        String eventname = 'Sommerfest'

        when: 'navigate to event search'
        mainPage.navigationBar.toEventSearch(driver)

        then:
        def eventSearchPage = at EventsSearchPage

        when: 'search event'
        eventSearchPage.searchEvent(eventname)

        then:
        EventSearchResultPage eventSearchResultPage = at EventSearchResultPage
        eventSearchResultPage.table.findRowByName(eventname) != null
    }

    def 'create new event and delete it afterward'() {
        given:
        String eventname = "event-${UUID.randomUUID()}"

        when: 'navigate to event creation'
        mainPage.navigationBar.toEventCreation(driver)

        then:
        EventCreatePage eventCreatPage = at EventCreatePage

        when: 'fill event with data'
        eventCreatPage.form.fillEventData(eventname, 'eventname', 10,
                10, '24.12.2030', 'note')
        eventCreatPage.form.saveEvent()

        then: 'we see the event edit page with the currently created event'
        EventEditPage eventEditPage = at EventEditPage
        eventEditPage.pageTitle.text() == 'Veranstaltung bearbeiten: '+eventname

        when: 'we go back to the event overview'
        eventEditPage.toEventOverview()

        then:
        EventOverviewPage eventOverviewPage = at EventOverviewPage

        when:
        eventOverviewPage.table.selectRowByName(eventname)
        eventOverviewPage.perfomDeletion()

        then:
        at EventOverviewPage
        eventOverviewPage.successMessage() == "Es wurde eine Veranstaltung gelöscht.."
        eventOverviewPage.table.selectRowByName(eventname) == false
    }
}
