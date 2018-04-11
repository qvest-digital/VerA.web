package de.tarent.octopus.server;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.security.TcSecurityException;

/**
 * Ein LoginManager regelt die Authentifizierung. Er stellt Wissen darüber bereit,
 * welche Aktionen vom Benutzer ausgeführt werden dürfen.
 * <br><br>
 * Seine Hauptaufgabe ist es, eine PersonalConfig modulweit bereit zu stellen.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface LoginManager {
    public static final String TASK_LOGIN = "login";
    public static final String TASK_LOGIN_SOAP = "login_SOAP";
    public static final String TASK_LOGOUT = "logout";
    public static final String TASK_LOGOUT_SOAP = "logout_SOAP";

    /**
     * Setzt eine Menge von Implementierungsspezifischen Konfigurationsparametern.
     * Die Verwendung von Parametern in einer Implementierung ist optional.
     */

    public void setConfiguration(Map configuration);

    /**
     * Regelt Login-Logout operationen.
     * Als Ergebnis wird eine TcPersonalConfig für das aktuelle Modul in der Session abgelegt.
     * In dieser TcPersonalConfig können die Informationen zum Login und den Rechten hinterlegt sein.
     *
     * @throws TcSecurityException bei einer Authentifizierung, die Fehlerhaft war
     */
    public void handleAuthentication(TcCommonConfig cConfig, TcRequest tcRequest, TcSession theSession)
      throws TcSecurityException;

    /**
     * Liefert die zuvor erstellte TcPersonalConfig dieses Moduls zurück.
     *
     * @return TcPersonalConfig des Benutzers
     */
    public PersonalConfig getPersonalConfig(TcCommonConfig config, TcRequest tcRequest, TcSession theSession);

    /**
     * Stellt fest, ob der LoginManager auch selber die Userverwaltung übernehmen kann.
     *
     * @return <code>true</code> falls Userverwaltung möglich, <code>false</code> sonst.
     */
    public boolean isUserManagementSupported();

    /**
     * Liefert den zuständigen UserManager zurück.
     *
     * @return UserManager oder <code>null</code>, falls LoginManager kein UserManagement unterstützt.
     */
    public UserManager getUserManager();
}