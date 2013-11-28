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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.aa.veraweb.beans.User;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.octopus.LoginManagerAA;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.content.TcAll;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;


/**
 * Diese Klasse dient als LoginManager �ber LDAP im Kontext des Ausw�rtigen Amts.
 * Insbesondere beachtet wird hier das Anmelden wahlweise als Benutzer oder als
 * Rolle. Ebenfalls beachtet wird das Anmelden mit noch zu erweiternden Namen.
 * 
 * @author mikel
 */
public class LoginManagerLDAPAA extends LoginManagerLDAPGeneric implements LoginManagerAA {
    //
    // Konstanten
    //
    /** Schl�ssel des Konfigurationseintrags f�r den Rollenfilter */
    public final static String KEY_ROLE_FILTER = "aarolefilter";
    
    /** Schl�ssel des Konfigurationseintrags f�r den Superadmin-Login */
    public final static String KEY_SYSTEM_ADMIN_LOGIN = "systemlogin";

    /** Schl�ssel des Konfigurationseintrags f�r das Superadmin-Passwort */
    public final static String KEY_SYSTEM_ADMIN_PASSWORD = "systempassword";


    //
    // Schnittstelle LoginManagerAA
    //
    /**
     * Diese Methode �ndert die pers�nliche Konfiguration so ab, dass sie
     * in Vertretung der angegebenen Rolle handelt.<br>
     * TODO: Ablauf der G�ltigkeit mitaufnehmen und bei Ablauf ung�ltig werden.
     * 
     * @param octx anzupassender Octopus-Kontext der Sitzung des Vertreters
     * @param proxyDescription Beschreibungs-Bean der Vertretung
     * @throws TcSecurityException Wenn keine authentisierte pers�nliche Konfiguration
     *  vorliegt oder schon als Vertreter agiert wird.
     * @see LoginManagerAA#setProxy(OctopusContext, Proxy)
     */
    public void setProxy(OctopusContext octx, Proxy proxyDescription) throws TcSecurityException {
        PersonalConfigAA pConfig = (PersonalConfigAA) octx.personalConfig();
        if (pConfig == null || !pConfig.getGrants().isAuthenticated())
            throw new TcSecurityException("Missing personal config for proxying.");
        if (pConfig.getProxy() != null)
            throw new TcSecurityException("Proxying is not transitive.");
        pConfig.setUserGroups(new String[0]);
        pConfig.setProxy(proxyDescription.proxy);
        pConfig.setRole(proxyDescription.userRole);
        fillInUserGroups(octx.commonConfig(), pConfig, octx.getRequestObject());
    }

    /**
     * Diese Methode liefert eine Auflistung verf�gbarer AA-Rollen, aus denen
     * VerA.web-Benutzer ausgew�hlt werden k�nnen. 
     * 
     * @return Liste verf�gbarer AA-Rollen.
     * @throws TcSecurityException 
     * @see LoginManagerAA#getAARoles()
     */
    public Set getAARoles() throws TcSecurityException {
        if (ldapManager instanceof LDAPManagerAA)
            try {
                return ((LDAPManagerAA)ldapManager).getPossibleRoles();
            } catch (NamingException e) {
                throw new TcSecurityException("Rollen konnten nicht bezogen werden", e);
            }
        else
            return null;
    }
    
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
        if (ldapManager instanceof LDAPManagerAA) {
            Map userdata = ((LDAPManagerAA) ldapManager).getUserData(userName);
            pConfig.setUserEmail(safeFirstToString(userdata.get("mail")));
            pConfig.setUserGivenName(safeFirstToString(userdata.get("givenName")));
            pConfig.setUserID(safeFirstToInteger(userdata.get("uidNumber")));
            pConfig.setUserLastName(safeFirstToString(userdata.get("sn")));
            pConfig.setUserName(safeFirstToString(userdata.get("cn")));
            if (pConfig instanceof PersonalConfigAA) {
                PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                aaConfig.setRole(safeFirstToString(userdata.get("uid")));
            }
        } else
            super.initPersonalConfig(pConfig, userName);
    }

    /**
     * Diese Methode erzeugt den zu verwendenden LDAPManager.
     * 
     * @throws LDAPException 
     * @see #doLogin(TcCommonConfig, PersonalConfig, TcRequest)
     * @see LoginManagerLDAPGeneric#initLDAPManager()
     */
    @Override
    protected void initLDAPManager() throws LDAPException {
        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(TcEnv.KEY_LDAP_BASE_DN));
        params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));
        params.put(LDAPManagerAA.KEY_ROLE_FILTER, getConfigurationString(KEY_ROLE_FILTER));
        params.put(LDAPManager.KEY_USER_OBJECT_CLASS, getConfigurationString(LoginManagerLDAPGeneric.KEY_USER_OBJECT_CLASS));
        params.put(LDAPManager.KEY_RECURSIVE_LOOKUPS, getConfigurationString(LoginManagerLDAPGeneric.KEY_RECURSIVE_LOOKUPS));
        ldapManager = LDAPManager.login(
                LDAPManagerAA.class,
                getConfigurationString(TcEnv.KEY_LDAP_URL),
                params
                );
    }

    /**
     * Diese Methode �berpr�ft die Credentials im Request und setzt im Erfolgsfall die
     * entsprechenden Daten in der �bergebenen PersonalConfig.<br>
     * Hier wird auf die Funktionalit�t, die schon in {@link LoginManagerLDAPGeneric}
     * vorliegt, zur�ckgegriffen, wenn diese nicht zum Erfolg f�hrt, wird aber versucht,
     * �ber den Inhalt des person-Attributs den Login zu schaffen.<br>
     * Zus�tzlich gibt es die M�glichkeit, sich als ein vorgegebener System-Admin ohne
     * LDAP-Authentisierung anzumelden
     * 
     * @param commonConfig Konfigurationsdaten des Octopus
     * @param pConfig pers�nliche Konfiguration des einzuloggenden Benutzers
     * @param tcRequest Benutzeranfrage mit Authentisierungsdaten
     * @throws TcSecurityException bei fehlgeschlagener Authorisierung
     * @see de.tarent.ldap.LoginManagerLDAPGeneric#doLogin(de.tarent.octopus.config.TcCommonConfig, de.tarent.octopus.server.PersonalConfig, de.tarent.octopus.request.TcRequest)
     */
    @Override
    protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) throws TcSecurityException {
        PasswordAuthentication origAuth = tcRequest.getPasswordAuthentication();
        // http://www.ietf.org/internet-drafts/draft-ietf-ldapbis-authmeth-18.txt
        // Clients SHOULD disallow an empty password input to a Name/Password Authentication user interface.
        if (origAuth != null && (origAuth.getPassword() == null || origAuth.getPassword().length == 0))
            throw new TcSecurityException("Leere Passwörter sind nicht zulässig.");
        try {
        	/* the password authentication returned by TcRequest contains the fully
        	 * qualified username. This will break with the current implementation
        	 * of the LoginManagerLDAPGeneric.
        	 * Therefore we will simply rewrite the username request paramter in
        	 * tcRequest.
        	 * 
        	 * Change Request 2.11 for the next release version 1.2.0
		     * requires that users may now use qualified names when logging in
		     * (i.e. users may specify their at-domain, e.g. username@domain.tld)
		     * instead of just their ldap name.
		     * 
		     * cklein
        	 * 2008-02-14
        	 * 
        	 * we will try the login twice, if the first try fails, then we
        	 * will retry using the username without the appended at domain
        	 */
        	try
        	{
        		super.doLogin( commonConfig, pConfig, tcRequest );
        	}
        	catch( TcSecurityException se )
        	{
        		String username = ( String ) tcRequest.getParam( "username" );
        		String[] parts = username.split( "@" );
        		tcRequest.setParam( "username", parts[ 0 ] );
        		super.doLogin( commonConfig, pConfig, tcRequest );
        	}

            if (pConfig instanceof PersonalConfigAA) {
                PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                // In doLogin wird initPersonalConfig aufgerufen, und dabei sollte die Rolle
                // bereits korrekt auf die erste vom LDAP gelieferte uid gesetzt sein, also
                // auf die uid, die auch von getAARoles zur Bearbeitung geliefert wird.
                if (aaConfig.getRole() == null) {
                    logger.warning("Rolle nicht aus uid gesetzt, Login wird genutzt.");
                    aaConfig.setRole(tcRequest.getPasswordAuthentication().getUserName());
                }
                aaConfig.setRoles(null);
                logger.fine("Login unmittelbar: Rolle = " + aaConfig.getRole() + ", Rollen nicht ermittelt");
                fillInUserGroups(commonConfig, aaConfig, tcRequest);
            }
        } catch (TcSecurityException se) {
            if (origAuth != null && ldapManager instanceof LDAPManagerAA) {
                Collection possibleRoles = null;
                try {
                    possibleRoles = ((LDAPManagerAA) ldapManager).getPossibleRoles(origAuth.getUserName());
                } catch (NamingException e) {
                    throw se;
                }
                Collection authorizedRoles = authorized(commonConfig, pConfig, tcRequest, possibleRoles);
                Iterator itRoles = authorizedRoles.isEmpty() ? possibleRoles.iterator() : authorizedRoles.iterator();
                while (itRoles.hasNext()) try {
                    PasswordAuthentication newAuth = new PasswordAuthentication(itRoles.next().toString(), origAuth.getPassword());
                    tcRequest.setPasswordAuthentication(newAuth);
                    super.doLogin(commonConfig, pConfig, tcRequest);
                    pConfig.setUserLogin(origAuth.getUserName());
                    if (pConfig instanceof PersonalConfigAA) {
                        PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                        switch(authorizedRoles.size()) {
                        case 0:
                            aaConfig.setRole(null);
                            aaConfig.setRoles(null);
                            logger.fine("Login mittelbar: Login = " + origAuth.getUserName() + ", keine Rolle, keine Rollen");
                            break;
                        case 1:
                            // In doLogin wird initPersonalConfig aufgerufen, und dabei sollte die Rolle
                            // bereits korrekt auf die erste vom LDAP gelieferte uid gesetzt sein, also
                            // auf die uid, die auch von getAARoles zur Bearbeitung geliefert wird.
                            if (aaConfig.getRole() == null) {
                                logger.warning("Rolle nicht aus uid gesetzt, Prüfrolle wird genutzt.");
                                aaConfig.setRole(newAuth.getUserName());
                            }
                            aaConfig.setRoles(null);
                            logger.fine("Login mittelbar: Login = " + origAuth.getUserName() + ", Rolle = " + aaConfig.getRole() + ", Rollen nicht ermittelt");
                            break;
                        default:
                            aaConfig.setRole(null);
                            aaConfig.setRoles(new ArrayList(authorizedRoles));
                            logger.fine("Login mittelbar: Login = " + origAuth.getUserName() + ", Rolle nicht ermittelt, Rollen = " + aaConfig.getRoles());
                        }
                        fillInUserGroups(commonConfig, aaConfig, tcRequest);
                    }
                    return;
                } catch(TcSecurityException se2) {
                }
            }
            tcRequest.setPasswordAuthentication(origAuth);
            if (pConfig instanceof PersonalConfigAA && isSystemUser(tcRequest.getPasswordAuthentication())) {
                logger.warning("Login als Superadmin");
                PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                pConfig.setUserEmail("root@localhost");
                pConfig.setUserGivenName("System");
                pConfig.setUserID(new Integer(-1));
                pConfig.setUserLastName("Manager");
                pConfig.setUserName("System Manager");
                pConfig.setUserGroups(new String[]{PersonalConfigAA.GROUP_SYSTEM_USER});
                /*
                pConfig.setUserGroups(new String[]{PersonalConfigAA.GROUP_ADMIN,
                        PersonalConfigAA.GROUP_PARTIAL_ADMIN, PersonalConfigAA.GROUP_WRITE,
                        PersonalConfigAA.GROUP_READ_REMARKS, PersonalConfigAA.GROUP_EXPORT,
                        PersonalConfigAA.GROUP_READ_STANDARD, PersonalConfig.GROUP_USER,
                        PersonalConfigAA.GROUP_IN_PERSON});
                */
                pConfig.userLoggedIn(tcRequest.getPasswordAuthentication().getUserName());
                aaConfig.setRole(tcRequest.getPasswordAuthentication().getUserName());
                aaConfig.setRoles(null);
                aaConfig.setVerawebId(new Integer(-1));
                aaConfig.setOrgUnitId(new Integer(-1));
                return;
            }
            throw se;
        }
    }
    
    /**
     * Diese Methode f�hrt ein Ausloggen des Benutzers durch. Insbesondere werden
     * entsprechende Markierungen in seiner pers�nlichen Konfiguration gesetzt.<br>
     * Hier werden die speziellen Rollen- und Stellvertreterfelder geleert.
     * 
     * @param commonConfig Konfigurationsdaten des Octopus
     * @param pConfig pers�nliche Konfiguration des auszuloggenden Benutzers
     * @param tcRequest Benutzeranfrage
     * @see de.tarent.ldap.LoginManagerLDAPGeneric#doLogout(de.tarent.octopus.config.TcCommonConfig, de.tarent.octopus.server.PersonalConfig, de.tarent.octopus.request.TcRequest)
     */
    @Override
    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) {
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            aaConfig.setRole(null);
            aaConfig.setRoles(null);
            aaConfig.setProxy(null);
            aaConfig.setOrgUnitId(null);
            aaConfig.setVerawebId(null);
        }
        super.doLogout(commonConfig, pConfig, tcRequest);
    }

    //
    // Hilfsmethoden
    //
    
    boolean isSystemUser(PasswordAuthentication pwdAuth) {
        String systemLogin = getConfigurationString(KEY_SYSTEM_ADMIN_LOGIN);
        String systemPassword = getConfigurationString(KEY_SYSTEM_ADMIN_PASSWORD);
        return pwdAuth != null &&
            systemLogin != null && systemLogin.equals(pwdAuth.getUserName()) &&
            pwdAuth.getPassword() != null && new String(pwdAuth.getPassword()).equals(systemPassword);
    }
    
    /**
     * Diese Methode filtert aus einer Liste von AA-Rollen diejenigen heraus,
     * die im VerA.web-Kontext autorisiert sind.
     * 
     * @param commonConfig aktuelle allgemeine Konfiguration
     * @param pConfig aktuelle pers�nliche Konfiguration
     * @param tcRequest aktueller Request
     * @param roles Sammlung von AA-Rollen
     * @return Sammlung der AA-Rollen aus <code>roles</code>, die VerA.web-autorisiert sind.
     */
    Collection authorized(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest, Collection roles) {
        if (roles == null || roles.size() == 0)
            return roles;
        Collection result = new ArrayList(roles.size());
        OctopusContext cntx = new TcAll(tcRequest, new TcContent(),
                new TcConfig(commonConfig, pConfig, tcRequest.getModule()));
        Database database = new DatabaseVeraWeb(cntx);
        Iterator itRoles = roles.iterator();
        while(itRoles.hasNext()) {
            String role = safeFirstToString(itRoles.next());
            try {
                User user = (User)database.getBean("User",
                        database.getSelect("User").where(Expr.equal("username", role)));
                if (user != null)
                    result.add(role);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Fehler beim Einlesen von Benutzerdaten zu " + role, e);
            }
        }
        logger.fine("Autorisierte Rollen: " + result);
        return result;
    }
    
    /**
     * Diese Methode liest zu der AA-Rolle der Anmeldung eines Benutzers die
     * zugeh�rigen Octopus-Benutzergruppen aus und setzt diese in der pers�nlichen
     * Konfiguration.
     * 
     * @param commonConfig aktuelle allgemeine Konfiguration
     * @param pConfig aktuelle pers�nliche Konfiguration, der Octopus-Benutzergruppen
     *  zugeordnet werden.
     * @param tcRequest aktueller Request
     */
    void fillInUserGroups(TcCommonConfig commonConfig, PersonalConfigAA pConfig, TcRequest tcRequest) {
        pConfig.setVerawebId(null);
        pConfig.setOrgUnitId(null);
        if (pConfig.getRole() == null || pConfig.getRole().length() == 0) {
            List aaRoles = pConfig.getRoles();
            String group = (aaRoles != null && aaRoles.size() > 0) ?
                    PersonalConfigAA.GROUP_UNCLEAR_ROLE : PersonalConfigAA.GROUP_UNAUTHORIZED;
            pConfig.setUserGroups(new String[]{ group });
        } else {
            OctopusContext cntx = new TcAll(tcRequest, new TcContent(),
                    new TcConfig(commonConfig, pConfig, tcRequest.getModule()));
            Database database = new DatabaseVeraWeb(cntx);
            List groups = new ArrayList();
            try {
                User user = (User)database.getBean("User",
                        database.getSelect("User").where(Expr.equal("username", pConfig.getRole())));
                if (user != null) {
                    pConfig.setVerawebId(user.id);
                    pConfig.setOrgUnitId(user.orgunit != null ? user.orgunit : new Integer(-1));
                    switch(user.role.intValue()) {
                    case User.ROLE_READ_RESTRICTED:
                        groups.add(PersonalConfig.GROUP_USER);
                        groups.add(PersonalConfigAA.GROUP_READ_STANDARD);
                        groups.add(PersonalConfigAA.GROUP_EXPORT);
                        break;
                    case User.ROLE_READ_FULL:
                        groups.add(PersonalConfig.GROUP_USER);
                        groups.add(PersonalConfigAA.GROUP_READ_STANDARD);
                        groups.add(PersonalConfigAA.GROUP_READ_REMARKS);
                        groups.add(PersonalConfigAA.GROUP_EXPORT);
                        break;
                    case User.ROLE_READ_WRITE_RESTRICTED:
                        groups.add(PersonalConfig.GROUP_USER);
                        groups.add(PersonalConfigAA.GROUP_READ_STANDARD);
                        groups.add(PersonalConfigAA.GROUP_WRITE);
                        groups.add(PersonalConfigAA.GROUP_EXPORT);
                        break;
                    case User.ROLE_READ_WRITE_FULL:
                        groups.add(PersonalConfig.GROUP_USER);
                        groups.add(PersonalConfigAA.GROUP_READ_STANDARD);
                        groups.add(PersonalConfigAA.GROUP_READ_REMARKS);
                        groups.add(PersonalConfigAA.GROUP_WRITE);
                        groups.add(PersonalConfigAA.GROUP_EXPORT);
                        break;
                    case User.ROLE_PARTIAL_ADMIN:
                        groups.add(PersonalConfig.GROUP_USER);
                        groups.add(PersonalConfigAA.GROUP_READ_STANDARD);
                        groups.add(PersonalConfigAA.GROUP_READ_REMARKS);
                        groups.add(PersonalConfigAA.GROUP_WRITE);
                        groups.add(PersonalConfigAA.GROUP_EXPORT);
                        groups.add(PersonalConfigAA.GROUP_PARTIAL_ADMIN);
                        break;
                    case User.ROLE_GLOBAL_ADMIN:
                        groups.add(PersonalConfig.GROUP_USER);
                        groups.add(PersonalConfigAA.GROUP_READ_STANDARD);
                        groups.add(PersonalConfigAA.GROUP_READ_REMARKS);
                        groups.add(PersonalConfigAA.GROUP_WRITE);
                        groups.add(PersonalConfigAA.GROUP_EXPORT);
                        groups.add(PersonalConfigAA.GROUP_PARTIAL_ADMIN);
                        groups.add(PersonalConfigAA.GROUP_ADMIN);
                        break;
                    }
                    groups.add(pConfig.getProxy() != null ? PersonalConfigAA.GROUP_BY_PROXY : PersonalConfigAA.GROUP_IN_PERSON);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Fehler beim Einlesen von Benutzerdaten zu " + pConfig.getRole(), e);
            }
            if (groups.size() == 0)
                groups.add(PersonalConfigAA.GROUP_UNAUTHORIZED);
            pConfig.setUserGroups((String[]) groups.toArray(new String[groups.size()]));
            logger.fine("Ermittelte Benutzergruppen: " + groups);
        }
    }
    
    /**
     * Diese Methode f�hrt ein toString aus, sichert dies aber durch einen <code>null</code>-Test
     * vorher ab. Sollte das Objekt eine Liste sein, so wird toString des ersten enthaltenen Objekts
     * ausgef�hrt.
     * 
     * @param o Objekt
     * @return {@link String}-Darstellung des Objekts, gegebenenfalls des ersten enthaltenen Objekts
     */
    final static String safeFirstToString(Object o) {
        if (o instanceof List) {
            List l = (List) o;
            o = l.isEmpty() ? null : l.get(0);
        }
        return o == null ? null : o.toString();
    }
    
    /**
     * Diese Methode versucht, das �bergebene Objekt als Integer zu interpretieren, wobei
     * dezimale, oktale und hexadezimale Darstellungen akzeptiert werden. Sollte das Objekt
     * eine Liste sein, so wird versucht, das erste enthaltene Objekt als Integer zu interpretieren.
     * 
     * @param o Objekt
     * @return {@link Integer}-Darstellung des Objekts, gegebenenfalls des ersten enthaltenen Objekts
     */
    final static Integer safeFirstToInteger(Object o) {
        if (o instanceof List) {
            List l = (List) o;
            o = l.isEmpty() ? null : l.get(0);
        }
        if (o == null)
            return null;
        if (o instanceof Integer)
            return (Integer) o;
        try {
            return Integer.decode(o.toString());
        } catch(NumberFormatException nfe) {
            return null;
        }
    }
    
    //
    // gesch�tzte Member
    //
    final static Logger logger = Logger.getLogger(LoginManagerLDAPAA.class.getName());
}
