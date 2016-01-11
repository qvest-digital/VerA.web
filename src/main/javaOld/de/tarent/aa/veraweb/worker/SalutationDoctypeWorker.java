/**
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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
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

	@Override
	protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {
        int count = 0;
        if (bean.isModified()) {
            if (bean.isCorrect()) {
                saveBean(cntx, bean, context);
                count++;
            } else {
                errors.addAll(bean.getErrors());
            }
        }
        return count;
    }

	@Override
	protected int updateBeanList(OctopusContext cntx, List errors, List beanlist, TransactionContext context) throws BeanException, IOException {
        int count = 0;
        for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
            Bean bean = (Bean)it.next();
            if (bean.isModified()) {
                if (bean.isCorrect()) {
                    saveBean(cntx, bean, context);
                    count++;
                } else {
                    errors.addAll(bean.getErrors());
                }
            }
        }
        return count;
    }
}
