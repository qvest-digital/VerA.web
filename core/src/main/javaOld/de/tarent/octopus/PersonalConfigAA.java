package de.tarent.octopus;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
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
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
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
import de.tarent.aa.veraweb.beans.Grants;
import de.tarent.octopus.config.TcPersonalConfig;

import java.text.MessageFormat;
import java.util.List;

/**
 * Diese Klasse stellt die Implementierung von {@link de.tarent.octopus.server.PersonalConfig}
 * für das Auswärtige Amt dar.
 *
 * @author mikel
 */
public class PersonalConfigAA extends TcPersonalConfig {
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
    /** Gruppe der Systemuser */
    public final static String GROUP_SYSTEM_USER = "SystemUser";

    //
    // überschreibungen von TcPersonalConfig
    //
    /**
     * @see de.tarent.octopus.config.TcPersonalConfig#setUserGroups(java.lang.String[], java.lang.String)
     */
    @Override
    public void setUserGroups(String[] newGroups, String area) {
        grants = null;
        super.setUserGroups(newGroups, area);
    }

    /**
     * @see de.tarent.octopus.config.TcPersonalConfig#setUserGroups(java.lang.String[])
     */
    @Override
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
        /** @see Grants#isSystemUser() */
        public boolean isSystemUser()           { return systemUser; }

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
            systemUser = isUserInGroup(GROUP_SYSTEM_USER);
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
        final boolean systemUser;
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
