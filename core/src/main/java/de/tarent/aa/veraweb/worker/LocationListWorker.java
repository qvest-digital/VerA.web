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
import de.tarent.aa.veraweb.beans.Location;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * List worker for handling {@link Location}s.
 *
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 *
 */
public class LocationListWorker extends ListWorkerVeraWeb {

    /**
     * Default constructor.
     */
    public LocationListWorker() {
        super("Location");
    }

    @Override
    protected void extendWhere(final OctopusContext octopusContext, final Select select)
            throws BeanException, IOException {
        select.where(Expr.equal("tlocation.fk_orgunit",
                ((PersonalConfigAA) (octopusContext.personalConfig())).getOrgUnitId()));
    }

    @Override
    protected void extendAll(final OctopusContext octopusContext, final Select select)
            throws BeanException, IOException {
        select.where(Expr.equal("tlocation.fk_orgunit",
                ((PersonalConfigAA) (octopusContext.personalConfig())).getOrgUnitId()));
    }

    protected Integer getAlphaStart(OctopusContext octopusContext, String start) throws BeanException, IOException {
        Database database = getDatabase(octopusContext);
        Select select = database.getCount(BEANNAME);
        extendWhere(octopusContext, select);
        if (start != null && start.length() > 0) {
            select.whereAnd(Expr.less("tlocation.locationname", Escaper.escape(start)));
        }

        Integer i = database.getCount(select);
        return new Integer(i.intValue() - (i.intValue() % getLimit(octopusContext).intValue()));
    }

    /**
     * Bestimmt ob ein Veranstaltungsort aufgrund bestimmter Kriterien gelöscht wird oder nicht
     */
    @Override
    protected int removeSelection(OctopusContext octopusContext, List errors, List selection,
                                  TransactionContext transactionContext) throws BeanException, IOException {

        int count = 0;
        if (selection == null || selection.size() == 0) {
            return count;
        }
        Database database = transactionContext.getDatabase();
        Map questions = new HashMap();

        Location location = (Location) database.createBean("Location");
        Clause clause = Expr.in("pk", selection);
        Select select = database.getSelect("Location").where(clause);

        List removeLocations = new ArrayList();

        List locationList = database.getBeanList("Location", select);

        for (Iterator it = locationList.iterator(); it.hasNext(); ) {
            location = (Location) it.next();

            Integer countReferences = database.getCount(database.getCount("Event").where(Expr.equal("fk_location", location.id)));
            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);

            if (countReferences != null && countReferences > 0) {
                errors.add(languageProvider.getProperty("HINT_DELETE_NOT_POSSIBLE_ONE") +
                        location.name +
                        languageProvider.getProperty("HINT_DELETE_NOT_POSSIBLE_TWO"));
            } else if (octopusContext.requestAsBoolean("remove-location" + location.id).booleanValue()) {
                removeLocations.add(location.id);
            } else {
                questions.put("remove-location" + location.id,
                        languageProvider.getProperty("CONFIRMATION_CREATE_LOCATION_ONE") +
                                location.name +
                                languageProvider.getProperty("CONFIRMATION_CREATE_LOCATION_TWO"));
            }
        }

        if (errors.isEmpty() && !questions.isEmpty()) {
            octopusContext.setContent("listquestions", questions);
        }

        if (removeLocations.size() > 0) {
            clause = Where.or(clause, Expr.in("pk", removeLocations));
        }

        select = database.getSelectIds(location).where(clause);

        if (!removeLocations.isEmpty()) {
            try {
                Map data;
                for (Iterator it = database.getList(select, transactionContext).iterator(); it.hasNext(); ) {
                    data = (Map) it.next();
                    location.id = (Integer) data.get("id");

                    if (removeBean(octopusContext, location, transactionContext)) {
                        selection.remove(location.id);
                        count++;
                    }
                }
                transactionContext.commit();
            } catch (BeanException e) {
                transactionContext.rollBack();
                throw new BeanException("Der Veranstaltungsort konnten nicht gel\u00f6scht werden.", e);
            }
        }
        return count;
    }

    /**
     * Temporarily method for inserting a {@link Location}. <br>
     * TODO: move this method to LocationDetailWorker!
     *
     * @param octopusContext
     *            Octopus context
     * @param errors
     *            list of errors
     * @param location
     *            bean {@link Location}
     * @param transactionContext
     *            database transaction context
     * @throws BeanException
     *             exception
     * @throws IOException
     *             exception
     */
    protected void insertBean(final OctopusContext octopusContext, final List<String> errors, final Location location,
                              final TransactionContext transactionContext) throws BeanException, IOException {
        if (location.isModified() && location.isCorrect()) {
            Database database = transactionContext.getDatabase();

            String orgunit = database.getProperty(location, "orgunit");
            Clause clause = Expr.equal(database.getProperty(location, "name"), location.getField("name"));
            if (orgunit != null) {
                clause = Where.and(Expr.equal(orgunit, ((PersonalConfigAA) (octopusContext.personalConfig())).getOrgUnitId()),
                        clause);
            }

            Integer exist = database.getCount(database.getCount(location).where(clause), transactionContext);
            if (exist.intValue() != 0) {
                errors.add("Es existiert bereits ein Veranstaltungsort unter dem Namen '" + location.getField("name")
                        + "'.");
            } else {
                transactionContext.getDatabase().saveBean(location, transactionContext, location.isModified());
            }
        }
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException, IOException {
        if(octopusContext.getRequestObject().containsParam("noneDeleted")) {
                octopusContext.setContent("countRemove", 0);
        }
        super.saveList(octopusContext);
    }

    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
        Boolean noneChecked = true;
        if(octopusContext.getRequestObject().getParam("list") != null) {
                String[] listOfIds = null;
                if(octopusContext.getRequestObject().getParam("list").getClass().getName().equals("java.lang.String")) {
                listOfIds = new String[1];
                listOfIds[0]= (String) octopusContext.getRequestObject().getParam("list");
                } else {
                        listOfIds = (String[]) octopusContext.getRequestObject().getParam("list");
                }
                        for (String id : listOfIds) {
                                        if(octopusContext.getRequestObject().containsParam(id+"-select")){
                                                noneChecked = false;
                                                break;
                                }
                        }
        }
    if(octopusContext.getRequestObject().get("remove") != null
                && !octopusContext.getRequestObject().get("remove").equals("Ja")
                && !noneChecked) {
        Boolean noMessage = null;
        octopusContext.setContent("countRemove", noMessage);
    }

    return super.showList(octopusContext);
    }
}
