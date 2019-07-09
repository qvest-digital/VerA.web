package de.tarent.veraweb.pages.person

import geb.Page

class PersonSearchReplacePage extends Page {
    static at = {
        pageTitle.text() == "Suchen und Ersetzen in Personendaten"
    }

    static content = {
        pageTitle {$('h1')}
    }
}
