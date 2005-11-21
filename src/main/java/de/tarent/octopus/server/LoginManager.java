/* $Id: LoginManager.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.server;

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