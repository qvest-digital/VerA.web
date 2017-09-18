package de.tarent.octopus.server;

/*
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
     * @return <code>true</code> falls Userverwaltung möglich, <code>false</code> sonst.
     */
    public boolean isUserManagementSupported();
    
    /**
     * Liefert den zuständigen UserManager zurück.
     * @return UserManager oder <code>null</code>, falls LoginManager kein UserManagement unterstützt.
     */
    public UserManager getUserManager();

}