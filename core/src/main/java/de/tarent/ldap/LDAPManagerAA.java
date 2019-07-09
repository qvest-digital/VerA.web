package de.tarent.ldap;
import lombok.extern.log4j.Log4j2;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Diese Klasse
 *
 * @author mikel
 */
@Log4j2
public class LDAPManagerAA extends LDAPManager {
    //
    // Konstanten
    //
    /** LDAP-Objektklasse fuer AA-Rollen */
    /**
     * Parameter-Schluessel fuer den AA-Rollen-Filter
     */
    public final static String KEY_ROLE_FILTER = "role-filter";

    //
    // Konstruktor
    //

    /**
     * Dieser Konstruktor reicht die übergebenen Parameter durch und setzt die
     * Vorgabe-Objektklassen.
     *
     * @param lctx   initialer LDAP-Kontext, auf dem dieser LDAP-Manager arbeitet
     * @param params LDAP-Manager-Parameter, vergleiche
     *               {@link LDAPManager#LDAPManager(InitialLdapContext, Map)}
     */
    public LDAPManagerAA(InitialLdapContext lctx, Map params) {
        super(lctx, params);
        String roleFilter = (String) params.get(KEY_ROLE_FILTER);
        // setup a valid filter template, by default we only search for the uid
        String objFilter = "(uid={0})";
        if (this.defaultUserObjectClass != null && this.defaultUserObjectClass.length() > 0) {
            // if set, we extend the default search filter template by the defined user object class
            objFilter = "(&(objectclass=" + this.defaultUserObjectClass + ")" + objFilter + ")";
        }
        filterTemplate = new MessageFormat(roleFilter != null ? roleFilter : objFilter);
    }

    //
    // Öffentliche AA-spezifische Methoden
    //

    /**
     * Diese Methode erzeugt zu einem Benutzer eine {@link Map} seiner Attribute und
     * Attributswerte. Wenn ein Attribut mehrere Werte hat, so wird in der {@link Map}
     * der Attributname auf eine {@link java.util.List} der Werte abgebildet.
     *
     * @param userName Name des Benutzers
     * @return {@link Map} der Attribute des Benutzers
     * @throws LDAPException
     */
    public Map getUserData(String userName) throws LDAPException {
        try {
            String dn = fullUserDN(userName);
            return LDAPUtil.toMap(lctx.getAttributes(dn));
        } catch (NamingException e) {
            throw new LDAPException("Es ist ein Fehler beim Holen der Userdaten aufgetreten!", e);
        }
    }

    /**
     * Diese Methode ermittelt alle verfügbaren Rollen.
     *
     * @return Sammlung verfügbarer Rollen
     * @throws NamingException
     */
    public Set getPossibleRoles() throws NamingException {
        SearchControls cons = new SearchControls();
        this.initializeSearchControls(cons);
        Set roleUids = getPossibleRoles("(objectclass=" + this.defaultUserObjectClass + ")", cons);
        logger.debug("Alle verfügbaren Rollen: " + roleUids);
        return roleUids;
    }

    /**
     * Diese Methode ermittelt die verfügbaren Rollen zu einem Vorname.Nachname-Login.
     * Dies geschieht über eine Suche in den Rollen, die über den Rollenfilter (Parameter
     * mit Schlüssel {@link #KEY_ROLE_FILTER}) gefiltert wird. Dieser Rollenfilter ist
     * ein LDAP-Suchfilter, in dem aber {0} als Variable erlaubt ist, in die der Login
     * eingetragen wird.<br>
     * Zum Beispiel sucht folgender Filter die AARole-Knoten, in denen das login in
     * sinnvoller Weise im person-Attribut steht:<br>
     * (&amp;(|(person=uid={0}&#64;auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0}.auswaertiges-amt.de,ou=Personen,dc=aa)
     * (person=uid={0},
     * ou=Personen,dc=aa))(objectclass=AARole))
     *
     * @param login Login, zu dem mögliche Rollen gesucht werden sollen
     * @return Sammlung möglicher Rollen zu dem Login
     * @throws NamingException
     */
    public Set getPossibleRoles(String login) throws NamingException {
        SearchControls cons = new SearchControls();
        this.initializeSearchControls(cons);
        Set roleUids = getPossibleRoles(filterTemplate.format(new Object[] { login }), cons);
        logger.debug("Rollen für " + login + ": " + roleUids);
        return roleUids;
    }

    /**
     * Diese Methode ermittelt die verfügbaren Rollen zu einem Filter.
     *
     * @param filter JNDI-Suchfilter
     * @param cons   SearchControls
     * @return Sammlung möglicher Rollen zu dem Filter
     * @throws NamingException
     */
    public Set getPossibleRoles(String filter, SearchControls cons) throws NamingException {
        Set roleUids = new HashSet();
        try {
            this.recreateInitialContext();
            NamingEnumeration ergebnis = lctx.search(relativeUser.substring(1) + baseDN, filter, cons);
            while (ergebnis.hasMore()) {
                Attributes result = ((SearchResult) ergebnis.nextElement()).getAttributes();
                // TODO: hier wird einer der uid-Einträge genommen, nicht alle. Nach Anpassung andere getPossibleRoles
                // entsprechend anpassen
                roleUids.add(result.get("uid").get());
            }
            return roleUids;
        } catch (LDAPException e) {
            throw new NamingException("Die Verbindung zum LDAP Server wurde geschlossen.");
        }
    }
}
