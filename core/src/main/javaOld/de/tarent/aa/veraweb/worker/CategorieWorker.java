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
import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Diese Octopus-Worker-Klasse stellt Operationen für Dokumenttypen
 * zur Verfügung. Details bitte dem BeanListWorker entnehmen.
 * </p>
 * <p>
 * Wenn eine <em>person</em> im Octopus-Content steht, wird bei der
 * <em>getAll</em>-Aktion die Ergebnis-Liste auf die Kategorien
 * eingeschränkt die NICHT dieser Person zugeordnet sind.
 * </p>
 * <p>
 * Wenn ein <em>event</em> im Octopus-Content steht, wird bei der
 * <em>getAll</em>-Aktion die Ergebnis-Liste auf die Kategorien
 * eingeschränkt die entweder ALLEN oder DIESEM Event zugeordnet sind.
 * </p>
 *
 * @author Christoph
 * @see de.tarent.octopus.beans.BeanListWorker
 */
public class CategorieWorker extends StammdatenWorker {
    //
    // Öffentliche Konstanten
    //
    /**
     * Parameter: Wessen Kategorien?
     */
    public final static String PARAM_DOMAIN = "domain";

    /**
     * Parameterwert: beliebige Kategorien
     */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /**
     * Parameterwert: Kategorien des gleichen Mandanten
     */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";

    //
    // Konstruktoren
    //

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public CategorieWorker() {
        super("Categorie");
    }

    //
    // Basisklasse BeanListWorker
    //
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        Clause orgUnitTest = getWhere(cntx);
        if (orgUnitTest != null) {
            select.where(orgUnitTest);
        }
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
        if (cntx.requestContains("order")) {
            String order = cntx.requestAsString("order");
            if ("name".equals(order)) {
                select.orderBy(Order.asc(order));
                cntx.setContent("order", order);
            } else if ("flags".equals(order)) {
                select.orderBy(Order.asc(order).andAsc("name"));
                cntx.setContent("order", order);
            }
        }
    }

    @Override
    public void getAll(OctopusContext cntx) throws BeanException, IOException {
        Integer count = cntx.requestAsInteger("count");
        if (count != null) {
            cntx.setContent("count", count);
        }

//        super.getAll(octopusContext);
        getAllAvailableEventCategories(cntx);
    }

    /**
     * Returns all available person categories that have not been
     * assigned to a specific event.
     */
    public static String[] INPUT_getAllAvailablePersonCategories = {};
    public static String[] MANDATOR_getAllAvailablePersonCategories = {};

    public void getAllAvailablePersonCategories(OctopusContext cntx) throws BeanException, IOException {
        final Integer orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
        final Database database = getDatabase(cntx);
        final Select select = database.getSelect(BEANNAME);
        extendColumns(cntx, select);
        extendAll(cntx, select);
        select.whereAnd(Expr.isNull("fk_event"));
        select.whereAndEq("fk_orgunit", orgunit);


        Integer count = cntx.requestAsInteger("count");
        if (count != null) {
            cntx.setContent("count", count);
        }

        cntx.setContent("all" + BEANNAME, database.getList(select, database));
    }


    private void getAllAvailableEventCategories(OctopusContext cntx) throws BeanException, IOException {
        final Integer orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
        final Database database = getDatabase(cntx);
        final Select select = database.getSelect(BEANNAME);
        extendColumns(cntx, select);
        extendAll(cntx, select);
        select.whereAndEq("fk_orgunit", orgunit);


        Integer count = cntx.requestAsInteger("count");
        if (count != null) {
            cntx.setContent("count", count);
        }

        cntx.setContent("all" + BEANNAME, database.getList(select, database));
    }



    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        Person person = (Person) cntx.contentAsObject("person");
        Event event = (Event) cntx.contentAsObject("event");
        if (person != null && person.id != null) {
            buildSelectForPerson(cntx, select, person, event);
        }
    }

    private void buildSelectForPerson(OctopusContext cntx, Select select, Person person, Event event) throws BeanException {
        Clause clause = Where.and(
                Expr.isNull("fk_event"),
                new RawClause("pk NOT IN (SELECT fk_categorie FROM veraweb.tperson_categorie WHERE fk_person = " + person.id + ")"));

        Integer eventId = cntx.requestAsInteger("eventId");
        if (eventId.intValue() == 0) {
            if (event != null) {
                eventId = event.id;
            } else {
                eventId = null;
            }
        }
        if (eventId != null) {
            clause = Where.or(
                    Expr.isNull("fk_event"),
                    Expr.equal("fk_event", eventId)
            );
        }

        Clause orgUnitTest = getWhere(cntx);
        if (orgUnitTest != null) {
            clause = clause == null ? orgUnitTest : Where.and(orgUnitTest, clause);
        }
        if (clause != null) {
            select.where(clause);
        }
    }

    @Override
    protected Clause getWhere(OctopusContext cntx) throws BeanException {
        TcPersonalConfig pConfig = cntx.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = cntx.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))) {
                Integer orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
                if (orgunit == null)
                    return Expr.isNull("tcategorie.fk_orgunit");
                else
                    return Expr.equal("tcategorie.fk_orgunit", aaConfig.getOrgUnitId());
            }
            return null;
        } else {
            throw new BeanException("Missing user information");
        }
    }

    /**
     * Overrides parent class' behaviour in order to safely implement
     * change request 2.8 for version 1.2.0 which states that all authenticated
     * users must have viewer access to the list of categories.
     * The feature is implemented reusing existing code and templates and
     * therefore we must first test whether the user is authorized to
     * save the list. For that, the user must implement either the Administrator
     * or the PartialAdmin role.
     * Will delegate processing to the super class if the user implements one
     * of the above roles and processing continues as normal.
     *
     * @throws BeanException in case that the user is not authorized to save the list
     * @throws IOException
     */
    @Override
    public void saveList(OctopusContext cntx) throws BeanException, IOException {
        super.saveList(cntx);
    }

    @Override
    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {
        int count = 0;
        if (bean.isModified()) {
            if (bean instanceof Categorie) {
                ((Categorie) bean).verify(cntx);
            }
            if (bean.isCorrect()) {
                Database database = context.getDatabase();

                Clause sameOrgunit = getWhere(cntx);
                Clause sameName = Expr.equal(database.getProperty(bean, "name"), bean.getField("name"));
                Clause sameCategorie = sameOrgunit == null ? sameName : Where.and(sameOrgunit, sameName);
                Integer exist = database.getCount(database.getCount(bean).where(sameCategorie), context);

                List groups = Arrays.asList(cntx.personalConfig().getUserGroups());
                boolean admin = groups.contains(PersonalConfigAA.GROUP_ADMIN) || groups.contains(PersonalConfigAA.GROUP_PARTIAL_ADMIN);

                if (exist.intValue() != 0) {

                    LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
                    LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);

                    cntx.getContentObject().setField("beanToAdd", bean);
                    errors.add(languageProvider.getProperty("CATEGORY_DETAIL_ALREADY_EXISTS").toString() + bean.getField("name") + "'.");
                } else if (admin && !cntx.requestAsBoolean("questionConfirmed").booleanValue()) {
                    cntx.getContentObject().setField("beanToAdd", bean);
                    cntx.getContentObject().setField("resortQuestion", true);
                } else {
                    saveBean(cntx, bean, context);
                    count++;
                }
            } else {
                cntx.getContentObject().setField("beanToAdd", bean);
                errors.addAll(bean.getErrors());
            }
        }
        return count;
    }

    /**
     * Die Kategorie bean innerhalb der bestehenden Kategorieen einsortieren. Dazu werden alle bestehenden Kategorien mit
     * Rang >= bean.rank in ihrem Rang um eins erhoeht.
     *
     * @param cntx {@link OctopusContext}
     * @param bean {@link Categorie}
     * @throws BeanException
     */
    protected void incorporateBean(OctopusContext cntx, Categorie bean, TransactionContext transactionContext) throws BeanException {
        assert bean != null;
        assert cntx != null;

        if (bean.rank != null) {
            transactionContext.execute(SQL.Update(transactionContext.getDatabase()).
                    table("veraweb.tcategorie").
                    update("rank", new RawClause("rank + 1")).
                    where(Expr.greaterOrEqual("rank", bean.rank)));
            transactionContext.commit();
        }
    }

    @Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
        ((Categorie) bean).orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
        if (bean.isModified() && bean.isCorrect()) {
            if (((Categorie) bean).rank != null && cntx.requestAsBoolean("resort").booleanValue()) {
                incorporateBean(cntx, (Categorie) bean, context);
            }
        }
        super.saveBean(cntx, bean, context);
    }

    @Override
    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext transactionContext) throws BeanException, IOException {
        int count = super.removeSelection(cntx, errors, selection, transactionContext);

        // now remove all stale person category assignments
        transactionContext.execute(
                SQL.Delete(transactionContext.getDatabase()).
                        from("veraweb.tperson_categorie").
                        where(new RawClause("fk_categorie NOT IN (" +
                                "SELECT pk FROM veraweb.tcategorie)")));
        transactionContext.commit();

        return count;
    }
}
