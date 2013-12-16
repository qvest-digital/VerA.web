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

import java.util.HashMap;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.PersonalConfig;
import de.tarent.octopus.server.UserManager;

/** 
 * Implementierung eines LoginManagers im tarent-contact-Umfeld �ber LDAP.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class LoginManagerLDAPTarentContact extends LoginManagerLDAPGeneric {
    //
    // LoginManagerLDAPGeneric �berschreibungen
    //
    /**
     * Diese Methode setzt nach einem erfolgreichen Login in der PersonalConfig in
     * eigener Weise Attribute.
     * 
     * @param pConfig PersonalConfig des neu eingelogten Benutzers
     * @param userName Benutzer-ID des neu eingeloggten Benutzers
     * @throws LDAPException
     * @see #doLogin(TcCommonConfig, PersonalConfig, TcRequest)
     * @see LoginManagerLDAPGeneric#initPersonalConfig(de.tarent.octopus.server.PersonalConfig, java.lang.String)
     */
	@Override
    protected void initPersonalConfig(PersonalConfig pConfig, String userName) throws LDAPException {
        if (ldapManager instanceof LDAPManagerTarentContact) {
            Map userdata = ((LDAPManagerTarentContact) ldapManager).getUserData(userName);
            pConfig.setUserLastName((String) userdata.get("nachname"));
            pConfig.setUserGivenName((String) userdata.get("vorname"));
            pConfig.setUserEmail((String) userdata.get("mail"));

            // TODO: Erweiterung der LDAP Daten auf Groups
//            String adminflag = (String) userdata.get("adminflag");
//            if("TRUE".equalsIgnoreCase(adminflag))
//                pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_USER, PersonalConfig.GROUP_ADMINISTRATOR});
//            else 
                pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_USER});
        } else
            super.initPersonalConfig(pConfig, userName);
	}

    /**
     * Diese Methode erzeugt den zu verwendenden LDAPManager.
     * @throws LDAPException 
     * 
     * @see #doLogin(TcCommonConfig, PersonalConfig, TcRequest)
     * @see LoginManagerLDAPGeneric#initLDAPManager()
     */
    @Override
    protected void initLDAPManager() throws LDAPException {
        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(TcEnv.KEY_LDAP_BASE_DN));
        params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
        params.put(LDAPManagerTarentContact.KEY_OBJECTCLASS, getConfigurationString("disableForceTarentObjectClass"));
        params.put(LDAPManagerTarentContact.KEY_GOSASUPPORT, getConfigurationString(LDAPManagerTarentContact.KEY_GOSASUPPORT));
        params.put(LDAPManagerTarentContact.KEY_REALLYDELETE, getConfigurationString(LDAPManagerTarentContact.KEY_REALLYDELETE));
        ldapManager = LDAPManager.login(
                LDAPManagerTarentContact.class,
                getConfigurationString(TcEnv.KEY_LDAP_URL),
                params
                );
    }
    
    //
    // LoginManager - AbstractLoginManager �berschreibungen
    //
	/**
     * Liefert den zust�ndigen UserManager zur�ck.
     * 
     * @return UserManager oder <code>null</code>, falls Konfigurationsprobleme bestehen.
	 * @see de.tarent.octopus.server.LoginManager#getUserManager()
	 */
	@Override
    public UserManager getUserManager() {
        try {
            Map params = new HashMap();
            params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(TcEnv.KEY_LDAP_BASE_DN));
            params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
            params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
            return (UserManager) LDAPManager.login(
                    LDAPManagerTarentContact.class,
                    getConfigurationString(TcEnv.KEY_LDAP_URL),
                    params,
                    getConfigurationString(TcEnv.KEY_LDAP_USER),
                    getConfigurationString(TcEnv.KEY_LDAP_PWD),
                    getConfigurationString(TcEnv.KEY_LDAP_AUTHORIZATION)
                    );
        } catch (LDAPException e) {
            logger.warning(e.getLocalizedMessage());
            return null;
        }
	}
	
	/**
     * Stellt fest, ob der LoginManager auch selber die Userverwaltung �bernehmen kann.
     * 
     * @return <code>true</code> falls Userverwaltung m�glich, <code>false</code> sonst.
	 * @see de.tarent.octopus.server.LoginManager#isUserManagementSupported()
	 */
	@Override
    public boolean isUserManagementSupported() {
		return true;
	}
}
