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
