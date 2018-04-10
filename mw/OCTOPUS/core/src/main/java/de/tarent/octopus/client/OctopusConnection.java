package de.tarent.octopus.client;
import java.util.Map;

/**
 * Schnittstelle der Verbindung zu einem Octopus.
 * Dabei wird davon abstrahiert, ob es sich um eine lokale, oder eine entfernte Instanz handelt.
 *
 * Eine OctopusConnection wird von der Factory bereits vorkonfiguriert 'geliefert'.
 * Sie ist als Verbindung zu einem Octopus-Modul zu verstehen. Bei verwendung unterschiedlicher
 * Module müssen mehrere OctopusConnections verwendet werden.
 *
 * TODO: Mögliche Erweiterungen: startSession(), getCallableTasks(), getModuleVersion()
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusConnection {

    /**
     * Liefert ein CallObject, dass für den Aufruf dieses Task verwendet werden kann.
     */
    public OctopusTask getTask(String taskName)
        throws OctopusCallException;

    /**
     * Aufruf eines Task mit seinen Parametern als Map.
     * Kurzform für getCallObject(taskName).add(k1,v1).add(..,..).invoke()
     */
    public OctopusResult callTask(String taskName, Map paramMap)
        throws OctopusCallException;

    /**
     * Setzen des Passwortes, mit dem Authentifiziert werden soll.
     */
    public void setPassword(String newPassword);

    /**
     * Setzen des  Benutzernamens, mit dem Authentifiziert werden soll.
     */
    public void setUsername(String newUsername);

    /**
     * Stößt das Authentifizieren der Verbindung an
     */
    public void login() throws OctopusCallException;

    /**
     * Beendet die Session
     */
    public void logout() throws OctopusCallException;

    /**
     * Alternative zum Setzen der Benutzerdaten: Diese werden bei einem Login vom
     * UserDataProvider erfragt.
     *
     * Connections, die keine Authentifizierung unterstützen,
     * können diesen Provider ignorieren.
     */
    public void setUserDataProvider(UserDataProvider provider);
}
