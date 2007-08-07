/* $Id: LoginManagerLDAPGeneric.java,v 1.10 2005/07/28 14:10:02 kirchner Exp $
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

package de.tarent.ldap;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;

import de.tarent.octopus.config.CommonConfig;
import de.tarent.octopus.config.OctopusEnvironment;
import de.tarent.octopus.request.OctopusRequest;
import de.tarent.octopus.security.AbstractLoginManager;
import de.tarent.octopus.security.OctopusSecurityException;
import de.tarent.octopus.server.PersonalConfig;

/** 
 * Implementierung eines LoginManagers, über LDAP
 * <br><br>
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @author Michael Klink
 * 
 */
public class LoginManagerLDAPGeneric extends AbstractLoginManager {
    //
    // zu überschreibende Methoden
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
        params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(OctopusEnvironment.KEY_LDAP_BASE_DN));
        params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
        ldapManager = LDAPManager.login(
                LDAPManager.class,
                getConfigurationString(OctopusEnvironment.KEY_LDAP_URL),
                params
                );
    }
	
	//
	// Überschreibungen von AbstractLoginManager
	//
	/**
	 * Diese Methode überprüft die Credentials im Request und setzt im Erfolgsfall die
	 * entsprechenden Daten in der übergebenen PersonalConfig.
	 * 
	 * @param commonConfig Konfigurationsdaten des Octopus
	 * @param pConfig persönliche Konfiguration des einzuloggenden Benutzers
	 * @param tcRequest Benutzeranfrage mit Authentisierungsdaten
	 * @throws TcSecurityException bei fehlgeschlagener Authorisierung
	 */
    protected void doLogin(CommonConfig commonConfig, PersonalConfig pConfig, OctopusRequest tcRequest) 
        throws OctopusSecurityException {
        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        if (pwdAuth == null)
            throw new OctopusSecurityException(OctopusSecurityException.ERROR_AUTH_ERROR);
        doLogin(pwdAuth, pConfig, true);
    }
    
    /**
     * Diese Methode führt die eigentliche Überprüfung der übergebenen Credentials
     * aus, bei Serverproblemen ggf. nach LDAP-Verbindung-Neuaufsetzen sogar ein zweites
     * Mal. 
     * 
     * @param pwdAuth Passwort-Authentifizierung
     * @param pConfig persönliche Konfiguration des einzuloggenden Benutzers
     * @param repeat Flag: Soll bei Server-Problemen ein zweiter Login versucht werden
     * @throws TcSecurityException bei fehlgeschlagener Authorisierung
     */
    private void doLogin(PasswordAuthentication pwdAuth, PersonalConfig pConfig, boolean repeat) throws OctopusSecurityException {
        try {
            if (ldapManager == null)
                initLDAPManager();
            ldapManager.login(pwdAuth.getUserName(), new String(pwdAuth.getPassword()), getConfigurationString(OctopusEnvironment.KEY_LDAP_AUTHORIZATION));
            initPersonalConfig(pConfig, pwdAuth.getUserName());
			
            pConfig.userLoggedIn(pwdAuth.getUserName());
        } catch (LDAPException e) {
            logger.log(Level.SEVERE, "Fehler beim LDAP-Zugriff!", e);
            if(e.getCause() instanceof AuthenticationException)
                throw new OctopusSecurityException(OctopusSecurityException.ERROR_AUTH_ERROR, e);
            if (repeat) {
                try {
                    initLDAPManager();
                    doLogin(pwdAuth, pConfig, false);
                    return;
                } catch (LDAPException e1) {
                    logger.log(Level.SEVERE, "Fehler beim LDAP-Reconnect!", e1);
                }
            }
            throw new OctopusSecurityException(OctopusSecurityException.ERROR_SERVER_AUTH_ERROR, e);
        }
    }
    
    /**
     * Diese Methode führt ein Ausloggen des Benutzers durch. Insbesondere werden
     * entsprechende Markierungen in seiner persönlichen Konfiguration gesetzt. 
	 * 
	 * @param commonConfig Konfigurationsdaten des Octopus
	 * @param pConfig persönliche Konfiguration des auszuloggenden Benutzers
	 * @param tcRequest Benutzeranfrage
     */
    protected void doLogout(CommonConfig commonConfig, PersonalConfig pConfig, OctopusRequest tcRequest) {
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_LOGGED_OUT});
        pConfig.userLoggedOut();
    }

    //
    // Variablen
    //
    /** LDAP-Konnektor */
	protected LDAPManager ldapManager = null;
	
    /** Logger für diese Klasse */
	static Logger logger = Logger.getLogger(LoginManagerLDAPGeneric.class.getName());
}