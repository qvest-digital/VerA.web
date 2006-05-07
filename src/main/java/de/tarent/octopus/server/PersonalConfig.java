/* $Id: PersonalConfig.java,v 1.2 2006/05/07 23:05:57 jens Exp $
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
     * @group Bezeichner der Gruppe, auf die getestet wird
     */
    public boolean isUserInGroup(String group);

    /**
     * Liefert eine Liste aller Gruppen, die einem Benutzer 
     * für den Bereich area zugeordnet sind.
     *
     * @area Ein Bezeichner eines Zugriffsbereiches
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
     * @group Bezeichner der Gruppe, auf die getestet wird
     * @area Ein Bezeichner eines Zugriffsbereiches
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