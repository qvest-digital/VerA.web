package de.tarent.veraweb.pages.person

import geb.Page

class PersonImportPage extends Page {
    static at = {
        pageTitle.text() == "Import von Personendaten"
    }

    static content = {
        pageTitle {$('h1')}
    }
}
