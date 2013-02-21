/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
 * Created on 28.02.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Location;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker bearbeitet und liefert Ort-Stammdaten-Listen.
 */
public class LocationWorker extends StammdatenWorker {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public LocationWorker() {
		super("Location");
	}

    //
    // Oberklasse BeanListWorker
    //
	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId()));
	}

	@Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId()));
	}

	@Override
    protected Clause getWhere(OctopusContext cntx) {
		return Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId());
	}

	@Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		((Location)bean).orgunit = ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId();
		super.saveBean(cntx, bean, context);
	}
}
