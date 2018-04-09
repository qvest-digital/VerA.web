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

import java.util.Date;

public class Person {

    String givenName;
    String lastName;
    Date birthday;
    int firmaFk;
    Firma firma;

    int id;

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public int getFirmaFk() {
        return firmaFk;
    }

    public void setFirmaFk(int newFirmaFk) {
        this.firmaFk = newFirmaFk;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date newBirthday) {
        this.birthday = newBirthday;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String newGivenName) {
        this.givenName = newGivenName;

   }

    public Firma getFirma() {
        return firma;
    }

    public void setFirma(Firma newFirma) {
        this.firma = newFirma;
    }
}
