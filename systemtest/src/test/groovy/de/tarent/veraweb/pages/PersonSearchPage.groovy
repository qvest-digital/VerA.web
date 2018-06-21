package de.tarent.veraweb.pages

import geb.Page

class PersonSearchPage extends Page {

    static at = {
        pageTitle.text() == 'Personensuche'
    }

    static content = {
        pageTitle { $('h1') }
    }
}
