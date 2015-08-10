/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author sweiz - tarent solutions GmbH - tarent solutions GmbH on 30.07.15.
 */
public class EventPreconditionWorker extends ListWorkerVeraWeb {
    private static final Logger LOGGER = Logger.getLogger(EventPreconditionWorker.class.getCanonicalName());
    public static final String INPUT_savePrecondition[] = { };

    public EventPreconditionWorker() {
        super("EventPrecondition");
    }
    
    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tevent_precondition.fk_event_main", getEvent(cntx).id));

        //TODO Hier weitermachen
        //cntx.getContextField("").get()
        select.where(Expr.notEqual("tevent.pk", cntx.requestAsInteger("list.event_precondition")));
    }

    @Override
    public List showList(OctopusContext octopusContext) throws BeanException {
        final Database database = new DatabaseVeraWeb(octopusContext);

        String mainEventId = octopusContext.;
        final Select selectEventsStatement = getSelectStatement(database, mainEventId);

        return database.getList(selectEventsStatement, database);
    }

    private Select getSelectStatement(Database database, String mainEventId) {
        Clause clause = Expr.equal("p.fk_event_main", mainEventId);

        return SQL.SelectDistinct(database).
            select("e.*").select("p.invitationstatus").selectAs("p.datebegin", "precondition_date").
            join("veraweb.tevent_precondition p", "p.fk_event_precondition", "e.pk").
            from("veraweb.tevent e").
            where(clause);
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tevent", "tevent.pk", "tevent_precondition.fk_event_precondition");
        select.selectAs("tevent.shortname", "shortName");
        select.addOrderBy(Order.asc("tevent_precondition.fk_event_precondition"));
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        if(getEvent(cntx) != null) {
            select.where(Expr.equal("tevent_precondition.fk_event_main", getEvent(cntx).id));
        } else {
            Request request = new RequestVeraWeb(cntx);
            select.where(Expr.equal("tevent_precondition.fk_event_main", Integer.valueOf((String) request.getField("id"))));
        }
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
        try {
            super.saveBean(octopusContext, bean, context);
        } catch (BeanException e) {
            LOGGER.error("Fehler beim speichern der neuen Kategorie", e);
        } catch (IOException e) {
            LOGGER.error("Fehler beim speichern der neuen Kategorie", e);
        }
    }

//    /**
//     * Saves a precondition with given information in octopuscontext
//     *
//     * @param octopusContext Context data of octopus
//     * @throws BeanException BeanException
//     */
//    public void savePrecondition(OctopusContext octopusContext) throws BeanException {
//        Request request = new RequestVeraWeb(octopusContext);
//        String event_main = (String) request.getField("id");
//        String event_precondition = (String) request.getField("event_precondition");
//        String invitationstatus = (String) request.getField("invitationstatus_a");
//        String max_begin = (String) request.getField("max_begin");
//
//        if(event_main != null && !event_main.equals(0) && event_precondition != null && !event_precondition.equals(0) &&
//                invitationstatus != null && !invitationstatus.equals(0) && max_begin != null && !max_begin.equals(0)) {
//            TransactionContext transactionContext = (new DatabaseVeraWeb(octopusContext)).getTransactionContext();
//            Database database = new DatabaseVeraWeb(octopusContext);
//
//            final Date maxBegin = getFormattedDate(max_begin);
//
//            transactionContext.execute(SQL.Insert(database).table("veraweb.tevent_precondition")
//                    .insert("fk_event_main", Integer.valueOf(event_main))
//                    .insert("fk_event_precondition", Integer.valueOf(event_precondition))
//                    .insert("invitationstatus", Integer.valueOf(invitationstatus))
//                    .insert("datebegin", maxBegin));
//
//            transactionContext.commit();
//        }
//    }

    private Date getFormattedDate(String datebegin) {
        final String formattedDate = datebegin.replaceAll("\\.", "-");
        Date correctDate = null;

        try {
            final Date format = new SimpleDateFormat("dd-MM-yyyy").parse(formattedDate);
            final String format2 = new SimpleDateFormat("yyyy-MM-dd").format(format);
            correctDate = new SimpleDateFormat("yyyy-MM-dd").parse(format2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return correctDate;
    }

    private Event getEvent(OctopusContext cntx) {
        if(cntx.contentAsObject("event") != null) {
            return (Event) cntx.contentAsObject("event");
        } else {
            return null;
        }
    }
}
