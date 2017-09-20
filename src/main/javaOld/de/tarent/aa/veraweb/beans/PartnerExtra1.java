package de.tarent.aa.veraweb.beans;

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
import de.tarent.aa.veraweb.beans.facade.AbstractMember;

/**
 * Diese Klasse liefert eine Facade für die Partner-Daten im Zusatzzeichensatz 1.
 */
class PartnerExtra1 extends AbstractMember {
    private Person person;

    public PartnerExtra1(Person person) {
        this.person = person;
    }

    public String getSalutation() {
        return person.salutation_b_e2;
    }

    public Integer getSalutationFK() {
        return person.fk_salutation_b_e2;
    }

    @Override
    public String getBirthplace() {
        throw new RuntimeException("Not implemented.");
        //return birthplace_b_e2;
    }

    public String getTitle() {
        return person.title_b_e2;
    }

    public String getFirstname() {
        return person.firstname_b_e2;
    }

    public String getLastname() {
        return person.lastname_b_e2;
    }

    public void setSalutation(String value) {
        person.salutation_b_e2 = value;
    }

    public void setSalutationFK(Integer value) {
        person.fk_salutation_b_e2 = value;
    }

    @Override
    public void setBirthplace(String value) {
        throw new RuntimeException("Not implemented.");
        //birthplace_b_e2 = value;
    }

    public void setTitle(String value) {
        person.title_b_e2 = value;
    }

    public void setFirstname(String value) {
        person.firstname_b_e2 = value;
    }

    public void setLastname(String value) {
        person.lastname_b_e2 = value;
    }

    // Für ist Firma
    public String getCompany() {
        return person.company_b_e2;
    }
}
