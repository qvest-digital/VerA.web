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
 * Created by sweiz - tarent solutions GmbH on 30.07.15.
 */
public class EventSubEventListWorker extends ListWorkerVeraWeb {
    private static final Logger LOGGER = Logger.getLogger(EventSubEventListWorker.class.getCanonicalName());

    public EventSubEventListWorker() {
        super("EventSubEventList");
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tevent_SubEventList.fk_event_main", getEvent(cntx).id));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tevent", "tevent.pk", "tevent_SubEventList.fk_event_SubEventList");
        select.selectAs("tevent.shortname", "shortName");
//        select.orderBy(Order.asc("tevent_SubEventList.fk_event_SubEventList"));
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        select.where(Expr.equal("tevent_SubEventList.fk_event_main", getEvent(cntx).id));
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
        try {
            super.saveBean(octopusContext, bean, context);
        } catch (BeanException e) {
            LOGGER.error("Fehler beim speichern der neuen Vorbedingung", e);
        } catch (IOException e) {
            LOGGER.error("Fehler beim speichern der neuen Vorbedingung", e);
        }
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
