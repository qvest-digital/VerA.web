/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Function;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.LoginManagerAA;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.content.TcContentWorker;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker erledigt Aufgaben, die Stellvertretungen betreffen.  
 * 
 * @author mikel
 */
public class ProxyWorker {
    //
    // Octopus-Aktionen
    //
    /** Eingabeparameter f�r Aktion {@link #select(OctopusContext, String)} */
    public static final String[] INPUT_select = {"proxyFor"};
    /** Eingabeparameterzwang f�r Aktion {@link #select(OctopusContext, String)} */
    public static final boolean[] MANDATORY_select = {false};
    /**
     * Diese Aktion setzt den aktuellen Benutzer als Stellvertreter der aktuellen
     * Rolle ein.<br>
     * Je nach Ablauf wird als Status "noRole", "noProxy", "noGrant", "noGrantNow"
     * oder RESULT_OK gesetzt.
     * 
     * @param octx Octopus-Kontext
     * @param proxyFor (optional) Rolle, die vertreten werden soll
     */
    public void select(OctopusContext octx, String proxyFor) {
        PersonalConfigAA pConfig = (PersonalConfigAA) octx.personalConfig();
        if (pConfig == null || !pConfig.getGrants().isAuthenticated())
            octx.setStatus("noProxy");
        else if (proxyFor == null || proxyFor.length() == 0)
            octx.setStatus("noRole");
        else {
            try {
                Proxy proxy = getApplicableProxyEntry(octx, proxyFor);
                if (proxy == null)
                    octx.setStatus("noRole");
                else {
                    // PersonalConfigAA an die Stellvertretung anpassen
                    ((LoginManagerAA)(octx.moduleConfig().getLoginManager())).setProxy(octx, proxy);
                    octx.setStatus(TcContentWorker.RESULT_ok);
                }
            } catch (TcSecurityException e) {
                octx.setStatus("noProxy");
            } catch (BeanException e) {
                logger.warn("BeanException bei Stellvertreterermittlung", e);
                octx.setStatus("noProxy");
            } catch (IOException e) {
                logger.warn("IOException bei Stellvertreterermittlung", e);
                octx.setStatus("noProxy");
            }
        }
    }
    
    //
    // gesch�tzte Hilfsmethoden
    //
    Proxy getApplicableProxyEntry(OctopusContext octx, String proxyFor) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octx);
        PersonalConfigAA pConfig = (PersonalConfigAA) octx.personalConfig();
        // Vertretungen einsammeln
        WhereList whereClause = new WhereList();
        if (pConfig == null)
            return null; // keine pers�nliche Konfiguration -> keine Stellvertretung
        else if (pConfig.getRole() != null)
            whereClause.addAnd(Expr.equal("proxy", pConfig.getRole()));
        else if (pConfig.getRoles() != null)
            whereClause.addAnd(Expr.in("proxy", pConfig.getRoles()));
        else
            return null; // keine erkennbare Rolle -> keine Stellvertretung
        whereClause.addAnd(Expr.equal("username", proxyFor));
        whereClause.addAnd(Where.or(Expr.isNull("validtill"), Expr.greaterOrEqual("validtill", new Function("now"))));
        whereClause.addAnd(Where.or(Expr.isNull("validfrom"), Expr.lessOrEqual("validfrom", new Function("now"))));
        Select select = database.getSelect("Proxy");
        ProxyListWorker.extendColumns(select);
        select.where(whereClause);
        List proxies = database.getBeanList("Proxy", select);
        Proxy resultProxy = null;
        for(Iterator itProxies = proxies.iterator(); itProxies.hasNext(); ) {
            Proxy proxy = (Proxy) itProxies.next();
            if (resultProxy == null || ( resultProxy.validTill != null &&
                    (proxy.validTill == null || proxy.validTill.after(resultProxy.validTill))))
                resultProxy = proxy;
        }
        return resultProxy;
    }
    
    //
    // gesch�tzte Member
    //
    private final static Logger logger = Logger.getLogger(ProxyWorker.class);
}
