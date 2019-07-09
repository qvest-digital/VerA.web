package de.tarent.veraweb.pages.event

import geb.Page
import org.openqa.selenium.By

class EventsSearchPage extends Page {

    static at = {
        pageTitle.text() == 'Veranstaltungssuche'
    }

    static content = {
        pageTitle { $('h1') }
        contentContainer { $('#content_container')}
        searchFieldShortName { contentContainer.find(By.id('input.shortname'))}
        searchButton { contentContainer.find(By.id('button.startSearch'))}
    }

    def searchEvent(String query) {
        searchFieldShortName = query
        searchButton.click()
    }
}
