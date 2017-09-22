package de.tarent.aa.veraweb.worker;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
	 * @return
	 */
    /** Default-Kontruktor der den Beannamen festlegt. */
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
        while(resultSet.next()) {
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
