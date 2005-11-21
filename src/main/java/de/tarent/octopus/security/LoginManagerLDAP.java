/* $Id: LoginManagerLDAP.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.security;

import java.net.PasswordAuthentication;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.security.ldap.TcSecurityLDAPManager;
import de.tarent.octopus.server.PersonalConfig;

/** 
 * Implementierung eines LoginManagers, über LDAP
 * <br><br>
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * 
 * @deprecated moved to LDAPLib, will be deleted as soon as 2005-06-01
 */
public class LoginManagerLDAP extends AbstractLoginManager {
	Logger logger = Logger.getLogger(this.getClass().getName());
    protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) 
        throws TcSecurityException {
    	logger.warning("Die Klasse " + this.getClass().getName() + " soll nicht mehr verwendet werden, sie wird nicht mehr gepflegt! Stattdessen muss das Jar LDAPLib.jar im Classpath liegen, dann kann die Klasse de.tarent.ldap.LoginManagerLDAP genutzt werden.");
        // TODO:
        // Eigentlich soll die Cofiguration 
        // dieses Loginmanagers aus der ModuleConfig kommen
        //         TcSecurityLDAPManager tcslm =
        //             new TcSecurityLDAPManager(
        //                                       getConfigurationString(TcEnv.KEY_LDAP_URL),
        //                                       getConfigurationString(TcEnv.KEY_LDAP_BASE_DN),
        //                                       getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));

        TcSecurityLDAPManager tcslm =
            new TcSecurityLDAPManager(
                                      commonConfig.getConfigData(TcEnv.KEY_LDAP_URL),
                                      commonConfig.getConfigData(TcEnv.KEY_LDAP_BASE_DN),
                                      commonConfig.getConfigData(TcEnv.KEY_LDAP_RELATIVE));
        
        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        if (pwdAuth == null)
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);
        // TODO
        // Eigentlich soll die Cofiguration 
        // dieses Loginmanagers aus der ModuleConfig kommen
        //tcslm.login(userName, true, tcRequest.get(PARAM_PASSWORD), getConfigurationString(TcEnv.KEY_LDAP_AUTHORIZATION));
        tcslm.login(pwdAuth.getUserName(), true, new String(pwdAuth.getPassword()), commonConfig.getConfigData(TcEnv.KEY_LDAP_AUTHORIZATION));
        if (!tcslm.checkuid(pwdAuth.getUserName())) {
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);
        }
        Map userdata = tcslm.getUserData(pwdAuth.getUserName());

        pConfig.setUserLastName((String) userdata.get("nachname"));
        pConfig.setUserGivenName((String) userdata.get("vorname"));
        pConfig.setUserEmail((String) userdata.get("mail"));

        // TODO: Erweiterung der LDAP Daten auf Groups
        String adminflag = (String) userdata.get("adminflag");
        if(adminflag=="TRUE")
            pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_ADMINISTRATOR});
        else 
            pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_USER});

        pConfig.userLoggedIn(pwdAuth.getUserName());
    }
    
    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
        throws TcSecurityException {
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_LOGGED_OUT});
        pConfig.userLoggedOut();
    }
}