package de.tarent.veraweb.pages

import geb.Page
import org.openqa.selenium.By

class PersonSearchPage extends Page {

    static at = {
        pageTitle.text() == 'Personensuche'
    }

    static content = {
        pageTitle { $('h1') }
        contentContainer { $('#content_container')}
        searchField { contentContainer.find(By.id('searchfield'))}
        searchButton { contentContainer.find('input.mainButton.submit')[0]}
    }

    def searchPerson(String query) {
        searchField = query
        searchButton.click()
    }
}
