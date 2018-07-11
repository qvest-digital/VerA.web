package de.tarent.veraweb.pages

import de.tarent.veraweb.modules.NavigationBar
import de.tarent.veraweb.modules.PersonForm
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
    }

    def toGuestList() {
        def button = guestListButton
        guestListButton.click()
    }
}
