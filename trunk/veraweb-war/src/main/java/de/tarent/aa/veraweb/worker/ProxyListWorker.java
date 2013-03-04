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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Function;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Stellvertreterlisten zur Verf�gung. Details bitte dem
 * {@link de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb}
 * entnehmen.
 * 
 * @author mikel
 */
public class ProxyListWorker extends ListWorkerVeraWeb {
    //
    // �ffentliche Konstanten
    //
    /** Parameter: Wer wird vertreten? */
    public final static String PARAM_PROXIES_FOR = "proxiesFor";
    /** Parameter: Vertretung g�ltig wann? */
    public final static String PARAM_PROXIES_VALID = "proxiesValid";
    /** Parameter: Wer vertritt? */
    public final static String PARAM_PROXY = "proxy";
    /** Parameter: Sortierreihenfolge */
    public final static String PARAM_ORDER = "order";
    /** Parameter: List von Proxy-Beans */
    public final static String PARAM_LIST = "list";
    
    /** Parameterwert: Vertreter f�r beliebige Benutzer */
    public final static String PARAM_PROXIES_FOR_VALUE_ALL = "all";
    /** Parameterwert: Vertreter f�r Benutzer des gleichen Mandanten */
    public final static String PARAM_PROXIES_FOR_VALUE_OU = "ou";
    /** Parameterwert: Vertreter f�r die angemeldete Rolle */
    public final static String PARAM_PROXIES_FOR_VALUE_SELF = "self";
    /** Parameterwert: Vertretungsg�ltigkeit ignorieren */
    public final static String PARAM_PROXIES_VALID_VALUE_IGNORE = "ignore";
    /** Parameterwert: Vertretung muss jetzt g�ltig sein */
    public final static String PARAM_PROXIES_VALID_VALUE_NOW = "now";
    /** Parameterwert: Vertretung muss zuk�nftig g�ltig sein */
    public final static String PARAM_PROXIES_VALID_VALUE_FUTURE = "future";
    /** Parameterwert: Vertretung durch beliebige Rollen */
    public final static String PARAM_PROXY_VALUE_ALL = "all";
    /** Parameterwert: Vertretung durch Rollen des gleichen Mandanten */
    public final static String PARAM_PROXY_VALUE_OU = "ou";
    /** Parameterwert: Vertretung durch die angemeldete(n) Rolle(n) */
    public final static String PARAM_PROXY_VALUE_SELF = "self";
    
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor bestimmt den Basis-Bean-Typ des Workers.
     */
    public ProxyListWorker() {
        super("Proxy");
    }

    //
    // statische �ffentliche Methoden
    //
    /**
     * Methode f�r das Erweitern des Select-Statements um Spalten.<br>
     * Hier wird <code>veraweb.tuser.username</code> als <code>userRole</code>
     * eingef�gt. 
     * 
     * @param select zu erweiterndes Select-Statement
     */
    public static void extendColumns(Select select) {
        //
        // tuser
        //
        select.joinLeftOuter("veraweb.tuser", "fk_user", "tuser.pk").selectAs("username", "userRole").selectAs("fk_orgunit", "orgunit");
    }
    
    //
    // Broker-Aktionen
    //
    /** Eingabeparameter f�r Aktion {@link #uniqueProxiedFilter(List, String)} */
    public static final String[] INPUT_uniqueProxiedFilter = {PARAM_LIST, PARAM_PROXIES_VALID};
    /** Eingabeparameterzwang f�r Aktion {@link #uniqueProxiedFilter(List, String)} */
    public static final boolean[] MANDATORY_uniqueProxiedFilter = {true, false};
    /** Ausgabeparameter f�r Aktion {@link #uniqueProxiedFilter(List, String)} */
    public static final String OUTPUT_uniqueProxiedFilter = PARAM_LIST;
    /**
     * Diese Aktion filtert aus der in {@link #PARAM_LIST "list"} enthaltenen
     * Stellvertretungen so, dass je vertretener Rolle nur eine Bean vorhanden
     * ist. Hierbei wird das Vertretungsg�ltigkeitsflag interpretiert, wenn es
     * entschieden werden muss, welche Vertretungsregelung genommen werden soll. 
     * 
     * @param proxies Liste mit {@link de.tarent.aa.veraweb.beans.Proxy}-Instanzen
     * @param proxiesValid Parameter, der �ber Relevanz der G�ltigkeitszeit entscheidet.
     * @return eine neue Liste mit den hinreichend eindeutigen {@link de.tarent.aa.veraweb.beans.Proxy}-Instanzen
     */
    public List uniqueProxiedFilter(List proxies, String proxiesValid) {
        List result = null;
        if (proxies != null) {
            Map proxiesByProxied = new TreeMap();
            for (Iterator itProxies = proxies.iterator(); itProxies.hasNext(); ) {
                Proxy proxy = (Proxy) itProxies.next();
                if (proxy == null)
                    continue;
                String proxied = proxy.userRole;
                if (proxiesByProxied.containsKey(proxied)) {
                    Proxy currentProxy = (Proxy) proxiesByProxied.get(proxied);
                    assert currentProxy != null;
                    if (PARAM_PROXIES_VALID_VALUE_IGNORE.equals(proxiesValid))
                        continue;
                    else if (PARAM_PROXIES_VALID_VALUE_FUTURE.equals(proxiesValid)) {
                        if ((currentProxy.validTill == null && proxy.validTill != null) ||
                            (currentProxy.validTill != null && proxy.validTill != null && currentProxy.validTill.after(proxy.validTill)))
                            continue;
                    } else { // if (PARAM_PROXIES_VALID_VALUE_NOW.equals(proxiesValid))
                        if (containsNow(currentProxy.validFrom, currentProxy.validTill) &&
                            !containsNow(proxy.validFrom, proxy.validTill))
                            continue;
                        if ((currentProxy.validTill == null && proxy.validTill != null) ||
                            (currentProxy.validTill != null && proxy.validTill != null && currentProxy.validTill.after(proxy.validTill)))
                            continue;
                    }
                }
                proxiesByProxied.put(proxied, proxy);
            }
            result = new ArrayList(proxiesByProxied.values());
        }
        return result;
    }
    
    //
    // Klasse ListWorkerVeraWeb
    //
    /**
     * Methode f�r das Erweitern des ListWorkerVeraWeb-Select-Statements um Spalten.<br>
     * Hier wird <code>veraweb.tuser.username</code> als <code>userRole</code>
     * eingef�gt. 
     * 
     * @param cntx Octopus-Context
     * @param select Select-Statement
     * @see #extendColumns(Select)
     * @see de.tarent.octopus.beans.BeanListWorker#extendColumns(de.tarent.octopus.server.OctopusContext, de.tarent.dblayer.sql.statement.Select)
     */
    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
        extendColumns(select);
        String order = cntx.contentAsString(PARAM_ORDER);
        if (order != null) {
            Database database = getDatabase(cntx);
            order = database.getProperty(new Proxy(), order);
            if (order != null) {
            	select.orderBy(Order.asc(order).andAsc("tuser.fk_orgunit").andAsc("tuser.username").andAsc("proxy"));
            }
        }
    }

    /**
     * Methode f�r das Erweitern des Select-Statements um Bedingungen.<br>
     * Hier werden die Parameter {@link #PARAM_PROXIES_FOR "proxiesFor"},
     * {@link #PARAM_PROXIES_VALID "proxiesValid"} und {@link #PARAM_PROXY "proxy"}
     * ausgewertet.<br>
     * {@link #PARAM_PROXIES_FOR "proxiesFor"} kann neben einer Rollenbezeichnung die Werte
     * {@link #PARAM_PROXIES_FOR_VALUE_ALL "all"}, {@link #PARAM_PROXIES_FOR_VALUE_OU "ou"}
     * und {@link #PARAM_PROXIES_FOR_VALUE_SELF "self"} haben.<br>
     * {@link #PARAM_PROXIES_VALID "proxiesValid"} kann die Werte
     * {@link #PARAM_PROXIES_VALID_VALUE_IGNORE "ignore"}, {@link #PARAM_PROXIES_VALID_VALUE_NOW "now"}
     * und {@link #PARAM_PROXIES_VALID_VALUE_FUTURE "future"} haben. Default ist
     * {@link #PARAM_PROXIES_VALID_VALUE_NOW "now"}<br>
     * {@link #PARAM_PROXY "proxy"} kann neben einer Rollenbezeichnung die Werte
     * {@link #PARAM_PROXY_VALUE_ALL "all"}, {@link #PARAM_PROXY_VALUE_OU "ou"}
     * und {@link #PARAM_PROXY_VALUE_SELF "self"} haben.
     * 
     * @param cntx Octopus-Context
     * @param select Select-Statement
     * @see de.tarent.octopus.beans.BeanListWorker#extendWhere(de.tarent.octopus.server.OctopusContext, de.tarent.dblayer.sql.statement.Select)
     */
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        PersonalConfigAA pCfg = (PersonalConfigAA) cntx.personalConfig();
        String proxiesFor = cntx.contentAsString(PARAM_PROXIES_FOR);
        String proxyValid = cntx.contentAsString(PARAM_PROXIES_VALID);
        String proxy = cntx.contentAsString(PARAM_PROXY);
        WhereList list = new WhereList();
        // TODO: Je nach Benutzergruppe passende Einschr�nkung machen
        //
        // proxiesValid: G�ltig wann?
        //
        if (PARAM_PROXIES_VALID_VALUE_IGNORE.equals(proxyValid)) {
            // G�ltigkeit ignorieren
        } else if (PARAM_PROXIES_VALID_VALUE_FUTURE.equals(proxyValid)) {
            list.addAnd(Where.or(Expr.isNull("validtill"), Expr.greaterOrEqual("validtill", new Function("now"))));
        } else /*if (PARAM_PROXIES_VALID_VALUE_NOW.equals(proxyValid))*/ { // Default: "now"
            list.addAnd(Where.or(Expr.isNull("validtill"), Expr.greaterOrEqual("validtill", new Function("now"))));
            list.addAnd(Where.or(Expr.isNull("validfrom"), Expr.lessOrEqual("validfrom", new Function("now"))));
        }
        //
        // proxy: Wer vertritt?
        //
        if (PARAM_PROXY_VALUE_ALL.equals(proxy)) {
            // alle Stellvertretenden, keine Einschr�nkung
        } else if (PARAM_PROXY_VALUE_OU.equals(proxy)) {
            if (pCfg == null || pCfg.getOrgUnitId() == null)
                list.addAnd(new RawClause("proxy IN (SELECT username FROM veraweb.tuser WHERE fk_orgunit IS NULL)"));
            else
                list.addAnd(new RawClause("proxy IN (SELECT username FROM veraweb.tuser WHERE fk_orgunit = " + pCfg.getOrgUnitId() + ")"));
        } else if (PARAM_PROXY_VALUE_SELF.equals(proxy)) {
            if (pCfg == null || (pCfg.getRole() == null && pCfg.getRoles() == null))
                list.addAnd(Expr.isNull("proxy"));
            else if (pCfg.getRole() != null)
                list.addAnd(Expr.equal("proxy", pCfg.getRole()));
            else
                list.addAnd(Expr.in("proxy", pCfg.getRoles()));
        } else if (proxy == null)
            list.addAnd(Expr.isNull("proxy"));
        else
            list.addAnd(Expr.equal("proxy", proxy));
        //
        // proxiesFor: Wer wird vertreten?
        //
        // (Da nicht immer zuvor extendColumns aufgerufen wird, kann nicht vom gejointen tuser ausgegangen werden -> Subselects)
        if (PARAM_PROXIES_FOR_VALUE_ALL.equals(proxiesFor)) {
            // Stellvertreter aller Benutzer, keine Einschr�nkung
        } else if (PARAM_PROXIES_FOR_VALUE_OU.equals(proxiesFor)) {
            if (pCfg == null || pCfg.getOrgUnitId() == null)
                list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE fk_orgunit IS NULL)"));
            else
                list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE fk_orgunit = " + pCfg.getOrgUnitId() + ")"));
        } else if (PARAM_PROXIES_FOR_VALUE_SELF.equals(proxiesFor)) { // TODO: auf Roles ausdehnen?
            if (pCfg == null || pCfg.getRole() == null)
                list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE username IS NULL)"));
            else
                list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE username = '" + Escaper.escape(pCfg.getRole()) + "')"));
        } else if (proxiesFor == null)
            list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE username IS NULL)"));
        else
            list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE username = '" + Escaper.escape(proxiesFor) + "')"));
        select.where(list);
    }

	@Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		Proxy proxy = (Proxy)bean;
		if (proxy.validFrom != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(proxy.validFrom);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			proxy.validFrom.setTime(calendar.getTimeInMillis());
		}
		if (proxy.validTill != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(proxy.validTill);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 0);
			proxy.validTill.setTime(calendar.getTimeInMillis());
		}
		super.saveBean(cntx, bean, context);
	}
    
    //
    // gesch�tzte Hilfsmethoden
    //
    static boolean containsNow(Timestamp from, Timestamp till) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return (from == null || from.before(now)) && (till == null || till.after(now));
    }
}
