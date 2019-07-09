package de.tarent.veraweb.pages.event

import de.tarent.veraweb.modules.event.EventCreateForm
import geb.Page

class EventCreatePage extends Page {
    static at = {
        pageTitle.text() == 'Neue Veranstaltung anlegen'
    }

    static content = {
        pageTitle {$('h1')}

        form {module EventCreateForm}
    }
}
