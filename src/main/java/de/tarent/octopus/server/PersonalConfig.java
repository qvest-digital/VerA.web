package de.tarent.octopus.server;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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
import de.tarent.octopus.request.*;
import de.tarent.octopus.config.*;
import de.tarent.octopus.security.*;
import java.util.Iterator;

/**
 * Schnittstelle zur Bereitstellung von Informationen und Einstellungen eines Benutzers.
 * Eine PersonalConfig ist an eine Session des Benutzers an einem Octopus Modul gebunden.
 * PersonalConfigs werden von LoginManagern verwaltet. Es kann unterschiedliche
 * Implementierungen geben, die die Grundfunktionalität erweitern.
 * <br><br>
 *
 * Eine PersonalConfig soll Modulweit im Octopus sichtbar sein. Damit bekommt jedes Modul
 * einen eigenen Scope in dem Benutzerinformationen und Sessiondaten liegen. *
 * <br><br>
 *
 * Ein Benutzer kann in verschiedenen Bereichen Zugehörigkeiten zu Gruppen haben.
 * Ein Bereich wird über einen String-Bezeichner angegeben. Er schließt all die Bereiche (Unterbereiche) ein,
 * von deren Bezeichner er Prefix ist.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface PersonalConfig {

    /**
     * Teilt der PersonalConfig mit, wenn sich ein Benutzer authentifiziert hat.
     * Diese kann damit abhängig von ihrer Implementierung die Benutzerdaten laden.
     *
     */
    public void userLoggedIn(String userName);

    /**
     * Teilt der PersonalConfig mit, wenn sich ein Benutzer ausgeloggt hat.
     */
    public void userLoggedOut();

    /**
     * Gruppe für Administratoren eines Moduls
     */
    public static final String GROUP_ADMINISTRATOR = "Administrator";

    /**
     * Std. Gruppe für gewöhnliche User
     */
    public static final String GROUP_USER = "User";

    /**
     * Gruppe, in der sich ein User befindet, wenn er sich nicht authentifiziert hat.
     */
    public static final String GROUP_ANONYMOUS = "Anonymous";

    /**
     * Gruppe, in der Sich ein Benutzer befindet, der einmal Authentifiziert war,
     * sich aber ausgeloggt hat.
     */
    public static final String GROUP_LOGGED_OUT = "LoggedOut";

    /**
     * Speichert einen Wert innrhalb der aktuellen Session
     */
    public void setSessionValue(String key, Object value);

    /**
     * Liefert einen Wert innerhalb der aktuellen Session
     */
    public Object getSessionValue(String key);

    /**
     * Liefert die Keys der aktuellen Session
     */
    public Iterator getSessionKeys(String key);

    /**
     * Gets the user preference by key
     */
    public String getUserPreference(String key);

    /**
     * Sets the user preference key to the given value
     */
    public void setUserPreference(String key, String value);

    /**
     * Liefert den Login Namen
     */
    public String getUserLogin();

    public void setUserLogin(String login);

    /**
     * Liefert die User ID
     */
    public Integer getUserID();
    public void setUserID(Integer id);

    /**
     * Liefert eine Liste aller Gruppen, die einem Benutzer
     * für den Moludweit globalen Bereich zugeordnet sind.
     */
    public String[] getUserGroups();

    /**
     * Setzt eine Liste der Gruppen, denen ein User im
     * globalen Bereich zugeordnet ist.
     */
    public void setUserGroups(String[] newGroups);

    /**
     * Testet, ob ein User für den Moludweit globalen Bereich
     * in einer Gruppe ist.
     *
     * @param group Bezeichner der Gruppe, auf die getestet wird
     */
    public boolean isUserInGroup(String group);

    /**
     * Liefert eine Liste aller Gruppen, die einem Benutzer
     * für den Bereich area zugeordnet sind.
     *
     * @param area Ein Bezeichner eines Zugriffsbereiches
     */
    public String[] getUserGroups(String area);

    /**
     * Setzt eine Liste der Gruppen, denen ein User im
     * Bereich area zugeordnet ist.
     */
    public void setUserGroups(String[] newGroups, String area);

    /**
     * Testet, ob ein User für den Bereich area
     * in einer Gruppe ist.
     *
     * @param group Bezeichner der Gruppe, auf die getestet wird
     * @param area Ein Bezeichner eines Zugriffsbereiches
     */
    public boolean isUserInGroup(String group, String area);

    /**
     * Testet, ob der User das gewollte Task ausführen darf.
     * Wenn kein Zugriff gewäht wird muss eine TcSecurityException geworfen werden.
     */
    public void testTaskAccess(TcCommonConfig config, TcRequest tcRequest)
        throws TcSecurityException;

    /**
     * @return Returns the email.
     */
    public String getUserEmail();
    public void setUserEmail(String mail);

    /**
     * Liefert den Nachnamen des Users
     */
    public String getUserLastName();
    public void setUserLastName(String lastName);

    /**
     * Liefert den Vornamen
     */
    public String getUserGivenName();

    /**
     * Liefert den vollständigen Namen des Users
     */
    public String getUserName();

    /**
     * Setzt den vollständigen Namen des Users
     */
    public void setUserName(String name);

    public void setUserGivenName(String givenName);
}
