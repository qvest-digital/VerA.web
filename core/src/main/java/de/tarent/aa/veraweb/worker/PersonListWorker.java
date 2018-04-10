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
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
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

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.Format;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.StatementList;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige von Personenlisten
 * zur Verfügung. Details bitte dem BeanListWorker entnehmen.
 *
 * @author Christoph
 * @author mikel
 */
public class PersonListWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public PersonListWorker() {
        super("Person");
    }

    //
    // Oberklasse BeanListWorker
    //

    /**
     * Should be after vera lifetime: 9999-12-01 GMT
     */
    private static final Timestamp INFINITY_TIMESTAMP = new Timestamp(253399622400000L);

    /**
     * Octopus-Aktion die eine <strong>blätterbare</strong> Liste mit Beans aus
     * der Datenbank in den Content stellt. Kann durch
     * {@link #extendColumns(OctopusContext, Select)} erweitert bzw.
     * {@link #extendWhere(OctopusContext, Select)} eingeschränkt werden.
     *
     * Lenkt hier die entsprechende getSelect - Anfrage an eine spezialisierte
     * Form.
     *
     * @param octopusContext Octopus-Context
     * @return Liste mit Beans, nie null.
     * @throws BeanException
     * @throws IOException
     * @see #getSelection(OctopusContext, Integer)
     */
    @Override
    public List showList(final OctopusContext octopusContext) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        if (octopusContext.getRequestObject().get("searchTask") == null) {
            octopusContext.setContent("searchTask", "personSearchTask");
        } else {
            octopusContext.setContent("searchTask", octopusContext.requestAsObject("searchTask"));
        }

        /*
         * modified (refactored part of behaviour to prepareShowList for
         * additional reuse) as per change request for version 1.2.0 cklein
         * 2008-02-21
         */
        final Select personSelect = prepareShowList(octopusContext, database);

        filterByFirstCharacterOfLastname(octopusContext, personSelect);

        final Integer start = getStart(octopusContext);
        final Integer limit = getLimit(octopusContext);
        final Select select = (Select) personSelect.clone();
        select.clearColumnSelection();
        select.add("count(*)", Integer.class);
        select.orderBy(null);
        final Integer count = database.getCount(select);
        final Map listParams = getParamMap(start, limit, count);
        octopusContext.setContent(OUTPUT_showListParams, listParams);
        octopusContext.setContent("filterLetter", octopusContext.requestAsObject("filter"));

        personSelect.Limit(new Limit(limit, start));

        /*
         * FIXME remove this temporary fix ASAP cklein 2009-09-16 Temporary
         * workaround for NPE Exception in Conjunction with temporary Connection
         * Pooling Fix in tarent-database Somehow the resultlist returned by
         * getResultList or its underlying ResultSet will be NULL when entering
         * the view although, upon exiting this method the first time that it is
         * called, will return the correct resultlist with at most 10 entries in
         * the underlying resultset as is defined by the query.
         */
        final PersonSearch search = (PersonSearch) octopusContext.contentAsObject("search");
        if (search.listorder == null) {
            personSelect.addOrderBy(new Order().asc("lastname_a_e1").andAsc("firstname_a_e1"));
        }
        final List personList = getResultList(database, personSelect);

        final Map<Integer, Map> result = getUserData(database, personList);

        octopusContext.setContent(OUTPUT_getSelection, getSelection(octopusContext, getCount(octopusContext, database)));

        octopusContext.setContent("deleted", octopusContext.getRequestObject().getParamAsInt("deleted"));

        octopusContext.setContent("workareaAssigned", octopusContext.requestAsObject("workareaAssigned"));

        octopusContext.setContent("categoryAssigned", octopusContext.requestAsObject("categoryAssigned"));

        return new ArrayList(result.values());
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #countRecipients(OctopusContext)}
     */
    public static final String INPUT_countRecipients[] = {};
    /**
     * Octopus-Ausgabe-Parameter für {@link #countRecipients(OctopusContext)}
     */
    public static final String OUTPUT_countRecipients = "mailinglistParams";

    public Map countRecipients(OctopusContext octopusContext) throws IOException, BeanException {
        Map result = (Map) octopusContext.contentAsObject("mailinglistParams");
        if (result == null) {
            result = new HashMap();
        }
        final Integer countPeople = countSelectedPeople(octopusContext);
        if (countPeople != null) {
            result.put("count", countPeople);
        }
        return result;
    }

    public Integer countSelectedPeople(OctopusContext octopusContext) throws IOException, BeanException {
        final Integer countPeople = getSelection(octopusContext, null).size();
        if (countPeople == null) {
            return 0;
        }
        return countPeople;
    }

    @Override
    protected String getJumpOffsetsColumn(OctopusContext octopusContext) throws BeanException {
        final String col = getSearch(octopusContext).listorder;
        if (Arrays.asList("lastname_a_e1", "firstname_a_e1", "mail_a_e1").contains(col)) {
            return col;
        }
        return null;
    }

    private void filterByFirstCharacterOfLastname(final OctopusContext octopusContext, final Select personSelect) {
        final Map allRequestParameters = octopusContext.getRequestObject().getRequestParameters();
        if (allRequestParameters.get("filter") != null) {
            addClauseLastnameFirstCharacter(personSelect, allRequestParameters);
        }
    }

    private void addClauseLastnameFirstCharacter(final Select personSelect, final Map allRequestParameters) {
        final String charachterFilter = allRequestParameters.get("filter").toString();
        final WhereList whereLastnameClause = new WhereList();
        whereLastnameClause.add(Expr.like("lastname_a_e1", charachterFilter + "%"));
        personSelect.whereAnd(whereLastnameClause);
    }

    private Map<Integer, Map> getUserData(final Database database, final List personList) throws BeanException, IOException {
        final Map<Integer, Map> result = new LinkedHashMap<Integer, Map>();
        for (int i = 0; i < personList.size(); i++) {
            final HashMap<String, Object> tmp = new HashMap<String, Object>();
            Set<String> keys = ((ResultMap) personList.get(i)).keySet();

            Integer id = null;
            for (final String key : keys) {
                final Object val = ((ResultMap) personList.get(i)).get(key);
                if ("id".equals(key)) {
                    id = (Integer) val;
                    tmp.put(key, val);
                } else {
                    tmp.put(key, val);
                }
            }

            /* select all relevant event/task information for each person */
            final Integer personId = (Integer) tmp.get("id");
            final Select eventSelect = SQL.SelectDistinct(database).from("veraweb.tperson").selectAs("tperson.pk", "id")
                    .selectAs("tevent.dateend", "eventenddate").selectAs("event2.dateend", "taskeventenddate")
                    .selectAs("tevent.datebegin", "eventbegindate").selectAs("event2.datebegin", "taskeventbegindate")
                    .joinOuter("veraweb.tguest", "tguest.fk_person", "tperson.pk")
                    .joinOuter("veraweb.tevent", "tevent.pk", "tguest.fk_event")
                    .joinOuter("veraweb.ttask", "ttask.fk_person", "tperson.pk")
                    .joinOuter("veraweb.tevent event2", "event2.pk", "ttask.fk_event")
                    .where(Expr.equal("tperson.pk", personId));

            Timestamp eventBeginDate = null;
            Timestamp eventEndDate = null;
            Timestamp taskEventBeginDate = null;
            Timestamp taskEventEndDate = null;

            final List eventList = getResultList(database, eventSelect);
            for (int j = 0; j < eventList.size(); j++) {
                keys = ((ResultMap) eventList.get(j)).keySet();
                for (final String key : keys) {
                    final Object val = ((ResultMap) eventList.get(j)).get(key);
                    if ("id".equals(key)) {
                        id = (Integer) val;
                        tmp.put(key, val);
                    } else if ("eventbegindate".equals(key)) {
                        eventBeginDate = (Timestamp) val;
                    } else if ("eventenddate".equals(key)) {
                        eventEndDate = (Timestamp) val;
                    } else if ("taskeventbegindate".equals(key)) {
                        taskEventBeginDate = (Timestamp) val;
                    } else if ("taskeventenddate".equals(key)) {
                        taskEventEndDate = (Timestamp) val;
                    }
                }
            }

            if (eventBeginDate != null && eventEndDate == null) { // end =
                // infinity
                eventEndDate = INFINITY_TIMESTAMP;
            }
            if (taskEventBeginDate != null && taskEventEndDate == null) { // end
                // =
                // infinity
                taskEventEndDate = INFINITY_TIMESTAMP;
            }
            if (eventEndDate != null) {
                tmp.put("eventmaxenddate", eventEndDate);
            }
            if (taskEventEndDate != null) {
                tmp.put("taskeventmaxenddate", taskEventEndDate);
            }
            if (!result.containsKey(id)) {
                result.put(id, tmp);
            } else {
                final Map map = result.get(id);
                Timestamp date = (Timestamp) map.get("eventmaxenddate");
                if (eventEndDate != null && (date == null || eventEndDate.after(date))) {
                    map.put("eventmaxenddate", eventEndDate);
                }
                date = (Timestamp) map.get("taskeventmaxenddate");
                if (taskEventEndDate != null && (date == null || taskEventEndDate.after(date))) {
                    map.put("taskeventmaxenddate", taskEventEndDate);
                }
            }
        }

        return result;
    }

    @Override
    public void saveList(final OctopusContext octopusContext) throws BeanException, IOException {
        final String categoryAssignmentAction = octopusContext.requestAsString("categoryAssignmentAction");
        final String workareaAssignmentAction = octopusContext.requestAsString("workareaAssignmentAction");

        // does the user request categories to be assigned or unassigned?
        if (categoryAssignmentAction != null && categoryAssignmentAction.length() > 0) {
            final Database database = getDatabase(octopusContext);
            final TransactionContext transactionContext = database.getTransactionContext();
            final PersonCategorieWorker personCategoryWorker = WorkerFactory.getPersonCategorieWorker(octopusContext);
            final Integer categoryId = octopusContext.requestAsInteger("categoryAssignmentId");
            final List selection = getSelection(octopusContext, getCount(octopusContext, database));
            // Iterator iter = selection.iterator();

            for (final Object id : selection) {

                // while (iter.hasNext()) {
                // Integer personId = (Integer) iter.next();
                final Integer personId = (Integer) id;
                if ("assign".compareTo(categoryAssignmentAction) == 0 && categoryId.intValue() > 0) {
                    final PersonCategorie category =
                            personCategoryWorker.addCategoryAssignment(octopusContext, categoryId, personId, database,
                                    transactionContext, false);
                    if (category != null) {
                        database.saveBean(category, transactionContext, false);
                    }
                } else {
                    if (categoryId.intValue() == 0) {
                        personCategoryWorker.removeAllCategoryAssignments(octopusContext, personId, database, transactionContext);
                    } else {
                        personCategoryWorker
                                .removeCategoryAssignment(octopusContext, categoryId, personId, database, transactionContext);
                    }
                }
                // iter.remove();
            }
            try {
                transactionContext.commit();
            } catch (final BeanException e) {
                transactionContext.rollBack();
                throw e;
            }
        }

        // does the user request workareas to be assigned or unassigned?
        else if (workareaAssignmentAction != null && workareaAssignmentAction.length() > 0) {
            handleWorkareaActions(octopusContext, workareaAssignmentAction);
        } else {
            super.saveList(octopusContext);
        }
    }

    private void handleWorkareaActions(final OctopusContext octopusContext, final String workareaAssignmentAction)
            throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final List<Integer> selection = getSelection(octopusContext, getCount(octopusContext, database));
        if (!selection.isEmpty()) {
            octopusContext.setContent("deleted", selection.size());
            final Integer workareaId = octopusContext.requestAsInteger("workareaAssignmentId");
            if ("assign".compareTo(workareaAssignmentAction) == 0) {
                assignWorkArea(octopusContext, selection, workareaId);
            } else if ("unassign".compareTo(workareaAssignmentAction) == 0) {
                unassignWorkArea(octopusContext, selection, workareaId);
            }
            selection.clear();
        }
    }

    /**
     * Entfernt die Zuordnungen von Arbeitsbereichen der übergebenen Personen
     * (IDs).
     *
     * @param cntx       Octopus-Context
     * @param personIds  Liste von Personen IDs für die das entfernen der Zuordnung
     *                   gilt
     * @param workAreaId ID des Arbeitsbereiches deren Zuordnung entfernt werden soll
     * @throws BeanException
     * @throws IOException
     */
    public void unassignWorkArea(final OctopusContext cntx, final List<Integer> personIds, final Integer workAreaId)
            throws BeanException,
            IOException {
        final Database database = getDatabase(cntx);
        final TransactionContext transactionContext = database.getTransactionContext();
        handleUnassignWorkarea(personIds, workAreaId, transactionContext);
        try {
            transactionContext.commit();
        } catch (final Exception e) {
            transactionContext.rollBack();
        }
    }

    private void handleUnassignWorkarea(final List<Integer> personIds, final Integer workAreaId,
            final TransactionContext transactionContext)
            throws BeanException, IOException {
        if (workAreaId > 0) {
            unassignWorkArea(transactionContext, workAreaId, personIds);
        } else if (workAreaId == 0) {
            unassignAllWorkAreas(transactionContext, personIds);
        }
    }

    /**
     * Ordnet den übergebenen Arbeitsbereich der Liste von Personen hinzu.
     *
     * @param octopusContext OctopusContext
     * @param personIds      Liste von Personen IDs für die die neue Zuordnung gilt
     * @param workAreaId     ID des Arbeitsbereiches der zugeordnet werden soll
     * @throws BeanException
     * @throws IOException
     */
    public void assignWorkArea(final OctopusContext octopusContext, final List<Integer> personIds, final Integer workAreaId)
            throws BeanException,
            IOException {
        final Database database = getDatabase(octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();
        PersonListWorker.assignWorkArea(transactionContext, workAreaId, personIds);
        try {
            transactionContext.commit();
        } catch (final Exception e) {
            transactionContext.rollBack();
        }
    }

    /*
     * 2009-05-12 cklein introduced as part of fix for issue #1530 - removal of
     * orgunits, and subsequently also individual workareas
     *
     * unassigns from all persons the given workArea. Will not commit the query
     * as this is left to the caller.
     */
    public static void unassignWorkArea(final TransactionContext transactionContext, final Integer workAreaId,
            final List<Integer> personIds)
            throws BeanException, IOException {
        final Update updateStatement = transactionContext.getDatabase().getUpdate("Person");
        updateStatement.update("tperson.fk_workarea", 0);
        updateStatement.where(Expr.equal("tperson.fk_workarea", workAreaId));
        if (personIds != null && personIds.size() > 0) {
            updateStatement.whereAnd(Expr.in("tperson.pk", personIds));
        }
        transactionContext.execute(updateStatement);
        transactionContext.commit();
    }

    private static void unassignAllWorkAreas(final TransactionContext transactionContext, final List<Integer> personIds)
            throws IOException,
            BeanException {
        final Update updateStatement = transactionContext.getDatabase().getUpdate("Person");
        updateStatement.update("tperson.fk_workarea", 0);
        if (personIds != null && personIds.size() > 0) {
            updateStatement.whereAnd(Expr.in("tperson.pk", personIds));
        }
        transactionContext.execute(updateStatement);
        transactionContext.commit();
    }

    public static void assignWorkArea(final TransactionContext transactionContext, final Integer workAreaId,
            final List<Integer> personIds)
            throws BeanException, IOException {
        final Update updateStatement = transactionContext.getDatabase().getUpdate("Person");
        updateStatement.update("tperson.fk_workarea", workAreaId);
        if (personIds != null && personIds.size() > 0) {
            updateStatement.whereAnd(Expr.in("tperson.pk", personIds));
        }

        transactionContext.execute(updateStatement);
        transactionContext.commit();
    }

    public Select prepareShowList(final OctopusContext octopusContext, final Database database)
            throws BeanException, IOException {
        final Select select = getSelect(getSearch(octopusContext), database);
        extendColumns(octopusContext, select);
        extendWhere(octopusContext, select);
        return select;
    }

    @Override
    protected void extendColumns(final OctopusContext octopusContext, final Select select) throws BeanException, IOException {
        final PersonSearch personSearch = getSearch(octopusContext);
        select.selectAs("tworkarea.name", "workarea_name");
        select.selectAs("dateexpire", "dateexpire");

        /*
         * modified to support workarea display in the search result list as per
         * change request for version 1.2.0 cklein 2008-02-12
         */
        final String searchFiled = octopusContext.getRequestObject().getParamAsString("searchField");
        if (searchFiled == null) {
            select.join("veraweb.tworkarea", "tworkarea.pk", "tperson.fk_workarea");
        }

        final List<String> order = new ArrayList<String>();

        /*
         * TODO: Needed to optimise that snippet and move it to a location where
         * all List can use it for sortation
         */
        if (personSearch.sortList) {
            if (personSearch.sort == null || personSearch.lastlistorder == null ||
                    !personSearch.lastlistorder.equals(personSearch.listorder)) {
                personSearch.sort = "ASC";
            } else if ("ASC".equals(personSearch.sort)) {
                personSearch.sort = "DESC";
            } else if ("DESC".equals(personSearch.sort)) {
                personSearch.sort = "ASC";
            }
        }
        octopusContext.getContentObject().setField("personSearchOrder", personSearch.sort);

        if (personSearch != null && personSearch.listorder != null && !personSearch.listorder.equals("")) {
            order.add(personSearch.listorder);
            order.add(personSearch.sort);
        }

        select.orderBy(DatabaseHelper.getOrder(order));
    }

    @Override
    protected void extendWhere(final OctopusContext octopusContext, final Select select) throws BeanException {
        final PersonSearch personSearch = getSearch(octopusContext);
        select.whereAnd(getPersonListFilter(octopusContext, true));

        select.setDistinct(true);
        final String searchFiled = octopusContext.getRequestObject().getParamAsString("searchField");

        if (personSearch.listorder == null) {
            octopusContext.getContentObject().setField("personSearchField", "lastname_a_e1");
            octopusContext.getContentObject().setField("personSearchOrder", "ASC");
        }

        /*
         * extension to support for multiple categories at once
         *
         * cklein 2008-02-20/26
         */
        extendSelectByMultipleCategorySearch(octopusContext, personSearch, select);
        if (personSearch.categorie2 != null) {
            select.join("veraweb.tperson_categorie cat2", "cat2.fk_person", "tperson.pk");
        } else if (searchFiled != null) {
            select.joinOuter("veraweb.tperson_categorie cat2", "cat2.fk_person", "tperson.pk");

        }
        if (searchFiled != null) {
            select.joinOuter("veraweb.tworkarea", "tworkarea.pk", "tperson.fk_workarea");
            select.joinOuter("veraweb.tcategorie", "tcategorie.pk", "cat2.fk_categorie");
        }
    }

    /**
     * Extends the select statement in order to allow search for multiple
     * categories at once using either AND or OR.
     *
     * @param octopusContext The {@link OctopusContext}
     * @param personSearch   FIXME
     * @param select         FIXME
     */
    protected void extendSelectByMultipleCategorySearch(final OctopusContext octopusContext, final PersonSearch personSearch,
            final Select select) {
        if ((personSearch.categoriesSelection != null) && (personSearch.categoriesSelection.size() >= 1)
                && (personSearch.categoriesSelection.get(0).toString().length() > 0) // workaround
            // for
            // octopus
            // behaviour
                ) {
            if (((Integer) personSearch.categoriesSelection.get(0)).intValue() != 0) {
                // FUTURE extension for supporting OR a/o AND
                boolean isOr = false;
                if (octopusContext.contentContains("disjunctCategorySearch")) {
                    isOr = octopusContext.requestAsBoolean("disjunctCategorySearch").booleanValue();
                }
                if (isOr) {
                    // FIXME does not work, misses join on tperson_categorie
                    // any of the selected categories (OR clause)
                    select.whereAnd(new RawClause("tperson.pk=cat1.fk_person"));
                    select.whereAnd(Expr.in("cat1.fk_categorie", personSearch.categoriesSelection));
                } else {
                    // all of the selected categories (AND clause)
                    final Iterator iter = personSearch.categoriesSelection.iterator();
                    int count = 0;
                    while (iter.hasNext()) {
                        final String alias = "cat" + count;
                        select.joinLeftOuter("veraweb.tperson_categorie " + alias, "tperson.pk", alias + ".fk_person");
                        select.whereAnd(new RawClause(alias + ".fk_categorie=" + iter.next()));
                        count++;
                    }
                }
            } else {
                // no categories assigned
                final Select subSelect = new Select(true);
                subSelect.from("veraweb.tperson_categorie");
                subSelect.selectAs("tperson_categorie.fk_person");
                try {
                    select.whereAnd(Expr.notIn("tperson.pk", new RawClause("(" + subSelect.statementToString() + ")")));
                } catch (final SyntaxErrorException e) {
                    // just catch, will never happen
                }
            }
        } else {
            // search in all categories
        }
    }

    @Override
    protected Integer getAlphaStart(final OctopusContext octopusContext, final String start) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final Select select = database.getCount(BEANNAME);
        extendWhere(octopusContext, select);
        if (start != null && start.length() > 0) {
            select.whereAnd(Expr.less("tperson.lastname_a_e1", Escaper.escape(start)));
        }

        final Integer selectCounter = database.getCount(select);

        return new Integer(selectCounter.intValue() - (selectCounter.intValue() % getLimit(octopusContext).intValue()));
    }

    @Override
    protected Select getSelect(final Database database) throws BeanException, IOException {
        return getSelect(null, database);
    }

    protected Select getSelect(final PersonSearch personSearch, final Database database) throws BeanException, IOException {
        Select select;
        if (personSearch != null) {
            select = new Select(personSearch.categoriesSelection != null || personSearch.categorie2 != null);
        } else {
            select = SQL.SelectDistinct(database);
        }

        return select.from("veraweb.tperson").selectAs("tperson.pk", "id").select("firstname_a_e1").select("lastname_a_e1")
                .select("firstname_b_e1")
                .select("lastname_b_e1").select("function_a_e1").select("company_a_e1").select("street_a_e1")
                .select("zipcode_a_e1")
                .select("state_a_e1").select("city_a_e1").select("iscompany");
    }

    @Override
    protected List getResultList(final Database database, final Select select) throws BeanException, IOException {
        return database.getList(select, database);
    }

    /**
     * Überprüft ob eine Person die nötigen Berechtigungen hat um Personen zu
     * löschen und ob diese ggf. noch Veranstaltungen zugeordent sind.
     *
     * Bei Veränderungen an dieser Methode müssen diese ggf. auch in der
     * personList.vm übernommen werden, dort werden entsprechende JavaScript
     * Meldungen ausgegeben.
     *
     * siehe Anwendungsfall: UC.PERSON.LOESCH
     */
    @Override
    protected int removeSelection(final OctopusContext octopusContext, final List errors, final List selection,
            final TransactionContext transactionContext) throws BeanException, IOException {

        int count = 0;
        if (selection == null || selection.size() == 0) {
            return count;
        }
        final List selectionRemove = new ArrayList(selection);

        final Database database = transactionContext.getDatabase();
        final Map questions = new HashMap();

        final List groups = Arrays.asList(octopusContext.personalConfig().getUserGroups());
        boolean user = groups.contains(PersonalConfigAA.GROUP_WRITE);
        final boolean admin =
                groups.contains(PersonalConfigAA.GROUP_ADMIN) || groups.contains(PersonalConfigAA.GROUP_PARTIAL_ADMIN);
        if (admin) {
            user = false;
        }

        LanguageProviderHelper languageProviderHelper = null;
        LanguageProvider languageProvider = null;

        if (!(user || admin)) {
            languageProviderHelper = new LanguageProviderHelper();
            languageProvider = languageProviderHelper.enableTranslation(octopusContext);
            errors.add(languageProvider.getProperty("PERSON_LIST_WARNING_NO_PERMISSION_TO_DELETE"));
            return count;
        }
        /** User dürfen immer nur eine Person gleichzeitig löschen. */
        if (user && selectionRemove.size() > 1) {
            if (languageProviderHelper == null) {
                languageProviderHelper = new LanguageProviderHelper();
                languageProvider = languageProviderHelper.enableTranslation(octopusContext);
            }
            errors.add(languageProvider.getProperty("PERSON_LIST_WARNING_ONLY_DELETE_ONE_PERSON"));
            octopusContext.setContent("listerrors", errors);
            return count;
        }

        final int maxquestions = 0;
        final int subselectsize = 1000;

        /** Test ob Personen noch gültig sind und nicht gelöscht werden dürfen. */
        if ((user || admin) && !selectionRemove.isEmpty()) {
            for (int i = 0; i < selectionRemove.size(); i += subselectsize) {
                final List subList = selectionRemove.subList(i,
                        i + subselectsize < selectionRemove.size() ? i + subselectsize : selectionRemove.size());
                final List personExpireInFuture = database.getBeanList(
                        "Person",
                        database.getSelect("Person").where(
                                new RawClause("dateexpire >= " + Format.format(new Date()) + " AND pk IN " +
                                        new StatementList(subList))));
                for (final Object singlePerson : personExpireInFuture) {
                    // for (Iterator it = personExpireInFuture.iterator();
                    // it.hasNext(); ) {
                    final Person person = (Person) singlePerson;
                    if (getContextAsBoolean(octopusContext, "remove-expire-" + person.id)) {
                        octopusContext.setContent("remove-person", Boolean.TRUE);
                    } else {
                        if (maxquestions == 0 || questions.size() < maxquestions) {
                            /**
                             * questions.put("remove-expire-" + person.id,
                             * "Das Gültigkeitsdatum der Person \"" +
                             * person.getMainLatin().getSaveAs() +
                             * "\" liegt in der Zukunft. Soll die Person trotzdem gelöscht werden?"
                             * );
                             */
                            questions.put("remove-expire-" + person.id, person.getMainLatin().getSaveAs());
                        }
                        selectionRemove.remove(person.id);
                        i--;
                    }
                }
            }
        }

        /** Fragen ob alle Personen wirklich gelöscht werden sollen. */
        if (!getContextAsBoolean(octopusContext, "remove-person")) {
            if (languageProviderHelper == null) {
                languageProviderHelper = new LanguageProviderHelper();
                languageProvider = languageProviderHelper.enableTranslation(octopusContext);
            }
            questions.put("remove-person", languageProvider.getProperty("PERSON_LIST_QUESTION_DELETE_CONFIRMATION"));
        }

        if (!questions.isEmpty()) {
            octopusContext.setContent("listquestions", questions);
        }

        /** Löscht Personen aus VerA.web */
        if ((user || admin) && !selectionRemove.isEmpty() && getContextAsBoolean(octopusContext, "remove-person")) {
            try {
                final PersonDetailWorker personDetailWorker = WorkerFactory.getPersonDetailWorker(octopusContext);
                personDetailWorker.setDatabase(database);
                personDetailWorker.setTransactionalContext(transactionContext);

                for (final Object personId : selectionRemove) {
                    final Integer id = (Integer) personId;

                    /*
                     * updated to reflect interface changes on removePerson
                     * cklein 2008-02-12
                     */
                    final Person person = (Person) database.getBean("Person", id);
                    personDetailWorker.removePerson(octopusContext, person, person.username);
                    selection.remove(id);
                    count++;
                }
                transactionContext.commit();

            } catch (final BeanException e) {
                transactionContext.rollBack();
                throw new BeanException("Die ausgew\u00e4hlten Personen konnten nicht gel\u00f6scht werden.", e);
            }
        }

        /*
         * fix: selection remained active in session, causing lists to
         * autoselect individual entries in the lists, will remove the session
         * variable additionally, non-deletable entries remained selected, will
         * reset the session variable with the new selection list cklein
         * 2008-03-12
         */
        octopusContext.setSession("selection" + BEANNAME, selection);
        return count;
    }

    /**
     * Wirft eine BeanException, die Personen werden mit ihren Abhängigkeiten
     * direkt in der Methode @link #removeSelection(OctopusContext, List, List)
     * gelöscht.
     */
    protected boolean removeBean(final OctopusContext octopusContext, final Bean bean) throws BeanException, IOException {
        throw new BeanException("PersonListWorker#removeBean is deprecated");
    }

    //
    // Octopus-Aktionen
    //
    /**
     * Octopus-Eingabeparameter für {@link #getSearch(OctopusContext)}
     */
    public static final String INPUT_getSearch[] = {};
    /**
     * Octopus-Ausgabeparameter für {@link #getSearch(OctopusContext)}
     */
    public static final String OUTPUT_getSearch = "search";

    /**
     * Diese Octopus-Aktion liefert ein aktuelles {@link PersonSearch}-Objekt.
     * Dies wird aus dem Request geholt geholt, falls ein Requestparameter
     * "search" den Wert "reset" hat. Beim Wert "clear" wird ein leeres Objekt
     * zurück gegeben. Ausgewichen wird dann auf ein entsprechendes
     * Session-Objekt. Das Ergebnis wird in der Session gesetzt.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException
     */
    public PersonSearch getSearch(final OctopusContext octopusContext) throws BeanException {

        if (octopusContext.contentContains("search") && octopusContext.contentAsObject("search") instanceof PersonSearch) {
            return (PersonSearch) octopusContext.contentAsObject("search");
        }

        final String param = octopusContext.requestAsString("search");
        final Boolean sortList = octopusContext.requestAsBoolean("sortList");
        PersonSearch personSearch;

        if ("clear".equals(param)) {
            personSearch = new PersonSearch();
        } else if ("reset".equals(param)) {
            personSearch = (PersonSearch) getRequest(octopusContext).getBean("PersonSearch");
            /*
             * modified to support category multi selection cklein 2008-02-26
             */
            final List list = (List) BeanFactory.transform(octopusContext.requestAsObject("categoriesSelection"), List.class);
            final ArrayList<Integer> selection = new ArrayList<Integer>(list.size());
            if (list.size() > 0 && list.get(0).toString().length() > 0) {
                final Iterator iter = list.iterator();
                while (iter.hasNext()) {
                    selection.add(new Integer((String) iter.next()));
                }
            }
            personSearch.categoriesSelection = selection;
        } else {
            personSearch = (PersonSearch) getRequest(octopusContext).getBean("PersonSearch", "search");
            final PersonSearch searchFromRequest = personSearch;
            personSearch = (PersonSearch) octopusContext.sessionAsObject("search" + BEANNAME);
            if (searchFromRequest != null && searchFromRequest.listorder != null) {
                personSearch.listorder = searchFromRequest.listorder;
            }
        }
        if (personSearch == null) {
            personSearch = new PersonSearch();
        }

        final PersonSearch sessionSearchPerson = (PersonSearch) octopusContext.sessionAsObject("search" + BEANNAME);

        if (sessionSearchPerson != null) {
            personSearch.lastlistorder = sessionSearchPerson.listorder;
            /*
             * Gets the last string order of the session SearchPerson object and
             * set it to the new session.
             */
            personSearch.sort = sessionSearchPerson.sort;
        }
        personSearch.sortList = sortList;

        octopusContext.setSession("search" + BEANNAME, personSearch);

        octopusContext.getContentObject().setField("personSearchField", personSearch.listorder);
        octopusContext.getContentObject().setField("personSearchOrder", personSearch.sort);
        return personSearch;
    }

    //
    // geschützte Hilfsmethoden
    //
    private boolean getContextAsBoolean(final OctopusContext octopusContext, final String key) {
        return Boolean.valueOf(octopusContext.contentAsString(key)).booleanValue() ||
                octopusContext.requestAsBoolean(key).booleanValue();
    }

    /**
     * Gibt eine Person-List-Filter Bedinung inkl. Mandanten Einschränkung
     * zurück.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException
     */
    protected Clause getPersonListFilter(final OctopusContext octopusContext, final boolean status) throws BeanException {
        final WhereList list = new WhereList();

        final String searchFiled = octopusContext.getRequestObject().getParamAsString("searchField");
        if (searchFiled == null) {
            addPersonListFilter(octopusContext, list);
        } else {
            addPersonListFilterSimple(searchFiled, list, status);
        }

        final Where orgunitFilter =
                Expr.equal("tperson.fk_orgunit", ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId());
        if (list.size() == 0) {
            return orgunitFilter;
        } else {
            return Where.and(orgunitFilter, list);
        }
    }

    /**
     * Erweitert die übergebene WhereList um Bedingungen der Suche. Die
     * WhereList ist danach <strong>niemals</strong> leer.
     *
     * @param octopusContext The {@link OctopusContext}
     * @param list           FIXME
     * @throws BeanException
     */
    private void addPersonListFilter(final OctopusContext octopusContext, final WhereList list) throws BeanException {
        final PersonSearch personSearch = getSearch(octopusContext);

        list.addAnd(Expr.equal("tperson.deleted", PersonConstants.DELETED_FALSE));

        /*
         * modified to support search for individual workareas as per change
         * request for version 1.2.0 cklein 2008-02-21
         */
        if (personSearch.workarea != null) {
            list.addAnd(Expr.equal("tperson.fk_workarea", personSearch.workarea));
        }

        if (personSearch.categorie2 != null) {
            list.addAnd(Expr.equal("cat2.fk_categorie", personSearch.categorie2));
        }
        if (personSearch.city != null && personSearch.city.length() != 0) {
            list.addAnd(getCityFilter(personSearch));
        }
        if (personSearch.country != null && personSearch.country.length() != 0) {
            list.addAnd(getCountryFilter(personSearch));
        }
        if (personSearch.company != null && personSearch.company.length() != 0) {
            list.addAnd(getCompanyFilter(personSearch));
        }
        if (personSearch.importsource != null && personSearch.importsource.length() != 0) {
            list.addAnd(DatabaseHelper.getWhere(personSearch.importsource, new String[] { "importsource" }));
        }
        if (personSearch.firstname != null && personSearch.firstname.length() != 0) {
            list.addAnd(getFirstnameFilter(personSearch));
        }
        if (personSearch.function != null && personSearch.function.length() != 0) {
            list.addAnd(getFunctionFilter(personSearch));
        }
        if (personSearch.iscompany != null && personSearch.iscompany.booleanValue()) {
            list.addAnd(Expr.equal("iscompany", PersonConstants.ISCOMPANY_TRUE));
        } else {
            list.addAnd(Expr.equal("iscompany", PersonConstants.ISCOMPANY_FALSE));
        }
        if (personSearch.lastname != null && personSearch.lastname.length() != 0) {
            list.addAnd(getLastnameFilter(personSearch));
        }
        if (personSearch.street != null && personSearch.street.length() != 0) {
            list.addAnd(getStreetFilter(personSearch));
        }
        if (personSearch.validdate != null && personSearch.validtype != null) {
            final Date end = new Date(personSearch.validdate.getTime() + 86400000 - 1000);
            switch (personSearch.validtype.intValue()) {
            case 1:
                list.addAnd(Expr.lessOrEqual("dateexpire", end));
                break;
            case 2:
                list.addAnd(Expr.greaterOrEqual("dateexpire", personSearch.validdate));
                break;
            case 3:
                list.addAnd(Where.and(Expr.greaterOrEqual("dateexpire", personSearch.validdate),
                        Expr.lessOrEqual("dateexpire", end)));
                break;
            }
        }
        if (personSearch.zipcode != null && personSearch.zipcode.length() != 0) {
            list.addAnd(getZipCodeFilter(personSearch));
        }
        if (personSearch.state != null && personSearch.state.length() != 0) {
            list.addAnd(getStateFilter(personSearch));
        }
        if (personSearch.languages != null && personSearch.languages.length() != 0) {
            final LanguagesFilterFactory factory = new LanguagesFilterFactory();
            list.addAnd(factory.createLanguagesFilter(personSearch));
        }
        if (personSearch.onlyhosts != null && personSearch.onlyhosts.booleanValue()) {
            list.addAnd(Expr.in("tperson.pk", new RawClause("(SELECT fk_host FROM veraweb.tevent)")));
        }
        if (personSearch.internal_id != null && personSearch.internal_id.length() != 0) {
            list.addAnd(getInternalIdFilter(personSearch));
        }
    }

    private Clause getStateFilter(final PersonSearch personSearch) {
        final String value = personSearch.state;
        final String[] columns =
                { "state_a_e1", "state_a_e2", "state_a_e3", "state_b_e1", "state_b_e2", "state_b_e3", "state_c_e1", "state_c_e2",
                        "state_c_e3" };
        return DatabaseHelper.getWhere(value, columns);
    }

    private Clause getInternalIdFilter(PersonSearch personSearch) {
        return DatabaseHelper.getWhere(personSearch.internal_id, new String[] { "internal_id" });
    }

    private Clause getZipCodeFilter(final PersonSearch personSearch) {
        return DatabaseHelper
                .getWhere(personSearch.zipcode, new String[] { "zipcode_a_e1", "zipcode_a_e2", "zipcode_a_e3", "zipcode_b_e1",
                        "zipcode_b_e2", "zipcode_b_e3", "zipcode_c_e1", "zipcode_c_e2", "zipcode_c_e3" });
    }

    private Clause getStreetFilter(final PersonSearch personSearch) {
        return DatabaseHelper.getWhere(personSearch.street,
                new String[] { "street_a_e1", "street_a_e2", "street_a_e3", "street_b_e1", "street_b_e2",
                        "street_b_e3", "street_c_e1", "street_c_e2", "street_c_e3" });
    }

    private Clause getLastnameFilter(final PersonSearch personSearch) {
        return DatabaseHelper.getWhere(personSearch.lastname,
                new String[] { "lastname_a_e1", "lastname_a_e2", "lastname_a_e3", "lastname_b_e1",
                        "lastname_b_e2", "lastname_b_e3" });
    }

    private Clause getFunctionFilter(final PersonSearch personSearch) {
        return DatabaseHelper.getWhere(personSearch.function,
                new String[] { "function_a_e1", "function_a_e2", "function_a_e3", "function_b_e1",
                        "function_b_e2", "function_b_e3", "function_c_e1", "function_c_e2", "function_c_e3" });
    }

    private Clause getFirstnameFilter(final PersonSearch personSearch) {
        return DatabaseHelper.getWhere(personSearch.firstname,
                new String[] { "firstname_a_e1", "firstname_a_e2", "firstname_a_e3", "firstname_b_e1",
                        "firstname_b_e2", "firstname_b_e3" });
    }

    private Clause getCompanyFilter(final PersonSearch personSearch) {
        return DatabaseHelper
                .getWhere(personSearch.company, new String[] { "company_a_e1", "company_a_e2", "company_a_e3", "company_b_e1",
                        "company_b_e2", "company_b_e3", "company_c_e1", "company_c_e2", "company_c_e3" });
    }

    private Clause getCountryFilter(final PersonSearch personSearch) {
        return DatabaseHelper
                .getWhere(personSearch.country, new String[] { "country_a_e1", "country_a_e2", "country_a_e3", "country_b_e1",
                        "country_b_e2", "country_b_e3", "country_c_e1", "country_c_e2", "country_c_e3" });
    }

    private Clause getCityFilter(final PersonSearch personSearch) {
        return DatabaseHelper
                .getWhere(personSearch.city, new String[] { "city_a_e1", "city_a_e2", "city_a_e3", "city_b_e1", "city_b_e2",
                        "city_b_e3", "city_c_e1", "city_c_e2", "city_c_e3" });
    }

    /**
     * Erweitert die übergebene WhereList um Bedingungen der Suche. Die
     * WhereList ist danach <strong>niemals</strong> leer.
     *
     * @param searchField FIXME
     * @throws BeanException
     */
    private void addPersonListFilterSimple(final String searchField, final WhereList list2, final boolean status)
            throws BeanException {

        /*
         * modified to support search for individual workareas as per change
         * request for version 1.2.0 cklein 2008-02-21
         */

        final WhereList list = new WhereList();

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "firstname_a_e1", "firstname_a_e2", "firstname_a_e3", "firstname_b_e1",
                        "firstname_b_e2", "firstname_b_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "lastname_a_e1", "lastname_a_e2", "lastname_a_e3", "lastname_b_e1",
                        "lastname_b_e2", "lastname_b_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "company_a_e1", "company_a_e2", "company_a_e3", "company_b_e1",
                        "company_b_e2", "company_b_e3", "company_c_e1", "company_c_e2", "company_c_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "fon_a_e1", "fon_a_e2", "fon_a_e3", "fon_b_e1", "fon_b_e2", "fon_b_e3",
                        "fon_c_e1", "fon_c_e2", "fon_c_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "fax_a_e1", "fax_a_e2", "fax_a_e3", "fax_b_e1", "fax_b_e2", "fax_b_e3",
                        "fax_c_e1", "fax_c_e2", "fax_c_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "mobil_a_e1", "mobil_a_e2", "mobil_a_e3", "mobil_b_e1", "mobil_b_e2",
                        "mobil_b_e3", "mobil_c_e1", "mobil_c_e2", "mobil_c_e3" }));

        list.addOr(DatabaseHelper.getWhere(searchField,
                new String[] { "mail_a_e1", "mail_a_e2", "mail_a_e3", "mail_b_e1", "mail_b_e2", "mail_b_e3",
                        "mail_c_e1", "mail_c_e2", "mail_c_e3" }));

        list.addOr(DatabaseHelper.getWhere(searchField, new String[] { "tcategorie.catname" }));

        if (status) {
            list.addOr(DatabaseHelper.getWhere(searchField, new String[] { "tworkarea.name" }));
        }

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "function_a_e1", "function_a_e2", "function_a_e3", "function_b_e1",
                        "function_b_e2", "function_b_e3", "function_c_e1", "function_c_e2", "function_c_e3" }));

        list.addOr(DatabaseHelper.getWhere(searchField, new String[] { "note_a_e1", "note_b_e1" }));

        list.addOr(DatabaseHelper.getWhere(searchField,
                new String[] { "city_a_e1", "city_a_e2", "city_a_e3", "city_b_e1", "city_b_e2", "city_b_e3",
                        "city_c_e1", "city_c_e2", "city_c_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "street_a_e1", "street_a_e2", "street_a_e3", "street_b_e1", "street_b_e2",
                        "street_b_e3", "street_c_e1", "street_c_e2", "street_c_e3" }));

        list.addOr(DatabaseHelper
                .getWhere(searchField, new String[] { "zipcode_a_e1", "zipcode_a_e2", "zipcode_a_e3", "zipcode_b_e1",
                        "zipcode_b_e2", "zipcode_b_e3", "zipcode_c_e1", "zipcode_c_e2", "zipcode_c_e3" }));

        list2.addAnd(Where.and(Expr.equal("tperson.deleted", PersonConstants.DELETED_FALSE), list));

        return;
    }
}
