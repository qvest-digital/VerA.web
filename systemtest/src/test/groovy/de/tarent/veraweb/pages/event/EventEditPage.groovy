package de.tarent.veraweb.pages.event

import de.tarent.veraweb.modules.NavigationBar
import geb.Page

class EventEditPage extends Page {

    static at = {
        pageTitle.text().startsWith('Veranstaltung bearbeiten')
        waitFor {
            navigationBar.isDisplayed()
        }
    }

    static content = {
        pageTitle { $('h1') }
        navigationBar { module NavigationBar }
        contentContainer { $('#content_container')}
        guestListButton {contentContainer.$('input.mainButton')[0]}

        backToOverview {$('input', type: 'reset', name: 'cancel')}
    }

    def toGuestList() {
        def button = guestListButton
        guestListButton.click()
    }

    def toEventOverview() {
        backToOverview.click()
    }
}
