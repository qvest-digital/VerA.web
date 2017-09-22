package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
/**
 * Types for an optional field.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public enum OptionalFieldTypeFacade {
    inputfield("Eingabefeld", 1), simple_combobox("Einfaches Auswahlfeld", 2), multiple_combobox("Mehrfaches Auswahlfeld", 3);

    private String text;

    private Integer value;

    /**
     * TODO
     *
     * @param text Field type content
     */
    OptionalFieldTypeFacade(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
}
