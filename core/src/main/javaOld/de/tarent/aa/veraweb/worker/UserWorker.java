package de.tarent.aa.veraweb.worker;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.aa.veraweb.beans.User;
import de.tarent.octopus.LoginManagerAA;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dieser Octopus-Worker erledigt Aufgaben, die verwaltend die Benutzer betreffen.
 *
 * @author mikel
 */
public class UserWorker {
    //
    // Öffentliche Konstanten
    //
    /** Parameter: Wer alles? */
    public final static String PARAM_DOMAIN = UserListWorker.PARAM_DOMAIN;

    /** Parameterwert: beliebige Benutzer */
    public final static String PARAM_DOMAIN_VALUE_ALL = UserListWorker.PARAM_DOMAIN_VALUE_ALL;

    /** Parameterwert: beliebige Benutzer */
    public final static String PARAM_DOMAIN_VALUE_UNASSIGNED = "unassigned";

    //
    // Öffentliche statische Methoden
    //
    /**
     * Diese statische Hilfsmethode lädt einen VerA.web-Benutzer.
     *
     * @param cntx Octopus-Kontext
     * @param id Benutzer-ID; wenn <code>null</code>, so wird <code>null</code> zurückgeliefert.
     * @return das passende {@link User}-Objekt oder <code>null</code>
     */
    public static User getUser(OctopusContext cntx, Integer id) throws BeanException, IOException {
        if (id == null) return null;
        Database database = new DatabaseVeraWeb(cntx);
        return (User) database.getBean("User", id);
    }

    //
    // Octopus-Aktionen
    //
    /** Eingabeparameter für Aktion {@link #showDetail(OctopusContext, Integer)} */
    public static final String[] INPUT_showDetail = {"id"};
    /** Eingabeparameterzwang für Aktion {@link #showDetail(OctopusContext, Integer)} */
    public static final boolean[] MANDATORY_showDetail = {false};
    /** Octopus-Ausgabe-Parameter für {@link #showDetail(OctopusContext, Integer)} */
    public static final String OUTPUT_showDetail = "user";
    /**
     * Diese Octopus-Aktion lädt einen VerA.web-Benutzer.
     *
     * @param cntx Octopus-Kontext
     * @param id Benutzer-ID; wenn <code>null</code>, so wird <code>null</code> zurückgeliefert.
     * @return das passende {@link User}-Objekt oder <code>null</code>
     * @throws BeanException
     * @throws IOException
     */
    public User showDetail(OctopusContext cntx, Integer id) throws BeanException, IOException {
        return getUser(cntx, id);
    }

    /** Eingabeparameter für Aktion {@link #showActiveUser(OctopusContext)} */
    public static final String[] INPUT_showActiveUser = {};
    /** Eingabeparameterzwang für Aktion {@link #showActiveUser(OctopusContext)} */
    public static final boolean[] MANDATORY_showActiveUser = {};
    /** Octopus-Ausgabe-Parameter für {@link #showActiveUser(OctopusContext)} */
    public static final String OUTPUT_showActiveUser = "user";
    /**
     * Diese Aktion lädt den aktuell eingeloggten User.
     *
     * @param octx Octopus-Kontext
     * @return {@link User}-Objekt zum aktuell eingelogten Benutzer oder <code>null</code>
     * @throws IOException
     * @throws BeanException
     */
    public User showActiveUser(OctopusContext octx) throws BeanException, IOException {
        PersonalConfigAA aaConfig = (PersonalConfigAA) octx.personalConfig();
        return aaConfig != null ? getUser(octx, aaConfig.getVerawebId()) : null;
    }

    /** Eingabeparameter für Aktion {@link #showAARoleList(OctopusContext, String)} */
    public static final String[] INPUT_showAARoleList = { PARAM_DOMAIN };
    /** Eingabeparameterzwang für Aktion {@link #showAARoleList(OctopusContext, String)} */
    public static final boolean[] MANDATORY_showAARoleList = { false };
    /** Octopus-Ausgabe-Parameter für {@link #showAARoleList(OctopusContext, String)} */
    public static final String OUTPUT_showAARoleList = "roleList";
    /**
     * Diese Aktion lädt eine Liste verfügbarer AA-Rollen.
     *
     * @param octx Octopus-Kontext
     * @param domain Domäne der Rollen
     * @return Liste verfügbarer AA-Rollen
     * @throws TcSecurityException
     * @throws IOException
     * @throws BeanException
     */
    public List showAARoleList(OctopusContext octx, String domain) throws TcSecurityException, BeanException, IOException {
        LoginManagerAA loginManager = (LoginManagerAA) octx.moduleConfig().getLoginManager();
        Set roleSet = loginManager.getAARoles();
        if (roleSet != null) {
            if (domain != null && domain.length() > 0) {
                Collection exclusions = null;
                if (PARAM_DOMAIN_VALUE_ALL.equals(domain)) {
                    // nichts auszuschließen
                } else if (PARAM_DOMAIN_VALUE_UNASSIGNED.equals(domain)) {
                    Database database = new DatabaseVeraWeb(octx);
                    List users = database.getList(database.getSelect("User"), database);
                    if (users != null && users.size() > 0) {
                        exclusions = new ArrayList(users.size());
                        for (Iterator itUsers = users.iterator(); itUsers.hasNext(); )
                            exclusions.add(((Map)itUsers.next()).get("name"));
                    }
                }
                if (exclusions != null)
                    roleSet.removeAll(exclusions);
            }
            List roleList = new ArrayList(roleSet);
            Collections.sort(roleList);
            return roleList;
        } else
            return Collections.EMPTY_LIST;
    }
}
