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
