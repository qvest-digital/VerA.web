package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.Task;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Functions needed by webapp to handle event tasks.
 */
public class EventTaskListWorker extends ListWorkerVeraWeb {

    private final static String TASK_TABLE_NAME = "veraweb.ttask";

    /**
     * Load and return all tasks of the event with the given id.
     *
     * @param oc
     * @param eventId
     */
    /**
     * Default-Kontruktor der den Beannamen festlegt.
     */
    public EventTaskListWorker() {
        super("Task");
    }

    @Override
    public List<Task> showList(OctopusContext octopusContext) throws IOException, BeanException {
        sendNoChangesMessage(octopusContext);
        super.showList(octopusContext);
        final String eventId = octopusContext.requestAsString("id");
        octopusContext.setContent("id", eventId);

        final Database database = new DatabaseVeraWeb(octopusContext);
        final List<Task> allTasks = getTasksByEventId(eventId, database);
        for (Task task : allTasks) {
            setEditorsFullName(database, task);
        }

        return allTasks;
    }

    private void setEditorsFullName(Database database, Task task) throws BeanException, IOException {
        Person person = (Person) database.getBean("Person", task.getPersonId());
        // Select statement bauen um  person mit 'personId' aus der db zu holen

        if (person != null) {
            task.setPersonName(person.lastname_a_e1 + ", " + person.firstname_a_e1);
        }
    }

    private List<Task> getTasksByEventId(String eventId, Database database) throws BeanException {
        List<Task> allTasks = null;
        final Select select = getSelectTaskStatement(eventId, database);
        final ResultSet tasksAsResultSet = database.result(select);
        try {
            allTasks = getTasksAsList(tasksAsResultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allTasks;
    }

    private List<Task> getTasksAsList(ResultSet resultSet) throws SQLException {
        final List<Task> result = new ArrayList<Task>();
        while (resultSet.next()) {
            final Task task = new Task(resultSet);
            result.add(task);
        }

        return result;
    }

    private Select getSelectTaskStatement(String eventId, Database database) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_event", eventId, "="));
        final Select select = SQL.Select(database).
          select("*").
          from(TASK_TABLE_NAME).
          where(whereCriterias).
          orderBy(Order.asc("pk"));

        return select;
    }

    private void sendNoChangesMessage(OctopusContext octopusContext) {
        if (octopusContext.getContextField("remove-task") != null &&
          octopusContext.getContextField("remove-task").equals("true")) {
            octopusContext.setContent("isEntityModified", true);
        } else if (octopusContext.getContextField("remove-task") != null) {
            octopusContext.setContent("isEntityModified", false);
        }
    }

    @Override
    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException {
        WhereList where = new WhereList();
        String strEventId = octopusContext.getRequestObject().getParamAsString("id");
        Integer eventId = null;
        if (strEventId != null && !strEventId.equals("")) {
            eventId = Integer.valueOf(strEventId);
        }
        where.addAnd(Expr.equal("fk_event", eventId));
        select.where(where);
    }

    /**
     * Bestimmt ob eine Aufgabe aufgrund bestimmter Kriterien gelöscht wird oder nicht.
     */
    @Override
    protected int removeSelection(OctopusContext octopusContext, List errors, List selection,
      TransactionContext transactionContext) throws BeanException, IOException {

        int count = 0;
        if (selection == null || selection.size() == 0) {
            return count;
        }
        Database database = transactionContext.getDatabase();

        Task task = (Task) database.createBean("Task");
        Clause clause = Expr.in("pk", selection);
        Select select = database.getSelectIds(task).where(clause);

        if (!selection.isEmpty()) {
            try {

                Map data;
                for (Iterator it = database.getList(select, transactionContext).iterator(); it.hasNext(); ) {
                    data = (Map) it.next();
                    task.id = (Integer) data.get("id");
                    if (removeBean(octopusContext, task, transactionContext)) {
                        selection.remove(task.id);
                        count++;
                    }
                }
                transactionContext.commit();
            } catch (BeanException e) {
                transactionContext.rollBack();
                throw new BeanException("Die Aufgabe(n) konnten nicht gel\u00f6scht werden.", e);
            }
        }
        return count;
    }
}
