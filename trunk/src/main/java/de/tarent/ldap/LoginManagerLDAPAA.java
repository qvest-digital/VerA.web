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
import de.tarent.octopus.config.CommonConfig;
import de.tarent.octopus.config.OctopusConfig;
import de.tarent.octopus.config.OctopusEnvironment;
import de.tarent.octopus.content.Content;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.request.OctopusRequest;
import de.tarent.octopus.security.OctopusSecurityException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.OctopusContextImpl;
import de.tarent.octopus.server.PersonalConfig;


/**
 * Diese Klasse dient als LoginManager über LDAP im Kontext des Auswärtigen Amts.
 * Insbesondere beachtet wird hier das Anmelden wahlweise als Benutzer oder als
 * Rolle. Ebenfalls beachtet wird das Anmelden mit noch zu erweiternden Namen.
 * 
 * @author mikel
 */
public class LoginManagerLDAPAA extends LoginManagerLDAPGeneric implements LoginManagerAA {
    //
    // Konstanten
    //
    /** Schlüssel des Konfigurationseintrags für den Rollenfilter */
    public final static String KEY_ROLE_FILTER = "aarolefilter";
    
    /** Schlüssel des Konfigurationseintrags für den Superadmin-Login */
    public final static String KEY_SYSTEM_ADMIN_LOGIN = "systemlogin";

    /** Schlüssel des Konfigurationseintrags für das Superadmin-Passwort */
    public final static String KEY_SYSTEM_ADMIN_PASSWORD = "systempassword";
    
    //
    // Schnittstelle LoginManagerAA
    //
    /**
     * Diese Methode ändert die persönliche Konfiguration so ab, dass sie
     * in Vertretung der angegebenen Rolle handelt.<br>
     * TODO: Ablauf der Gültigkeit mitaufnehmen und bei Ablauf ungültig werden.
     * 
     * @param octx anzupassender Octopus-Kontext der Sitzung des Vertreters
     * @param proxyDescription Beschreibungs-Bean der Vertretung
     * @throws OctopusSecurityException Wenn keine authentisierte persönliche Konfiguration
     *  vorliegt oder schon als Vertreter agiert wird.
     * @see LoginManagerAA#setProxy(OctopusContext, Proxy)
     */
    public void setProxy(OctopusContext octx, Proxy proxyDescription) throws OctopusSecurityException {
        PersonalConfigAA pConfig = (PersonalConfigAA) octx.configImpl();
        if (pConfig == null || !pConfig.getGrants().isAuthenticated())
            throw new OctopusSecurityException("Missing personal config for proxying.");
        if (pConfig.getProxy() != null)
            throw new OctopusSecurityException("Proxying is not transitive.");
        pConfig.setUserGroups(new String[0]);
        pConfig.setProxy(proxyDescription.proxy);
        pConfig.setRole(proxyDescription.userRole);
        fillInUserGroups(octx.commonConfig(), pConfig, octx.getRequestObject());
    }

    /**
     * Diese Methode liefert eine Auflistung verfügbarer AA-Rollen, aus denen
     * VerA.web-Benutzer ausgewählt werden können. 
     * 
     * @return Liste verfügbarer AA-Rollen.
     * @throws OctopusSecurityException 
     * @see LoginManagerAA#getAARoles()
     */
    public Set getAARoles() throws OctopusSecurityException {
        if (ldapManager instanceof LDAPManagerAA)
            try {
                return ((LDAPManagerAA)ldapManager).getPossibleRoles();
            } catch (NamingException e) {
                throw new OctopusSecurityException("Rollen konnten nicht bezogen werden", e);
            }
        else
            return null;
    }
    
    //
    // LoginManagerLDAPGeneric Überschreibungen
    //
    /**
     * Diese Methode setzt nach einem erfolgreichen Login in der PersonalConfig in
     * eigener Weise Attribute.
     * 
     * @param pConfig PersonalConfig des neu eingelogten Benutzers
     * @param userName Benutzer-ID des neu eingeloggten Benutzers
     * @throws LDAPException
     * @see #doLogin(CommonConfig, PersonalConfig, OctopusRequest)
     * @see LoginManagerLDAPGeneric#initPersonalConfig(de.tarent.octopus.server.PersonalConfig, java.lang.String)
     */
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
     * @see #doLogin(CommonConfig, PersonalConfig, OctopusRequest)
     * @see LoginManagerLDAPGeneric#initLDAPManager()
     */
    protected void initLDAPManager() throws LDAPException {
        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, getConfigurationString(OctopusEnvironment.KEY_LDAP_BASE_DN));
        params.put(LDAPManager.KEY_RELATIVE, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
        params.put(LDAPManager.KEY_RELATIVE_USER, getConfigurationString(OctopusEnvironment.KEY_LDAP_RELATIVE));
        params.put(LDAPManagerAA.KEY_ROLE_FILTER, getConfigurationString(KEY_ROLE_FILTER));
        ldapManager = LDAPManager.login(
                LDAPManagerAA.class,
                getConfigurationString(OctopusEnvironment.KEY_LDAP_URL),
                params
                );
    }

    /**
     * Diese Methode überprüft die Credentials im Request und setzt im Erfolgsfall die
     * entsprechenden Daten in der übergebenen PersonalConfig.<br>
     * Hier wird auf die Funktionalität, die schon in {@link LoginManagerLDAPGeneric}
     * vorliegt, zurückgegriffen, wenn diese nicht zum Erfolg führt, wird aber versucht,
     * über den Inhalt des person-Attributs den Login zu schaffen.<br>
     * Zusätzlich gibt es die Möglichkeit, sich als ein vorgegebener System-Admin ohne
     * LDAP-Authentisierung anzumelden
     * 
     * @param commonConfig Konfigurationsdaten des Octopus
     * @param pConfig persönliche Konfiguration des einzuloggenden Benutzers
     * @param octopusRequest Benutzeranfrage mit Authentisierungsdaten
     * @throws OctopusSecurityException bei fehlgeschlagener Authorisierung
     * @see de.tarent.ldap.LoginManagerLDAPGeneric#doLogin(de.tarent.octopus.config.CommonConfig, de.tarent.octopus.server.PersonalConfig, de.tarent.octopus.request.OctopusRequest)
     */
    protected void doLogin(CommonConfig commonConfig, PersonalConfig pConfig, OctopusRequest octopusRequest) throws OctopusSecurityException {
        PasswordAuthentication origAuth = octopusRequest.getPasswordAuthentication();
        // http://www.ietf.org/internet-drafts/draft-ietf-ldapbis-authmeth-18.txt
        // Clients SHOULD disallow an empty password input to a Name/Password Authentication user interface.
        if (origAuth != null && (origAuth.getPassword() == null || origAuth.getPassword().length == 0))
            throw new OctopusSecurityException("Leere Passwörter sind nicht zulässig.");
        try {
            super.doLogin(commonConfig, pConfig, octopusRequest);
            if (pConfig instanceof PersonalConfigAA) {
                PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                // In doLogin wird initPersonalConfig aufgerufen, und dabei sollte die Rolle
                // bereits korrekt auf die erste vom LDAP gelieferte uid gesetzt sein, also
                // auf die uid, die auch von getAARoles zur Bearbeitung geliefert wird.
                if (aaConfig.getRole() == null) {
                    logger.warning("Rolle nicht aus uid gesetzt, Login wird genutzt.");
                    aaConfig.setRole(octopusRequest.getPasswordAuthentication().getUserName());
                }
                aaConfig.setRoles(null);
                logger.fine("Login unmittelbar: Rolle = " + aaConfig.getRole() + ", Rollen nicht ermittelt");
                fillInUserGroups(commonConfig, aaConfig, octopusRequest);
            }
        } catch (OctopusSecurityException se) {
            if (origAuth != null && ldapManager instanceof LDAPManagerAA) {
                Collection possibleRoles = null;
                try {
                    possibleRoles = ((LDAPManagerAA) ldapManager).getPossibleRoles(origAuth.getUserName());
                } catch (NamingException e) {
                    throw se;
                }
                Collection authorizedRoles = authorized(commonConfig, pConfig, octopusRequest, possibleRoles);
                Iterator itRoles = authorizedRoles.isEmpty() ? possibleRoles.iterator() : authorizedRoles.iterator();
                while (itRoles.hasNext()) try {
                    PasswordAuthentication newAuth = new PasswordAuthentication(itRoles.next().toString(), origAuth.getPassword());
                    octopusRequest.setPasswordAuthentication(newAuth);
                    super.doLogin(commonConfig, pConfig, octopusRequest);
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
                        fillInUserGroups(commonConfig, aaConfig, octopusRequest);
                    }
                    return;
                } catch(OctopusSecurityException se2) {
                }
            }
            octopusRequest.setPasswordAuthentication(origAuth);
            if (pConfig instanceof PersonalConfigAA && isSystemUser(octopusRequest.getPasswordAuthentication())) {
                logger.warning("Login als Superadmin");
                PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                pConfig.setUserEmail("root@localhost");
                pConfig.setUserGivenName("System");
                pConfig.setUserID(new Integer(-1));
                pConfig.setUserLastName("Manager");
                pConfig.setUserName("System Manager");
                pConfig.setUserGroups(new String[]{PersonalConfigAA.GROUP_ADMIN,
                        PersonalConfigAA.GROUP_PARTIAL_ADMIN, PersonalConfigAA.GROUP_WRITE,
                        PersonalConfigAA.GROUP_READ_REMARKS, PersonalConfigAA.GROUP_EXPORT,
                        PersonalConfigAA.GROUP_READ_STANDARD, PersonalConfig.GROUP_USER,
                        PersonalConfigAA.GROUP_IN_PERSON});
                pConfig.userLoggedIn(octopusRequest.getPasswordAuthentication().getUserName());
                aaConfig.setRole(octopusRequest.getPasswordAuthentication().getUserName());
                aaConfig.setRoles(null);
                aaConfig.setVerawebId(new Integer(-1));
                aaConfig.setOrgUnitId(new Integer(-1));
                return;
            }
            throw se;
        }
    }
    
    /**
     * Diese Methode führt ein Ausloggen des Benutzers durch. Insbesondere werden
     * entsprechende Markierungen in seiner persönlichen Konfiguration gesetzt.<br>
     * Hier werden die speziellen Rollen- und Stellvertreterfelder geleert.
     * 
     * @param commonConfig Konfigurationsdaten des Octopus
     * @param pConfig persönliche Konfiguration des auszuloggenden Benutzers
     * @param octopusRequest Benutzeranfrage
     * @see de.tarent.ldap.LoginManagerLDAPGeneric#doLogout(de.tarent.octopus.config.CommonConfig, de.tarent.octopus.server.PersonalConfig, de.tarent.octopus.request.OctopusRequest)
     */
    protected void doLogout(CommonConfig commonConfig, PersonalConfig pConfig, OctopusRequest octopusRequest) {
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            aaConfig.setRole(null);
            aaConfig.setRoles(null);
            aaConfig.setProxy(null);
            aaConfig.setOrgUnitId(null);
            aaConfig.setVerawebId(null);
        }
        super.doLogout(commonConfig, pConfig, octopusRequest);
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
     * @param pConfig aktuelle persönliche Konfiguration
     * @param octopusRequest aktueller Request
     * @param roles Sammlung von AA-Rollen
     * @return Sammlung der AA-Rollen aus <code>roles</code>, die VerA.web-autorisiert sind.
     */
    Collection authorized(CommonConfig commonConfig, PersonalConfig pConfig, OctopusRequest octopusRequest, Collection roles) {
        if (roles == null || roles.size() == 0)
            return roles;
        Collection result = new ArrayList(roles.size());
        OctopusContext cntx = new OctopusContextImpl(octopusRequest, new Content(),
                new OctopusConfig(commonConfig, pConfig, octopusRequest.getModule()));
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
     * zugehörigen Octopus-Benutzergruppen aus und setzt diese in der persönlichen
     * Konfiguration.
     * 
     * @param commonConfig aktuelle allgemeine Konfiguration
     * @param pConfig aktuelle persönliche Konfiguration, der Octopus-Benutzergruppen
     *  zugeordnet werden.
     * @param octopusRequest aktueller Request
     */
    void fillInUserGroups(CommonConfig commonConfig, PersonalConfigAA pConfig, OctopusRequest octopusRequest) {
        pConfig.setVerawebId(null);
        pConfig.setOrgUnitId(null);
        if (pConfig.getRole() == null || pConfig.getRole().length() == 0) {
            List aaRoles = pConfig.getRoles();
            String group = (aaRoles != null && aaRoles.size() > 0) ?
                    PersonalConfigAA.GROUP_UNCLEAR_ROLE : PersonalConfigAA.GROUP_UNAUTHORIZED;
            pConfig.setUserGroups(new String[]{ group });
        } else {
            OctopusContext cntx = new OctopusContextImpl(octopusRequest, new Content(),
                    new OctopusConfig(commonConfig, pConfig, octopusRequest.getModule()));
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
     * Diese Methode führt ein toString aus, sichert dies aber durch einen <code>null</code>-Test
     * vorher ab. Sollte das Objekt eine Liste sein, so wird toString des ersten enthaltenen Objekts
     * ausgeführt.
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
     * Diese Methode versucht, das übergebene Objekt als Integer zu interpretieren, wobei
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
    // geschützte Member
    //
    final static Logger logger = Logger.getLogger(LoginManagerLDAPAA.class.getName());
}
