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
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.TransactionContext;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
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
	protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("fk_event", getEvent(cntx).id));
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		select.join("veraweb.tdoctype", "tevent_doctype.fk_doctype", "tdoctype.pk");
		select.selectAs("tdoctype.docname", "name");
		select.selectAs("tdoctype.sortorder", "sortorder");
		select.selectAs("tdoctype.isdefault", "isdefault");
		select.orderBy(Order.asc("name"));
	}

	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		select.where(Expr.equal("fk_event", getEvent(cntx).id));
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		super.saveBean(cntx, bean);
		
		Database database = getDatabase(cntx);
		TransactionContext context = database.getTransactionContext();
		
		try {
			List list =
					database.getList(
					database.getSelectIds(new Guest()).
					where(Expr.equal("fk_event", ((EventDoctype)bean).event)));
			GuestWorker worker = WorkerFactory.getGuestWorker(cntx);
			for (Iterator it = list.iterator(); it.hasNext(); ) {
				worker.refreshDoctypes(cntx, database, context, (Integer)((Map)it.next()).get("id"));
			}
			context.commit();
		} finally {
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
