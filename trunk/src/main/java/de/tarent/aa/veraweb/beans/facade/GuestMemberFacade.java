package de.tarent.aa.veraweb.beans.facade;

/**
 * Definiert eine Member-Facade für einen Gast
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

	/** @return Bemerkung für die Orga */
	public String getNoteOrga();

	/** @return Bemerkung für den Gastgeber */
	public String getNoteHost();

	/** @return Sprachen */
	public String getLanguages();

	/** @return Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public String getSex();

	/** @return Nationalität */
	public String getNationality();

	/** @return Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public String getDomestic();

	/** @return Farbe */
	public String getColor();

	/** @return Farbe */
	public Integer getColorFK();

	/** Ändert Einladungstyp */
	public void setInvitationType(Integer value);

	/** Ändert Einlatungsstatus */
	public void setInvitationStatus(Integer value);

	/** Ändert Tisch-Nummer */
	public void setTableNo(Integer value);

	/** Ändert Sitz-Nummer */
	public void setSeatNo(Integer value);

	/** Ändert Laufende Nummer */
	public void setOrderNo(Integer value);

	/** Ändert Bemerkung für die Orga */
	public void setNoteOrga(String value);

	/** Ändert Bemerkung für den Gastgeber */
	public void setNoteHost(String value);

	/** Ändert Sprachen */
	public void setLanguages(String value);

	/** Ändert Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public void setSex(String value);

	/** Ändert Nationalität */
	public void setNationality(String value);

	/** Ändert Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public void setDomestic(String value);

	/** Ändert Farbe */
	public void setColor(String value);

	/** Ändert Farbe */
	public void setColorFK(Integer value);
}
