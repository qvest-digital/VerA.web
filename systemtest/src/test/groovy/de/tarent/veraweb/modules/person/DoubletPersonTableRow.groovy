package de.tarent.veraweb.modules.person

import geb.Module

class DoubletPersonTableRow extends Module {
    static content = {
        cell(required: false) { $("td") }
        checkbox(required: false) { cell[0].$('input')}
        id(required: false) {cell[1].text()}
        internalId(required: false) {cell[2].text()}
        date(required: false) {cell[3].text()}
        workarea(required: false) {cell[4].text()}
        lastName(required: false) {cell[5]}
        firstName(required: false) {cell[6].text()}
    }
}
