/**
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
package de.tarent.aa.veraweb.beans.facade;

/**
 * Definiert eine Member-Facade für einen Gast
 *
 * @author Michael Klink, Christoph Jerolimov
 */
public interface GuestMemberFacade extends EventConstants {
	/** @return Einladungstyp */
	Integer getInvitationType();

	/** @return Einlatungsstatus */
	Integer getInvitationStatus();

	/** @return Tisch-Nummer */
	Integer getTableNo();

	/** @return Sitz-Nummer */
	Integer getSeatNo();

	/** @return Laufende Nummer */
	Integer getOrderNo();

	/** @return Bemerkung für die Orga */
	String getNoteOrga();

	/** @return Bemerkung für den Gastgeber */
	String getNoteHost();

	/** @return Sprachen */
	String getLanguages();

	/** @return Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	String getSex();

	/** @return Nationalität */
	String getNationality();

	/** @return Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	String getDomestic();

	/** Ändert Einladungstyp */
	void setInvitationType(Integer value);

	/** Ändert Einlatungsstatus */
	void setInvitationStatus(Integer value);

	/** Ändert Tisch-Nummer */
	void setTableNo(Integer value);

	/** Ändert Sitz-Nummer */
	void setSeatNo(Integer value);

	/** Ändert Laufende Nummer */
	void setOrderNo(Integer value);

	/** Ändert Bemerkung für die Orga */
	void setNoteOrga(String value);

	/** Ändert Bemerkung für den Gastgeber */
	void setNoteHost(String value);

	/** Ändert Sprachen */
	void setLanguages(String value);

	/** Ändert Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	void setSex(String value);

	/** Ändert Nationalität */
	void setNationality(String value);

	/** Ändert Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	void setDomestic(String value);

	String getImageUuid();

	void setImageUuid(String value);
}
