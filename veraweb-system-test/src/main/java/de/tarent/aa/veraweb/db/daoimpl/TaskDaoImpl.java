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
package de.tarent.aa.veraweb.db.daoimpl;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.TaskDao;
import de.tarent.aa.veraweb.db.entity.Task;

@Component(value = "TaskDao")
public class TaskDaoImpl extends GenericDaoImpl<Task, Long> implements TaskDao {

    /**
     * Logger used wihtin this class.
     */
    private static final Logger LOG = Logger.getLogger(TaskDaoImpl.class);

    @Override
    public int deleteAll() {
        int count = super.deleteAll();

        Query q = em.createNativeQuery("ALTER SEQUENCE veraweb.ttask_pk_seq MINVALUE 1 START 1 RESTART 1");
        q.executeUpdate();

        return count;

    }

    @Override
    public Task getTask(String title) throws Exception {
        if (title == null) {
            LOG.error("title is null");
            throw new NullPointerException();
        }
        Query q = em.createNamedQuery(Task.GET_TASK_BY_TITLE);
        q.setParameter("title", title);
        return (Task) q.getSingleResult();
    }
}
