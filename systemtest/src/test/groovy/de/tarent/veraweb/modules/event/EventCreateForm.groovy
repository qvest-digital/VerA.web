package de.tarent.veraweb.modules.event

import geb.Module
import org.openqa.selenium.By

class EventCreateForm extends Module {
    static content = {
        form {$('form', id: 'formlist')}

        shortname {form.find(By.id('event-shortname'))}
        eventname {form.find(By.id('event-eventname'))}
        // TODO Einladungsart
        maxGuests {form.find(By.id('event-maxguest'))}
        maxReserve {form.find(By.id('event-maxreserve'))}
        //location {form.find(By.id('event-location'))}
        eventBegin {form.find(By.id('event-begin'))}
        // TODO Restliche Felder
        note {form.find(By.id('event-note'))}

        saveButton {$('input', type: 'submit', name: 'save')}
        cancelButton {$('input', type: 'button', name: 'cancel')}
    }

    def saveEvent() {
        saveButton.click()
    }

    def toEventOverview() {
        cancelButton.click()
    }

    def fillEventData(String shortname, String eventname, int maxGuests, int maxReserve, String eventBegin, String note) {
        this.shortname = shortname
        this.eventname = eventname
        this.maxGuests = maxGuests
        this.maxReserve = maxReserve
        this.eventBegin = eventBegin
        this.note = note
    }
}
