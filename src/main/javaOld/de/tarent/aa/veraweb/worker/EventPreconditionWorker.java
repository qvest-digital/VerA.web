package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.EventPrecondition;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWebFactory;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
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

    //private static final String PARAM_EVENT_ID = "eventId";
    //public static final String[] INPUT_setEventPrecondition = {PARAM_EVENT_ID};
    //public static final String OUTPUT_setEventPrecondition = PARAM_EVENT_ID;
    //public static final String[] INPUT_saveDetail = {PARAM_EVENT_ID};
    //public static final boolean eventId = false;

    public EventPreconditionWorker() {
        super("EventPrecondition");
    }
    
    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tevent_precondition.fk_event_main", getEvent(cntx). id));

        //TODO Hier weitermachen
        //cntx.getContextField("").get()
        select.where(Expr.notEqual("tevent.pk", cntx.requestAsInteger("list.event_precondition")));
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

//    @Override
//    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
//        try {
//            super.saveBean(octopusContext, bean, context);
//        } catch (BeanException e) {
//            LOGGER.error("Fehler beim Speichern der neuen Vorbedingung", e);
//        } catch (IOException e) {
//            LOGGER.error("Fehler beim Speichern der neuen Vorbedingung", e);
//        } catch (NumberFormatException e) {
//            LOGGER.error("Fehler beim Speichern der neuen Vorbedingung", e);
//        }
//    }

    public static final String INPUT_saveDetail[] = {};
    private DatabaseVeraWebFactory databaseVeraWebFactory = new DatabaseVeraWebFactory();

    public void saveDetail(OctopusContext cntx/*, Boolean saveprecondition, Boolean eventId*/)
            throws BeanException, IOException {
        //if (saveprecondition == null || !saveprecondition.booleanValue()) {
        //    return;
        //}
        //if (eventId == null || !eventId.booleanValue()) {
        //    return;
        //}

        //Request request = new RequestVeraWeb(cntx);








        Database database = databaseVeraWebFactory.createDatabaseVeraWeb(cntx);
        EventPrecondition eventPrecondition = (EventPrecondition) cntx.contentAsObject("precondition");

        if(eventPrecondition != null) {
            Insert insert = database.getInsert(eventPrecondition);
            insert.insert("fk_event_main", eventPrecondition.event_main);
            insert.insert("fk_event_precondition", eventPrecondition.event_precondition);
            insert.insert("invitationstatus", eventPrecondition.invitationstatus);
            insert.insert("datebegin", eventPrecondition.max_begin);
        }







//        final String PARAM_TASK = "precondition";
//        final String PARAM_EVENT_ID = "eventId";
//
//        try {
//
//            if (eventPrecondition == null) {
//                eventPrecondition = (Task) request.getBean("Task", PARAM_TASK);
//                DateHelper.addTimeToDate(task.startdate, task.starttime, task.getErrors());
//                DateHelper.addTimeToDate(task.enddate, task.endtime, task.getErrors());
//            }
//
//            /** Aufgabe speichern */
//            if (eventPrecondition.isModified() && eventPrecondition.isCorrect()) {
//                BeanChangeLogger clogger = new BeanChangeLogger(database,
//                        context);
//                if (eventPrecondition.getId() == null) {
//                    cntx.setContent("countInsert", Integer.valueOf(1));
//                    database.getNextPk(task, context);
//
//                    eventPrecondition.updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
//
//                    Insert insert = database.getInsert(eventPrecondition);
//                    insert.insert("pk", eventPrecondition.getId());
//
//                    if (!((PersonalConfigAA) cntx.personalConfig()).getGrants()
//                            .mayReadRemarkFields()) {
//                        insert.remove("note");
//                    }
//                    context.execute(insert);
//
//                    clogger.logInsert(cntx.personalConfig().getLoginname(),
//                            task);
//                } else {
//                    cntx.setContent("countUpdate", Integer.valueOf(1));
//                    Update update = database.getUpdate(task);
//                    if (!((PersonalConfigAA) cntx.personalConfig()).getGrants()
//                            .mayReadRemarkFields()) {
//                        update.remove("note");
//                    }
//                    context.execute(update);
//
//                    clogger.logUpdate(cntx.personalConfig().getLoginname(), oldTask, task);
//                }
//            } else {
//                cntx.setStatus("notsaved");
//            }
//            cntx.setContent(PARAM_TASK, task);
//            cntx.setContent("task-starthastime",
//                    Boolean.valueOf(DateHelper.isTimeInDate(task.getStartdate())));
//            cntx.setContent("task-endhastime",
//                    Boolean.valueOf(DateHelper.isTimeInDate(task.getEnddate())));
//
//            if (task != null && task.personId != null) {
//                Person person = getPersonFromDB(cntx, task.personId);
//                cntx.getContentObject().setField("refPerson", person);
//            };
//
//            context.commit();
//        } catch (BeanException e) {
//            context.rollBack();
//            // must report error to user
//            throw new BeanException(
//                    "Die Taskdetails konnten nicht gespeichert werden.", e);
//        }
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
