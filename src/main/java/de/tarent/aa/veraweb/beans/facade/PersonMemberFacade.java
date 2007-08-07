package de.tarent.aa.veraweb.beans.facade;

import java.sql.Timestamp;

/**
 * Definiert eine Member-Facade, inkl. Vorname, Nachname, etc.
 * 
 * @author Michael Klink, Christoph Jerolimov
 */
public interface PersonMemberFacade {
	/** @return Anrede */
	public String getSalutation();

	/** @return Anrede-ID */
	public Integer getSalutationFK();

	/** @return Akad. Titel */
	public String getTitle();

	/** @return Vorname */
	public String getFirstname();

	/** @return Nachname */
	public String getLastname();

	/** @return Flag f�r {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public String getDomestic();

	/** @return Flag f�r {@link PersonConstants#SEX_MALE m�nnlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public String getSex();

	/** @return Geburtsdatum */
	public Timestamp getBirthday();

	/** @return Akkretierungsdatum */
	public Timestamp getDiplodate();

	/** @return Sprachen */
	public String getLanguages();

	/** @return Nationalit�t */
	public String getNationality();

	/** @return Bemerkung */
	public String getNote();

	/** @return Bemerkung f�r die Orga */
	public String getNoteOrga();

	/** @return Bemerkung f�r den Gastgeber */
	public String getNoteHost();

	/** �ndert Anrede */
	public void setSalutation(String value);

	/** �ndert Anrede-ID */
	public void setSalutationFK(Integer value);

	/** �ndert Akad. Titel */
	public void setTitle(String value);

	/** �ndert Vorname */
	public void setFirstname(String value);

	/** �ndert Nachname */
	public void setLastname(String value);

	/** �ndert Flag f�r {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland} */
	public void setDomestic(String value);

	/** �ndert Flag f�r {@link PersonConstants#SEX_MALE m�nnlich} oder {@link PersonConstants#SEX_FEMALE weiblich} */
	public void setSex(String value);

	/** �ndert Geburtsdatum */
	public void setBirthday(Timestamp value);

	/** �ndert Akkretierungsdatum */
	public void setDiplodate(Timestamp value);

	/** �ndert Sprachen */
	public void setLanguages(String value);

	/** �ndert Nationalit�t */
	public void setNationality(String value);

	/** �ndert Bemerkung */
	public void setNote(String value);

	/** �ndert Bemerkung f�r die Orga */
	public void setNoteOrga(String value);

	/** �ndert Bemerkung f�r den Gastgeber */
	public void setNoteHost(String value);

	/** Gibt einen zusammengesetzten Namen zur�ck. */
	public String getSaveAs();
}
