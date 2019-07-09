package de.tarent.veraweb.modules.person

import geb.Module

class PersonTableRow extends Module {

    static content = {
        cell(required: false) { $("td") }
        checkbox(required: false) { cell[0].$('input')}
        id(required: false) {cell[1].text()}
        internalId(required: false) {cell[2].text()}
        lastName(required: false) {cell[4]}
        firstName(required: false) {cell[5].text()}
    }
}
