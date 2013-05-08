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
import java.util.List;

import de.tarent.aa.veraweb.beans.Location;
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
    protected void extendWhere(final OctopusContext cntx, final Select select) throws BeanException, IOException {
        select.where(Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId()));
    }

    @Override
    protected void extendAll(final OctopusContext cntx, final Select select) throws BeanException, IOException {
        select.where(Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId()));
    }

    /**
     * Temporarily method for inserting a {@link Location}. <br/>
     * TODO: move this method to LocationDetailWorker!
     * 
     * @param cntx
     *            Octopus context
     * @param errors
     *            list of errors
     * @param location
     *            bean {@link Location}
     * @param context
     *            database transaction context
     * @throws BeanException
     *             exception
     * @throws IOException
     *             exception
     */
    protected void insertBean(final OctopusContext cntx, final List<String> errors, final Location location,
            final TransactionContext context) throws BeanException, IOException {
        if (location.isModified() && location.isCorrect()) {
            Database database = context.getDatabase();

            String orgunit = database.getProperty(location, "orgunit");
            Clause clause = Expr.equal(database.getProperty(location, "name"), location.getField("name"));
            if (orgunit != null) {
                clause = Where.and(Expr.equal(orgunit, ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId()),
                        clause);
            }

            Integer exist = database.getCount(database.getCount(location).where(clause), context);
            if (exist.intValue() != 0) {
                errors.add("Es existiert bereits ein Veranstaltungsort unter dem Namen '" + location.getField("name")
                        + "'.");
            } else {
                context.getDatabase().saveBean(location, context, location.isModified());
            }
        }
    }

}
