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
import de.tarent.aa.veraweb.beans.EventFunction;
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
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventFunctionWorker extends ListWorkerVeraWeb {

    private static final Logger LOGGER = Logger.getLogger(EventFunctionWorker.class.getCanonicalName());

    public EventFunctionWorker() {
        super("EventFunction");
    }

    @Override
    public List showList(OctopusContext octopusContext) throws IOException, BeanException {


        Integer countRemove = (Integer) octopusContext.getContextField("countRemove");
        Integer countUpdate = (Integer) octopusContext.getContextField("countUpdate");
        Integer countInsert = (Integer) octopusContext.getContextField("countInsert");

        if (((countRemove != null) && !(countRemove.equals(0))) ||
                ((countUpdate != null) && !(countUpdate.equals(0))) ||
                ((countInsert != null) && !(countInsert.equals(0)))) {
            octopusContext.setContent("isEntityModified", true);
        } else if ((countRemove != null || countUpdate != null || countInsert != null) &&
                octopusContext.getRequestObject().get("remove") == null) {
            octopusContext.setContent("isEntityModified", false);
        }

        return super.showList(octopusContext);
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
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context) {
        try {
            ((EventFunction) bean).verify(octopusContext);
            super.saveBean(octopusContext, bean, context);
        } catch (BeanException e) {
            LOGGER.error("Fehler beim speichern der neuen Amtsbezeichnung/Funktion", e);
        } catch (IOException e) {
            LOGGER.error("Fehler beim speichern der neuen Amtsbezeichnung/Funktion", e);
        }
    }

    private Event getEvent(OctopusContext octopusContext) {
        Event event = (Event) octopusContext.contentAsObject("event");

        if (event == null) {

            Database database = new DatabaseVeraWeb(octopusContext);
            Request request = new RequestVeraWeb(octopusContext);

            String eventId = octopusContext.getRequestObject().get("add-event");
            try {
                event = (Event) database.getBean("Event", Integer.valueOf(eventId));
            } catch (BeanException e) {
                LOGGER.error("Fehler beim speichern der neuen Amtsbezeichnung/Funktion", e);
            } catch (IOException e) {
                LOGGER.error("Fehler beim speichern der neuen Amtsbezeichnung/Funktion", e);
            }
        }

        return event;
    }
}
