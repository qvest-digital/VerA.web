/*
 * $Id: PersonalConfigAA.java,v 1.1 2007/06/20 11:56:53 christoph Exp $
 * 
 * Created on 20.04.2005
 */
package de.tarent.octopus;

import java.text.MessageFormat;
import java.util.List;

import de.tarent.aa.veraweb.beans.Grants;
import de.tarent.octopus.config.PersonalConfigImpl;

/**
 * Diese Klasse stellt die Implementierung von {@link de.tarent.octopus.server.PersonalConfig}
 * für das Auswärtige Amt dar.
 * 
 * @author mikel
 */
public class PersonalConfigAA extends PersonalConfigImpl {
    //
    // Konstanten
    //
    /** Gruppe der Benutzer, die als Vertreter angemeldet sind */
    public final static String GROUP_BY_PROXY = "ByProxy";
    /** Gruppe der Benutzer, die persönlich (also nicht als Vertreter) angemeldet sind */
    public final static String GROUP_IN_PERSON = "InPerson";
    /** Gruppe der Benutzer, deren AA-Rolle nach Login nicht klar ist */
    public final static String GROUP_UNCLEAR_ROLE = "Unclear";
    /** Gruppe der Benutzer, deren (gewählte) Rollen nicht autorisiert sind */
    public final static String GROUP_UNAUTHORIZED = "Unauthorized";
    /** Gruppe der Benutzer, die die Standardfelder lesen dürfen */
    public final static String GROUP_READ_STANDARD = "StandardFieldsReader";
    /** Gruppe der Benutzer, die die limitierten Bemerkungsfelder lesen dürfen */
    public final static String GROUP_READ_REMARKS = "RemarkFieldsReader";
    /** Gruppe der Benutzer, die exportieren dürfen */
    public final static String GROUP_EXPORT = "Exporter";
    /** Gruppe der Benutzer, die schreiben dürfen, abhängig von ihren Leserechten. */
    public final static String GROUP_WRITE = "Writer";
    /** Gruppe der Teiladministratoren */
    public final static String GROUP_PARTIAL_ADMIN = "PartialAdmin";
    /** Gruppe der Volladministratoren */
    public final static String GROUP_ADMIN = GROUP_ADMINISTRATOR;
    
    //
    // Überschreibungen von PersonalConfigImpl
    //
    /**
     * @see de.tarent.octopus.config.PersonalConfigImpl#setUserGroups(java.lang.String[], java.lang.String)
     */
    public void setUserGroups(String[] newGroups, String area) {
        grants = null;
        super.setUserGroups(newGroups, area);
    }
    
    /**
     * @see de.tarent.octopus.config.PersonalConfigImpl#setUserGroups(java.lang.String[])
     */
    public void setUserGroups(String[] newGroups) {
        grants = null;
        super.setUserGroups(newGroups);
    }
    
    //
    // Getter und Setter
    //
    /**
     * Dieses Attribut stellt die Mandanten-Id des Angemeldeten dar.
     * 
     * @return Mandanten-Id des Angemeldeten.
     */
    public Integer getOrgUnitId() {
        return orgUnitId;
    }
    
    /**
     * Dieses Attribut stellt die Mandanten-Id des Angemeldeten dar.
     * 
     * @param orgUnitId Mandanten-Id des Angemeldeten.
     */
    public void setOrgUnitId(Integer orgUnitId) {
        this.orgUnitId = orgUnitId;
    }
    
    /**
     * Dieses Attribut stellt die VerA.web-Id des Angemeldeten dar.
     * 
     * @return VerA.web-Id des Angemeldeten.
     */
    public Integer getVerawebId() {
        return verawebId;
    }
    
    /**
     * Dieses Attribut stellt die VerA.web-Id des Angemeldeten dar.
     * 
     * @param verawebId VerA.web-Id des Angemeldeten.
     */
    public void setVerawebId(Integer verawebId) {
        this.verawebId = verawebId;
    }
    
    /**
     * Dieses Attribut stellt die AA-Rolle des handelnden Stellvertreters dar.
     * 
     * @return AA-Rolle des Stellvertreters; <code>null</code>, wenn
     *  nicht in Vertretung gearbeitet wird.
     */
    public String getProxy() {
        return proxy;
    }
    
    /**
     * Dieses Attribut stellt die AA-Rolle des handelnden Stellvertreters dar.
     * 
     * @param proxy AA-Rolle des Stellvertreters; <code>null</code>, wenn
     *  nicht in Vertretung gearbeitet wird.
     */
    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
    
    /**
     * Dieses Attribut stellt die AA-Rolle der Anmeldung dar.
     * 
     * @return AA-Rolle
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Dieses Attribut stellt die AA-Rolle der Anmeldung dar.
     * 
     * @param role AA-Rolle
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Dieses Attribut stellt die verfügbaren AA-Rollen der Anmeldung dar.
     * 
     * @return AA-Rollen der Anmeldung
     */
    public List getRoles() {
        return roles;
    }
    
    /**
     * Dieses Attribut stellt die verfügbaren AA-Rollen der Anmeldung dar.
     * 
     * @param roles AA-Rollen der Anmeldung
     */
    public void setRoles(List roles) {
        this.roles = roles;
    }

    /**
     * Diese berechnete Attribut liefert die Rolle gegebenenfalls ergänzt um
     * Vertretungsinformationen.
     * 
     * @return "Rolle" oder "Rolle (i.V. Vertreter)"
     */
    public String getRoleWithProxy() {
        return proxy != null ? roleAndProxyFormat.format(new String[]{role, proxy}) : role;
    }
    
    /**
     * Diese Methode liefert eine {@link Grants}-Instanz passend zu den 
     * Benutzergruppen der persönlichen Konfiguration. 
     * 
     * @return {@link Grants} des Benutzers
     */
    public Grants getGrants() {
        if (grants == null)
            grants = new AAGrants();
        return grants;
    }
    
    //
    // innere Klassen
    //
    /**
     * Diese Klasse implementiert {@link Grants} auf Basis der in der persönlichen
     * Konfiguration gehaltenen Octopus-Benutzergruppen. 
     */
    class AAGrants implements Grants {
        //
        // Implementierung von Grants
        //
        /** @see Grants#isAuthenticated() */
        public boolean isAuthenticated()        { return authenticated; }
        /** @see Grants#isUser() */
        public boolean isUser()                 { return user; }
        /** @see Grants#mayReadStandardFields() */
        public boolean mayReadStandardFields()  { return readStandardFields; }
        /** @see Grants#mayReadRemarkFields() */
        public boolean mayReadRemarkFields()    { return readRemarkFields; }
        /** @see Grants#mayExport() */
        public boolean mayExport()              { return export; }
        /** @see Grants#mayWrite() */
        public boolean mayWrite()               { return write; }
        /** @see Grants#isPartialAdmin() */
        public boolean isPartialAdmin()         { return partialAdmin; }
        /** @see Grants#isAdmin() */
        public boolean isAdmin()                { return admin; }
        
        //
        // Konstruktor
        //
        /**
         * Der Konstruktor ermittelt die gehaltenen Flag-Werte aus den
         * Gruppenmitgliedschaften des Benutzers.
         */
        AAGrants() {
            authenticated = !(isUserInGroup(GROUP_ANONYMOUS) || isUserInGroup(GROUP_LOGGED_OUT));
            user = isUserInGroup(GROUP_USER);
            readStandardFields = isUserInGroup(GROUP_READ_STANDARD);
            readRemarkFields = isUserInGroup(GROUP_READ_REMARKS);
            export = isUserInGroup(GROUP_EXPORT);
            write = isUserInGroup(GROUP_WRITE);
            partialAdmin = isUserInGroup(GROUP_PARTIAL_ADMIN);
            admin = isUserInGroup(GROUP_ADMIN);
        }
        
        //
        // Member
        //
        final boolean authenticated;
        final boolean user;
        final boolean readStandardFields;
        final boolean readRemarkFields;
        final boolean export;
        final boolean write;
        final boolean partialAdmin;
        final boolean admin;
    }
    
    //
    // Variablen
    //
    String role = null;
    Integer orgUnitId = null;
    Integer verawebId = null;
    String proxy = null;
    List roles = null;
    Grants grants = null;
    
    final static MessageFormat roleAndProxyFormat = new MessageFormat("{0} (i.V. {1})");
}
