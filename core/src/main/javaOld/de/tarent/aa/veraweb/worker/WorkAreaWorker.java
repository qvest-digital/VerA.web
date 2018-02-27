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
import de.tarent.aa.veraweb.beans.WorkArea;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class WorkAreaWorker is a concrete worker
 * for operations on the {@link WorkArea} entity
 * bean.
 *
 * @author cklein
 * @since 1.2.0
 * @see de.tarent.aa.veraweb.beans.WorkArea
 */
public class WorkAreaWorker extends StammdatenWorker {
    /**
     * Constructs a new instance of this.
     */
    public WorkAreaWorker() {
        super("WorkArea");
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) throws BeanException, IOException {
        WorkArea workArea = (WorkArea) bean;
        if (workArea.orgunit == null) {
            workArea.orgunit = ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId();
        }

        if (workArea.orgunit == null) {
            handleMissingOrgunitErrorMessage(octopusContext, workArea);
        } else if (workArea.name.trim().isEmpty()) {
            handleWorkareaNameEmptyErrorMessage(octopusContext);
        } else {
            super.saveBean(octopusContext, workArea, context);
        }
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        // hide default entry with pk=0 from user, the workarea "Kein" with pk ::= 0
        // is only used internally in order to be able to use foreign key constraints
        // with individual workareas being assigned to one or multiple users.
        select.where(Expr.greater("pk", 0));
        select.where(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select)
            throws BeanException, IOException {
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
    /*
	 * 2009-05-12 cklein
	 *
	 * fixed as part of issue #1530 - deletion of workareas and automatic unassignment from existing persons
	 */
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext transactionContext) throws BeanException, IOException {
        Database database = transactionContext.getDatabase();
        // first remove all workArea assignments from all persons
        PersonListWorker.unassignWorkArea(transactionContext, ((WorkArea) bean).id, null);
        Delete stmt = database.getDelete("WorkArea");
        stmt.byId("pk", ((WorkArea) bean).id);
        transactionContext.execute(stmt);
        transactionContext.commit();
        return true;
    }

    /*
     * 2009-05-12 cklein
     *
     * introduced as part of fix for issue #1530 - deletion of orgunits and automatic deletion of associated work areas. will not commit itself.
     */
    @SuppressWarnings("unchecked")
    public static void removeAllWorkAreasFromOrgUnit(TransactionContext transactionContext, Integer orgUnitId) throws BeanException, IOException {
        Select stmt = transactionContext.getDatabase().getSelect("WorkArea");
        stmt.select("pk");
        stmt.where(Expr.equal("fk_orgunit", orgUnitId));

        try {
            ResultSet beans = ((Result) stmt.execute()).resultSet();
            while (beans.next()) {
                // first remove all workArea assignments from all persons
                PersonListWorker.unassignWorkArea(transactionContext, beans.getInt("pk"), null);
                Delete delstmt = transactionContext.getDatabase().getDelete("WorkArea");
                delstmt.byId("pk", beans.getInt("pk"));
                transactionContext.execute(delstmt);
                transactionContext.commit();
            }
        } catch (SQLException e) {
            throw new BeanException("Die dem Mandanten zugeordneten Arbeitsbereiche konnten nicht entfernt werden.", e);
        }
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select)
            throws BeanException, IOException {
        // hide default entry with pk=0 from user, the workarea "Kein" with pk ::= 0
        // is only used internally in order to be able to use foreign key constraints
        // with individual workareas being assigned to one or multiple users.
        select.where(Expr.greater("pk", 0));
        select.where(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
    }

    @Override
    public void getAll(OctopusContext cntx) throws BeanException, IOException {
        super.getAll(cntx);

        Integer count = cntx.requestAsInteger("count");
        if (count != null) {
            cntx.setContent("count", count);
        }
    }

    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
        if (octopusContext.getContextField("listerrors") != null) {
            Integer noMessages = -1;
            octopusContext.setContent("countInsert", noMessages);
            octopusContext.setContent("countUpdate", noMessages);
        }
        return super.showList(octopusContext);
    }


    private void handleWorkareaNameEmptyErrorMessage(OctopusContext octopusContext) {
        final LanguageProvider languageProvider = initLanguageProvider(octopusContext);
        List<String> errors = new ArrayList<>();
        errors.add(languageProvider.getProperty("WORKAREA_ERROR_MISSING_NAME"));
        octopusContext.setContent(OUTPUT_saveListErrors, errors);
    }

    private String getErrorMessageMissingOrgunit(OctopusContext octopusContext, WorkArea workArea) {
        final LanguageProvider languageProvider = initLanguageProvider(octopusContext);
        return languageProvider.getProperty("WORKAREA_ERROR_INCORRECT_NAME_ONE") + workArea.name + languageProvider.getProperty("WORKAREA_ERROR_INCORRECT_NAME_TWO");
    }

    private LanguageProvider initLanguageProvider(OctopusContext octopusContext) {
        final LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
        return languageProviderHelper.enableTranslation(octopusContext);
    }

    private void handleMissingOrgunitErrorMessage(OctopusContext octopusContext, WorkArea workArea) {
        List<String> errors;
        final String errorMessage;
        if (workArea.getErrors() != null && !workArea.getErrors().isEmpty()) {
            errors = workArea.getErrors();
        } else {
            errors = (List<String>) octopusContext.getContextField(OUTPUT_saveListErrors);
        }
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errorMessage = getErrorMessageMissingOrgunit(octopusContext, workArea);
        errors.add(errorMessage);
        octopusContext.setContent(OUTPUT_saveListErrors, errors);
    }
}
