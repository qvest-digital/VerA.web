package de.tarent.octopus.server;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.security.TcSecurityException;

import java.io.Serializable;
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
public interface PersonalConfig extends Serializable {
    /**
     * Teilt der PersonalConfig mit, wenn sich ein Benutzer authentifiziert hat.
     * Diese kann damit abhängig von ihrer Implementierung die Benutzerdaten laden.
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
     * @param area  Ein Bezeichner eines Zugriffsbereiches
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
