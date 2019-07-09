package de.tarent.veraweb.pages.event

import de.tarent.veraweb.modules.event.EventTable
import geb.Page

class EventSearchResultPage extends Page {
    static at = {
        pageTitle.text() == 'Suchergebnis: Veranstaltung(en)'
    }

    static content = {
        pageTitle {$('h1')}

        form {$('form#formlist')}

        table {module EventTable}
    }
}
