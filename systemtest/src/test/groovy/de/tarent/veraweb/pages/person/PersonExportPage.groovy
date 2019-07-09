package de.tarent.veraweb.pages.person

import geb.Page

class PersonExportPage extends Page {
    static at = {
        pageTitle.text() == "Export von Personendaten"
    }

    static content = {
        pageTitle {$('h1')}
    }
}
