package de.tarent.veraweb.pages.event

import de.tarent.veraweb.modules.event.EventTable
import geb.Page
import org.openqa.selenium.By

class EventOverviewPage extends Page {
    static at = {
        pageTitle.text() == "Veranstaltungsübersicht"
    }

    static content = {
        pageTitle {$('h1')}

        form {$('form#formlist')}

        table {module EventTable}

        remove {form.find(By.id('button.remove'))}

        reallyDeleteButton { browser.$('div.msg.errormsg.errormsgButton').$('div.floatRight').$('input')[0]}
        successMessage { browser.$('div.msg.successmsg').$('span') }
    }

    def perfomDeletion() {
        remove.click()
        waitFor {
            reallyDeleteButton.displayed
        }
        reallyDeleteButton.click()
    }

    def successMessage() {
        waitFor {
            successMessage.displayed
        }
        successMessage.text()
    }
}
