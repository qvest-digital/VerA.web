package de.tarent.ldap;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2015 benja <benja@benja.com>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2015 Christian Luginbühl <dinkel@pimprecords.com>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2015 Dmytro Pishchukhin <demon@Demons-MBP.fritz.box>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 rysiekpl <rysiek@hackerspace.pl>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015 hong Xu <hong@topbug.net>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.PersonalConfig;
import de.tarent.octopus.server.UserManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementierung eines LoginManagers im tarent-contact-Umfeld über LDAP.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class LoginManagerLDAPTarentContact extends LoginManagerLDAPGeneric {
    //
    // LoginManagerLDAPGeneric überschreibungen
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
    // LoginManager - AbstractLoginManager überschreibungen
    //
	/**
     * Liefert den zuständigen UserManager zurück.
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
            LOGGER.warning(e.getLocalizedMessage());
            return null;
        }
	}

	/**
     * Stellt fest, ob der LoginManager auch selber die Userverwaltung übernehmen kann.
     *
     * @return <code>true</code> falls Userverwaltung möglich, <code>false</code> sonst.
	 * @see de.tarent.octopus.server.LoginManager#isUserManagementSupported()
	 */
	@Override
    public boolean isUserManagementSupported() {
		return true;
	}
}
