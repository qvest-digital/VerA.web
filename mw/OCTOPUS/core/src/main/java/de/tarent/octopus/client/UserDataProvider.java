package de.tarent.octopus.client;
/**
 * Liefert auf Nachfrage die Benutzerdaten,
 * die z.B. über einen Dialog angefordert werden können.
 */
public interface UserDataProvider {

    /**
     * Fordert die Benutzerdaten an (z.B. über einen Login-Dialog);
     *
     * @param message              Nachricht, die einem Benutzer gezeigt werden kann.
     * @param usernamePreselection Vorbelegung des Benutzernamens, darf null sein.
     * @return true, wenn die Daten vorliegen, false sonst (z.B. bei Abbruch des Dialogs).
     */
    public boolean requestUserData(String message, String usernamePreselection);

    /**
     * @return Liefert den bereit gestellten Benutzernamen
     */
    public String getUsername();

    /**
     * @return Liefert das bereit gestellte Passwort
     */
    public String getPassword();
}
