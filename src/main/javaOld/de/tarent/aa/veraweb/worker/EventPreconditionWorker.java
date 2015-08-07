package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Insert;
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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sweiz - tarent solutions GmbH - tarent solutions GmbH on 30.07.15.
 */
public class EventPreconditionWorker extends ListWorkerVeraWeb {
    private static final Logger LOGGER = Logger.getLogger(EventPreconditionWorker.class.getCanonicalName());

    public EventPreconditionWorker() {
        super("EventPrecondition");
    }
    
    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tevent_precondition.fk_event_main", getEvent(cntx).id));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tevent", "tevent.pk", "tevent_precondition.fk_event_precondition");
        select.selectAs("tevent.shortname", "shortName");
        select.addOrderBy(Order.asc("tevent_precondition.fk_event_precondition"));
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        select.where(Expr.equal("tevent_precondition.fk_event_main", getEvent(cntx).id));
    }

    public void savePrecondition(OctopusContext octopusContext) {
        Database database = new DatabaseVeraWeb(octopusContext);
        final Integer eventMain = octopusContext.requestAsInteger("id");
        final Integer eventPrecondition = octopusContext.requestAsInteger("event_precondition");
        final Integer invitationstatus = octopusContext.requestAsInteger("invitationstatus_a");
        Date maxBegin = null;

        DateFormat format = new SimpleDateFormat("d-m-yyyy");
        try {
            maxBegin = format.parse(octopusContext.requestAsString("max_begin"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Insert insert = new Insert();
        insert.table("tevent_precondition");
        insert.insert("fk_event_main", eventMain);
        insert.insert("fk_event_precondition", eventPrecondition);
        insert.insert("invitationstatus", invitationstatus);
        insert.insert("datebegin", maxBegin);

        try {
            insert.executeInsert(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
        try {
            super.saveBean(octopusContext, bean, context);
        } catch (BeanException e) {
            LOGGER.error("Fehler beim Speichern der neuen Vorbedingung", e);
        } catch (IOException e) {
            LOGGER.error("Fehler beim Speichern der neuen Vorbedingung", e);
        } catch (NumberFormatException e) {
            LOGGER.error("Fehler beim Speichern der neuen Vorbedingung", e);
        }
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
