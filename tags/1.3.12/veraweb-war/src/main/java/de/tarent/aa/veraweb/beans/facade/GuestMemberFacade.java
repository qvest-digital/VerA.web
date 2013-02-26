/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans.facade;

/**
 * Definiert eine Member-Facade f�r einen Gast
 * 
 * @author Michael Klink, Christoph Jerolimov
 */
public interface GuestMemberFacade extends EventConstants {
	/** @return Einladungstyp */
	public Integer getInvitationType();

	/** @return Einlatungsstatus */
	public Integer getInvitationStatus();

	/** @return Tisch-Nummer */
	public Integer getTableNo();

	/** @return Sitz-Nummer */
	public Integer getSeatNo();

	/** @return Laufende Nummer */
	public Integer getOrderNo();

	/** @return Bemerkung f�r die Orga */
	public String getNoteOrga();

	/** @return Bemerkung f�r den Gastgeber */
	public String getNoteHost();

	/** @return Sprachen */
	public String getLanguages();

	/** @return Flag f�r {@link PersonConstants#SEX_MALE m�nnlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public String getSex();

	/** @return Nationalit�t */
	public String getNationality();

	/** @return Flag f�r {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public String getDomestic();

	/** @return Farbe */
	public String getColor();

	/** @return Farbe */
	public Integer getColorFK();

	/** �ndert Einladungstyp */
	public void setInvitationType(Integer value);

	/** �ndert Einlatungsstatus */
	public void setInvitationStatus(Integer value);

	/** �ndert Tisch-Nummer */
	public void setTableNo(Integer value);

	/** �ndert Sitz-Nummer */
	public void setSeatNo(Integer value);

	/** �ndert Laufende Nummer */
	public void setOrderNo(Integer value);

	/** �ndert Bemerkung f�r die Orga */
	public void setNoteOrga(String value);

	/** �ndert Bemerkung f�r den Gastgeber */
	public void setNoteHost(String value);

	/** �ndert Sprachen */
	public void setLanguages(String value);

	/** �ndert Flag f�r {@link PersonConstants#SEX_MALE m�nnlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public void setSex(String value);

	/** �ndert Nationalit�t */
	public void setNationality(String value);

	/** �ndert Flag f�r {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public void setDomestic(String value);

	/** �ndert Farbe */
	public void setColor(String value);

	/** �ndert Farbe */
	public void setColorFK(Integer value);
}
