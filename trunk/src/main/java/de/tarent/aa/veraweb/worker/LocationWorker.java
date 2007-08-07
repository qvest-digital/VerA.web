/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: LocationWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 28.02.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Location;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
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
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA)(cntx.configImpl())).getOrgUnitId()));
	}

	protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA)(cntx.configImpl())).getOrgUnitId()));
	}

	protected Clause getWhere(OctopusContext cntx) {
		return Expr.equal("tlocation.fk_orgunit", ((PersonalConfigAA)(cntx.configImpl())).getOrgUnitId());
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		((Location)bean).orgunit = ((PersonalConfigAA)(cntx.configImpl())).getOrgUnitId();
		super.saveBean(cntx, bean);
	}
}
