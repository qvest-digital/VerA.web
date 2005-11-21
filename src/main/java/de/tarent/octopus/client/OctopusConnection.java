/* $Id: OctopusConnection.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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
