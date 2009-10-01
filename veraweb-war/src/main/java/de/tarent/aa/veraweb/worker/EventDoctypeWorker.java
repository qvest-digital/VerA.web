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

package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.EventDoctype;
import de.tarent.aa.veraweb.beans.Guest;
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
 * Dieser Octopus-Worker dient der Bearbeitung und Anzeige von
 * Ereignis-Dokumenttypen-Listen. 
 * 
 * @author mikel
 */
public class EventDoctypeWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public EventDoctypeWorker() {
		super("EventDoctype");
	}

    //
    // Oberklasse BeanListWorker
    //
	@Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("fk_event", getEvent(cntx).id));
	}

	@Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		select.join("veraweb.tdoctype", "tevent_doctype.fk_doctype", "tdoctype.pk");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
		select.selectAs("tdoctype.isdefault", "isdefault");
		select.orderBy(Order.asc("name"));
	}

	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		select.where(Expr.equal("fk_event", getEvent(cntx).id));
	}

	@Override
    protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		super.saveBean(cntx, bean);
		
		Database database = getDatabase(cntx);
		TransactionContext context = database.getTransactionContext();
		
		try {
			List list =
					database.getList(
					database.getSelectIds(new Guest()).
					where(Expr.equal("fk_event", ((EventDoctype)bean).event)), database);
			GuestWorker worker = WorkerFactory.getGuestWorker(cntx);
			for (Iterator it = list.iterator(); it.hasNext(); ) {
				worker.refreshDoctypes(cntx, database, context, (Integer)((Map)it.next()).get("id"));
			}
			context.commit();
		} 
		catch ( BeanException e )
		{
			context.rollBack();
		}
	}

    //
    // Hilfsmethoden
    //
	protected Event getEvent(OctopusContext cntx) {
		return (Event)cntx.contentAsObject("event");
	}
}
