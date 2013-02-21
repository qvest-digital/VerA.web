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
 * 
 * Created on 08.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker bearbeitet {@link de.tarent.aa.veraweb.beans.SalutationDoctype}-Listen
 */
public class SalutationDoctypeWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public SalutationDoctypeWorker() {
		super("SalutationDoctype");
	}

    //
    // Oberklasse BeanListWorker
    //
	@Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.join("veraweb.tdoctype", "fk_doctype", "tdoctype.pk");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
		select.orderBy(Order.asc("tdoctype.sortorder").andAsc("tdoctype.docname"));
	}

	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Integer id = cntx.requestAsInteger("id");
		select.where(Expr.equal("fk_salutation", id));
		
		Database database = getDatabase(cntx);
		cntx.setContent("salutation", database.getBean("Salutation", id));
	}
}
