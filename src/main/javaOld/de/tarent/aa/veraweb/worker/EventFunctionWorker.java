package de.tarent.aa.veraweb.worker;

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
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventFunctionWorker extends ListWorkerVeraWeb {

    public EventFunctionWorker() {
        super("EventFunction");
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("fk_event", getEvent(cntx).id));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tfunction", "tevent_function.fk_function", "tfunction.pk");
        select.selectAs("tfunction.functionname", "name");
        select.orderBy(Order.asc("name"));
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        select.where(Expr.equal("fk_event", getEvent(cntx).id));
    }

    @Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {

        Database database = context.getDatabase();
        super.saveBean(cntx, bean, context);
        List list =
                database.getList(
                        database.getSelectIds(new Guest()).
                                where(Expr.equal("fk_event", ((EventDoctype)bean).event)), context);
        GuestWorker worker = WorkerFactory.getGuestWorker(cntx);
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            worker.refreshDoctypes(cntx, database, context, (Integer)((Map)it.next()).get("id"));
        }
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
