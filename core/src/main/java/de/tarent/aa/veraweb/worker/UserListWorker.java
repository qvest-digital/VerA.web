package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
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

import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.aa.veraweb.beans.User;
import de.tarent.aa.veraweb.beans.UserConfig;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.List;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Benutzerlisten zur Verfügung. Details bitte dem
 * {@link de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb}
 * entnehmen.
 *
 * @author mikel
 */
public class UserListWorker extends ListWorkerVeraWeb {
    //
    // Öffentliche Konstanten
    //
    /**
     * Parameter: Wer alles?
     */
    public final static String PARAM_DOMAIN = "domain";
    /**
     * Parameter: Sortierreihenfolge
     */
    public final static String PARAM_ORDER = "order";

    /**
     * Parameterwert: beliebige Benutzer
     */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /**
     * Parameterwert: Benutzer des gleichen Mandanten
     */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";
    /**
     * Parameterwert: angemeldeter Benutzer
     */
    public final static String PARAM_DOMAIN_VALUE_SELF = "self";

    //
    // Konstruktoren
    //

    /**
     * Dieser Konstruktor bestimmt den Basis-Bean-Typ des Workers.
     */
    public UserListWorker() {
        super("User");
    }

    //
    // BeanListWorker
    //

    /**
     * Methode für das Erweitern des ListWorkerVeraWeb-Select-Statements um Spalten.<br>
     * Hier wird eine Sortierung eingefügt.
     *
     * @param cntx   Octopus-Context
     * @param select Select-Statement
     * @see de.tarent.octopus.beans.BeanListWorker#extendColumns(de.tarent.octopus.server.OctopusContext,
     * de.tarent.dblayer.sql.statement.Select)
     */
    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
        String order = cntx.contentAsString(PARAM_ORDER);
        if (order != null) {
            Database database = getDatabase(cntx);
            // Bug 1599 orgunit (Mandant) nach Name, nicht nach Schluessel sortieren
            if ("orgunit".equals(order)) {
                select.select("torgunit.unitname");
                select.joinLeftOuter("torgunit", "torgunit.pk", "tuser.fk_orgunit");
                select.orderBy(Order.asc("torgunit.unitname").andAsc("tuser.username"));
            } else {
                order = database.getProperty(new User(), order);
                if (order != null) {
                    select.orderBy(Order.asc(order).andAsc("tuser.username"));
                }
            }
        }
    }

    /**
     * Methode für das Erweitern des Select-Statements um Bedingungen.<br>
     * Hier wird der Parameter {@link #PARAM_DOMAIN "domain"} ausgewertet.<br>
     * {@link #PARAM_DOMAIN "domain"} kann neben einer Rollenbezeichnung die Werte {@link #PARAM_DOMAIN_VALUE_ALL "all"},
     * {@link #PARAM_DOMAIN_VALUE_OU "ou"} und {@link #PARAM_DOMAIN_VALUE_SELF "self"} haben.
     *
     * @param cntx   Octopus-Context
     * @param select Select-Statement
     * @see de.tarent.octopus.beans.BeanListWorker#extendWhere(de.tarent.octopus.server.OctopusContext,
     * de.tarent.dblayer.sql.statement.Select)
     */
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        PersonalConfigAA pCfg = (PersonalConfigAA) cntx.personalConfig();
        String domain = cntx.contentAsString(PARAM_DOMAIN);
        WhereList list = new WhereList();
        // TODO: Je nach Benutzergruppe passende Einschränkung machen
        //
        // domain: Wer alles?
        //
        if (PARAM_DOMAIN_VALUE_ALL.equals(domain)) {
            // alle Benutzer, keine Einschränkung
        } else if (PARAM_DOMAIN_VALUE_OU.equals(domain)) {
            if (pCfg == null || pCfg.getOrgUnitId() == null) {
                list.addAnd(Expr.isNull("fk_orgunit"));
            } else {
                list.addAnd(Expr.equal("fk_orgunit", pCfg.getOrgUnitId()));
            }
        } else if (PARAM_DOMAIN_VALUE_SELF.equals(domain)) {
            if (pCfg == null || (pCfg.getRole() == null && pCfg.getRoles() == null)) {
                list.addAnd(Expr.isNull("username"));
            } else if (pCfg.getRole() != null) {
                list.addAnd(Expr.equal("username", pCfg.getRole()));
            } else {
                list.addAnd(Expr.in("username", pCfg.getRoles()));
            }
        } else if (domain == null) {
            list.addAnd(Expr.isNull("username"));
        } else {
            list.addAnd(Expr.equal("username", domain));
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
     * @see #saveBean(OctopusContext, Bean, TransactionContext)
     */
    @Override
    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context)
            throws BeanException, IOException {
        int count = 0;
        if (bean.isModified()) {
            if (bean instanceof User) {
                ((User) bean).verify(cntx);
            }
            if (bean.isCorrect()) {
                if (bean instanceof User) {
                    LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
                    LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);
                    User userBean = (User) bean;
                    if (userBean.id != null) {
                        errors.add(languageProvider.getProperty("USER_LIST_WARN_NO_ID"));
                        return count;
                    }
                    Database database = new DatabaseVeraWeb(cntx);
                    User dupBean = (User) database.getBean("User",
                            database.getSelect("User").
                                    where(Expr.equal("username", userBean.name)), context);
                    if (dupBean != null) {
                        OrgUnit ou = (OrgUnit) database.getBean("OrgUnit", dupBean.orgunit, context);
                        if (ou != null) {
                            errors.add(languageProvider.getProperty("USER_LIST_WARN_MANDANT_ONE") +
                                    ((ou.name != null && ou.name.length() > 0) ? ou.name : ou.id.toString()) +
                                    languageProvider.getProperty("USER_LIST_WARN_MANDANT_TWO"));
                        } else {
                            errors.add(languageProvider.getProperty("USER_LIST_WARN_ALREADY_USER"));
                        }
                        return count;
                    }
                }
                saveBean(cntx, bean, context);
                count++;

                /* set default user tab configuration for new user */
                for (String configParamString : UserConfigWorker.PARAMS_STRING) {
                    UserConfig userConfig = new UserConfig();
                    userConfig.user = ((User) bean).id;
                    userConfig.key = configParamString;
                    userConfig.value = "1";
                    context.getDatabase().saveBean(userConfig);
                }
            } else {
                errors.addAll(bean.getErrors());
            }
        }

        return count;
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#removeSelection(OctopusContext, List, List, TransactionContext)}
     * aufgerufen und soll das übergebene Bean löschen.
     *
     * @param cntx Octopus-Kontext
     * @param bean zu löschende Bean
     * @throws BeanException BeanException
     * @throws IOException   IOException
     * @see #removeBean(OctopusContext, Bean, TransactionContext)
     */
    @Override
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext transactionContext)
            throws BeanException, IOException {
        if (bean != null && ((User) bean).id != null) {
            Integer userId = ((User) bean).id;

            /* delete related proxy configurations */
            Proxy proxy = new Proxy();
            proxy.user = userId;
            Database database = transactionContext.getDatabase();
            transactionContext.execute(database.getDelete("Proxy").where(database.getWhere(proxy)));

            /* delete related user configurations */
            transactionContext.execute(database.getDelete("UserConfig").where(Expr.equal("fk_user", userId)));
            transactionContext.commit();
        }
        return super.removeBean(cntx, bean, transactionContext);
    }
}
