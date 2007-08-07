/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
 * Copyright (c) 2005-2007 tarent GmbH
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.aa.veraweb.beans.facade;

import java.sql.Timestamp;

/**
 * Implementiert die Basis der Grundfunktionalität einer MemberFacade.
 * 
 * @author Michael Klink, Christoph Jerolimov
 */
public abstract class AbstractMember implements PersonMemberFacade {
    /** @return Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public String getDomestic() {
		return null;
	}

    /** @return Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public String getSex() {
		return null;
	}

    /** @return Geburtsdatum */
	public Timestamp getBirthday() {
		return null;
	}

    /** @return Akkretierungsdatum */
	public Timestamp getDiplodate() {
		return null;
	}

    /** @return Sprachen */
	public String getLanguages() {
		return null;
	}

    /** @return Nationalität */
	public String getNationality() {
		return null;
	}

    /** @return Bemerkung */
	public String getNote() {
		return null;
	}

    /** @return Bemerkung für die Orga */
	public String getNoteOrga() {
		return null;
	}

    /** @return Bemerkung für den Gastgeber */
	public String getNoteHost() {
		return null;
	}

    /** Ändert Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public void setDomestic(String value) {
	}

    /** Ändert Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public void setSex(String value) {
	}

    /** Ändert Geburtsdatum */
	public void setBirthday(Timestamp value) {
	}

    /** Ändert Akkretierungsdatum */
	public void setDiplodate(Timestamp value) {
	}

    /** Ändert Sprachen */
	public void setLanguages(String value) {
	}

    /** Ändert Nationalität */
	public void setNationality(String value) {
	}

    /** Ändert Bemerkung */
	public void setNote(String value) {
	}

    /** Ändert Bemerkung für die Orga */
	public void setNoteOrga(String value) {
	}

    /** Ändert Bemerkung für den Gastgeber */
	public void setNoteHost(String value) {
	}

    /** Gibt einen zusammengesetzten Namen zurück. */
	public String getSaveAs() {
		StringBuffer buffer = new StringBuffer();
		if (getLastname() != null)
			buffer.append(getLastname());
		if (getLastname() != null && getLastname().length() != 0 && getFirstname() !=  null && getFirstname().length() != 0)
			buffer.append(", ");
		if (getFirstname() != null)
			buffer.append(getFirstname());
		return buffer.toString();
	}
}
