package de.tarent.veraweb.modules

import geb.Module

class EventTableRow extends Module {

    static content = {
        cell(required: false) { $("td") }
        checkbox(required: false) { cell[0].$('input')}
        id(required: false) {cell[1].text()}
        name(required: false) {cell[2]}
        begin(required: false) {cell[3].text()}
        end(required: false) {cell[4].text()}
        host(required: false) {cell[5].text()}
        location(required: false) {cell[6].text()}
    }
}
