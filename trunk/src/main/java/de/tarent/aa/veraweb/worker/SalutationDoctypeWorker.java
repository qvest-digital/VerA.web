/*
 * $Id: SalutationDoctypeWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 08.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
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
	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.join("veraweb.tdoctype", "fk_doctype", "tdoctype.pk");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
		select.orderBy(Order.asc("tdoctype.sortorder").andAsc("tdoctype.docname"));
	}

	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Integer id = cntx.requestAsInteger("id");
		select.where(Expr.equal("fk_salutation", id));
		
		Database database = getDatabase(cntx);
		cntx.setContent("salutation", database.getBean("Salutation", id));
	}
}
