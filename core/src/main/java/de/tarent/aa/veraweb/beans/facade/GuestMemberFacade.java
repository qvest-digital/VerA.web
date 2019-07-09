package de.tarent.aa.veraweb.beans.facade;
/**
 * Definiert eine Member-Facade für einen Gast
 *
 * @author Michael Klink, Christoph Jerolimov
 */
public interface GuestMemberFacade extends EventConstants {
    /**
     * @return Einladungstyp
     */
    Integer getInvitationType();

    /**
     * @return Einlatungsstatus
     */
    Integer getInvitationStatus();

    /**
     * @return Tisch-Nummer
     */
    Integer getTableNo();

    /**
     * @return Sitz-Nummer
     */
    Integer getSeatNo();

    /**
     * @return Laufende Nummer
     */
    Integer getOrderNo();

    /**
     * @return Bemerkung für die Orga
     */
    String getNoteOrga();

    /**
     * @return Bemerkung für den Gastgeber
     */
    String getNoteHost();

    /**
     * @return Sprachen
     */
    String getLanguages();

    /**
     * @return Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich}
     */
    String getSex();

    /**
     * @return Nationalität
     */
    String getNationality();

    /**
     * @return Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland}
     */
    String getDomestic();

    /**
     * Ändert Einladungstyp
     *
     * @param value invitationType
     */
    void setInvitationType(Integer value);

    /**
     * Ändert Einlatungsstatus
     *
     * @param value invitationStatus
     */
    void setInvitationStatus(Integer value);

    /**
     * Ändert Tisch-Nummer
     *
     * @param value tableNumber
     */
    void setTableNo(Integer value);

    /**
     * Ändert Sitz-Nummer
     *
     * @param value seatNumber
     */
    void setSeatNo(Integer value);

    /**
     * Ändert Laufende Nummer
     *
     * @param value orderNumber
     */
    void setOrderNo(Integer value);

    /**
     * Ändert Bemerkung für die Orga
     *
     * @param value orgaNote
     */
    void setNoteOrga(String value);

    /**
     * Ändert Bemerkung für den Gastgeber
     *
     * @param value noteHost
     */
    void setNoteHost(String value);

    /**
     * Ändert Sprachen
     *
     * @param value language
     */
    void setLanguages(String value);

    /**
     * Ändert Flag für {@link PersonConstants#SEX_MALE männlich} oder {@link PersonConstants#SEX_FEMALE weiblich}
     *
     * @param value sex
     */
    void setSex(String value);

    /**
     * Ändert Nationalität
     *
     * @param value nationality
     */
    void setNationality(String value);

    /**
     * Ändert Flag für {@link PersonConstants#DOMESTIC_INLAND Inland} oder {@link PersonConstants#DOMESTIC_AUSLAND Ausland}
     *
     * @param value domestic
     */
    void setDomestic(String value);

    String getImageUuid();

    void setImageUuid(String value);
}
