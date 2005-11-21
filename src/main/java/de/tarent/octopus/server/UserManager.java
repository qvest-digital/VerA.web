/* $Id: UserManager.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * Created on 24.03.2005 by kirchner
 * 
 * tarent-contact, Plattform-Independent Webservice-Based Contact Management
 * Copyright (C) 2004 tarent GmbH
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
 * by Philipp Kirchner.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.server;

import de.tarent.octopus.security.TcSecurityException;

/**
 * @author kirchner
 * 
 * Interface für einen UserManager.
 */
public interface UserManager {
	/**
	 * Hinzufügen eines Users zur Authentifizierungs-DB.
	 * @param userID userID des neuen Users
	 * @param firstName Vorname des Users
	 * @param lastName Nachname des Users
	 * @param password Passwort des Users
	 */
	public void addUser(String userID, String firstName, String lastName, String password) throws TcSecurityException;
	
	/**
	 * Modifizieren eines Users in der Authentifizierungs-DB.
	 * @param userID userID des zu modifizierenden Users
	 * @param firstName Vorname des Users
	 * @param lastName Nachname des Users
	 * @param password Password des Users
	 */
	public void modifyUser(String userID, String firstName, String lastName, String password) throws TcSecurityException;
	
	/**
	 * Setzt einen Parameter zu einem bestimmten User.
	 * @param userID ID des Users
	 * @param paramname Name des Parameters
	 * @param paramvalue Wert des Parameters
	 */
	public void setUserParam(String userID, String paramname, Object paramvalue) throws TcSecurityException;
	
	/**
	 * Liest einen UserParameter aus.
	 * @param userID ID des Users
	 * @param paramname Name des Parameters
	 * @return Wert des Parameters, falls vorhanden, <code>null</code> sonst.
	 */
	public Object getUserParam(String userID, String paramname) throws TcSecurityException;
	
	/**
	 * Löschen eines Users aus der Authentifizierungs-DB.
	 * @param userID userID des zu löschenden Users
	 */
	public void deleteUser(String userID) throws TcSecurityException;
}
