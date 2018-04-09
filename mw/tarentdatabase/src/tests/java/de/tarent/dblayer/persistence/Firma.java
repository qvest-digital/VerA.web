/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.dblayer.persistence;

import java.util.*;

public class Firma {

    int turnover;
    String name;
    int id;

    List mitarbeiter;

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public int getTurnover() {
        return turnover;
    }

    public void setTurnover(int newTurnover) {
        this.turnover = newTurnover;
    }

    public List getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(List newMitarbeiter) {
        this.mitarbeiter = newMitarbeiter;
    }

    public void addMitarbeiter(Person person) {
        if (mitarbeiter == null)
            mitarbeiter = new ArrayList();
        mitarbeiter.add(person);
    }
}
