package de.tarent.octopus.client;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
