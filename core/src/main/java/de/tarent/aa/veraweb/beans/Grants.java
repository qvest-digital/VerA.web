package de.tarent.aa.veraweb.beans;
/**
 * Diese Bohne stellt die Berechtigungen eines Nutzers dar
 *
 * @author Christoph
 * @author mikel
 */
public interface Grants {
    /**
     * Dieses Attribut gibt an, ob der Benutzer authentisiert ist, wenn
     * er sich also gegenüber dem LDAP-Server anmelden konnte. Dies impliziert
     * noch keine Rechte im VerA.web-Kontext.
     *
     * @return <code>true</code> wenn der Benutzer angemeldet ist.
     */
    boolean isAuthenticated();

    /**
     * Dieses Attribut gibt an, ob der Benutzer ein VerA.web-User ist, wenn
     * es zu seiner Anmeldungsrolle also einen VerA.web-Benutzereintrag gibt.
     * Dies impliziert noch keine Rechte auf Personen, Veranstaltungen,
     * Stamm- oder Konfigurationsdaten.
     *
     * @return <code>true</code> wenn der Benutzer VerA.web-User ist.
     */
    boolean isUser();

    /**
     * Dieses Attribut gibt an, ob der Benutzer die nicht beschränkten
     * Felder lesen darf, also alles außer Bemerkungen.
     *
     * @return <code>true</code> wenn der Benutzer nicht beschränkte Felder lesen darf.
     */
    boolean mayReadStandardFields();

    /**
     * Dieses Attribut gibt an, ob der Benutzer die beschränkten Felder lesen
     * darf, also die Bemerkungen.
     *
     * @return <code>true</code> wenn der Benutzer beschränkte Felder lesen darf.
     */
    boolean mayReadRemarkFields();

    /**
     * Dieses Attribut gibt an, ob der Benutzer exportieren darf. Hierbei ist
     * zusätzlich mit {@link #mayReadStandardFields()} und {@link #mayReadRemarkFields()}
     * zu ermitteln, welche Feldinhalte exportiert werden dürfen.
     *
     * @return <code>true</code> wenn der Benutzer exportieren darf.
     */
    boolean mayExport();

    /**
     * Dieses Attribut gibt an, ob der Benutzer Daten ändern darf. Hierbei ist mit
     * {@link #mayReadStandardFields()} und {@link #mayReadRemarkFields()} zu ermitteln,
     * auf welche Felder der Benutzer Zugriff hat.
     *
     * @return <code>true</code> wenn der Benutzer schreiben darf.
     */
    boolean mayWrite();

    /**
     * Dieses Attribut gibt an, ob der Benutzer Teiladmin ist, ob er also innerhalb
     * seines Mandanten konfigurieren darf
     *
     * @return <code>true</code> wenn der Benutzer Teiladmin ist.
     */
    boolean isPartialAdmin();

    /**
     * Dieses Attribut gibt an, ob der Benutzer Volladmin ist, ob er also global
     * konfigurieren darf.
     *
     * @return <code>true</code>, wenn der Benutzer Volladmin ist.
     */
    boolean isAdmin();

    /**
     * Dieses Attribut gibt an, ob der Benutzer dem SystemUser entspricht.
     * Dieser Nutzer hat nur eingeschränkten Zugriff auf die Applikation und ist
     * in erster Linie dafür verantwortlich, während einer VerA.web Installation
     * die Benutzer für die Applikation einzurichten.
     *
     * @return <code>true</code>, wenn der Benutzer der SystemUser ist.
     */
    boolean isSystemUser();
}
