/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.ldap;

import java.util.HashMap;
import java.util.Map;

import de.tarent.octopus.config.OctopusEnvironment;
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
    protected void initLDAPManager() throws LDAPException {
        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(OctopusEnvironment.KEY_LDAP_BASE_DN));
        params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
        params.put(LDAPManagerTarentContact.KEY_OBJECTCLASS, getConfigurationString("disableForceTarentObjectClass"));
        params.put(LDAPManagerTarentContact.KEY_GOSASUPPORT, getConfigurationString(LDAPManagerTarentContact.KEY_GOSASUPPORT));
        params.put(LDAPManagerTarentContact.KEY_REALLYDELETE, getConfigurationString(LDAPManagerTarentContact.KEY_REALLYDELETE));
        ldapManager = LDAPManager.login(
                LDAPManagerTarentContact.class,
                getConfigurationString(OctopusEnvironment.KEY_LDAP_URL),
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
	public UserManager getUserManager() {
        try {
            Map params = new HashMap();
            params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(OctopusEnvironment.KEY_LDAP_BASE_DN));
            params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
            params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
            return (UserManager) LDAPManager.login(
                    LDAPManagerTarentContact.class,
                    getConfigurationString(OctopusEnvironment.KEY_LDAP_URL),
                    params,
                    getConfigurationString(OctopusEnvironment.KEY_LDAP_USER),
                    getConfigurationString(OctopusEnvironment.KEY_LDAP_PWD),
                    getConfigurationString(OctopusEnvironment.KEY_LDAP_AUTHORIZATION)
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
	public boolean isUserManagementSupported() {
		return true;
	}
}