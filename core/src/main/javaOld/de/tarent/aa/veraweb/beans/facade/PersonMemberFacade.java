package de.tarent.aa.veraweb.beans.facade;

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
import java.sql.Timestamp;

/**
 * Definiert eine Member-Facade, inkl. Vorname, Nachname, etc.
 *
 * @author Michael Klink, Christoph Jerolimov
 */
public interface PersonMemberFacade {
	/** @return Anrede */
	String getSalutation();

	/** @return Anrede-ID */
	Integer getSalutationFK();

	/** @return Akad. Titel */
	String getTitle();

	/** @return Vorname */
	String getFirstname();

	/** @return Nachname */
	String getLastname();

	/** @return Firma */
	String getCompany();

	/** @return Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	String getDomestic();

	/** @return Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	String getSex();

	/** @return Geburtsdatum */
	Timestamp getBirthday();

	/** @return Geburtsort */
	String getBirthplace();

	/** @return Akkretierungsdatum */
	Timestamp getDiplodate();

	/** @return Sprachen */
	String getLanguages();

	/** @return Nationalität */
	String getNationality();

	/** @return Bemerkung */
	String getNote();

	/** @return Bemerkung für die Orga */
	String getNoteOrga();

	/** @return Bemerkung für den Gastgeber */
	String getNoteHost();

	/** Ändert Anrede */
	void setSalutation(String value);

	/** Ändert Anrede-ID */
	void setSalutationFK(Integer value);

	/** Ändert Akad. Titel */
	void setTitle(String value);

	/** Ändert Vorname */
	void setFirstname(String value);

	/** Ändert Nachname */
	void setLastname(String value);


	/** Ändert Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	void setDomestic(String value);

	/** Ändert Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	void setSex(String value);

	/** Ändert Geburtsdatum */
	void setBirthday(Timestamp value);

	/** aendert Geburtsort */
	void setBirthplace(String birthplace);

	/** Ändert Akkretierungsdatum */
	void setDiplodate(Timestamp value);

	/** Ändert Sprachen */
	void setLanguages(String value);

	/** Ändert Nationalität */
	void setNationality(String value);

	/** Ändert Bemerkung */
	void setNote(String value);

	/** Ändert Bemerkung für die Orga */
	void setNoteOrga(String value);

	/** Ändert Bemerkung für den Gastgeber */
	void setNoteHost(String value);

	/** Gibt einen zusammengesetzten Namen zurück. */
	String getSaveAs();


}
