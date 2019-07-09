package de.tarent.veraweb.modules.guest

import geb.Module

class AddGuestTableRow extends Module {
    static content = {
        cell(required: false) { $("td") }
        mainPersonCheckbox(required: false) { cell[0].$('input')}
        id(required: false) {cell[4]}
        name(required: false) {cell[5]}
        firstName(required: false) {cell[6]}
    }
}
