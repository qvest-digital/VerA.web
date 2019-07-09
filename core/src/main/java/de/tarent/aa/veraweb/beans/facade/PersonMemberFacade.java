package de.tarent.aa.veraweb.beans.facade;
import java.sql.Timestamp;

/**
 * Definiert eine Member-Facade, inkl. Vorname, Nachname, etc.
 *
 * @author Michael Klink, Christoph Jerolimov
 */
public interface PersonMemberFacade {
    /**
     * @return Anrede
     */
    String getSalutation();

    /**
     * @return Anrede-ID
     */
    Integer getSalutationFK();

    /**
     * @return Akad. Titel
     */
    String getTitle();

    /**
     * @return Vorname
     */
    String getFirstname();

    /**
     * @return Nachname
     */
    String getLastname();

    /**
     * @return Firma
     */
    String getCompany();

    /**
     * @return Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland}
     */
    String getDomestic();

    /**
     * @return Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich}
     */
    String getSex();

    /**
     * @return Geburtsdatum
     */
    Timestamp getBirthday();

    /**
     * @return Geburtsort
     */
    String getBirthplace();

    /**
     * @return Akkretierungsdatum
     */
    Timestamp getDiplodate();

    /**
     * @return Sprachen
     */
    String getLanguages();

    /**
     * @return Nationalität
     */
    String getNationality();

    /**
     * @return Bemerkung
     */
    String getNote();

    /**
     * @return Bemerkung für die Orga
     */
    String getNoteOrga();

    /**
     * @return Bemerkung für den Gastgeber
     */
    String getNoteHost();

    /**
     * Ändert Anrede
     *
     * @param value salutation
     */
    void setSalutation(String value);

    /**
     * Ändert Anrede-ID
     *
     * @param value salutation id
     */
    void setSalutationFK(Integer value);

    /**
     * Ändert Akad. Titel
     *
     * @param value title
     */
    void setTitle(String value);

    /**
     * Ändert Vorname
     *
     * @param value first name
     */
    void setFirstname(String value);

    /**
     * Ändert Nachname
     *
     * @param value last name
     */
    void setLastname(String value);

    /**
     * Ändert Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland}
     *
     * @param value domestic or international
     */
    void setDomestic(String value);

    /**
     * Ändert Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich}
     *
     * @param value gender
     */
    void setSex(String value);

    /**
     * Ändert Geburtsdatum
     *
     * @param value birthday
     */
    void setBirthday(Timestamp value);

    /**
     * aendert Geburtsort
     *
     * @param birthplace birthplace
     */
    void setBirthplace(String birthplace);

    /**
     * Ändert Akkretierungsdatum
     *
     * @param value Akkretierungsdatum
     */
    void setDiplodate(Timestamp value);

    /**
     * Ändert Sprachen
     *
     * @param value language
     */
    void setLanguages(String value);

    /**
     * Ändert Nationalität
     *
     * @param value nationality
     */
    void setNationality(String value);

    /**
     * Ändert Bemerkung
     *
     * @param value note
     */
    void setNote(String value);

    /**
     * Ändert Bemerkung für die Orga
     *
     * @param value orga
     */
    void setNoteOrga(String value);

    /**
     * Ändert Bemerkung für den Gastgeber
     *
     * @param value note host
     */
    void setNoteHost(String value);

    /**
     * Gibt einen zusammengesetzten Namen zurück.
     *
     * @return combined name
     */
    String getSaveAs();
}
