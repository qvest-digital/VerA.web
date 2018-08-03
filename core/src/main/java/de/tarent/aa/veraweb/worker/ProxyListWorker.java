package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.aa.veraweb.utils.VerawebMessages;
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
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Stellvertreterlisten zur Verfügung. Details bitte dem
 * {@link de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb}
 * entnehmen.
 *
 * @author mikel
 */
public class ProxyListWorker extends ListWorkerVeraWeb {
    //
    // Öffentliche Konstanten
    //
    /**
     * Parameter: Wer wird vertreten?
     */
    public final static String PARAM_PROXIES_FOR = "proxiesFor";
    /**
     * Parameter: Vertretung gültig wann?
     */
    public final static String PARAM_PROXIES_VALID = "proxiesValid";
    /**
     * Parameter: Wer vertritt?
     */
    public final static String PARAM_PROXY = "proxy";
    /**
     * Parameter: Sortierreihenfolge
     */
    public final static String PARAM_ORDER = "order";
    /**
     * Parameter: List von Proxy-Beans
     */
    public final static String PARAM_LIST = "list";

    /**
     * Parameterwert: Vertreter für beliebige Benutzer
     */
    public final static String PARAM_PROXIES_FOR_VALUE_ALL = "all";
    /**
     * Parameterwert: Vertreter für Benutzer des gleichen Mandanten
     */
    public final static String PARAM_PROXIES_FOR_VALUE_OU = "ou";
    /**
     * Parameterwert: Vertreter für die angemeldete Rolle
     */
    public final static String PARAM_PROXIES_FOR_VALUE_SELF = "self";
    /**
     * Parameterwert: Vertretungsgültigkeit ignorieren
     */
    public final static String PARAM_PROXIES_VALID_VALUE_IGNORE = "ignore";
    /**
     * Parameterwert: Vertretung muß jetzt gültig sein
     */
    public final static String PARAM_PROXIES_VALID_VALUE_NOW = "now";
    /**
     * Parameterwert: Vertretung muß zukünftig gültig sein
     */
    public final static String PARAM_PROXIES_VALID_VALUE_FUTURE = "future";
    /**
     * Parameterwert: Vertretung durch beliebige Rollen
     */
    public final static String PARAM_PROXY_VALUE_ALL = "all";
    /**
     * Parameterwert: Vertretung durch Rollen des gleichen Mandanten
     */
    public final static String PARAM_PROXY_VALUE_OU = "ou";
    /**
     * Parameterwert: Vertretung durch die angemeldete(n) Rolle(n)
     */
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
    // statische Öffentliche Methoden
    //

    /**
     * Methode für das Erweitern des Select-Statements um Spalten.<br>
     * Hier wird <code>veraweb.tuser.username</code> als <code>userRole</code>
     * eingefügt.
     *
     * @param select zu erweiterndes Select-Statement
     */
    public static void extendColumns(Select select) {
        //
        // tuser
        //
        select.joinLeftOuter("veraweb.tuser", "fk_user", "tuser.pk").selectAs("username", "userRole")
          .selectAs("fk_orgunit", "orgunit");
    }

    //
    // Broker-Aktionen
    //
    /**
     * Eingabeparameter für Aktion {@link #uniqueProxiedFilter(List, String)}
     */
    public static final String[] INPUT_uniqueProxiedFilter = { PARAM_LIST, PARAM_PROXIES_VALID };
    /**
     * Eingabeparameterzwang für Aktion {@link #uniqueProxiedFilter(List, String)}
     */
    public static final boolean[] MANDATORY_uniqueProxiedFilter = { true, false };
    /**
     * Ausgabeparameter für Aktion {@link #uniqueProxiedFilter(List, String)}
     */
    public static final String OUTPUT_uniqueProxiedFilter = PARAM_LIST;

    /**
     * Diese Aktion filtert aus der in {@link #PARAM_LIST "list"} enthaltenen
     * Stellvertretungen so, dass je vertretener Rolle nur eine Bean vorhanden
     * ist. Hierbei wird das Vertretungsgültigkeitsflag interpretiert, wenn es
     * entschieden werden muss, welche Vertretungsregelung genommen werden soll.
     *
     * @param proxies      Liste mit {@link de.tarent.aa.veraweb.beans.Proxy}-Instanzen
     * @param proxiesValid Parameter, der über Relevanz der Gültigkeitszeit entscheidet.
     * @return eine neue Liste mit den hinreichend eindeutigen {@link de.tarent.aa.veraweb.beans.Proxy}-Instanzen
     */
    public List uniqueProxiedFilter(List proxies, String proxiesValid) {
        List result = null;
        if (proxies != null) {
            Map proxiesByProxied = new TreeMap();
            for (Iterator itProxies = proxies.iterator(); itProxies.hasNext(); ) {
                Proxy proxy = (Proxy) itProxies.next();
                if (proxy == null) {
                    continue;
                }
                String proxied = proxy.userRole;
                if (proxiesByProxied.containsKey(proxied)) {
                    Proxy currentProxy = (Proxy) proxiesByProxied.get(proxied);
                    assert currentProxy != null;
                    if (PARAM_PROXIES_VALID_VALUE_IGNORE.equals(proxiesValid)) {
                        continue;
                    } else if (PARAM_PROXIES_VALID_VALUE_FUTURE.equals(proxiesValid)) {
                        if ((currentProxy.validTill == null && proxy.validTill != null) ||
                          (currentProxy.validTill != null && proxy.validTill != null &&
                            currentProxy.validTill.after(proxy.validTill))) {
                            continue;
                        }
                    } else { // if (PARAM_PROXIES_VALID_VALUE_NOW.equals(proxiesValid))
                        if (containsNow(currentProxy.validFrom, currentProxy.validTill) &&
                          !containsNow(proxy.validFrom, proxy.validTill)) {
                            continue;
                        }
                        if ((currentProxy.validTill == null && proxy.validTill != null) ||
                          (currentProxy.validTill != null && proxy.validTill != null &&
                            currentProxy.validTill.after(proxy.validTill))) {
                            continue;
                        }
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
     * Methode für das Erweitern des ListWorkerVeraWeb-Select-Statements um Spalten.<br>
     * Hier wird <code>veraweb.tuser.username</code> als <code>userRole</code>
     * eingefügt.
     *
     * @param cntx   Octopus-Context
     * @param select Select-Statement
     * @see #extendColumns(Select)
     * @see de.tarent.octopus.beans.BeanListWorker#extendColumns(de.tarent.octopus.server.OctopusContext,
     * de.tarent.dblayer.sql.statement.Select)
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
     * Methode für das Erweitern des Select-Statements um Bedingungen.<br>
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
     * @param cntx   Octopus-Context
     * @param select Select-Statement
     * @see de.tarent.octopus.beans.BeanListWorker#extendWhere(de.tarent.octopus.server.OctopusContext,
     * de.tarent.dblayer.sql.statement.Select)
     */
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        PersonalConfigAA pCfg = (PersonalConfigAA) cntx.personalConfig();
        String proxiesFor = cntx.contentAsString(PARAM_PROXIES_FOR);
        String proxyValid = cntx.contentAsString(PARAM_PROXIES_VALID);
        String proxy = cntx.contentAsString(PARAM_PROXY);
        WhereList list = new WhereList();
        // TODO: Je nach Benutzergruppe passende Einschränkung machen
        //
        // proxiesValid: Gültig wann?
        //
        if (PARAM_PROXIES_VALID_VALUE_IGNORE.equals(proxyValid)) {
            // Gültigkeit ignorieren
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
            // alle Stellvertretenden, keine Einschränkung
        } else if (PARAM_PROXY_VALUE_OU.equals(proxy)) {
            if (pCfg == null || pCfg.getOrgUnitId() == null) {
                list.addAnd(new RawClause("proxy IN (SELECT username FROM veraweb.tuser WHERE fk_orgunit IS NULL)"));
            } else {
                list.addAnd(new RawClause(
                  "proxy IN (SELECT username FROM veraweb.tuser WHERE fk_orgunit = " + pCfg.getOrgUnitId() + ")"));
            }
        } else if (PARAM_PROXY_VALUE_SELF.equals(proxy)) {
            if (pCfg == null || (pCfg.getRole() == null && pCfg.getRoles() == null)) {
                list.addAnd(Expr.isNull("proxy"));
            } else if (pCfg.getRole() != null) {
                list.addAnd(Expr.equal("proxy", pCfg.getRole()));
            } else {
                list.addAnd(Expr.in("proxy", pCfg.getRoles()));
            }
        } else if (proxy == null) {
            list.addAnd(Expr.isNull("proxy"));
        } else {
            list.addAnd(Expr.equal("proxy", proxy));
        }
        //
        // proxiesFor: Wer wird vertreten?
        //
        // (Da nicht immer zuvor extendColumns aufgerufen wird, kann nicht vom gejointen tuser ausgegangen werden -> Subselects)
        if (PARAM_PROXIES_FOR_VALUE_ALL.equals(proxiesFor)) {
            // Stellvertreter aller Benutzer, keine Einschränkung
        } else if (PARAM_PROXIES_FOR_VALUE_OU.equals(proxiesFor)) {
            if (pCfg == null || pCfg.getOrgUnitId() == null) {
                list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE fk_orgunit IS NULL)"));
            } else {
                list.addAnd(new RawClause(
                  "fk_user IN (SELECT pk FROM veraweb.tuser WHERE fk_orgunit = " + pCfg.getOrgUnitId() + ")"));
            }
        } else if (PARAM_PROXIES_FOR_VALUE_SELF.equals(proxiesFor)) { // TODO: auf Roles ausdehnen?
            if (pCfg == null || pCfg.getRole() == null) {
                list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE username IS NULL)"));
            } else {
                list.addAnd(new RawClause(
                  "fk_user IN (SELECT pk FROM veraweb.tuser WHERE username = '" + Escaper.escape(pCfg.getRole()) + "')"));
            }
        } else if (proxiesFor == null) {
            list.addAnd(new RawClause("fk_user IN (SELECT pk FROM veraweb.tuser WHERE username IS NULL)"));
        } else {
            list.addAnd(new RawClause(
              "fk_user IN (SELECT pk FROM veraweb.tuser WHERE username = '" + Escaper.escape(proxiesFor) + "')"));
        }
        select.where(list);
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll das übergebene Bean als neuen Eintrag speichern.
     *
     * @param cntx   Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param bean   einzufügendes Bean
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    @Override
    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context)
      throws BeanException, IOException {
        int count = 0;
        if (bean.isModified()) {
            checkMandatoryFields(cntx, (Proxy) bean);
            if (bean.isCorrect()) {
                saveBean(cntx, bean, context);
                count++;
            } else {
                errors.addAll(bean.getErrors());
            }
        }
        return count;
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException {
        final List errors = new ArrayList();

        final TransactionContext transactionContext = getDatabase(octopusContext).getTransactionContext();
        try {
            handleAction(octopusContext, errors, transactionContext);
            if (!errors.isEmpty()) {
                octopusContext.setContent(OUTPUT_saveListErrors, errors);
            }
        } catch (Throwable e) {
            transactionContext.rollBack();
            throw new BeanException("Die Änderungen an der Datenliste konnten nicht gespeichert werden.", e);
        }
    }

    @Override
    protected void saveBean(final OctopusContext octopusContext, Bean bean, TransactionContext context)
      throws BeanException, IOException {
        Proxy proxy = (Proxy) bean;
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
        proxy.verify(octopusContext);

        if (proxy.isCorrect()) {
            super.saveBean(octopusContext, bean, context);
        }
    }

    //
    // geschützte Hilfsmethoden
    //
    static boolean containsNow(Timestamp from, Timestamp till) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return (from == null || from.before(now)) && (till == null || till.after(now));
    }

    private void handleAction(OctopusContext octopusContext, List errors, TransactionContext transactionContext)
      throws BeanException, IOException {
        boolean doInsert = octopusContext.requestAsBoolean(INPUT_INSERT).booleanValue();
        boolean doUpdate = octopusContext.requestAsBoolean(INPUT_UPDATE).booleanValue();
        boolean doRemove = octopusContext.requestAsBoolean(INPUT_REMOVE).booleanValue();
        if (!octopusContext.requestContains(INPUT_BUTTON_SAVE)) {
            doInsert = false;
            doUpdate = false;
        }
        if (!octopusContext.requestContains(INPUT_BUTTON_REMOVE)) {
            doRemove = false;
        }

        final Request request = getRequest(octopusContext);
        if (doInsert) {
            executeInsertReplacement(octopusContext, errors, transactionContext, request);
        }
        if (doUpdate) {
            executeUpdateReplacement(octopusContext, errors, transactionContext, request);
        }
        if (doRemove) {
            executeDeleteReplacement(octopusContext, errors, transactionContext);
        }
        transactionContext.commit();
    }

    private void executeDeleteReplacement(OctopusContext octopusContext, List errors, TransactionContext transactionContext)
      throws BeanException, IOException {
        int count = removeSelection(octopusContext, errors, getSelection(octopusContext, null), transactionContext);
        if (count > 0) {
            octopusContext.setContent("countRemove", new Integer(count));
        }
    }

    private void executeUpdateReplacement(OctopusContext octopusContext, List errors, TransactionContext transactionContext,
      Request request)
      throws BeanException, IOException {
        int count = updateBeanList(octopusContext, errors, request.getBeanList(BEANNAME, INPUT_LIST), transactionContext);
        if (count > 0) {
            octopusContext.setContent("countUpdate", new Integer(count));
        }
    }

    private void executeInsertReplacement(OctopusContext octopusContext, List errors, TransactionContext transactionContext,
      Request request)
      throws BeanException, IOException {
        int count = insertBean(octopusContext, errors, request.getBean(BEANNAME, INPUT_ADD), transactionContext);
        if (count > 0) {
            octopusContext.setContent("countInsert", new Integer(count));
        }
    }

    private void checkMandatoryFields(OctopusContext octopusContext, Proxy proxy) {
        final VerawebMessages messages = new VerawebMessages(octopusContext);
        checkBothMandatoryFields(proxy, messages);
    }

    private void checkBothMandatoryFields(Proxy proxy, VerawebMessages messages) {
        final String replacement = proxy.proxy;
        final Integer originId = proxy.user;
        if ((replacement == null || replacement.equals("")) && (originId == null || originId.equals(""))) {
            proxy.addError(messages.getMessageProxyBothRolleAndProxyMissing());
        } else {
            checkMandatoryFieldRepresentative(proxy, messages);
            checkMandatoryFieldRole(proxy, messages);
        }
    }

    private void checkMandatoryFieldRepresentative(Proxy proxy, VerawebMessages messages) {
        final String replacement = proxy.proxy;
        if (replacement == null || replacement.equals("")) {
            proxy.addError(messages.getMessageProxyNoRepresentative());
        }
    }

    private void checkMandatoryFieldRole(Proxy proxy, VerawebMessages messages) {
        final Integer originId = proxy.user;
        if (originId == null || originId.equals("")) {
            proxy.addError(messages.getMessageProxyNoRole());
        }
    }
}
