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
public class EventCategoryWorker extends ListWorkerVeraWeb {

    private static final Logger LOGGER = Logger.getLogger(EventCategoryWorker.class.getCanonicalName());

    public EventCategoryWorker() {
        super("EventCategory");
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tevent_category.fk_event", getEvent(cntx).id));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tcategorie", "tevent_category.fk_category", "tcategorie.pk");
        select.selectAs("tcategorie.catname", "name");
        select.orderBy(Order.asc("name"));
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        select.where(Expr.equal("tevent_category.fk_event", getEvent(cntx).id));
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

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
