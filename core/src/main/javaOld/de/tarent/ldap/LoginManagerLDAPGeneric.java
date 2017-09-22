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
import de.tarent.octopus.security.AbstractLoginManager;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.PersonalConfig;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import javax.naming.AuthenticationException;
import java.net.PasswordAuthentication;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implementierung eines LoginManagers, über LDAP
 * <br><br>
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @author Michael Klink
 *
 */
public class LoginManagerLDAPGeneric extends AbstractLoginManager
{
    protected static final Logger LOGGER = Logger.getLogger(LoginManagerLDAPGeneric.class.getName());

    /**
     * Schluessel des Konfigurationseintrags fuer die Objekt Klasse,
     * welche von den in LDAP definierten Benutzern implementiert wird.
     */
    public final static String KEY_USER_OBJECT_CLASS = "ldapuserobjectclass";

    /**
     * Schluessel des Konfigurationseintrags fuer rekursive LDAP lookups.
     */
    public final static String KEY_RECURSIVE_LOOKUPS = "ldaprecursivelookups";

    /**
     * ExpiringMap für Login Versuch beschränkung
     */
    private final static Map<UUID, String> LOGIN_ATTEMPT_HISTORY = ExpiringMap.builder()
            .expiration(1, TimeUnit.MINUTES)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    /** LDAP-Konnektor */
    protected LDAPManager ldapManager = null;

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
	// überschreibungen von AbstractLoginManager
	//
	/**
	 * Diese Methode Überprüft die Credentials im Request und setzt im Erfolgsfall die
	 * entsprechenden Daten in der übergebenen PersonalConfig.
	 *
	 * @param commonConfig Konfigurationsdaten des Octopus
	 * @param pConfig persönliche Konfiguration des einzuloggenden Benutzers
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
     * Diese Methode führt die eigentliche Überprüfung der übergebenen Credentials
     * aus, bei Serverproblemen ggf. nach LDAP-Verbindung-Neuaufsetzen sogar ein zweites
     * Mal.
     *
     * @param pwdAuth Passwort-Authentifizierung
     * @param pConfig persönliche Konfiguration des einzuloggenden Benutzers
     * @param repeat Flag: Soll bei Server-Problemen ein zweiter Login versucht werden
     * @throws TcSecurityException bei fehlgeschlagener Authorisierung
     */
    private void doLogin(PasswordAuthentication pwdAuth, PersonalConfig pConfig, boolean repeat) throws TcSecurityException {
        try {
            checkLoginAttempts(pwdAuth);
            executeLdapLogin(pwdAuth);
            createPersonalConfig(pwdAuth, pConfig);
        } catch (LDAPException e) {
            addFailedLoginAttempt(pwdAuth);
            handleLoginErrors(pwdAuth, pConfig, repeat, e);
        }
    }

    private void checkLoginAttempts(PasswordAuthentication pwdAuth) throws TcSecurityException {
        /**
         * check if to much login attempts with this username were made
         * "msg_too_many_attempts" is used in login_de/en/es/fr.vm - modify there when changed here
         */
        if (Collections.frequency(LOGIN_ATTEMPT_HISTORY.values(), pwdAuth.getUserName()) > 9) {
            throw new TcSecurityException("msg_too_many_attempts");
        }
    }

    private void executeLdapLogin(PasswordAuthentication pwdAuth) throws LDAPException {
        if (ldapManager == null) {
            initLDAPManager();
        }
        final String password = new String(pwdAuth.getPassword());
        ldapManager.login(pwdAuth.getUserName(), password, getConfigurationString(TcEnv.KEY_LDAP_AUTHORIZATION));
    }

    private void createPersonalConfig(PasswordAuthentication pwdAuth, PersonalConfig pConfig) throws LDAPException {
        initPersonalConfig(pConfig, pwdAuth.getUserName());
        pConfig.userLoggedIn(pwdAuth.getUserName());
    }

    private void addFailedLoginAttempt(PasswordAuthentication pwdAuth) {
        //immer, egal welcher fehler, so können keine usernamen erraten werden
        LOGIN_ATTEMPT_HISTORY.put(UUID.randomUUID(), pwdAuth.getUserName());
    }

    private void handleLoginErrors(PasswordAuthentication pwdAuth,
                                   PersonalConfig pConfig,
                                   boolean repeat,
                                   LDAPException e) throws TcSecurityException {
        LOGGER.log(Level.SEVERE, "Fehler beim LDAP-Zugriff!", e);
        if (e.getCause() instanceof AuthenticationException) {
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR, e);
        }
        if (repeat) {
            retryLogin(pwdAuth, pConfig);
            return;
        }
        throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
    }

    private void retryLogin(PasswordAuthentication pwdAuth, PersonalConfig pConfig) throws TcSecurityException {
        ldapManager = null;
        doLogin(pwdAuth, pConfig, false);
    }

    /**
     * Diese Methode führt ein Ausloggen des Benutzers durch. Insbesondere werden
     * entsprechende Markierungen in seiner persönlichen Konfiguration gesetzt.
	 *
	 * @param commonConfig Konfigurationsdaten des Octopus
	 * @param pConfig persönliche Konfiguration des auszuloggenden Benutzers
	 * @param tcRequest Benutzeranfrage
     */
    @Override
    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) {
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_LOGGED_OUT});
        pConfig.userLoggedOut();
    }
}
