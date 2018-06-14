package de.tarent.veraweb.pages

import geb.Page

class PersonOverviewPage extends Page {

    static at = {
        pageTitle.text() == 'Personenübersicht'
    }

    static content = {
        pageTitle { $('h1') }
    }
}
