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

import java.sql.Timestamp;

/**
 * Diese Klasse liefert eine Facade für die Hauptperson-Daten im Latin-Zeichensatz.
 */
class MainLatin extends AbstractMember {
    private Person person;

    public MainLatin(Person person) {
        this.person = person;
    }

    public String getSalutation() {
        return person.salutation_a_e1;
    }

    public Integer getSalutationFK() {
        return person.fk_salutation_a_e1;
    }

    public String getTitle() {
        return person.title_a_e1;
    }

    public String getFirstname() {
        return person.firstname_a_e1;
    }

    public String getLastname() {
        return person.lastname_a_e1;
    }

    @Override
    public String getDomestic() {
        return person.domestic_a_e1;
    }

    @Override
    public String getSex() {
        return person.sex_a_e1;
    }

    @Override
    public Timestamp getBirthday() {
        return person.birthday_a_e1;
    }

    @Override
    public String getBirthplace() {
        return person.birthplace_a_e1;
    }

    @Override
    public Timestamp getDiplodate() {
        return person.diplodate_a_e1;
    }

    @Override
    public String getLanguages() {
        return person.languages_a_e1;
    }

    @Override
    public String getNationality() {
        return person.nationality_a_e1;
    }

    @Override
    public String getNote() {
        return person.note_a_e1;
    }

    @Override
    public String getNoteOrga() {
        return person.noteorga_a_e1;
    }

    @Override
    public String getNoteHost() {
        return person.notehost_a_e1;
    }

    public void setSalutation(String value) {
        person.salutation_a_e1 = value;
    }

    public void setSalutationFK(Integer value) {
        person.fk_salutation_a_e1 = value;
    }

    public void setTitle(String value) {
        person.title_a_e1 = value;
    }

    public void setFirstname(String value) {
        person.firstname_a_e1 = value;
    }

    public void setLastname(String value) {
        person.lastname_a_e1 = value;
    }

    @Override
    public void setDomestic(String value) {
        person.domestic_a_e1 = value;
    }

    @Override
    public void setSex(String value) {
        person.sex_a_e1 = value;
    }

    @Override
    public void setBirthday(Timestamp value) {
        person.birthday_a_e1 = value;
    }

    @Override
    public void setBirthplace(String value) {
        person.birthplace_a_e1 = value;
    }

    @Override
    public void setDiplodate(Timestamp value) {
        person.diplodate_a_e1 = value;
    }

    @Override
    public void setLanguages(String value) {
        person.languages_a_e1 = value;
    }

    @Override
    public void setNationality(String value) {
        person.nationality_a_e1 = value;
    }

    @Override
    public void setNote(String value) {
        person.note_a_e1 = value;
    }

    @Override
    public void setNoteOrga(String value) {
        person.noteorga_a_e1 = value;
    }

    @Override
    public void setNoteHost(String value) {
        person.notehost_a_e1 = value;
    }

    // Für ist Firma
    public String getCompany() {
        return person.company_a_e1;
    }
}
