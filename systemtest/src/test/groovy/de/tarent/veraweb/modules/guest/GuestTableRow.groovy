package de.tarent.veraweb.modules.guest

import geb.Module

class GuestTableRow extends Module {

    static content = {
        cell(required: false) { $("td") }
        checkbox(required: false) { cell[0].$('input')[2]}
        name(required: false) {cell[4]}
        firstName(required: false) {cell[5]}
        mainPerson(required: false) {cell[10]}
        partner(required: false) {cell[11]}
    }
}
