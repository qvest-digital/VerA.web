/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.ldap;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.security.AbstractLoginManager;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.PersonalConfig;

/** 
 * Implementierung eines LoginManagers, �ber LDAP
 * <br><br>
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @author Michael Klink
 * 
 */
public class LoginManagerLDAPGeneric extends AbstractLoginManager
{
    /**
     * Schluessel des Konfigurationseintrags fuer die Objekt Klasse,
     * welche von den in LDAP definierten Benutzern implementiert wird.
     */
    public final static String KEY_USER_OBJECT_CLASS = "ldapuserobjectclass";

    /**
     * Schluessel des Konfigurationseintrags fuer rekursive LDAP lookups.
     */
    public final static String KEY_RECURSIVE_LOOKUPS = "ldaprecursivelookups";

    //
    // zu �berschreibende Methoden
    //
	/**
	 * Diese Methode soll von LoginManager-Klassen, die von dieser abgeleitet werden,
	 * genutzt werden, um nach einem erfolgreichen Login in der PersonalConfig in
	 * eigener Weise Attribute zu setzen.
	 * 
	 * @param pConfig PersonalConfig des neu eingelogten Benutzers
	 * @param userName Benutzer-ID des neu eingeloggten Benutzers
	 * @throws LDAPException
	 * @see #doLogin(TcCommonConfig, PersonalConfig, TcRequest)
	 */
	protected void initPersonalConfig(PersonalConfig pConfig, String userName) throws LDAPException {
	    pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_USER});
	}
	
	/**
	 * Diese Methode soll von LoginManager-Klassen, die von dieser abgeleitet werden,
	 * genutzt werden, um den zu verwendenden LDAPManager zu erzeugen.
	 * @throws LDAPException 
	 * 
	 * @see #doLogin(TcCommonConfig, PersonalConfig, TcRequest)
	 */
	protected void initLDAPManager() throws LDAPException {
        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(TcEnv.KEY_LDAP_BASE_DN));
        params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_USER_OBJECT_CLASS, getConfigurationString(LoginManagerLDAPGeneric.KEY_USER_OBJECT_CLASS));
        params.put(LDAPManager.KEY_RECURSIVE_LOOKUPS, getConfigurationString(LoginManagerLDAPGeneric.KEY_RECURSIVE_LOOKUPS));
        ldapManager = LDAPManager.login(
                LDAPManager.class,
                getConfigurationString(TcEnv.KEY_LDAP_URL),
                params
                );
    }
	
	//
	// �berschreibungen von AbstractLoginManager
	//
	/**
	 * Diese Methode �berpr�ft die Credentials im Request und setzt im Erfolgsfall die
	 * entsprechenden Daten in der �bergebenen PersonalConfig.
	 * 
	 * @param commonConfig Konfigurationsdaten des Octopus
	 * @param pConfig pers�nliche Konfiguration des einzuloggenden Benutzers
	 * @param tcRequest Benutzeranfrage mit Authentisierungsdaten
	 * @throws TcSecurityException bei fehlgeschlagener Authorisierung
	 */
    @Override
    protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) 
        throws TcSecurityException {
        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        if (pwdAuth == null)
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);
        doLogin(pwdAuth, pConfig, true);
    }
    
    /**
     * Diese Methode f�hrt die eigentliche �berpr�fung der �bergebenen Credentials
     * aus, bei Serverproblemen ggf. nach LDAP-Verbindung-Neuaufsetzen sogar ein zweites
     * Mal. 
     * 
     * @param pwdAuth Passwort-Authentifizierung
     * @param pConfig pers�nliche Konfiguration des einzuloggenden Benutzers
     * @param repeat Flag: Soll bei Server-Problemen ein zweiter Login versucht werden
     * @throws TcSecurityException bei fehlgeschlagener Authorisierung
     */
    private void doLogin(PasswordAuthentication pwdAuth, PersonalConfig pConfig, boolean repeat) throws TcSecurityException {
        try {
            if (ldapManager == null)
                initLDAPManager();
            ldapManager.login(pwdAuth.getUserName(), new String(pwdAuth.getPassword()), getConfigurationString(TcEnv.KEY_LDAP_AUTHORIZATION));
            initPersonalConfig(pConfig, pwdAuth.getUserName());
			
            pConfig.userLoggedIn(pwdAuth.getUserName());
        } catch (LDAPException e) {
            logger.log(Level.SEVERE, "Fehler beim LDAP-Zugriff!", e);
            if(e.getCause() instanceof AuthenticationException)
                throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR, e);
            if (repeat) {
                try {
                    initLDAPManager();
                    doLogin(pwdAuth, pConfig, false);
                    return;
                } catch (LDAPException e1) {
                    logger.log(Level.SEVERE, "Fehler beim LDAP-Reconnect!", e1);
                }
            }
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
        }
    }
    
    /**
     * Diese Methode f�hrt ein Ausloggen des Benutzers durch. Insbesondere werden
     * entsprechende Markierungen in seiner pers�nlichen Konfiguration gesetzt. 
	 * 
	 * @param commonConfig Konfigurationsdaten des Octopus
	 * @param pConfig pers�nliche Konfiguration des auszuloggenden Benutzers
	 * @param tcRequest Benutzeranfrage
     */
    @Override
    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) {
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_LOGGED_OUT});
        pConfig.userLoggedOut();
    }

    //
    // Variablen
    //
    /** LDAP-Konnektor */
	protected LDAPManager ldapManager = null;
	
    /** Logger f�r diese Klasse */
	static Logger logger = Logger.getLogger(LoginManagerLDAPGeneric.class.getName());
}
