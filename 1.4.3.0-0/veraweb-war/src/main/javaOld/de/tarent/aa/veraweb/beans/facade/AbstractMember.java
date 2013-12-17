/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
package de.tarent.aa.veraweb.beans.facade;

import java.sql.Timestamp;

/**
 * Implementiert die Basis der Grundfunktionalit�t einer MemberFacade.
 * 
 * @author Michael Klink, Christoph Jerolimov
 */
public abstract class AbstractMember implements PersonMemberFacade {
    /** @return Flag f�r {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public String getDomestic() {
		return null;
	}

    /** @return Flag f�r {@link PersonConstants#SEX_MALE m�nnlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public String getSex() {
		return null;
	}

    /** @return Geburtsdatum */
	public Timestamp getBirthday() {
		return null;
	}

	/** @return Geburtsort */
	public String getBirthplace()
	{
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

    /** @return Nationalit�t */
	public String getNationality() {
		return null;
	}

    /** @return Bemerkung */
	public String getNote() {
		return null;
	}

    /** @return Bemerkung f�r die Orga */
	public String getNoteOrga() {
		return null;
	}

    /** @return Bemerkung f�r den Gastgeber */
	public String getNoteHost() {
		return null;
	}

    /** �ndert Flag f�r {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public void setDomestic(String value) {
	}

    /** �ndert Flag f�r {@link PersonConstants#SEX_MALE m�nnlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public void setSex(String value) {
	}

    /** �ndert Geburtsdatum */
	public void setBirthday(Timestamp value) {
	}

	/** @return Geburtsort */
	public void setBirthplace(String value)
	{
	}

	/** �ndert Akkretierungsdatum */
	public void setDiplodate(Timestamp value) {
	}

    /** �ndert Sprachen */
	public void setLanguages(String value) {
	}

    /** �ndert Nationalit�t */
	public void setNationality(String value) {
	}

    /** �ndert Bemerkung */
	public void setNote(String value) {
	}

    /** �ndert Bemerkung f�r die Orga */
	public void setNoteOrga(String value) {
	}

    /** �ndert Bemerkung f�r den Gastgeber */
	public void setNoteHost(String value) {
	}

    /** Gibt einen zusammengesetzten Namen zur�ck. */
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
