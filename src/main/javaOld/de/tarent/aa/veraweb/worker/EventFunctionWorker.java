package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventFunctionWorker extends ListWorkerVeraWeb {

    private static final Logger LOGGER = Logger.getLogger(EventFunctionWorker.class.getCanonicalName());

    public EventFunctionWorker() {
        super("EventFunction");
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("fk_event", getEventAndMediaRepresentativeURL(cntx).id));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tfunction", "tevent_function.fk_function", "tfunction.pk");
        select.selectAs("tfunction.functionname", "name");
        select.orderBy(Order.asc("name"));
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        select.where(Expr.equal("fk_event", getEventAndMediaRepresentativeURL(cntx).id));
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
        try {
            super.saveBean(octopusContext, bean, context);
        } catch (BeanException e) {
            LOGGER.error("Fehler beim speichern der neuen Amtsbezeichnung/Funktion", e);
        } catch (IOException e) {
            LOGGER.error("Fehler beim speichern der neuen Amtsbezeichnung/Funktion", e);
        }
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
