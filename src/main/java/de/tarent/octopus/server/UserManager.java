package de.tarent.octopus.server;

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
