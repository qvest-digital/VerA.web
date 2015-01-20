/* $Id: TcPersonalConfig.java,v 1.3 2006/05/11 11:44:26 jens Exp $
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

package de.tarent.octopus.config;

import de.tarent.octopus.security.*;
import de.tarent.octopus.content.CookieMap;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.*;
import de.tarent.octopus.server.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/** 
 * Default Implementierung einer PersonalConfig ohne Erweiterungen.
 * 
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcPersonalConfig implements PersonalConfig {

    Map sessionData = new HashMap();
    String userLogin;
    Integer userID;
    // Areas als String Keys, Listen mit Gruppen als Values
    Map areaGroups = new HashMap();
    // Groups der globalen Area
    String[] globalGroups;
    String userEmail;
    String userLastName;
    String userGivenName;
    String userName;


    public void userLoggedIn(String userName) {
        setUserLogin(userName);
    }

    public void userLoggedOut() {
        
    }

    /**
     * Speichert einen Wert innrhalb der aktuellen Session
     */
    public void setSessionValue(String key, Object value) {
        sessionData.put(key, value);
    }

    /**
     * Liefert einen Wert innerhalb der aktuellen Session
     */
    public Object getSessionValue(String key) {
        return sessionData.get(key);
    }
    
    /**
     * Gets the user preference by key
     */
    public String getUserPreference(String key) {
    	String value;
    	OctopusContext oc = Context.getActive();
        
        // Looking for a cookie with the name "key" in the cookies
        // given from the HttpServletRequest. These cookies has been put
        // into the TcRequest by the HttpHelper-Class.
        TcRequest tcRequest = oc.getRequestObject();
        Map cookiesMap = (Map) tcRequest.getParam(CookieMap.PREFIX_COOKIE_MAP);
        value = cookiesMap != null ? (String) cookiesMap.get(key) : null;
        
        // Looking for cookies that has been set within
        // this Octopus call and are stored in the TcContent.
        // It is also possible to set a Cookie-Object directly
        // by setting this Object to the field "cookie" in the content's
        // cookies-Map. Because we do not want the octopus to have the
        // Servlet-API as a dependency the values of this directly assigned
        // Cookies will not be found as a user preference during the request
        // in which this cookie has been set.
        TcContent tcContent = oc.getContentObject();
        Map unstoredCookies = (Map)tcContent.get(CookieMap.PREFIX_COOKIE_MAP);
        if (unstoredCookies != null && unstoredCookies.containsKey(key)) {
            return (String) ((Map)unstoredCookies.get(key)).get(CookieMap.COOKIE_MAP_FIELD_VALUE);
        }
            
        return value;        
    }
    
    /**
     * Sets the user preference key to the given value 
     */
    public void setUserPreference(String key, String value) {
    	OctopusContext oc = Context.getActive();
    	TcContent tcContent = oc.getContentObject();
    	
    	Map cookiesMap;
    	if (tcContent.getAsObject(CookieMap.PREFIX_COOKIE_MAP) instanceof Map)
			cookiesMap = (Map) tcContent.getAsObject(CookieMap.PREFIX_COOKIE_MAP);
    	else {
    		cookiesMap = new HashMap();
    	}
    	
    	// Each field in the cookies-Map is another Map with two fields: the field
    	// "value" and the field "cookie". The cookies-Map in the request contains
    	// all cookies already stored and both the value and a Cookie-Object are
    	// available in the appropriate fields. When cookies are set over the user
    	// preferences (this method) only the field "value" will be filled and the
    	// Cookie-Object will be created after the dispatch is completed so the
    	// octopus-core does not need to import the Servlet-API. Nevertheless you
    	// can assign Cookie-Object directly by putting them into the "cookie"-field
    	// in the cookies-Map in the octopus-content. The "cookie"-field will be
    	// preferred to the "value"-field if both fields are present.
    	// For cookie configuration setting see: de.tarent.octopus.content.CookieMap
    	Map cookieMap = new HashMap(1);
    	cookieMap.put(CookieMap.COOKIE_MAP_FIELD_VALUE, value);
    	
    	cookiesMap.put(key, cookieMap);
    	tcContent.setField(CookieMap.PREFIX_COOKIE_MAP, cookiesMap);
    }

    /**
     * Liefert die Keys der aktuellen Session
     */
    public Iterator getSessionKeys(String key) {
        return sessionData.keySet().iterator();
    }


    /**
     * Liefert den Login Namen
     */
    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String login) {
        this.userLogin = login;
    }

    /**
     * Liefert die User ID
     */
    public Integer getUserID() {
        return userID;
    }     
    public void setUserID(Integer id) {
        this.userID = id;
    }
  

    /**
     * Liefert eine Liste aller Gruppen, die einem Benutzer 
     * f�r den Moludweit globalen Bereich zugeordnet sind.
     */
    public String[] getUserGroups() {
        return globalGroups;
    }

    /**
     * Setzt eine Liste der Gruppen, denen ein User im
     * globalen Bereich zugeordnet ist.
     */
    public void setUserGroups(String[] newGroups) {
        globalGroups = newGroups;
        areaGroups.put("", newGroups);
    }

    /**
     * Testet, ob ein User f�r den Moludweit globalen Bereich
     * in einer Gruppe ist.
     *
     * @group Bezeichner der Gruppe, auf die getestet wird
     */
    public boolean isUserInGroup(String group) {
//         System.out.println("isUserInGoup "+group+" ?");
//         System.out.print("groups:  ");
//         for (int i = 0; i < globalGroups.length; i++)
//             System.out.println(globalGroups[i]+" " );
//         System.out.println();        
        return arrayContains(globalGroups, group);
    }

    protected boolean arrayContains(String[] array, String item) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(item))
                return true;
        return false;
    }

    /**
     * Liefert eine Liste aller Gruppen, die einem Benutzer 
     * f�r den Bereich area zugeordnet sind.
     * Wenn f�r die Area selbst keine Gruppen definiert sind,
     * werden die Gruppen der �bergeordneten Area zur�ck gegeben,
     * die ab weitesten spezialisiert ist.
     *
     * @area Ein Bezeichner eines Zugriffsbereiches
     */
    public String[] getUserGroups(String area) {        
        String[] bestMatchingGroups = null;
        int bestMatchingRating = -1;

        // Get best matching area-Group-List
        // Bestimmt die Area, mit dem l�ngsten Bezeichner der Pr�fix von area ist
        for (Iterator iter = areaGroups.keySet().iterator(); iter.hasNext();) {
            String key = (String)iter.next();
            if (area.startsWith(key) && key.length() > bestMatchingRating) {
                bestMatchingRating = key.length();
                bestMatchingGroups = (String[])areaGroups.get(key);
            }
        }
        return bestMatchingGroups;        
    }

    /**
     * Setzt eine Liste der Gruppen, denen ein User im
     * Bereich area zugeordnet ist.
     */
    public void setUserGroups(String[] newGroups, String area) {
        areaGroups.put(area, newGroups);
    }


    /**
     * Testet, ob ein User f�r den Bereich area
     * in einer Gruppe ist.
     *
     * @group Bezeichner der Gruppe, auf die getestet wird
     * @area Ein Bezeichner eines Zugriffsbereiches
     */
    public boolean isUserInGroup(String group, String area) {
        return arrayContains(getUserGroups(area), group);
    }

    /**
     * Testet, ob der User das gewollte Task ausf�hren darf.
     * Wenn kein Zugriff gew�ht wird muss eine TcSecurityException geworfen werden.
     */
    public void testTaskAccess(TcCommonConfig config, TcRequest tcRequest)
        throws TcSecurityException {
        TcTaskList taskList = config.getTaskList(tcRequest.getModule());
        TcTask task = taskList.getTask(tcRequest.getTask());
        String[] taskGroups = task.getGroups();
        
        for (int i = 0; i < taskGroups.length; i++)
            if (isUserInGroup(taskGroups[i]))
                return;
        throw new TcSecurityException(TcSecurityException.ERROR_WRONG_GROUP_FOR_TASK);
    }


    /**
     * @return Returns the email.
     */
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String mail) {
        this.userEmail = mail;
    }


    /**                                         
     * Liefert den Nachnamen des Users
     */
    public String getUserLastName() {
        return userLastName;
    }
    public void setUserLastName(String lastName) {
        this.userLastName = lastName;
    }


    /**
     * Liefert den Vornamen
     */
    public String getUserGivenName() {
        return userGivenName;
    }
    public void setUserGivenName(String givenName) {
        this.userGivenName = givenName;
    }


    public String getUserName() {
        if (userName != null)
            return userName;
        return getUserGivenName() +" "+ getUserLastName();
    }
    /**
     * @param name The name to set.
     */
    public void setUserName(String name) {
        userName = name;
    }



    ///////////// Kompatibilit�t:
    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return getUserEmail();
    }
    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        setUserEmail(email);
    }
    /**
     * @return Returns the nachname.
     */
    public String getNachname() {
        return getUserLastName();
    }
    /**
     * @param nachname The nachname to set.
     */
    public void setNachname(String nachname) {
        setUserLastName(nachname);
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return getUserName();
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        setUserName(name);
    }

    /**
     * @return Returns the loginname.
     */
    public String getLoginname() {
        return getUserLogin();
    }
    /**
     * @param userName The loginname to set.
     */
    public void setLoginname(String userName) {
        setUserLogin(userName);
    }
    /**
     * @return Returns the vorname.
     */
    public String getVorname() {
        return getUserGivenName();
    }
    /**
     * @param vorname The vorname to set.
     */
    public void setVorname(String vorname) {
        setUserGivenName( vorname );
    }
    /**
     * @return Returns the admin.
     */
    public boolean isAdmin() {
        return isUserInGroup(GROUP_ADMINISTRATOR);
    }
    /**
     * @param admin The admin to set.
     */
    public void setAdmin(boolean admin) {
        if (! isAdmin()) {
            String[] newGroups = new String[getUserGroups().length+1];
            System.arraycopy(getUserGroups(), 0, newGroups, 0, getUserGroups().length+1);
        }
    }

}
